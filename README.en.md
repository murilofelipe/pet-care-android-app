[Português](README.md) · **English**

# 🐾 PetCare — Pet management for houses and apartments

**PetCare** is an Android app (Kotlin + Jetpack Compose) for pet owners, a companion to
**Sition Web** (a rural-management SaaS). The app has **no backend of its own**: it consumes the
same REST API as Sition Web, reusing the `Animal` entity with a direct link to the user — no
rural property involved.

## 🚀 Features (Phase 1 — Core)

- **Authentication**: login with e-mail/username + password (Sition Web JWT). Account sign-up
  stays in Sition Web (it requires an activation code).
- **Pets**: create, edit, list and delete (name, species, breed, sex, birth date, weight,
  photo, notes).
- **Vaccination**: type, application date, next dose, batch, vet/location, cost.
- **Medication**: name, dosage, frequency, treatment start/end.
- **Supplies/Food**: items with category, brand, quantity and expected restock date (visual
  alert when close).
- **Finance**: expenses by category with the month's total and a recurrence flag.
- **Vet access**: the owner grants access by e-mail (`VetAnimalAccess`); the vet sees shared
  pets in the "As vet" tab and records vaccines/medications.
- **Offline cache**: Room mirrors the API data; the last synced state stays available
  without network.

## 🛠️ Stack

Kotlin 2.3 · Jetpack Compose (Material 3) · MVVM (ViewModel + StateFlow) ·
Hilt · Retrofit + OkHttp + kotlinx.serialization · Room · DataStore · Coil.
Build: Gradle 9.4.1 + AGP 8.13.2 + KSP.

Detailed docs: [`docs/architecture/current.md`](docs/architecture/current.md) ·
[`docs/adr/001-pet-sem-propriedade.md`](docs/adr/001-pet-sem-propriedade.md) ·
[`docs/sition-web-changes.md`](docs/sition-web-changes.md) (backend changes) ·
[`docs/backlog.md`](docs/backlog.md) (prioritized backlog).

## ⚙️ Running

1. **Backend** (`sition-web` repo): bring up docker-compose and the Spring backend — the
   `V12__petcare_core.sql` migration creates PetCare's tables.
2. **App**: open in Android Studio and run on the emulator, or:

   ```bash
   ./gradlew installDebug
   ```

   The **debug** build points to `http://127.0.0.1:8080/` through the adb tunnel — before
   testing (emulator **or** physical device over USB/Wi-Fi adb), run:

   ```bash
   adb reverse tcp:8080 tcp:8080
   ```

   > The tunnel drops when the device disconnects from adb or reboots — just run the command
   > again. The **release** build points to `https://sition.murilofelipe.com/`. Adjust in
   > `app/build.gradle.kts` (`API_BASE_URL`) if needed.
3. Log in with an existing Sition Web user.

## 📂 Structure

```text
app/src/main/kotlin/com/murilo/petcare/
├── di/            # Hilt (Retrofit, OkHttp, Room)
├── data/
│   ├── auth/      # TokenStore (DataStore) + AuthInterceptor (JWT)
│   ├── remote/    # PetCareApi + DTOs (Sition Web contracts)
│   ├── local/     # Room (offline cache)
│   └── repository/
├── ui/
│   ├── login/ · home/ · pets/ · supplies/ · expenses/ · vet/
│   ├── navigation/ · session/ · common/ · theme/
└── MainActivity.kt
```

## 📍 Roadmap

1. **Phase 2 — External connections**: pet shops, training, parks, community.
2. **Notifications/alarms** (via Sition Web) for upcoming doses and restocking.
3. **Structured prescriptions** synced from Sition Web.
4. Real photo upload (camera/gallery) and in-app sign-up.

## 🌿 Branch flow

Branch from `develop`; open PRs back to `develop`, with Conventional Commits in pt-BR.
`main` only receives merges from `develop`. CI (GitHub Actions) runs quality, coverage and
duplication checks on every PR.

---
Developed by Murilo Silva Felipe 🌿
