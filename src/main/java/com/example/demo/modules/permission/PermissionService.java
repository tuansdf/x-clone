package com.example.demo.modules.permission;

import com.example.demo.dtos.PaginationResponseData;
import com.example.demo.modules.permission.dtos.PermissionDTO;
import com.example.demo.modules.permission.dtos.SearchPermissionRequestDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionService {

    PermissionDTO save(PermissionDTO permissionDTO);

    PermissionDTO findOneById(UUID id);

    PermissionDTO findOneByIdOrThrow(UUID id);

    PermissionDTO findOneByCode(String code);

    PermissionDTO findOneByCodeOrThrow(String code);

    Set<String> findAllCodesByRoleId(UUID roleId);

    Set<String> findAllCodesByUserId(UUID userId);

    List<PermissionDTO> findAllByRoleId(UUID roleId);

    List<PermissionDTO> findAllByUserId(UUID userId);

    PaginationResponseData<PermissionDTO> search(SearchPermissionRequestDTO requestDTO, boolean isCount);

}
