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

## Configuração

### 1) OpenWeather

Acesse <a href="https://openweathermap.org/api" target="_blank"> https://openweathermap.org/api </a>
Crie uma conta e se inscreva no `Free Plan` para conseguir uma `api key`.

### 2) AWS

Acesse o AWS Lambda e crie uma nova função com Java 21.

Você precisará fazer o upload do código como um arquivo `.jar`. Para isso rode o comando:

```Bash
mvn clean package
```

O arquivo estará pronto em `weatherToS3/target/weatherToS3.jar`.

Após subir o arquivo `.jar`, em Configurações de tempo de execução, clique em editar.
E em Manipulador, escreva o código abaixo que se refere ao caminho do método handleRequest().

```java
com.weatherToS3.App::handleRequest
```

Após essa configuração, acesse a aba `Configuração` e acesse `Permissões` para adicionar acesso a AWS S3.
Para isso você pode clicar no link para a função IAM para acessá-la diretamente.
Clicar em adicionar permissões e anexar políticas.
Procurar por `AmazonS3FullAccess` e adicionar permissões.

Ainda em `Configuração` acesse `Variáveis de Ambiente` e configure as váriaveis necessárias: `base_url`, `lat`, `lon`, `api_key`, `units`, `bucket_name`.

- Onde `base_url` é 
```
https://api.openweathermap.org/data/2.5/weather
```

- `lat` e `lon` são as coordenadas do local onde você quer coletar as temperaturas.

- `api_key` é a chave obtida na OpenWeather

- `units` é a unidade de medida você pode escolher entre `standard`, `metric` (Celsius) and `imperial` (Fahrenheit).

- `bucket_name` é o nome do bucket da S3 em que o arquivo será salvo.

Após essas configurações, acesse Gatilhos.
Escolha EventBridge, Criar uma regra, Preencha Nome e Descrição.

Em Expressão de programação digite o código abaixo para a aplicação coletar a temperatura e salvar no S3 a cada 1 hora:
```
rate(1 hour)
```

## Ajustes rápidos (bucket/arquivo/planilha)

Esses valores estão fixos em [Main.java](file:///c:/Users/Iagov/OneDrive/Documentos/GitHub/untitled/src/main/java/org/example/Main.java):
- `fileName`: `Data.xls`
- `sheetName`: `Temperatures`

Se necessário, altere ali para apontar para seu bucket e nomes desejados.


## Estrutura do projeto

- `src/main/java/org/example/Main.java`: orquestra o fluxo (API → Excel → S3)
- `src/main/java/org/example/WeatherData.java`: busca temperatura na OpenWeather
- `src/main/java/org/example/S3ExcelService.java`: manipula Excel `.xls` e integra com S3
