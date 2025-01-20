package com.example.springboot.controllers;

import com.example.springboot.constants.CommonStatus;
import com.example.springboot.entities.User;
import com.example.springboot.mappers.CommonMapper;
import com.example.springboot.modules.jwt.JWTService;
import com.example.springboot.modules.jwt.dtos.JWTPayload;
import com.example.springboot.modules.report.UserExportTemplate;
import com.example.springboot.modules.report.UserImportTemplate;
import com.example.springboot.modules.user.UserRepository;
import com.example.springboot.modules.user.dtos.UserDTO;
import com.example.springboot.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/public")
public class PublicController {

    private final CommonMapper commonMapper;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final JWTService jwtService;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @GetMapping(value = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    public String check() {
        return "OK";
    }

    private List<UserDTO> createData(int total) {
        List<UserDTO> data = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now();

        for (int i = 0; i < total; i++) {
            UserDTO user = new UserDTO();
            user.setId(UUIDUtils.generateId());
            user.setUsername(ConversionUtils.toString(UUIDUtils.generateId()));
            user.setEmail(ConversionUtils.toString(UUIDUtils.generateId()));
            user.setName(ConversionUtils.toString(UUIDUtils.generateId()));
            user.setPassword(ConversionUtils.toString(UUIDUtils.generateId()));
            user.setStatus(CommonStatus.ACTIVE);
            user.setCreatedBy(UUIDUtils.generateId());
            user.setUpdatedBy(UUIDUtils.generateId());
            user.setCreatedAt(now.plusSeconds(i));
            user.setUpdatedAt(now.plusMinutes(i));
            data.add(user);
        }
        return data;
    }

    @GetMapping("/export-excel")
    public String exportExcel(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<UserDTO> data = createData(total);
        String exportPath = ".temp/excel-" + DateUtils.toEpochMicro(null) + ".xlsx";
        ExcelUtils.Export.processTemplateWriteFile(new UserExportTemplate(data), exportPath);
        return "OK";
    }

    @GetMapping("/export-excel-batch")
    public String exportExcelBatch(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        int BATCH = 1000;
        List<UserDTO> data = createData(total);
        Workbook workbook = new SXSSFWorkbook();
        UserExportTemplate template = new UserExportTemplate();
        for (int i = 0; i < total; i += BATCH) {
            template.setBody(data.subList(i, Math.min(total, i + BATCH)));
            ExcelUtils.Export.processTemplate(template, workbook);
        }
        String exportPath = ".temp/excel-" + DateUtils.toEpochMicro(null) + ".xlsx";
        ExcelUtils.writeFile(workbook, exportPath);
        return "OK";
    }

    @GetMapping("/export-csv")
    public String exportCsv(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<UserDTO> data = createData(total);
        String exportPath = ".temp/csv-" + DateUtils.toEpochMicro(null) + ".csv";
        CSVUtils.Export.processTemplateWriteFile(new UserExportTemplate(data), exportPath);
        return "OK";
    }

    @GetMapping("/import-excel")
    public String importExcel(@RequestParam String inputPath) {
        var items = ExcelUtils.Import.processTemplate(new UserImportTemplate(), inputPath);
        log.info("items {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

    @GetMapping("/import-csv")
    public String importCsv(@RequestParam String inputPath) {
        var items = CSVUtils.Import.processTemplate(new UserImportTemplate(), inputPath);
        log.info("items {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

    @GetMapping("/test-mapper")
    public String mapper() {
        List<UserDTO> data = createData(10);
        log.info("DTOs: {}", data);
        log.info("Entities: {}", data.stream().map(commonMapper::toEntity).toList());
        return "OK";
    }

    @GetMapping("/generate-users")
    public String generateUsers(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<UserDTO> data = createData(total);
        List<User> entities = data.stream().map(commonMapper::toEntity).toList();
        userRepository.saveAll(entities);
        return "OK";
    }

    @GetMapping(value = "/i18n", produces = MediaType.TEXT_PLAIN_VALUE)
    public String testI18n(HttpServletRequest servletRequest, @RequestParam(required = false, defaultValue = "John Doe") String name) {
        return messageSource.getMessage("msg.hello", new String[]{name}, servletRequest.getLocale());
    }

    @GetMapping(value = "/rand", produces = MediaType.TEXT_PLAIN_VALUE)
    public String rand(HttpServletRequest servletRequest, @RequestParam(required = false, defaultValue = "John Doe") String name) {
        long nano = DateUtils.toEpochNano(null);
        log.info("nano {}", nano);
        log.info("nano instant {}", DateUtils.toInstant(nano));
        long micro = DateUtils.toEpochMicro(null);
        log.info("micro {}", micro);
        log.info("micro instant {}", DateUtils.toInstant(micro));
        long milli = Instant.now().toEpochMilli();
        log.info("milli {}", milli);
        log.info("milli instant {}", DateUtils.toInstant(milli));
        long second = Instant.now().getEpochSecond();
        log.info("second {}", second);
        log.info("second instant {}", DateUtils.toInstant(second));

        log.info("epoch instant {}", DateUtils.toInstant(9999999999L));
        return "OK";
    }

    @GetMapping(value = "/jwt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String jwttest(@RequestParam(required = false, defaultValue = "100") Integer total) {
        for (int i = 0; i < total; i++) {
            UUID uuid = UUIDUtils.generateId();
            JWTPayload jwtPayload = jwtService.createActivateAccountJwt(uuid);
            jwtService.verify(jwtPayload.getValue());
        }
        return "OK";
    }

    @PostMapping(value = "/import-excel-usage", produces = MediaType.TEXT_PLAIN_VALUE)
    public String importExcelUsage(@RequestParam(name = "file", required = false) MultipartFile file) {
        try {
            log.info("{} {}", file.getInputStream().readAllBytes().length, file.getSize());
            List<UserDTO> userDTOS = ExcelUtils.Import.processTemplate(new UserImportTemplate(), file);
            file.getInputStream().close();
            log.info("{} {}", file.getInputStream().readAllBytes().length, file.getSize());
            Thread.sleep(3000);
        } catch (Exception e) {
            log.error("sleep", e);
        }

        return "OK";
    }

    @GetMapping(value = "/bench", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bench() {
        try {
            for (int i = 0; i < 1_000_000; i++) {
                ZonedDateTime.now();
                OffsetDateTime.now();
                LocalDateTime.now();
                Instant.now();
                ZonedDateTime.now().toString();
                OffsetDateTime.now().toString();
                LocalDateTime.now().toString();
                Instant.now().toString();
                ZonedDateTime.now().format(DateUtils.Formatter.ID);
                OffsetDateTime.now().format(DateUtils.Formatter.ID);
                LocalDateTime.now().format(DateUtils.Formatter.ID);
                DateUtils.toEpochMicro(null);
                UUIDUtils.generate();
                UUIDUtils.generateId();
                UUID.randomUUID();
                UUIDUtils.generate().toString();
                UUIDUtils.generateId().toString();
                UUID.randomUUID().toString();
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return "OK";
    }

    @GetMapping(value = "/redis", produces = MediaType.TEXT_PLAIN_VALUE)
    public String redis() {
        try {
            reactiveRedisTemplate.opsForValue().set("allo", UUIDUtils.generateId().toString()).block();

            log.info("redis: {}", reactiveRedisTemplate.opsForValue().get("allo").block());
        } catch (Exception e) {
            log.error("", e);
        }
        return "OK";
    }

}
