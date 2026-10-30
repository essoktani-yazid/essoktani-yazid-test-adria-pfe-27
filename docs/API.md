# Référence API — ADRIA Bank Platform

> Documentation exhaustive des points d'accès exposés par la passerelle API Gateway et les microservices métier (REST, GraphQL et MCP).

- **URL de Base (API Gateway) :** `http://localhost:8080`
- **Format d'échange :** `application/json`
- **Registre Eureka :** `http://localhost:8761`
- **Serveur de Configuration :** `http://localhost:8888`

---

## 1. Endpoints REST — Account-Service

Préfixe via Gateway : `/api/accounts` (Service direct : `http://localhost:8081/api/accounts`)

### 1.1 Créer un Compte Bancaire
- **URL :** `/api/accounts`
- **Méthode :** `POST`
- **Corps de la requête :**
```json
{
  "customerReference": "Yazid ESSOKTANI",
  "currency": "MAD",
  "balance": 50000.0,
  "status": "ACTIVE"
}
```
- **Réponse (`201 Created`) :**
```json
{
  "id": "e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
  "customerReference": "Yazid ESSOKTANI",
  "status": "ACTIVE",
  "currency": "MAD",
  "balance": 50000.0,
  "createdAt": "2026-10-30T09:30:00Z"
}
```

### 1.2 Consulter un Compte par ID
- **URL :** `/api/accounts/{id}`
- **Méthode :** `GET`
- **Réponse (`200 OK`) :**
```json
{
  "id": "e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
  "customerReference": "Yazid ESSOKTANI",
  "status": "ACTIVE",
  "currency": "MAD",
  "balance": 50000.0,
  "createdAt": "2026-10-30T09:30:00Z"
}
```

### 1.3 Lister Tous les Comptes Bancaires
- **URL :** `/api/accounts`
- **Méthode :** `GET`
- **Réponse (`200 OK`) :**
```json
[
  {
    "id": "e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
    "customerReference": "Yazid ESSOKTANI",
    "status": "ACTIVE",
    "currency": "MAD",
    "balance": 50000.0,
    "createdAt": "2026-10-30T09:30:00Z"
  }
]
```

### 1.4 Mettre à jour le Solde (Débit / Crédit)
- **URL :** `/api/accounts/{id}/balance`
- **Méthode :** `PATCH`
- **Corps de la requête :**
```json
{
  "amount": 250.0,
  "operationType": "DEBIT"
}
```
- **Réponse (`200 OK`) :**
```json
{
  "id": "e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
  "customerReference": "Yazid ESSOKTANI",
  "status": "ACTIVE",
  "currency": "MAD",
  "balance": 49750.0,
  "createdAt": "2026-10-30T09:30:00Z"
}
```

---

## 2. Endpoints REST — Transaction-Service

Préfixe via Gateway : `/api/transactions` (Service direct : `http://localhost:8082/api/transactions`)

### 2.1 Enregistrer une Transaction
- **URL :** `/api/transactions`
- **Méthode :** `POST`
- **Corps de la requête :**
```json
{
  "accountId": "e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
  "amount": 250.00,
  "currency": "MAD",
  "merchantCategory": "TECH_EQUIPMENT",
  "country": "MA"
}
```
- **Règles validées :**
  - Montant > 0 et <= `1000` (Plafond `bank.transaction.max-amount`).
  - Compte existe et est `ACTIVE`.
  - Devise correspond à celle du compte.
  - Solde suffisant pour le débit.
  - Publication Kafka automatique dans `bank.transactions.created` avec `accountId` comme clé.
- **Réponse (`201 Created`) :**
```json
{
  "id": "7f8b9c10-2345-4a67-8901-bcdef1234567",
  "accountId": "e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
  "amount": 250.00,
  "currency": "MAD",
  "merchantCategory": "TECH_EQUIPMENT",
  "country": "MA",
  "createdAt": "2026-10-30T10:15:00Z"
}
```

### 2.2 Effectuer un Virement de Compte à Compte
- **URL :** `/api/transactions/transfer`
- **Méthode :** `POST`
- **Corps de la requête :**
```json
{
  "sourceAccountId": "e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
  "targetAccountId": "f5c3e4e1-4b26-5da6-0122-9fa00d26e593",
  "amount": 500.00,
  "currency": "MAD",
  "description": "Virement interne partenaires"
}
```
- **Réponse (`200 OK`) :**
```json
{
  "transferId": "9b1c2d3e-4f5a-6b7c-8d9e-0f1a2b3c4d5e",
  "sourceAccountId": "e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
  "targetAccountId": "f5c3e4e1-4b26-5da6-0122-9fa00d26e593",
  "amount": 500.00,
  "currency": "MAD",
  "status": "SUCCESS",
  "debitTransactionId": "11111111-2222-3333-4444-555555555555",
  "creditTransactionId": "66666666-7777-8888-9999-000000000000",
  "executedAt": "2026-10-30T10:20:00Z",
  "message": "Transfer completed successfully"
}
```

---

## 3. Endpoints REST — Audit & Agentic Chatbot

### 3.1 Consulter les Événements Kafka Reçus (Audit Service)
- **URL :** `/api/audit/logs`
- **Méthode :** `GET`
- **Réponse (`200 OK`) :**
```json
[
  {
    "logId": 0,
    "partition": 0,
    "key": "e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
    "event": {
      "eventId": "a2b3c4d5-e6f7-8a9b-0c1d-2e3f4a5b6c7d",
      "eventType": "TransactionCreated",
      "transactionId": "7f8b9c10-2345-4a67-8901-bcdef1234567",
      "accountId": "e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
      "amount": 250.00,
      "currency": "MAD",
      "occurredAt": "2026-10-30T10:15:00Z"
    },
    "receivedAt": "2026-10-30T10:15:00.123Z"
  }
]
```

### 3.2 Interagir avec l'Assistant Bancaire IA (Agentic Chatbot)
- **URL :** `/api/chat`
- **Méthode :** `POST`
- **Corps de la requête :**
```json
{
  "message": "Quel est le solde du compte e4b2d3d0-3a15-4cf5-9011-8e999c15d482 ?",
  "sessionId": "session-user-1"
}
```
- **Réponse (`200 OK`) :**
```json
{
  "reply": "Détails du compte :\n• Titulaire : Yazid ESSOKTANI\n• Solde : 49 750,00 MAD\n• Statut : ACTIVE\n• Identifiant : e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
  "toolsInvoked": [
    "account-service:get_account_details"
  ],
  "timestamp": "2026-10-30T10:25:00Z"
}
```

---

## 4. API GraphQL

Accessible via `http://localhost:8081/graphql` et `http://localhost:8082/graphql` (GraphiQL activé pour exploration interactive).

### Exemple de Requête Query (Account Service)
```graphql
query {
  allAccounts {
    id
    customerReference
    currency
    balance
    status
  }
}
```

### Exemple de Mutation (Transaction Service)
```graphql
mutation {
  transferFunds(input: {
    sourceAccountId: "e4b2d3d0-3a15-4cf5-9011-8e999c15d482",
    targetAccountId: "f5c3e4e1-4b26-5da6-0122-9fa00d26e593",
    amount: 150.0,
    currency: "MAD",
    description: "Virement test GraphQL"
  }) {
    transferId
    status
    message
  }
}
```

---

## 5. Spécification du Protocole MCP (Model Context Protocol)

Les deux microservices métier implémentent les endpoints standards MCP :
- `GET /mcp/tools` : Découverte du catalogue d'outils disponibles et schéma JSON des entrées.
- `POST /mcp/tools/call` : Invocation normalisée d'un outil avec payload `{ name, arguments }`.

### Format d'Appel MCP
```json
{
  "name": "get_account_details",
  "arguments": {
    "accountId": "e4b2d3d0-3a15-4cf5-9011-8e999c15d482"
  }
}
```

### Format de Réponse MCP
```json
{
  "content": [
    {
      "type": "text",
      "text": "{\"id\":\"e4b2d3d0-3a15-4cf5-9011-8e999c15d482\",\"customerReference\":\"Yazid ESSOKTANI\",\"status\":\"ACTIVE\",\"currency\":\"MAD\",\"balance\":49750.0}"
    }
  ],
  "isError": false
}
```

---

## 6. Codes de Statut et Gestion des Erreurs

| Code HTTP | Libellé | Cause Fréquente |
| :--- | :--- | :--- |
| `200 OK` | Succès | Consultation ou action validée. |
| `201 Created` | Ressource créée | Compte ou transaction persistant avec succès. |
| `400 Bad Request` | Validation métier | Dépassement de plafond (`> 1000`), devise discordante, solde insuffisant. |
| `404 Not Found` | Introuvable | Compte ou transaction inexistant. |
| `503 Service Unavailable`| Tolérance aux pannes | Circuit breaker Resilience4J ouvert ou microservice aval non joignable. |

---
[Retour au README principal](../README.md)

