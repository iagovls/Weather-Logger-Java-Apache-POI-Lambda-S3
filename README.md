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

## Como funciona

O fluxo principal está em `Main.java`
- Busca a temperatura via `WeatherData.java`
- Abre/cria o Excel no S3 e escreve na planilha via `S3ExcelService.java`

O arquivo gerado é `.xls` (Apache POI `HSSFWorkbook`), com as colunas:
- `Data e horário` (formato `dd/MM/yyyy HH:mm`)
- `Temperatura`

## Requisitos

- Java 21
- Maven 3.9+
- Conta AWS com permissão para ler/gravar no S3
- API key da OpenWeather

## Configuração

### 1) OpenWeather (weather.properties)

Crie o arquivo `src/main/resources/weather.properties` (ele já está no `.gitignore` para evitar vazamento de credenciais).

Você pode usar o template:
- Copie `src/main/resources/weather.properties.example` para `src/main/resources/weather.properties`
- Preencha `api_key` com sua chave

Campos:
- `base_url`: endpoint da OpenWeather
- `lat` / `lon`: latitude/longitude
- `units`: `metric` (Celsius) ou `imperial` (Fahrenheit)
- `api_key`: sua chave da OpenWeather

### 2) AWS (credenciais e região)

O projeto usa o AWS SDK v2 (`S3Client.create()`), então ele depende do *Default Credentials Provider Chain*:
- Variáveis de ambiente `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` (e `AWS_SESSION_TOKEN` se aplicável)
- Perfil local em `~/.aws/credentials`
- IAM Role (quando rodando dentro da AWS, ex.: Lambda/EC2)

Também é necessário definir uma região (exemplos):
- `AWS_REGION=sa-east-1` (ou a região do seu bucket)
- ou perfil configurado localmente

Permissões mínimas no bucket/objeto:
- `s3:GetObject`
- `s3:PutObject`

## Ajustes rápidos (bucket/arquivo/planilha)

Hoje esses valores estão fixos em [Main.java](file:///c:/Users/Iagov/OneDrive/Documentos/GitHub/untitled/src/main/java/org/example/Main.java):
- `bucket`: `analise-abastecimentos-bucket`
- `fileName`: `Dados.xls`
- `sheetName`: `Temperaturas`

Se necessário, altere ali para apontar para seu bucket e nomes desejados.

## Executando localmente

1) Compilar:

```bash
mvn clean package
```

2) Executar:

Como este projeto não empacota as dependências em um único JAR, a forma mais prática é rodar pela sua IDE (executando a classe `com.weatherToS3.Main`).

## Rodando na AWS Lambda (opcional)

A classe [Main.java](file:///c:/Users/Iagov/OneDrive/Documentos/GitHub/untitled/src/main/java/org/example/Main.java) implementa `RequestHandler` como base para execução em Lambda, mas o fluxo principal está no método `run()`.

Ao rodar em Lambda:
- Use uma IAM Role anexada à função com permissão no S3
- Forneça `weather.properties` no pacote (ou adapte para ler de variáveis de ambiente/Secrets Manager)

## Estrutura do projeto

- `src/main/java/org/example/Main.java`: orquestra o fluxo (API → Excel → S3)
- `src/main/java/org/example/WeatherData.java`: busca temperatura na OpenWeather
- `src/main/java/org/example/S3ExcelService.java`: manipula Excel `.xls` e integra com S3
- `src/main/resources/weather.properties.example`: modelo de configuração (sem segredo)

