package com.example.demo.modules.role;

import com.example.demo.constants.CommonRegex;
import com.example.demo.constants.CommonStatus;
import com.example.demo.constants.Constants;
import com.example.demo.modules.role.dtos.RoleDTO;
import com.example.demo.utils.I18nHelper;
import com.example.demo.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class RoleValidator {

    private static final List<Integer> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE);

    public void validateCreate(RoleDTO requestDTO) {
        ValidationUtils.notEmpty(requestDTO.getCode(), I18nHelper.getMessageX("form.error.missing", "field.code"));
        ValidationUtils.startsWith(requestDTO.getCode(), Constants.ROLE_STARTS_WITH, I18nHelper.getMessageX("form.error.not_start_with", "field.code", Constants.ROLE_STARTS_WITH));
        ValidationUtils.maxLength(requestDTO.getCode(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.code", 255));
        ValidationUtils.isPattern(requestDTO.getCode(), CommonRegex.CODE, I18nHelper.getMessageX("form.error.invalid", "field.code"));
        ValidationUtils.maxLength(requestDTO.getName(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.name", 255));
        ValidationUtils.maxLength(requestDTO.getDescription(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.description", 255));
        ValidationUtils.isIn(requestDTO.getStatus(), validStatus, I18nHelper.getMessageX("form.error.invalid", "field.status"));
    }

    public void validateUpdate(RoleDTO requestDTO) {
        ValidationUtils.maxLength(requestDTO.getName(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.name", 255));
        ValidationUtils.maxLength(requestDTO.getDescription(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.description", 255));
        ValidationUtils.isIn(requestDTO.getStatus(), validStatus, I18nHelper.getMessageX("form.error.invalid", "field.status"));
    }

}
