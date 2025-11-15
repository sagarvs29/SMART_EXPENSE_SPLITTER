# Smart Expense - Frontend

This is a small React (Vite) frontend for the Smart Expense backend.

## Dev (separate Vite server)
- Open PowerShell
- Change directory:
  cd "c:\Users\hp\Music\Desktop\projects\java project1\java-cloud-project\frontend"
- Install dependencies:
  npm install
- Start dev server:
  npm run dev

The frontend expects the backend API at http://localhost:8080/api (configured in `src/api/api.js`).

## Production build (served by Spring Boot)
The project includes a Vite config that outputs directly into the backend's `src/main/resources/public` folder. This replaces the old static page with the React app.

Steps:
1) From this `frontend` folder, build the app
   npm run build
2) Start the Spring Boot backend (in the project root)
   mvn -DskipTests spring-boot:run
3) Open the app at
   http://localhost:8080/

Notes:
- Uses React Router v6 and Axios.
- Minimal styles in `src/styles.css`.
- If you see the old static page, run `npm run build` again to overwrite it, then refresh your browser cache (Ctrl+F5).
