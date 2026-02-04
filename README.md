<h1 align="center" style="font-weight: bold;">Weather → Excel → S3 (Java) 💻</h1>

<p align="center">
<img src="https://img.shields.io/badge/Java-21-blue.svg" alt="Java">
<img src="https://img.shields.io/badge/Maven-3.9+-orange.svg" alt="Maven">
<img src="https://img.shields.io/badge/AWS-S3-green.svg" alt="AWS S3">
<img src="https://img.shields.io/badge/OpenWeather-API-yellow.svg" alt="OpenWeather API">
</p>

<p align="center">
  <b>Java Project (Maven) that reads weather data from OpenWeather, writes it to an Excel file, and stores it in an AWS S3 bucket.</b>
</p>

<h2 id="started">🚀 Getting started</h2>

### Required

- Java 21
- Maven 3.9+
- AWS Account with S3 permissions
- OpenWeather API key (free tier)


### Clone the repository

```bash
git clone https://github.com/iagovls/Weather-Logger-Java-Apache-POI-Lambda-S3.git
cd weatherToS3
```

## How it works

The main flow is in `Main.java`
- Fetches the temperature via `WeatherData.java`
- Opens/creates an Excel file in S3 and writes to the spreadsheet via `S3ExcelService.java`

The generated file is `.xls` (Apache POI `HSSFWorkbook`), with the following columns:
- `Data e horário` (formato `dd/MM/yyyy HH:mm`)
- `Temperatura`

## Configuration

### 1) OpenWeather

Go to <a href="https://openweathermap.org/api" target="_blank"> https://openweathermap.org/api </a>
Create an account and subscribe on the `Free Plan` to obtain an `API key`.

### 2) AWS

Go to AWS Lambda and create a new function using `Java 21`

You will need to upload the code as a `.jar` file. To do this, run:

```Bash
mvn clean package
```

The file will be generated at `weatherToS3/target/weatherToS3.jar`.

After uploading the `.jar` file, go to `Runtime settings`, click `Edit`
and in `Handler`, enter the code below, which points to the handleRequest() method.

```java
com.weatherToS3.App::handleRequest
```

After this, go to `Configuration` tab and then `Permissions` to add access to AWS S3.
You can click the link to the IAM role to open it directly. Then click `Add permissions`, `Attach policies`, search for `AmazonS3FullAccess`, and attach it.
Clicar em adicionar permissões e anexar políticas.
Procurar por `AmazonS3FullAccess` e adicionar permissões.

Still under `Configuration`, go to `Environment variables` and set the required variables: 
- `base_url`
- `lat`
- `lon`
- `api_key`
- `units`
- `bucket_name`

Where
- `base_url`:
```
https://api.openweathermap.org/data/2.5/weather
```

- `lat` and `lon` are the coordinates of the location where you want to collect temperatures.

- `api_key` is a key obtained from OpenWeather.

- `units` is the measurement unit. You can choose between:
  - `standard`
  - `metric` (Celsius)
  - `imperial` (Fahrenheit).

- `bucket_name` is the name of the S3 bucket where the file will be saved.

After setting these variables, go to `Triggers`.
Choose `EventBridge`, create a rule, and fill in the `Name` and `Description`

In `Schedule expression`, enter the code below so the application collects the temperature and saves it to S3 every 1 hour:
```
rate(1 hour)
```

## Quick Adjustments (bucket/file/sheet)

This values are hardcoded in [Main.java](file:///c:/Users/Iagov/OneDrive/Documentos/GitHub/untitled/src/main/java/org/example/Main.java):
- `fileName`: `Data.xls`
- `sheetName`: `Temperatures`

If needed change them there to point to your derided bucket and names.


## Project Structure

- `src/main/java/org/example/Main.java`: orchestrates the flow (API → Excel → S3)
- `src/main/java/org/example/WeatherData.java`: fetches temperature data from OpenWeather
- `src/main/java/org/example/S3ExcelService.java`: handles `.xls` files e integrates with S3
