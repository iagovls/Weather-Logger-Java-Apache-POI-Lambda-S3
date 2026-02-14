package com.weatherToS3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.lambda.powertools.logging.Logging;

public class S3ExcelService  {
    Workbook workbook = new HSSFWorkbook();
    App app = new App();
    Logger logger = app.logger;

    @Logging
    public Sheet createSheet(String sheetName){
        logger.info("Creating a sheet.");
        return workbook.createSheet(sheetName);
    }

    @Logging
    public Sheet getSheetOrCreateIfNotExists(String sheetName){
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null){
            logger.info("Sheet is null, creating sheet.");
            sheet = createSheet(sheetName);
        }
        return sheet;
    };

    public void writeHeader(Sheet sheet){

        Row row = sheet.createRow(0);
        Cell dateTimeHeader = row.createCell(0);
        dateTimeHeader.setCellValue("Data e horário");
        Cell tempHeader = row.createCell(1);
        tempHeader.setCellValue("Temperatura");
        Cell feelsLikeHeader = row.createCell(2);
        feelsLikeHeader.setCellValue("Sensação térmica");
        Cell humidityHeader = row.createCell(3);
        humidityHeader.setCellValue("Umidade");
    }

    public void writeDataInSheet(
            Sheet sheet,
            int colPosition,
            Data values) {
        ZonedDateTime dateTime = ZonedDateTime.now();
        Date date = Date.from(dateTime.minusHours(3).toInstant());

        CreationHelper helper = workbook.getCreationHelper();
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(helper.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));

        int nextRowIndex = sheet.getPhysicalNumberOfRows() == 0 ? 0 : sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(nextRowIndex);
        Cell tempCell = row.createCell(colPosition);
        tempCell.setCellValue(values.getTemp());
        Cell feelsLikeCell = row.createCell(colPosition + 1);
        feelsLikeCell.setCellValue(values.getFeelsLike());
        Cell humidityCell = row.createCell(colPosition + 2);
        humidityCell.setCellValue(values.getHumidity());

        Cell dateTimeCell = row.createCell(colPosition - 1);
        dateTimeCell.setCellValue(date);
        dateTimeCell.setCellStyle(style);

    }

    @Logging
    public void saveWorkbookToS3(String bucket, String key){
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            workbook.write(baos);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("application/vnd.ms-excel")
                    .build();
            DependencyFactory.s3Client().putObject(request, RequestBody.fromBytes(baos.toByteArray()));
            Path localCache = Paths.get(System.getProperty("java.io.tmpdir"),key);
            logger.info("Writing excel file in local cache.");
            Files.write(localCache, baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Logging
    public void loadWorkbookFromS3OrCreateIfNotExists(String bucket, String key) throws IOException {
        try{

            Path localCache = Paths.get(System.getProperty("java.io.tmpdir"),key);
            if (Files.exists(localCache)) {
                try(var in = Files.newInputStream(localCache)){
                    logger.info("Getting Excel file from local cache.");
                    this.workbook = new HSSFWorkbook(in);
                }
                return;
            }
            GetObjectRequest req = GetObjectRequest
                    .builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            try(var in = DependencyFactory.s3Client().getObject(req)){
                logger.info("Getting Excel file from S3.");
                this.workbook = new HSSFWorkbook(in);
            }
            try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
                workbook.write(baos);
                Files.write(localCache,baos.toByteArray());
            }
        } catch (S3Exception e){
            logger.info("Can't get Excel file, creating an blank one.");
            this.workbook = new HSSFWorkbook();
            System.out.println("Error details: " + e.getMessage());
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public S3ExcelService() {
    }

}
