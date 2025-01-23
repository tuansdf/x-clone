package com.example.springboot.controllers;

import com.example.springboot.constants.CommonStatus;
import com.example.springboot.dtos.CommonResponse;
import com.example.springboot.entities.User;
import com.example.springboot.mappers.CommonMapper;
import com.example.springboot.modules.configuration.ConfigurationService;
import com.example.springboot.modules.configuration.dtos.ConfigurationDTO;
import com.example.springboot.modules.jwt.JWTService;
import com.example.springboot.modules.jwt.dtos.JWTPayload;
import com.example.springboot.modules.report.UserExportTemplate;
import com.example.springboot.modules.report.UserImportTemplate;
import com.example.springboot.modules.role.RoleService;
import com.example.springboot.modules.user.UserRepository;
import com.example.springboot.modules.user.dtos.UserDTO;
import com.example.springboot.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/public")
public class PublicController {

    private final CommonMapper commonMapper;
    private final UserRepository userRepository;
    private final I18nHelper i18nHelper;
    private final JWTService jwtService;
    private final StringRedisTemplate redisTemplate;
    private final RoleService roleService;
    private final ConfigurationService configurationService;

    @GetMapping(value = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    public String check() {
        return "OK";
    }

    private List<UserDTO> createData(int total) {
        List<UserDTO> data = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now();

        for (int i = 0; i < total; i++) {
            UserDTO user = new UserDTO();
            user.setId(UUIDHelper.generateId());
            user.setUsername(ConversionUtils.toString(UUIDHelper.generateId()));
            user.setEmail(ConversionUtils.toString(UUIDHelper.generateId()));
            user.setName(ConversionUtils.toString(UUIDHelper.generateId()));
            user.setPassword(ConversionUtils.toString(UUIDHelper.generateId()));
            user.setStatus(CommonStatus.ACTIVE);
            user.setCreatedBy(UUIDHelper.generateId());
            user.setUpdatedBy(UUIDHelper.generateId());
            user.setCreatedAt(now.plusSeconds(i));
            user.setUpdatedAt(now.plusMinutes(i));
            data.add(user);
        }
        return data;
    }

    @GetMapping("/export-excel")
    public String exportExcel(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<UserDTO> data = createData(total);
        String exportPath = ".temp/excel-" + DateUtils.toEpochMicro() + ".xlsx";
        ExcelHelper.Export.processTemplateWriteFile(new UserExportTemplate(data), exportPath);
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
            ExcelHelper.Export.processTemplate(template, workbook);
        }
        String exportPath = ".temp/excel-" + DateUtils.toEpochMicro() + ".xlsx";
        ExcelHelper.writeFile(workbook, exportPath);
        return "OK";
    }

    @GetMapping("/export-csv")
    public String exportCsv(@RequestParam(required = false, defaultValue = "1000") Integer total) {
        List<UserDTO> data = createData(total);
        String exportPath = ".temp/csv-" + DateUtils.toEpochMicro() + ".csv";
        CSVHelper.Export.processTemplateWriteFile(new UserExportTemplate(data), exportPath);
        return "OK";
    }

    @GetMapping("/import-excel")
    public String importExcel(@RequestParam String inputPath) {
        var items = ExcelHelper.Import.processTemplate(new UserImportTemplate(), inputPath);
        log.info("items {}", items.subList(0, Math.min(items.size(), 100)));
        return "OK";
    }

    @GetMapping("/import-csv")
    public String importCsv(@RequestParam String inputPath) {
        var items = CSVHelper.Import.processTemplate(new UserImportTemplate(), inputPath);
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
        List<User> users = data.stream().map(commonMapper::toEntity).toList();
        users = userRepository.saveAll(users);
        Set<Long> roleIds = new HashSet<>();
        roleIds.add(1L);
        for (User user : users) {
            roleService.addToUser(user.getId(), roleIds);
        }
        return "OK";
    }

    @GetMapping(value = "/i18n", produces = MediaType.TEXT_PLAIN_VALUE)
    public String testI18n(HttpServletRequest servletRequest, @RequestParam(required = false, defaultValue = "John Doe") String name) {
        return i18nHelper.getMessage("msg.hello", servletRequest.getLocale(), name);
    }

    @GetMapping(value = "/rand", produces = MediaType.TEXT_PLAIN_VALUE)
    public String rand(HttpServletRequest servletRequest, @RequestParam(required = false, defaultValue = "John Doe") String name) {
        long nano = DateUtils.toEpochNano(null);
        log.info("nano {}", nano);
        log.info("nano instant {}", DateUtils.toInstant(nano));
        long micro = DateUtils.toEpochMicro();
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
            UUID uuid = UUIDHelper.generateId();
            JWTPayload jwtPayload = jwtService.createActivateAccountJwt(uuid, false);
            jwtService.verify(jwtPayload.getValue());
        }
        return "OK";
    }

    @PostMapping(value = "/import-excel-usage", produces = MediaType.TEXT_PLAIN_VALUE)
    public String importExcelUsage(@RequestParam(name = "file", required = false) MultipartFile file) {
        try {
            log.info("{} {}", file.getInputStream().readAllBytes().length, file.getSize());
            List<UserDTO> userDTOS = ExcelHelper.Import.processTemplate(new UserImportTemplate(), file);
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
                DateUtils.toEpochMicro();
                ConversionUtils.toString(DateUtils.toEpochMicro());
                UUIDHelper.generate();
                UUIDHelper.generateId();
                UUID.randomUUID();
                UUIDHelper.generate().toString();
                UUIDHelper.generateId().toString();
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
            redisTemplate.opsForValue().set("allo", UUIDHelper.generateId().toString());

            log.info("redis: {}", redisTemplate.opsForValue().get("allo"));
        } catch (Exception e) {
            log.error("", e);
        }
        return "OK";
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CommonResponse<ConfigurationDTO>> findOneByCode(@PathVariable String code) {
        try {
            var result = configurationService.findOneByCodeOrThrow(code);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
