# API de Notificações com Fila

API em Spring Boot para recebimento e processamento assíncrono de notificações com RabbitMQ.

## Objetivo

Receber requisições HTTP de notificação, publicar na fila RabbitMQ e processar no consumidor de forma desacoplada.

## Stack

- Java 17
- Spring Boot 3.3.4
- Spring Web
- Spring AMQP (RabbitMQ)
- Spring Validation
- Maven
- Docker Compose

## Arquitetura (resumo)

1. O cliente envia `POST /api/notificacoes`.
2. A API valida o payload (`email`, `mensagem`, `tipo`).
3. O produtor publica a mensagem no exchange `notificacoes.exchange`.
4. A fila `notificacoes.queue` recebe e o consumidor processa.
5. Em caso de falha no consumo, há retentativas automáticas.
6. Após exceder as tentativas, a mensagem é republicada para DLQ.

## Estrutura principal

```text
src/main/java/com/example/notificacoesapi/
	config/RabbitMQConfig.java
	controller/NotificationController.java
	service/NotificationProducerService.java
	consumer/NotificationConsumer.java
	dto/NotificationRequest.java
src/main/resources/application.properties
docker-compose.yml
pom.xml
```

## Configurações de mensageria

Arquivo: `src/main/resources/application.properties`

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin

app.rabbitmq.exchange=notificacoes.exchange
app.rabbitmq.queue=notificacoes.queue
app.rabbitmq.routingkey=notificacoes.routingkey

app.rabbitmq.dlx.exchange=notificacoes.dlx.exchange
app.rabbitmq.dlq=notificacoes.dlq
app.rabbitmq.dlq.routingkey=notificacoes.dlq.routingkey

app.rabbitmq.retry.max-attempts=3
app.rabbitmq.retry.initial-interval=1000
app.rabbitmq.retry.multiplier=2.0
app.rabbitmq.retry.max-interval=10000
```

### O que está configurado no RabbitMQ

- Exchange principal do tipo `DirectExchange`
- Fila principal durável com dead-letter configurada
- Exchange de DLX para mensagens com falha definitiva
- DLQ durável
- Conversão JSON com `Jackson2JsonMessageConverter`
- Mensagens publicadas como persistentes (`MessageDeliveryMode.PERSISTENT`)
- Retry com backoff exponencial no consumidor

## Contrato da API

### Endpoint

- Método: `POST`
- URL: `http://localhost:8080/api/notificacoes`
- Status de sucesso: `202 Accepted`

### Payload

```json
{
	"email": "usuario@exemplo.com",
	"mensagem": "Seu pedido foi aprovado",
	"tipo": "EMAIL"
}
```

### Regras de validação

- `email`: obrigatório e formato válido
- `mensagem`: obrigatória
- `tipo`: obrigatório e deve ser um de `EMAIL`, `SMS`, `PUSH`

### Exemplo com curl

```bash
curl -X POST http://localhost:8080/api/notificacoes \
	-H "Content-Type: application/json" \
	-d '{"email":"usuario@exemplo.com","mensagem":"Teste","tipo":"EMAIL"}'
```

## Como executar localmente

### 1. Subir RabbitMQ

```bash
docker compose up -d
```

RabbitMQ Management:

- URL: `http://localhost:15672`
- Usuário: `admin`
- Senha: `admin`

### 2. Rodar a aplicação

```bash
mvn spring-boot:run
```

### 3. (Opcional) Empacotar e executar JAR

```bash
mvn clean package
java -jar target/notificacoes-api-0.0.1-SNAPSHOT.jar
```

## Logs esperados no consumo

Ao enviar uma mensagem válida, o consumidor registra:

- dados recebidos da fila
- tipo de envio (`EMAIL`, `SMS` ou `PUSH`)
- confirmação de processamento

## Comandos úteis

```bash
docker compose up -d
docker compose ps
docker compose logs -f rabbitmq
docker compose down
```

## Problema comum no Windows: comando docker não encontrado

Se `docker`/`docker compose` não for reconhecido, use Docker Desktop oficial:

1. Instale: `https://www.docker.com/products/docker-desktop/`
2. Reinicie terminal/VS Code.
3. Valide:

```powershell
docker --version
docker compose version
```

Se necessário, confira se o PATH contém:

```text
C:\Program Files\Docker\Docker\resources\bin
```

## Variáveis de ambiente
Crie um arquivo .env na raiz com:
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin

A aplicação e o docker-compose usam essa variáveis.
Se não forem definidas, o ambiente local usa fallback "admin"