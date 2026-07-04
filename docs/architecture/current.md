# PetCare — Arquitetura atual (Fase 1: Core)

> Documento vivo. Atualize quando a arquitetura mudar de fato.
> Última atualização: 2026-07-04.

## Visão geral

O PetCare é um app Android (Kotlin + Jetpack Compose) para tutores de pets em
casas/apartamentos. Ele **não tem backend próprio**: consome a API REST do
Sition Web (Java 21 / Spring Boot 3.5 / PostgreSQL 15), reaproveitando o modelo
`Animal` com vínculo direto ao usuário (`owner_id`), sem propriedade rural —
ver `docs/adr/001-pet-sem-propriedade.md`.

```
┌─────────────── App Android (este repo) ───────────────┐
│ UI (Compose + Material 3)                             │
│   └─ ViewModels (MVVM, StateFlow)                     │
│        └─ Repositories                                │
│             ├─ Retrofit/OkHttp ──► API Sition Web     │
│             └─ Room (cache offline, fonte da UI)      │
│ Hilt (DI) · DataStore (JWT)                           │
└───────────────────────────────────────────────────────┘
```

## Stack

| Camada | Tecnologia |
|---|---|
| UI | Jetpack Compose + Material 3 (BOM 2025.09.01) |
| Arquitetura | MVVM — ViewModel + StateFlow |
| DI | Hilt 2.57.2 (KSP) — 2.58+ exige AGP 9 |
| Rede | Retrofit 2.12 + OkHttp 4.12 + kotlinx.serialization |
| Cache offline | Room 2.8 |
| Sessão | DataStore Preferences (JWT + dados do usuário) |
| Build | Gradle 9.4.1 · AGP 8.13.2 · Kotlin 2.3.0 · KSP 2.3.0 · JDK 21 target |

URL da API por `BuildConfig.API_BASE_URL`: debug → `http://10.0.2.2:8080/`
(backend local do sition-web visto do emulador; cleartext liberado só para
esse host em `res/xml/network_security_config.xml`), release →
`https://sition.murilofelipe.com/`.

## Estrutura de packages (`com.murilo.petcare`)

```
PetCareApplication.kt        @HiltAndroidApp
MainActivity.kt              decide Login × app autenticado (SessionViewModel)
di/AppModule.kt              OkHttp, Retrofit, Room, DAOs
data/
  auth/                      TokenStore (DataStore) + AuthInterceptor (Bearer)
  remote/                    PetCareApi (Retrofit) + dto/ (contratos da API)
  local/                     PetCareDatabase, entidades e DAOs Room
  repository/                AuthRepository, PetRepository, SupplyRepository,
                             ExpenseRepository, ApiCall (tradução de erros)
ui/
  session/                   SessionViewModel (Loading/LoggedOut/LoggedIn)
  login/                     LoginScreen + LoginViewModel
  navigation/                PetCareNavHost (home, pet_form, pet_detail)
  home/                      HomeScreen — abas: Pets · Itens · Gastos · Como vet
  pets/                      lista, formulário, detalhe (saúde + acesso vet)
  supplies/                  utensílios/ração (lista + dialog form)
  expenses/                  financeiro (total do mês + lista + dialog form)
  vet/                       pets compartilhados comigo (visão veterinário)
  common/                    Labels pt-BR, dropdown de enum, diálogos padrão
  theme/                     Material 3 theme
```

## Fluxos principais

- **Autenticação**: `POST /api/v1/auth/login` (`emailOrUsername` + `password`)
  → `{ token, type: "Bearer", user }`. O JWT (HS256, 24 h) vai no header
  `Authorization: Bearer` de todas as demais chamadas, via `AuthInterceptor`.
  Sessão persistida em DataStore; logout limpa DataStore + Room. **Não há
  cadastro no app** — o registro exige token de ativação e é feito no Sition Web.
- **Cache offline**: a UI observa Flows do Room; cada aba dispara `refresh()`
  que busca na API e substitui o cache. Sem rede, o último estado sincronizado
  continua visível (escritas exigem rede nesta fase).
- **Perfis**: tutor (aba Pets — CRUD completo, concede/revoga acesso de
  veterinário por e-mail) e veterinário (aba "Como vet" — pets compartilhados
  via `VetAnimalAccess`; pode registrar apenas VACINA e MEDICACAO, regra
  aplicada no backend).

## Endpoints consumidos (fatia PetCare da API do Sition Web)

| Método/rota | Uso |
|---|---|
| `POST /api/v1/auth/login` | login |
| `GET/POST /api/v1/pets` · `GET/PUT/DELETE /api/v1/pets/{id}` | CRUD de pets do tutor |
| `GET /api/v1/pets/shared-with-me` | pets compartilhados (visão vet) |
| `GET/POST /api/v1/pets/{id}/health` | vacinação/medicação (`AnimalHealthRecord`) |
| `GET/POST /api/v1/pets/{id}/vet-access` · `DELETE .../{accessId}` | acesso de veterinário |
| `GET/POST/PUT/DELETE /api/v1/pet-supplies` | utensílios/ração |
| `GET/POST/DELETE /api/v1/pet-expenses` | financeiro doméstico |

As mudanças correspondentes no backend (migration `V12__petcare_core.sql`,
entidades `VetAnimalAccess`/`PetSupply`/`PetExpense`, `PetService` e
controllers) estão descritas em `docs/sition-web-changes.md`.

## Fora de escopo nesta fase

Conexões externas (pet shops, comunidade), alarmes/notificações por e-mail e
receita médica estruturada — ficam para fases futuras (a receita virá do
Sition Web por sincronização).