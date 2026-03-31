# app-microservice-location-consumer

Microservicio consumidor del sistema de paisajes. Recibe eventos de RabbitMQ, notifica al moderador vía Telegram con la imagen, ubicación y botones de aprobación, y publica el resultado de vuelta a RabbitMQ.

## Tecnologías

- Java 17
- Spring Boot 3.2.5
- Spring WebFlux
- RabbitMQ
- Telegram Bot API

## Requisitos previos

- Java 17
- Maven
- RabbitMQ corriendo (ver infraestructura)
- Bot de Telegram creado con @BotFather
- Túnel público para exponer el webhook (cloudflared o similar)
- `app-microservice-location-producer` corriendo
- Variables de entorno configuradas

## Variables de entorno

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `RABBITMQ_HOST` | Host de RabbitMQ | `localhost` |
| `RABBITMQ_PORT` | Puerto de RabbitMQ | `5672` |
| `RABBITMQ_USER` | Usuario de RabbitMQ | `guest` |
| `RABBITMQ_PASSWORD` | Contraseña de RabbitMQ | `guest` |
| `TELEGRAM_BOT_TOKEN` | Token del bot de Telegram | `8786304847:AAEFBuo...` |
| `TELEGRAM_CHAT_ID` | ID del chat donde el bot envía mensajes | `954491851` |

## Cómo ejecutar localmente

1. Levantar la infraestructura:
```bash
cd infrastructure
docker-compose up -d
```

2. Exponer el servicio con cloudflared para recibir el webhook de Telegram:
```bash
cloudflared tunnel --url http://localhost:8002
```
Copia la URL generada (ej: `https://abc.trycloudflare.com`).

3. Registrar el webhook en Telegram:
```
https://api.telegram.org/bot<TELEGRAM_BOT_TOKEN>/setWebhook?url=https://abc.trycloudflare.com/webhook/telegram
https://api.telegram.org/bot8786304847:AAFTbIw5HZM_d3lT1Uq5qRoc-l4RJshDZO8/setWebhook?url=https://believes-processor-relevant-pubmed.trycloudflare.com/webhook/telegram

https://api.telegram.org/bot8786304847:AAFTbIw5HZM_d3lT1Uq5qRoc-l4RJshDZO8/setWebhook?url=https://drinks-defeat-quality-returned.trycloudflare.com/webhook/telegram

```
Debe responder `{"ok":true,"result":true}`.

4. Configurar las variables de entorno en `~/.bashrc`:
```bash
export TELEGRAM_BOT_TOKEN=tu_token
export TELEGRAM_CHAT_ID=tu_chat_id
export RABBITMQ_USER=guest
export RABBITMQ_PASSWORD=guest
source ~/.bashrc
```

5. Correr el servicio:
```bash
cd app-microservice-location-consumer
mvn spring-boot:run
```

El servicio queda disponible en `http://localhost:8002`

> Cada vez que reinicias cloudflared se genera una nueva URL — debes volver a registrar el webhook en Telegram con la nueva URL.

## Endpoints

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/webhook/telegram` | Recibe callbacks de Telegram | No |

Este endpoint es llamado automáticamente por Telegram cuando el moderador presiona un botón — no se llama manualmente.

## Flujo interno

```
Recibe LandscapeEvent de RabbitMQ
        ↓
Envía foto a Telegram (sendPhoto) ← puede fallar (Telegram caído, ngrok caído)
        ↓
Envía ubicación a Telegram (sendLocation)
        ↓
Envía botones ✅ Aprobar / ❌ Rechazar
        ↓
Moderador presiona un botón
        ↓
Telegram llama al webhook /webhook/telegram
        ↓
Consumer extrae landscapeId y decisión
        ↓
Publica LandscapeStatusEvent en RabbitMQ ← puede fallar (RabbitMQ caído)
        ↓
Producer recibe el evento y actualiza BD
```

## Colas RabbitMQ

| Cola | Dirección | Descripción |
|------|-----------|-------------|
| `landscape.pending.queue` | Producer → Consumer | Evento de nuevo paisaje para moderar |
| `landscape.status.queue` | Consumer → Producer | Resultado de la moderación |

## Cómo obtener el chat ID de Telegram

1. Abre Telegram y busca tu bot
2. Escribe `/start`
3. Abre en el navegador:
```
https://api.telegram.org/bot<TU_TOKEN>/getUpdates
```
4. Busca el campo `chat.id` en el resultado

## Notas

- El Consumer no tiene base de datos propia — delega la actualización al Producer vía RabbitMQ
- El Consumer no accede a MinIO — la URL de la imagen viene dentro del evento de RabbitMQ
- Las imágenes deben ser accesibles públicamente desde internet para que Telegram pueda mostrarlas