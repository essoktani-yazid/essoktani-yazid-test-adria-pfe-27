<div align="center">
  <!-- En-tête dynamique et graphique -->
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0D9488&height=160&section=header&text=ADRIA+BANK+PLATFORM&fontSize=48&fontAlignY=38&fontColor=ffffff" width="100%"/>

  <!-- Animation texte descriptive -->
  <img src="https://readme-typing-svg.herokuapp.com?font=JetBrains+Mono&weight=700&size=19&duration=3000&pause=1000&color=0D9488&center=true&vCenter=true&width=650&lines=Architecture+Microservices+Spring+Cloud;Assistant+Bancaire+Agentic+MCP+%26+IA;Streaming+%C3%89v%C3%A9nementiel+Kafka+%26+Resilience4J;Client+Web+Bento+Grid+(Clean+Light+UI)" alt="Typing SVG" />

  <br/>

  <!-- Badges technologiques -->
  <p>
    <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge" alt="Java 21">
    <img src="https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?style=for-the-badge" alt="Spring Boot">
    <img src="https://img.shields.io/badge/Spring_Cloud-2023.0.3-6DB33F?style=for-the-badge" alt="Spring Cloud">
    <img src="https://img.shields.io/badge/Apache_Kafka-Event--Driven-231F20?style=for-the-badge" alt="Kafka">
    <img src="https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge" alt="React">
    <img src="https://img.shields.io/badge/Tailwind_CSS-3.4-38B2AC?style=for-the-badge" alt="Tailwind">
    <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" alt="License">
  </p>
</div>

---

## À propos du projet

**ADRIA Bank Platform** est une solution bancaire full-stack hautement distribuée, conçue pour orchestrer la gestion des comptes bancaires, l'enregistrement temps réel des transactions financières, la diffusion d'événements métier asynchrones via Apache Kafka, et l'automatisation conversationnelle par un agent IA doté d'outils **MCP** (Model Context Protocol).

La plateforme applique scrupuleusement la séparation en couches requise (`domain/`, `repository/`, `service/`, `web/rest/`, `web/graphql/`, `mcp/`), connectant une API Gateway réactive, la découverte dynamique Eureka, un serveur de configuration Spring Cloud Config, et une interface web moderne en mode clair au design **Bento Grid**.

> [!NOTE]
> - Pour explorer les diagrammes de conception (Classes & Schéma d'Architecture global), consultez la [Documentation d'Architecture](docs/ARCHITECTURE.md).
> - Pour la spécification complète des points de terminaison REST, GraphQL et MCP, consultez la [Référence API](docs/API.md).
> - Pour le cahier de recette technique complet et l'analyse détaillée des 14 captures d'écran, consultez le [Cahier de Recette Technique](docs/SCREENSHOTS.md).

### Aperçu Visuel & Galerie des Preuves d'Exécution

<div align="center">
  <img src="docs/screenshots/11-frontend-dashboard-bento.png" alt="Tableau de Bord Bento Grid" width="88%" style="border-radius: 8px; border: 1px solid #E2E8F0;" />
  <p><em>Figure 1 : Interface Client Web — Bento Grid & Métriques Financières (Clean Light UI)</em></p>
</div>

<details>
  <summary><b>Afficher la galerie des captures d'écran clés (Cliquez pour dérouler)</b></summary>
  <br/>

  <table width="100%">
    <tr>
      <td width="50%" align="center">
        <b>01. Découverte Eureka Server</b><br/><br/>
        <img src="docs/screenshots/01-eureka-dashboard.png" alt="Eureka Dashboard" width="100%" style="border-radius: 6px;" />
      </td>
      <td width="50%" align="center">
        <b>14. Assistant IA & Tiroir MCP</b><br/><br/>
        <img src="docs/screenshots/14-frontend-ai-drawer.png" alt="Tiroir IA MCP" width="100%" style="border-radius: 6px;" />
      </td>
    </tr>
    <tr>
      <td width="50%" align="center">
        <b>12. Modale Nouvelle Transaction</b><br/><br/>
        <img src="docs/screenshots/12-frontend-modal-transaction.png" alt="Modale Transaction" width="100%" style="border-radius: 6px;" />
      </td>
      <td width="50%" align="center">
        <b>06. Validation Règles & Plafond</b><br/><br/>
        <img src="docs/screenshots/06-transaction-rest-rules.png" alt="Validation Métier" width="100%" style="border-radius: 6px;" />
      </td>
    </tr>
  </table>
  <p align="center"><em>Consultez le cahier complet <a href="docs/SCREENSHOTS.md">docs/SCREENSHOTS.md</a> pour l'analyse technique détaillée des 14 captures.</em></p>
</details>

---

## Pourquoi utiliser ce projet ?

- **Tolérance aux pannes & Résilience de pointe :** Communication inter-services sécurisée par **OpenFeign** et protégée par un Circuit Breaker **Resilience4J** avec bascule automatique sur Fallback en cas de défaillance réseau.
- **Règles métier strictes & Plafond dynamique :** Respect du plafond maximal par transaction (`bank.transaction.max-amount=1000`) distribué par Spring Cloud Config Server, validation des devises, vérification des comptes actifs et des soldes disponibles.
- **Événementiel temps réel avec Apache Kafka :** Publication asynchrone des événements `TransactionCreated` dans le topic `bank.transactions.created` indexés par `accountId`, consommés et journalisés par un service d'audit indépendant.
- **Tri-exposition unifiée (REST, GraphQL, MCP) :** Tous les contrôleurs REST, GraphQL et MCP appellent strictement la même couche service sans duplication.
- **Agent Bancaire Intelligent MCP :** Assistant IA conversationnel capable d'automatiser des consultations de solde, des virements et l'historique de compte via l'invocation standardisée d'outils MCP.
- **Design System Clean Light UI :** Interface web responsive, modales contextuelles avec flou givré, tiroir latéral pour l'IA et bouton flottant dynamique.

---

## Stack Technique

| Couche | Technologies |
| :--- | :--- |
| **Microservices & Backend** | Java 21, Spring Boot 3.3.4, Spring Cloud 2023.0.3, OpenFeign, Resilience4J |
| **API & Protocoles** | Spring Web REST, Spring GraphQL, Model Context Protocol (MCP) |
| **Découverte & Configuration**| Spring Cloud Netflix Eureka Server (8761), Spring Cloud Config Server (8888), Spring Cloud Gateway (8080) |
| **Messagerie & Persistance** | Apache Kafka (Topic: `bank.transactions.created`), Spring Data JPA, H2 In-Memory DB |
| **Frontend / Web Client** | React 18, Vite, Tailwind CSS (Clean Light UI), Lucide Icons |
| **Intelligence Artificielle** | Agentic Banking Engine, MCP Tools Executor, Telegram & Discord Webhooks |

---

## Pour commencer

### Prérequis
- **Java JDK 21** (v21.0+)
- **Apache Maven** (v3.9+)
- **Node.js** (v18+) et **npm** (v10+)
- **Docker & Docker Compose** (pour l'infrastructure optionnelle Kafka/Zookeeper)

### Compilation du Projet Multi-Module

Depuis la racine du projet :

```bash
# Compilation complète et vérification de tous les modules
mvn clean compile
```

### Démarrage des Services Backend

Pour démarrer l'ensemble des composants dans l'ordre recommandé :

```bash
# 1. Registre de découverte Eureka (Port 8761)
mvn -pl discovery-service spring-boot:run

# 2. Serveur de Configuration Centralisée (Port 8888)
mvn -pl config-service spring-boot:run

# 3. Microservice des Comptes Bancaires (Port 8081)
mvn -pl account-service spring-boot:run

# 4. Microservice des Transactions & Kafka Producer (Port 8082)
mvn -pl transaction-service spring-boot:run

# 5. Service d'Audit Kafka (Port 8083)
mvn -pl notification-audit-service spring-boot:run

# 6. Assistant IA & Serveur Chatbot Agentic (Port 8084)
mvn -pl agentic-chatbot-service spring-boot:run

# 7. Passerelle Spring Cloud Gateway (Port 8080)
mvn -pl gateway-service spring-boot:run
```

### Démarrage du Client Web (Frontend)

```bash
cd frontend-client
npm install
npm run dev
```
L'interface utilisateur est instantanément accessible sur `http://localhost:5173`.

---

## Exemples d'Utilisation Concrète (API cURL)

### 1. Consulter les comptes via Gateway
```bash
curl -X GET http://localhost:8080/api/accounts
```

### 2. Enregistrer une transaction (Vérification plafond, devise et solde)
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "a1000000-0000-0000-0000-000000000001",
    "amount": 150.00,
    "currency": "MAD",
    "merchantCategory": "TECH_EQUIPMENT",
    "country": "MA"
  }'
```

### 3. Effectuer un virement automatisé de compte à compte
```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "sourceAccountId": "a1000000-0000-0000-0000-000000000001",
    "targetAccountId": "a1000000-0000-0000-0000-000000000002",
    "amount": 250.00,
    "currency": "MAD",
    "description": "Virement interne"
  }'
```

### 4. Interagir en langage naturel avec l'Assistant IA
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Affiche la liste de tous mes comptes"}'
```

---

## Structure du Projet

```text
.
├── pom.xml                         # Racine Maven Parent Multi-Module
├── discovery-service/              # Eureka Service Discovery (Port 8761)
├── config-service/                 # Spring Cloud Config Server (Port 8888)
├── gateway-service/                # Spring Cloud API Gateway (Port 8080)
├── account-service/                # Microservice Comptes (Port 8081) - REST/GraphQL/MCP
├── transaction-service/            # Microservice Transactions (Port 8082) - REST/GraphQL/MCP
├── notification-audit-service/     # Consommateur d'Audit Kafka (Port 8083)
├── agentic-chatbot-service/        # Agent Conversationnel Bancaire MCP (Port 8084)
├── frontend-client/                # Client Web React + Tailwind CSS (Clean Light UI)
├── docs/                           # Documentation Technique
│   ├── ARCHITECTURE.md             # Diagramme de Classes & Architecture Mermaid
│   └── API.md                      # Spécification détaillée des endpoints
├── LICENSE                         # Licence MIT
└── README.md                       # Présentation du projet
```

---

## Auteur

<div align="center">

**Yazid ESSOKTANI**  
*Full-Stack Web & AI Developer*

[![Portfolio](https://img.shields.io/badge/Portfolio-essoktani.tech-0D9488?style=for-the-badge&logo=google-chrome&logoColor=white)](https://www.essoktani.tech/)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Yazid_ESSOKTANI-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/yazid-essoktani-337549251)
[![GitHub](https://img.shields.io/badge/GitHub-@essoktaniyazid-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/essoktaniyazid)

</div>

---

## Licence

Distribué sous la licence **MIT**. Consultez le fichier [`LICENSE`](LICENSE) pour plus d'informations.

