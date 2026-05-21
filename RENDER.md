Render backend deployment

Service type
- Web Service

Environment
- Docker

Render settings
- Dockerfile path: `Dockerfile`
- Root directory: backend repo root
- Health check path: `/api/auth/health`

Required environment variables
- `SPRING_DATASOURCE_URL` = MySQL JDBC URL for Render or your external MySQL host
- `SPRING_DATASOURCE_USERNAME` = MySQL username
- `SPRING_DATASOURCE_PASSWORD` = MySQL password
- `JWT_SECRET` = Base64-encoded secret for JWT signing
- `JWT_EXPIRATION` = token TTL in milliseconds, optional
- `APP_CORS_ALLOWED_ORIGINS` = comma-separated Vercel frontend URL(s)
- `APP_UPLOAD_DIR` = optional upload directory, e.g. `/tmp/ResolveIT/uploads/complaint-proofs`

Build and runtime notes
- Render will inject the `PORT` variable automatically.
- `application.properties` already uses `server.port=${PORT:8008}` so the app listens on Render's port.
- The health endpoint is available at `/api/auth/health` and is already public in Spring Security.

Frontend connection
- Set the Vercel environment variable `REACT_APP_API_URL` to the Render service URL, for example `https://your-service.onrender.com`.
- Ensure `APP_CORS_ALLOWED_ORIGINS` includes the exact Vercel domain, for example `https://your-app.vercel.app`.

Common mistakes to avoid
- Hardcoding `server.port=8008` on Render.
- Leaving MySQL URL pointed at `localhost`.
- Forgetting to add the Vercel origin to CORS.
- Using the wrong Docker build context when the repo is a monorepo.
- Storing JWT or database secrets in the repository instead of Render environment variables.
- Expecting file uploads to persist permanently on the free tier; Render's filesystem is ephemeral.
