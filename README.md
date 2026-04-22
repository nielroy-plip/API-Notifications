# API de Notificacoes com Fila

API em Spring Boot para envio/recebimento de notificacoes assincronas com RabbitMQ.

## Stack do projeto

- Java 17
- Spring Boot 3.3.4
- Spring Web
- Spring AMQP (RabbitMQ)
- Maven
- Docker Compose (para subir RabbitMQ)

## Estrutura atual

```text
src/
	main/
		java/com/example/notificacoesapi/
			NotificacoesApiApplication.java
			config/RabbitMQConfig.java
		resources/
			application.properties
docker-compose.yml
pom.xml
```

## Configuracoes principais

Arquivo: src/main/resources/application.properties

- spring.rabbitmq.host=localhost
- spring.rabbitmq.port=5672
- spring.rabbitmq.username=admin
- spring.rabbitmq.password=admin
- app.rabbitmq.exchange=notificacoes.exchange
- app.rabbitmq.queue=notificacoes.queue
- app.rabbitmq.routingkey=notificacoes.routingkey

## Como executar localmente

### 1) Subir RabbitMQ com Docker

Na raiz do projeto:

```bash
docker compose up -d
```

Painel RabbitMQ Management:

- URL: http://localhost:15672
- Usuario: admin
- Senha: admin

### 2) Subir a API Spring Boot

Na raiz do projeto:

```bash
mvn spring-boot:run
```

Opcional (build + jar):

```bash
mvn clean package
java -jar target/notificacoes-api-0.0.1-SNAPSHOT.jar
```

## Erro: "docker nao foi encontrado"

Esse erro acontece porque os pacotes NPM abaixo NAO instalam o Docker Engine/CLI no Windows:

- docker
- docker-cli
- docker-compose

Eles sao bibliotecas Node, nao o Docker oficial do sistema operacional.

### Como corrigir no Windows

1. Instale o Docker Desktop (oficial):
	 https://www.docker.com/products/docker-desktop/
2. Feche e abra novamente o terminal/VS Code.
3. Verifique no PowerShell:

```powershell
docker --version
docker compose version
```

4. Se ainda nao reconhecer, confirme se o PATH contem:

```text
C:\Program Files\Docker\Docker\resources\bin
```

Correcao rapida na sessao atual do PowerShell:

```powershell
$env:Path += ";C:\Program Files\Docker\Docker\resources\bin"
docker --version
docker compose version
```

Correcao permanente no Windows (PowerShell como usuario):

```powershell
setx PATH "$($env:PATH);C:\Program Files\Docker\Docker\resources\bin"
```

Depois do setx, feche e abra o terminal novamente.

5. Garanta que o Docker Desktop esta em execucao antes de rodar o compose.

## Comandos uteis

```bash
docker compose up -d
docker compose ps
docker compose logs -f rabbitmq
docker compose down
```

## Observacao importante

O package.json atual contem dependencias de Docker via NPM, mas para este projeto Java elas nao sao necessarias para executar containers locais. O recomendado e usar Docker Desktop + comando docker compose.
