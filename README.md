# Employee Leave and Payroll API

Spring Boot 3.4 / Java 21 API backed by Aiven MySQL. Authentication uses short-lived signed JWT bearer tokens, BCrypt password hashes, and server-side role checks.

## Required environment variables

Set these in the **same PowerShell terminal** that starts Maven. Obtain database values from Aiven Console. Never commit real secrets.

```powershell
$env:DB_HOST="YOUR_AIVEN_HOST"
$env:DB_PORT="YOUR_AIVEN_PORT"
$env:DB_NAME="employee_payroll"
$env:DB_USERNAME="YOUR_AIVEN_USERNAME"
$env:DB_PASSWORD="YOUR_AIVEN_PASSWORD"
$env:JWT_SECRET="REPLACE_WITH_A_RANDOM_SECRET_AT_LEAST_32_CHARACTERS_LONG"
$env:BOOTSTRAP_ADMIN_EMAIL="admin@yourcompany.com"
$env:BOOTSTRAP_ADMIN_PASSWORD="Use_A_Unique_Password_At_Least_12_Chars"
$env:FRONTEND_ORIGIN="http://localhost:5173"
```

Use a long, unique `JWT_SECRET`; do not reuse the admin password as the JWT secret. The first startup creates the bootstrap admin only when the `app_users` table has no accounts. Store the bootstrap password safely. If an account already exists, changing the bootstrap variables does not reset its password.

macOS/Linux: use `export NAME="value"` for each variable instead of `$env:NAME="value"`.

## Start

```bash
mvn spring-boot:run
```

Hibernate creates/updates development tables (`employees`, `app_users`, `revoked_tokens`) using `ddl-auto=update`. For production, use reviewed migrations and backups.

## API and permissions

| Endpoint | Permission | Purpose |
|---|---|---|
| `POST /api/auth/login` | Public | Verify email/password and issue token |
| `GET /api/auth/me` | Authenticated | Return current account role and IDs |
| `POST /api/auth/logout` | Authenticated | Revoke the current token server-side |
| `GET /api/employees` | `ADMIN` | List employees |
| `GET /api/employees/{id}` | `ADMIN` | Read employee record |
| `POST /api/employees` | `ADMIN` | Create employee and linked login account in one transaction |
| `PUT /api/employees/{id}` | `ADMIN` | Update employee record |
| `DELETE /api/employees/{id}` | `ADMIN` | Soft-deactivate employee and disable account |
| `GET /api/me` | `EMPLOYEE` | Return only the signed-in employee's profile |

Protected calls require `Authorization: Bearer <token>`. The API checks the account's active state and current database role on each request. Employee accounts are created only through the administrator-protected employee creation endpoint; there is no public registration endpoint.

## Security notes

- Passwords are stored as BCrypt hashes and never returned by the API.
- JWT lifetime is 30 minutes by default (`security.jwt.lifetime-minutes`).
- Logout records the JWT ID in `revoked_tokens` until expiry.
- The frontend stores the token in `sessionStorage`, which is appropriate for this learning project but is accessible to JavaScript. For a public production deployment, consider a secure `HttpOnly`, `Secure`, `SameSite` cookie design, rate limiting, audit logging, account recovery, and database migrations.
- The frontend must never contain Aiven credentials, JWT signing secrets, or the bootstrap admin password.
