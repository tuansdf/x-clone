package com.example.demo.modules.permission;

import com.example.demo.configs.RequestContextHolder;
import com.example.demo.constants.CommonStatus;
import com.example.demo.constants.ResultSetName;
import com.example.demo.dtos.PaginationResponseData;
import com.example.demo.entities.Permission;
import com.example.demo.exception.CustomException;
import com.example.demo.mappers.CommonMapper;
import com.example.demo.modules.permission.dtos.PermissionDTO;
import com.example.demo.modules.permission.dtos.SearchPermissionRequestDTO;
import com.example.demo.utils.ConversionUtils;
import com.example.demo.utils.SQLBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class PermissionServiceImpl implements PermissionService {

    private final CommonMapper commonMapper;
    private final PermissionRepository permissionRepository;
    private final EntityManager entityManager;
    private final PermissionValidator permissionValidator;

    @Override
    public PermissionDTO save(PermissionDTO requestDTO) {
        UUID actionBy = RequestContextHolder.get().getUserId();
        Permission result = null;
        if (requestDTO.getId() != null) {
            Optional<Permission> permissionOptional = permissionRepository.findById(requestDTO.getId());
            if (permissionOptional.isPresent()) {
                permissionValidator.validateUpdate(requestDTO);
                result = permissionOptional.get();
            }
        }
        if (result == null) {
            permissionValidator.validateCreate(requestDTO);
            String code = ConversionUtils.toCode(requestDTO.getCode());
            if (permissionRepository.existsByCode(code)) {
                throw new CustomException(HttpStatus.CONFLICT);
            }
            result = new Permission();
            result.setCode(code);
            result.setCreatedBy(actionBy);
        }
        result.setName(requestDTO.getName());
        if (requestDTO.getStatus() == null) {
            requestDTO.setStatus(CommonStatus.ACTIVE);
        }
        result.setStatus(requestDTO.getStatus());
        result.setUpdatedBy(actionBy);
        return commonMapper.toDTO(permissionRepository.save(result));
    }

    @Override
    public PermissionDTO findOneById(UUID id) {
        Optional<Permission> result = permissionRepository.findById(id);
        return result.map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public PermissionDTO findOneByIdOrThrow(UUID id) {
        PermissionDTO result = findOneById(id);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public PermissionDTO findOneByCode(String code) {
        Optional<Permission> result = permissionRepository.findTopByCode(code);
        return result.map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public PermissionDTO findOneByCodeOrThrow(String code) {
        PermissionDTO result = findOneByCode(code);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public Set<String> findAllCodesByRoleId(UUID roleId) {
        Set<String> result = permissionRepository.findAllCodesByRoleId(roleId);
        if (result == null) {
            return new HashSet<>();
        }
        return result;
    }

    @Override
    public Set<String> findAllCodesByUserId(UUID userId) {
        Set<String> result = permissionRepository.findAllCodesByUserId(userId);
        if (result == null) {
            return new HashSet<>();
        }
        return result;
    }

    @Override
    public List<PermissionDTO> findAllByRoleId(UUID roleId) {
        List<Permission> result = permissionRepository.findAllByRoleId(roleId);
        if (result == null) {
            return new ArrayList<>();
        }
        return result.stream().map(commonMapper::toDTO).toList();
    }

    @Override
    public List<PermissionDTO> findAllByUserId(UUID userId) {
        List<Permission> result = permissionRepository.findAllByUserId(userId);
        if (result == null) {
            return new ArrayList<>();
        }
        return result.stream().map(commonMapper::toDTO).toList();
    }

    @Override
    public PaginationResponseData<PermissionDTO> search(SearchPermissionRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<PermissionDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationResponseData<PermissionDTO> executeSearch(SearchPermissionRequestDTO requestDTO, boolean isCount) {
        PaginationResponseData<PermissionDTO> result = SQLBuilder.getPaginationResponseData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select p.* ");
        }
        builder.append(" from permission p ");
        builder.append(" where 1=1 ");
        if (StringUtils.isNotBlank(requestDTO.getCode())) {
            builder.append(" and p.code = :code ");
            params.put("code", requestDTO.getCode());
        }
        if (requestDTO.getStatus() != null) {
            builder.append(" and p.status = :status ");
            params.put("status", requestDTO.getStatus());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and p.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and p.created_at <= :createdAtTo ");
            params.put("createdAtTo", requestDTO.getCreatedAtTo());
        }
        if (!isCount) {
            builder.append(SQLBuilder.getPaginationString(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLBuilder.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLBuilder.getTotalPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.PERMISSION_SEARCH);
            SQLBuilder.setParams(query, params);
            List<PermissionDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

}
