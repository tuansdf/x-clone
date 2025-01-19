package com.example.springboot.modules.email;

import com.example.springboot.configs.RequestContextHolder;
import com.example.springboot.constants.CommonStatus;
import com.example.springboot.constants.CommonType;
import com.example.springboot.constants.Env;
import com.example.springboot.mappers.CommonMapper;
import com.example.springboot.modules.email.dtos.EmailDTO;
import com.example.springboot.utils.ConversionUtils;
import com.example.springboot.utils.I18nUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class EmailServiceImpl implements EmailService {

    private final Env env;
    private final CommonMapper commonMapper;
    private final I18nUtils i18nUtils;
    private final EmailRepository emailRepository;

    @Override
    public EmailDTO send(EmailDTO emailDTO) {
        // TODO: send email
        return commonMapper.toDTO(emailRepository.save(commonMapper.toEntity(emailDTO)));
    }

    @Override
    public EmailDTO sendResetPasswordEmail(String email, String name, String token) {
        UUID actionBy = ConversionUtils.toUUID(RequestContextHolder.get().getUserId());
        EmailDTO emailDTO = EmailDTO.builder()
                .fromEmail(env.getFromEmail())
                .toEmail(email)
                .subject(i18nUtils.getMessage("email.reset_password_subject"))
                .content(i18nUtils.getMessage("email.reset_password_content", new String[]{name, token}))
                .createdBy(actionBy)
                .updatedBy(actionBy)
                .status(CommonStatus.ACTIVE)
                .type(CommonType.RESET_PASSWORD)
                .build();
        return send(emailDTO);
    }

    @Override
    public EmailDTO sendActivateAccountEmail(String email, String name, String token) {
        UUID actionBy = ConversionUtils.toUUID(RequestContextHolder.get().getUserId());
        EmailDTO emailDTO = EmailDTO.builder()
                .fromEmail(env.getFromEmail())
                .toEmail(email)
                .subject(i18nUtils.getMessage("email.activate_account_subject"))
                .content(i18nUtils.getMessage("email.activate_account_content", new String[]{name, token}))
                .createdBy(actionBy)
                .updatedBy(actionBy)
                .status(CommonStatus.ACTIVE)
                .type(CommonType.ACTIVATE_ACCOUNT)
                .build();
        return send(emailDTO);
    }

}
