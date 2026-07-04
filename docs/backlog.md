# Backlog — PetCare Android

> Priorizado por fase; cada sprint deve caber em 1–2 sessões de trabalho.
> Status: ✅ feito · 🔜 próximo · ⛔ aguardando dependência.
> Atualizado em 2026-07-05.

## Fase 1 — Core ✅ (entregue)

Login JWT (Sition Web), CRUD de pets, vacinação, medicação, acesso de
veterinário (`VetAnimalAccess`), utensílios/ração, financeiro, cache offline
Room, docs + ADR. Validado em aparelho físico contra backend V12.

---

## Fase 1.5 — Polimento e robustez 🔜

### Sprint A — UX de entrada de dados
- [ ] Substituir campos de data texto (`aaaa-mm-dd`) por `DatePickerDialog` Material 3 (pets, saúde, itens, gastos)
- [ ] Foto do pet real: captura câmera/galeria (Photo Picker) + exibição — **decisão pendente**: upload para o backend (novo endpoint de mídia no sition-web) × URI local apenas
- [ ] Máscara/formatação de moeda no campo de valor (gastos e custos)
- [ ] Pull-to-refresh nas listas (pets, itens, gastos, como-vet)

### Sprint B — Qualidade
- [ ] Testes unitários: ViewModels (login, pets, gastos) e mapeamentos DTO↔entity
- [ ] Teste instrumentado mínimo: fluxo login → lista de pets (MockWebServer)
- [ ] CI GitHub Actions: `assembleDebug` + lint em PR para `developer`
- [ ] Tratamento de sessão expirada (401 → logout automático + volta ao login)

### Sprint C — Saúde e histórico
- [ ] Editar/excluir registro de saúde (backend: `PUT/DELETE /pets/{id}/health/{recordId}` — exige PR no sition-web)
- [ ] Tela "próximas doses" agregada (endpoint `/health/upcoming` já existe no padrão rural; expor no `/pets`)
- [ ] Lembretes locais de vacina/reposição com WorkManager + notificação (sem backend; ponte até os alarmes por e-mail da Fase 3)

## Fase 2 — Conexões externas (escopo do prompt original) ⛔

- [ ] Módulo de descoberta: pet shops, adestradores, praças pet-friendly (fonte de dados a definir — Google Places?)
- [ ] Comunidade/social básico (a refinar com o produto)
- [ ] Compartilhamento de ficha do pet (deep link/PDF)

## Fase 3 — Integrações com o Sition Web ⛔

- [ ] Receita médica estruturada sincronizada do Sition Web (gerada lá, leitura no app)
- [ ] Alarmes/notificações por e-mail disparados pelo backend
- [ ] Push notifications (FCM) — exige decisão de infraestrutura

## Infra / dívida técnica

- [ ] Migração AGP 9.x + Kotlin embutido (destrava Hilt 2.60+, compose-bom 2026.x, compileSdk 37) — tratar como tarefa isolada
- [ ] Fila offline de escrita (outbox): criar/editar sem rede e sincronizar depois
- [ ] Registro de conta no app (hoje só via web com token de ativação) — decisão de produto
- [ ] Assinatura de release + pipeline de distribuição (Play Console interno)
- [ ] Crash reporting (a decidir: Crashlytics × Sentry)