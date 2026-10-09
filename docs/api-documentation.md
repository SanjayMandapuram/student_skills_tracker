# API documentation

Public: `POST /api/auth/register` with `{name,email,phone,password,confirmPassword}`; `POST /api/auth/login` with `{email,password}`.

Authenticated Bearer JWT (`Authorization: Bearer <token>`): `GET /api/users/me`, `GET /api/dashboard`; CRUD routes are `/api/skills`, `/api/courses`, `/api/certifications`, with list routes `/user/{userId}`. Students can only modify their own records.

Admin Bearer JWT: `GET /api/admin/users?q=`, `GET /api/admin/certifications`, `PUT /api/admin/certifications/{id}/verify`, `PUT /api/admin/certifications/{id}/reject` with `{remarks}`, `GET /api/admin/verification-history`, and `GET /api/admin/reports`.
