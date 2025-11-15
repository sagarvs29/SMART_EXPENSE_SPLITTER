# SMART_EXPENSE_SPLITTER

A full-stack expense splitting app.

- Backend: Java, Spring Boot (REST) over JDBC, PostgreSQL
- Frontend: React (Vite), Axios, React Router


## Quick start (Windows PowerShell)

Prerequisites
- Java JDK 17+ (project compiles and runs on Java 17 or newer)
- Maven 3.8+
- Node.js 18+ and npm 9+

Backend (API at http://localhost:8080/api)
1) Ensure JDK is on PATH (or set JAVA_HOME before running Maven)
2) From project root, start Spring Boot:
	 - It will bind to port 8080

Frontend (development server at http://localhost:5173)
1) Open a second terminal
2) Install and run Vite dev server from `frontend/`
	 - The frontend calls the backend at http://localhost:8080/api (see `frontend/src/api/api.js`)

Production build (served by Spring Boot)
1) Build the React app (outputs into `src/main/resources/public/`)
2) Start Spring Boot and open http://localhost:8080/


## Project structure

```
pom.xml
README.md
frontend/
	index.html
	package.json
	src/
		App.jsx, main.jsx, styles.css
		api/api.js
		pages/ (Friends, AddExpense, ViewExpenses, Balances, Settlements, Home)
		ui/Navbar.jsx
src/
	main/
		java/com/app/
			controller/ExpenseController.java  # REST endpoints
			dto/ (ExpenseRequest, FriendRequest)
			... repositories, models, services ...
		resources/public/                   # Served by Spring Boot; Vite build outputs here
```


## Configure the database

The app uses PostgreSQL (e.g., Supabase). Connection details are currently defined in `src/main/java/com/app/DatabaseConnector.java`.

Recommended: externalize secrets. A simple approach is to read environment variables in `DatabaseConnector` (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASS). For now, update that file with your own connection details or refactor it to use env vars.

Common env var names you can use if you refactor:
- DB_HOST
- DB_PORT (default 5432)
- DB_NAME
- DB_USER
- DB_PASS


## Run the backend (Windows PowerShell)

Notes
- The server listens on port 8080. Free the port before starting.
- First run will create tables if your SQL scripts did so (or ensure the schema exists).

Typical steps
1) Optional: set JAVA_HOME to your JDK and add it to PATH for this session
2) From the project root, start Spring Boot with Maven
3) Verify the API at http://localhost:8080/api/friends

Troubleshooting: Port 8080 already in use
- Find the PID listening on 8080, then terminate it and retry.


## Run the frontend (development)

From `frontend/`:
1) Install dependencies once
2) Start the dev server (Vite)
3) Open http://localhost:5173

Configuration
- API base URL is set in `frontend/src/api/api.js` as `http://localhost:8080/api`.
- Adjust if you change the backend port or host.


## Build frontend for production and serve via Spring Boot

From `frontend/`:
1) Build the React app (writes to `../src/main/resources/public/`)
2) Start the backend
3) Open http://localhost:8080 (the React SPA is served by Spring Boot)


## API reference (summary)

Base URL: `http://localhost:8080/api`

Friends
- GET `/friends` → List<User>
- POST `/friends` → Create a friend
	- Body: { "name": string, "email": string, "phone": string }
- DELETE `/friends/{id}` → Delete by numeric userId

Expenses
- GET `/expenses` → List<Expense>
- POST `/expenses` → Create an expense and (optionally) split equally among participants
	- Body example:
		{
			"amount": 1200.0,
			"description": "Dinner",
			"payerId": 1,
			"date": "2025-11-15",
			"participantIds": [1, 2, 3],
			"splitType": "equal"
		}
	- Response: { "expenseId": number }

Balances
- GET `/balances` → Map<userId, netAmount>

Settlements
- GET `/settlements` → List<Settlement>

Admin
- POST `/admin/reset` → Clears users, expenses, splits, settlements (for a clean slate)


## Frontend features (SPA)

- Friends: list, add, delete
- Expenses: add (equal split), list
- Balances: see net owed/owed-to per user
- Settlements: view computed settlements
- Admin: reset all data from Friends page (Reset button)


## Common issues & fixes

Port 8080 in use
- Stop the process using 8080, then start the backend again.

400 Bad Request on POST /expenses
- Ensure the payload matches the example above:
	- `payerId` must be a number
	- `date` should be ISO format `YYYY-MM-DD`
	- Include `participantIds` for equal splits

Database connection failures
- Verify host/port/db/user/password
- Ensure network access and SSL settings as required by your provider (e.g., Supabase)

CORS
- The backend enables `@CrossOrigin("*")` on the controller; dev server should call it without extra config.


## Scripts and tooling

Backend
- Build: `mvn -DskipTests package`
- Run: `mvn -DskipTests spring-boot:run`

Frontend (from `frontend/`)
- Dev: `npm run dev`
- Build: `npm run build`
- Preview: `npm run preview`


## Next steps (optional enhancements)

- Externalize DB secrets via environment variables (remove any hardcoded credentials)
- Advanced split types (percentages, shares)
- Group management (create/list/join groups)
- Authentication and per-user sessions
- Tests (unit/integration, minimal e2e)


---

Made with Java + Spring Boot + React.

