# CLAUDE.md — PetCare Android

> Contexto mínimo por sessão. Para a maioria das tarefas, este arquivo basta —
> só abra os docs completos quando for mexer no assunto deles.

## O que é

App Android para tutores de pets (casas/apartamentos). **Sem backend próprio**:
consome a API do Sition Web (`~/Documentos/Github/sition-web`, Spring Boot,
fatia `/api/v1/pets/**` — migration V12, ADR-020 de lá). Repositório privado.

## Estado atual (2026-07-05)

Fase 1 entregue e validada em aparelho físico: login JWT, CRUD de pets,
vacinação/medicação, acesso de veterinário por e-mail (`VetAnimalAccess`),
utensílios/ração, financeiro, cache offline (Room). Próximos passos:
`docs/backlog.md`.

## Stack e limites de versão (NÃO subir sem migrar AGP)

- Gradle 9.4.1 (wrapper) · **AGP 8.13.2** · Kotlin 2.3.0 · KSP 2.3.0 · JDK alvo 21
- Tetos por causa do AGP 8.x: Hilt **2.57.2** · compose-bom **2025.09.01** ·
  core-ktx 1.17.0 · lifecycle 2.9.4 · navigation 2.9.5 (motivos no
  `gradle/libs.versions.toml`; versões acima exigem AGP 9.1+/compileSdk 37)
- MVVM + StateFlow · Hilt · Retrofit/OkHttp + kotlinx.serialization · Room ·
  DataStore (JWT) · Navigation Compose

## Comandos

- Build: `./gradlew :app:assembleDebug` — instalar: `./gradlew installDebug`
- **Antes de testar** (emulador ou aparelho físico): `adb reverse tcp:8080 tcp:8080`
  (debug aponta para `http://127.0.0.1:8080/`; o túnel cai quando o aparelho
  desconecta). Backend: `make run` no repo sition-web (Flyway aplica a V12).

## Regras deste projeto

- Branches a partir de `developer`; PR de volta para `developer`; Conventional
  Commits em pt-BR. `main` só recebe merge da `developer`.
- Contratos de API: nunca inventar — conferir nos controllers do sition-web
  (`backend/src/main/java/com/murilo/sition/controller/Pet*.java`). Mudança de
  contrato = PR nos dois repos.
- Datas trafegam como String ISO nos DTOs; dinheiro como Double no app,
  BigDecimal no backend.
- **Kotlin: nunca escrever `/*` dentro de KDoc/comentário** (comentários
  aninham; o arquivo inteiro vira comentário e o KSP falha com "could not be
  resolved" sem apontar o arquivo).
- Segredos: `.env` local (gitignored, deny de leitura) — carregar com
  `set -a; source .env; set +a`. Nunca commitar/exibir.

## Docs completos (abrir só quando necessário)

- `docs/architecture/current.md` — arquitetura, packages, endpoints consumidos
- `docs/adr/001-pet-sem-propriedade.md` — decisão do vínculo pet sem propriedade
- `docs/sition-web-changes.md` — o que a Fase 1 mudou no backend
- `docs/backlog.md` — backlog priorizado por fase/sprint