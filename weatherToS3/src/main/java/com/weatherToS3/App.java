package com.weatherToS3;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;


import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;

public class App implements RequestHandler<ScheduledEvent, String> {
    private final S3Client s3Client;
    Logger logger = LoggerFactory.getLogger(App.class);

    public App() {
        // Initialize the SDK client outside of the handler method so that it can be reused for subsequent invocations.
        // It is initialized when the class is loaded.
        s3Client = DependencyFactory.s3Client();
        // Consider invoking a simple api here to pre-warm up the application, eg: dynamodb#listTables
        logger = LoggerFactory.getLogger(App.class);
    }

    @Override
    public String handleRequest(final ScheduledEvent event, final Context context) {
        LambdaLogger lambdaLogger = context.getLogger();
        lambdaLogger.log("Start to handle request");
        WeatherData data = new WeatherData();
        S3ExcelService s3 = new S3ExcelService();
        String fileName = "Dados.xls";
        String sheetName = "Temperaturas";
        String bucket = System.getenv("bucket_name");

        try {
            lambdaLogger.log("Getting actual temp");
            Data actualWeatherData = data.getActualWeatherData();

            s3.loadWorkbookFromS3OrCreateIfNotExists(bucket, fileName);
            Sheet sheet = s3.getSheetOrCreateIfNotExists(sheetName);
            s3.writeHeader(sheet);
            s3.writeDataInSheet(sheet, 1, actualWeatherData);
            s3.saveWorkbookToS3(bucket, fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        lambdaLogger.log("End of application.");
        return "Ok";
    }
}
