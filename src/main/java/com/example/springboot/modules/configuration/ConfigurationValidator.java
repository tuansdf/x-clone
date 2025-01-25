package com.example.springboot.modules.configuration;

import com.example.springboot.constants.CommonRegex;
import com.example.springboot.constants.CommonStatus;
import com.example.springboot.modules.configuration.dtos.ConfigurationDTO;
import com.example.springboot.utils.I18nHelper;
import com.example.springboot.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ConfigurationValidator {

    private final static List<String> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE);

    public void validateCreate(ConfigurationDTO requestDTO) {
        ValidationUtils.notEmpty(requestDTO.getCode(), I18nHelper.getMessageX("form.error.missing", "field.code"));
        ValidationUtils.maxLength(requestDTO.getCode(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.code", 255));
        ValidationUtils.isPattern(requestDTO.getCode(), CommonRegex.CODE, I18nHelper.getMessageX("form.error.invalid", "field.code"));
        ValidationUtils.maxLength(requestDTO.getValue(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.value", 255));
        ValidationUtils.maxLength(requestDTO.getDescription(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.description", 255));
        ValidationUtils.isIn(requestDTO.getStatus(), validStatus, I18nHelper.getMessageX("form.error.invalid", "field.status"));
    }

    public void validateUpdate(ConfigurationDTO requestDTO) {
        ValidationUtils.maxLength(requestDTO.getValue(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.value"));
        ValidationUtils.maxLength(requestDTO.getDescription(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.description"));
        ValidationUtils.isIn(requestDTO.getStatus(), validStatus, I18nHelper.getMessageX("form.error.invalid", "field.status"));
    }

}
