# APKURL Database Design and ERD (Report Deliverable)

This document implements the database design for the FYP report. The **target** relational model matches a future **Room** implementation. The **current** app stores scan history in **Preferences DataStore + JSON** (`HistoryRepository`, `ScanHistoryRecord`); you may add one sentence in Chapter 3 or 4 stating that DataStore is the implemented prototype and Room is the normalised target schema.

---

## Figure X — Entity Relationship Diagram (primary: single-device, no login)

The primary ERD **omits `Users`**, which matches APKURL as an offline, single-phone scanner without accounts. An optional extension with `Users` appears at the end of this document.

### Mermaid source (paste into [mermaid.live](https://mermaid.live) to export PNG/SVG for Word)

```mermaid
erDiagram
    ScanHistory ||--|| ScanResult : "one result"
    ScanHistory ||--o| URLScanDetails : "if scanType URL"
    ScanHistory ||--o| APKScanDetails : "if scanType APK"

    ScanHistory {
        long id PK
        string scanType "URL or APK"
        string targetDisplay
        string title
        string subtitle
        string riskLevel
        int riskScore
        bool isHighRisk
        long createdAtMs
        string source "MANUAL or INTENT optional"
    }

    ScanResult {
        long id PK
        long scanId FK_UK "UNIQUE"
        string summary
        string reason
    }

    URLScanDetails {
        long id PK
        long scanId FK_UK "UNIQUE"
        string url
        string ruleFlags "TEXT or JSON"
        int ruleHintScore "optional"
    }

    APKScanDetails {
        long id PK
        long scanId FK_UK "UNIQUE"
        string fileName
        string sha256Hash
        string hashShort "optional display"
        long fileSizeBytes "optional"
        string analysisNote "e.g. VT or heuristic ref"
    }
```

### Crow’s-foot style (text layout for redraw in draw.io / Visio)

```
                    +------------------+
                    |   ScanHistory    |
                    |------------------|
                    | id (PK)          |
                    | scanType         |
                    | targetDisplay    |
                    | title, subtitle  |
                    | riskLevel        |
                    | riskScore        |
                    | isHighRisk       |
                    | createdAtMs      |
                    | source           |
                    +--------+---------+
                             |
              +--------------+--------------+
              | 1                            | 1
              v                              v
    +------------------+            +------------------+
    |   ScanResult     |            | subtype (XOR)    |
    |------------------|            |                  |
    | id (PK)          |            | URLScanDetails   |
    | scanId (FK,UK)   |            | OR               |
    | summary          |            | APKScanDetails   |
    | reason           |            | (one per scan)   |
    +------------------+            +------------------+

ScanHistory (1) ---- (0..1) URLScanDetails   [when scanType = URL]
ScanHistory (1) ---- (0..1) APKScanDetails   [when scanType = APK]
```

---

## Relationships (summary)

| From | To | Cardinality | Notes |
|------|-----|-------------|--------|
| ScanHistory | ScanResult | 1 : 1 | `ScanResult.scanId` UNIQUE → `ScanHistory.id` |
| ScanHistory | URLScanDetails | 1 : 0..1 | Only when `scanType = URL` |
| ScanHistory | APKScanDetails | 1 : 0..1 | Only when `scanType = APK` |

**Room:** Use `FOREIGN KEY(scanId) REFERENCES ScanHistory(id) ON DELETE CASCADE` and **UNIQUE** on `scanId` in child tables.

---

## Entity attribute tables

| Entity | PK | FK | Attributes (non-key) |
|--------|----|----|----------------------|
| **ScanHistory** | `id` | — | `scanType` (URL/APK), `targetDisplay`, `title`, `subtitle`, `riskLevel`, `riskScore`, `isHighRisk`, `createdAtMs`, `source` (manual vs intent) |
| **ScanResult** | `id` | `scanId` → `ScanHistory.id` (UNIQUE) | `summary`, `reason` |
| **URLScanDetails** | `id` | `scanId` → `ScanHistory.id` (UNIQUE) | `url`, `ruleFlags` (TEXT/JSON), optional `ruleHintScore` (rule-based URL hints) |
| **APKScanDetails** | `id` | `scanId` → `ScanHistory.id` (UNIQUE) | `fileName`, `sha256Hash`, optional `hashShort`, optional `fileSizeBytes`, optional `analysisNote` (e.g. cloud multi-engine summary, heuristic fallback note—align with your methodology chapter) |

**Primary keys:** Surrogate integer/long `id` on each table (auto-generated).  
**Foreign keys:** `ScanResult`, `URLScanDetails`, and `APKScanDetails` each reference `ScanHistory.id`; enforce URL vs APK rows in application or DAO logic (Room cannot enforce XOR subtypes).

---

## Paragraph for the report (Figure X discussion)

The Entity Relationship Diagram (ERD) shows how data is organised for the APKURL security scanning application. The central entity is **ScanHistory**, which records each scan event: what was scanned (a web address or an APK package), when it occurred, and the overall risk label and numeric score. Each history record has exactly one **ScanResult**, which stores the explanatory summary and reason text presented on the result screen. Because URL scans and APK scans require different technical attributes, the design uses two specialised tables. **URLScanDetails** stores the full URL and rule-based indicators (rule flags or hint scores) used for link analysis. **APKScanDetails** stores file-oriented data such as the display file name and SHA-256 hash, plus optional notes that reference cloud or heuristic analysis outcomes described elsewhere in the system design. Together, these tables separate scan history, user-facing outcomes, and type-specific analysis data in a normalised form suitable for implementation with Room on Android.

---

## Optional extension — Users entity (multi-user / future sync)

If your report requires a **Users** table, add:

```mermaid
erDiagram
    Users ||--o{ ScanHistory : "owns"
    ScanHistory ||--|| ScanResult : "one result"
    ScanHistory ||--o| URLScanDetails : "if URL"
    ScanHistory ||--o| APKScanDetails : "if APK"

    Users {
        long userId PK
        string deviceLabel
        long createdAtMs
    }

    ScanHistory {
        long id PK
        long userId FK
        string scanType
        string targetDisplay
        string title
        string subtitle
        string riskLevel
        int riskScore
        bool isHighRisk
        long createdAtMs
        string source
    }

    ScanResult {
        long id PK
        long scanId FK_UK
        string summary
        string reason
    }

    URLScanDetails {
        long id PK
        long scanId FK_UK
        string url
        string ruleFlags
        int ruleHintScore
    }

    APKScanDetails {
        long id PK
        long scanId FK_UK
        string fileName
        string sha256Hash
        string hashShort
        long fileSizeBytes
        string analysisNote
    }
```

Add **`userId` (FK → Users.userId)** to **ScanHistory** and adjust the crow’s-foot diagram accordingly.

---

## Alignment with analysis methodology (Chapter 3)

- **URL analysis:** Document **URLScanDetails.ruleFlags** / **ruleHintScore** as supporting **rule-based** URL heuristics in your methodology.
- **APK analysis:** Document **APKScanDetails.analysisNote** as holding references to **cloud multi-engine results (e.g. VirusTotal)** and/or **local heuristic fallback**, consistent with the implemented pipeline—avoid claiming an on-device ML model in the schema unless your thesis defines one.

---

## Deliverable checklist (Word / PDF)

1. **Figure caption:** *Figure X: Entity Relationship Diagram for the APKURL scan data model.*
2. **Figure:** Export PNG/SVG from Mermaid (primary diagram without Users) or redraw with crow’s feet to match your faculty template.
3. **Tables:** Copy the entity attribute tables from this document.
4. **Paragraph:** Copy or shorten the “Paragraph for the report” section.
5. **Implementation note (one sentence):** e.g. *The current prototype persists denormalised history in DataStore; the ERD describes the normalised Room schema targeted for a future release.*

---

## File reference in project

| Current implementation | File |
|------------------------|------|
| JSON history records | [`app/src/main/java/com/example/finalapkurl/data/local/ScanHistoryRecord.kt`](../app/src/main/java/com/example/finalapkurl/data/local/ScanHistoryRecord.kt) |
| Persistence | [`app/src/main/java/com/example/finalapkurl/data/local/HistoryRepository.kt`](../app/src/main/java/com/example/finalapkurl/data/local/HistoryRepository.kt) |

Field names in code (`riskLabel`, etc.) map conceptually to `riskLevel` / result columns in this ERD; mention mapping in the report if supervisors compare code to the diagram.
