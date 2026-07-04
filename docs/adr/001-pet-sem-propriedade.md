# ADR 001 — Pet vinculado ao usuário, sem propriedade rural

- Status: **Aceito** (2026-07-04)
- Contexto: PetCare Fase 1 (core), consumindo o backend do Sition Web

## Contexto

O Sition Web modela `Animal` como parte de uma propriedade rural: os endpoints
existentes (`/api/v1/properties/{propertyId}/animals/**`) e o `AnimalService`
inteiro assumem `propertyId`, e a autorização deriva de `UserPropertyRole`
(vínculo usuário ↔ propriedade ↔ papel).

O PetCare atende tutores em casas/apartamentos, **sem** propriedade rural. Era
preciso suportar "animal vinculado apenas a um usuário" sem quebrar o modelo
rural nem os relatórios existentes.

## Decisão

1. **Reaproveitar a entidade `Animal` (sem fork de tabela).** O schema já havia
   sido preparado na V2: `property_id` e `owner_id` são ambos opcionais com
   `CHECK (property_id IS NOT NULL OR owner_id IS NOT NULL)`, e o enum
   `AnimalSourceApp` já continha `PET_APP`. Pets são `Animal` com
   `owner = usuário`, `property = null`, `sourceApp = PET_APP`,
   `trackingType = INDIVIDUAL`.
2. **Fatia de API paralela, não alteração da existente.** Novos endpoints
   `/api/v1/pets/**` com `PetService` próprio, escopado pelo usuário
   autenticado. `AnimalController`/`AnimalService` rurais ficam intocados
   (lotes, genealogia, venda, cotas continuam exclusivos do fluxo rural).
3. **`VetAnimalAccess` replica o padrão `UserPropertyRole`,** trocando
   propriedade por animal: `user_id + animal_id + role` com unique
   `(user_id, animal_id)`. O tutor concede acesso pelo e-mail do veterinário;
   o veterinário vê o pet e registra apenas VACINA/MEDICACAO.
4. **Vacinação/medicação reutilizam `animal_health_records`** com colunas
   opcionais novas (`vaccine_batch`, `applied_by`, `dosage`, `frequency`,
   `end_date`) — a linha do tempo de saúde continua única para os dois apps.
5. **Financeiro do pet em tabela nova (`pet_expenses`),** separado de
   `budget_entries`: relaxar o `property_id NOT NULL` do financeiro rural
   contaminaria os relatórios orçamentários do Sition Web. Utensílios/ração
   (`pet_supplies`) também é tabela nova, sem equivalente rural.

## Alternativas rejeitadas

- **Tornar `UserPropertyRole.property` opcional** ou criar uma "propriedade
  fantasma" por tutor: distorce o conceito de propriedade, vaza pets para
  telas e cotas rurais e complica a autorização existente.
- **Tabela `pets` separada de `animals`**: duplicaria histórico de saúde e o
  vínculo com veterinário, e quebraria a visão multi-app (rastreabilidade por
  `source_app`) que a V2 já estabeleceu.
- **Reusar `budget_entries` para gastos do pet**: exigiria relaxar NOT NULL e
  filtrar pets em todos os relatórios financeiros rurais.

## Consequências

- O app funciona apenas com a migration `V12__petcare_core.sql` aplicada e os
  novos controllers no ar (ver `docs/sition-web-changes.md`).
- Animais rurais e pets convivem na mesma tabela; qualquer query rural nova
  deve continuar filtrando por `property_id` (padrão já vigente).
- Receita médica estruturada, notificações e módulo de conexões ficam para
  fases futuras, sem impacto nesta decisão.
