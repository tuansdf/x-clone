package com.example.springboot.controllers;

import com.example.springboot.constants.PermissionCode;
import com.example.springboot.dtos.CommonResponse;
import com.example.springboot.dtos.PaginationResponseData;
import com.example.springboot.modules.permission.PermissionService;
import com.example.springboot.modules.permission.dtos.PermissionDTO;
import com.example.springboot.modules.permission.dtos.SearchPermissionRequestDTO;
import com.example.springboot.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/code/{code}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PermissionDTO>> findOneByCode(@PathVariable String code) {
        try {
            var result = permissionService.findOneByCodeOrThrow(code);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @GetMapping("/{id}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PermissionDTO>> findOne(@PathVariable Long id) {
        try {
            var result = permissionService.findOneByIdOrThrow(id);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PutMapping
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PermissionDTO>> save(@RequestBody PermissionDTO requestDTO) {
        try {
            var result = permissionService.save(requestDTO);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @GetMapping("/search")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PaginationResponseData<PermissionDTO>>> search(
            @RequestParam(required = false) Long pageNumber,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) OffsetDateTime createdAtFrom,
            @RequestParam(required = false) OffsetDateTime createdAtTo,
            @RequestParam(required = false, defaultValue = "false") Boolean count) {
        try {
            var requestDTO = SearchPermissionRequestDTO.builder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .code(code)
                    .status(status)
                    .createdAtTo(createdAtTo)
                    .createdAtFrom(createdAtFrom)
                    .build();
            var result = permissionService.search(requestDTO, count);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
