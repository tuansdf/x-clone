package com.example.sbt.common.util.io;

import com.example.sbt.common.exception.InvalidImportTemplateException;
import com.example.sbt.common.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class CSVHelper {

    private static final int DEFAULT_BODY_ROW = 1;

    public static class Export {
        public static <T> void processTemplate(ExportTemplate<T> template, Writer writer) {
            var header = template.getHeader();
            var body = template.getBody();
            var rowDataExtractor = template.getRowExtractor();
            var rowSize = header.size();

            try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(header.toArray(new String[0])).build())) {
                int i = 0;
                for (T data : body) {
                    int rowNum = DEFAULT_BODY_ROW + i;
                    csvPrinter.printRecord(CommonUtils.rightPad(rowDataExtractor.apply(data, rowNum), rowSize));
                    i++;
                }
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static <T> byte[] processTemplateToBytes(ExportTemplate<T> exportTemplate) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                 OutputStreamWriter writer = new OutputStreamWriter(bufferedOutputStream)) {
                processTemplate(exportTemplate, writer);
                return outputStream.toByteArray();
            } catch (Exception e) {
                log.error("processTemplateToBytes", e);
                return new byte[0];
            }
        }

        public static <T> void processTemplateWriteFile(ExportTemplate<T> exportTemplate, String outputPath) {
            try (FileWriter writer = new FileWriter(outputPath, StandardCharsets.UTF_8);
                 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                processTemplate(exportTemplate, bufferedWriter);
            } catch (Exception e) {
                log.error("processTemplateWriteFile", e);
            }
        }
    }

    public static class Import {
        public static <T> void processTemplate(ImportTemplate<T> template, Reader reader) {
            var header = template.getHeader();
            var rowPreProcessor = template.getRowPreProcessor();
            var rowProcessor = template.getRowProcessor();
            var rowSize = header.size();

            try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {
                List<String> csvHeader = csvParser.getHeaderNames();
                if (csvHeader.size() != rowSize) {
                    throw new InvalidImportTemplateException();
                }

                for (int i = 0; i < rowSize; i++) {
                    if (!csvHeader.get(i).equals(header.get(i))) {
                        throw new InvalidImportTemplateException();
                    }
                }

                for (CSVRecord record : csvParser) {
                    var item = rowPreProcessor.apply(CommonUtils.rightPad(record.stream().map(x -> (Object) x).toList(), rowSize));
                    if (rowProcessor != null) {
                        rowProcessor.accept(item);
                    }
                }
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static <T> void processTemplate(ImportTemplate<T> template, String filePath) {
            try (FileReader reader = new FileReader(Paths.get(filePath).toFile())) {
                processTemplate(template, reader);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static <T> void processTemplate(ImportTemplate<T> template, byte[] bytes) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                processTemplate(template, reader);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static <T> void processTemplate(ImportTemplate<T> template, MultipartFile file) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                processTemplate(template, reader);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }
    }

}
