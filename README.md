# 💸 Smart Expense Splitter

A simple app to add friends, record shared expenses, split them fairly, and see who owes whom.

- Backend: Java + Spring Boot (REST) over JDBC (PostgreSQL)
- Frontend: React (Vite) + Axios + React Router

## What it does

- Add friends (people you share expenses with)
- Add an expense with payer and participants — equal split by default
- See running balances per person (net owed / net to receive)
- View suggested settlements to square up
- Reset all data for a clean test slate

## Setup

You’ll need Java 17+, Maven, and Node.js 18+.

Backend (API at http://localhost:8080/api):

```powershell
# Optional: set JAVA_HOME for this session
$env:JAVA_HOME="E:\path\to\jdk"; $env:Path="$env:JAVA_HOME\bin;$env:Path"

# From the project root
mvn -DskipTests spring-boot:run
```

Frontend (dev server at http://localhost:5173):

```powershell
cd frontend
npm install
npm run dev
```

Production build (served by Spring Boot at http://localhost:8080):

```powershell
cd frontend
npm install
npm run build   # outputs to ../src/main/resources/public
cd ..
mvn -DskipTests spring-boot:run
```

Notes
- The frontend calls the backend at http://localhost:8080/api (see `frontend/src/api/api.js`).
- If port 8080 is busy, free it first and rerun the backend.

## How to use

1) Open the app (dev: http://localhost:5173, prod: http://localhost:8080)
2) Go to Friends and add people you share expenses with
3) Go to Add Expense, pick a payer, select participants, enter amount and description, Save
4) Check View Expenses and Balances to see updates
5) See Settlements for who should pay whom; use Reset (on Friends) to clear all data

The app supports equal splits today and uses numeric user IDs for payer/participants.

## Customizing

- API base URL: update `frontend/src/api/api.js` if your backend host/port changes
- Database connection: see `src/main/java/com/app/DatabaseConnector.java`
  - Recommended: externalize DB secrets via environment variables rather than hardcoding
- Data reset: POST `/api/admin/reset` or click Reset on the Friends page

## Tech stuff

- Spring Boot 3 (REST), JDBC repositories
- PostgreSQL (works great with Supabase)
- React 18, Vite, React Router, Axios
- Simple equal-split algorithm with balances and settlement suggestions

## Note

This is a demo/learning app. Double-check balances before sending real money and adapt to your needs.
