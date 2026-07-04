# Mudanças aplicadas no backend do Sition Web (PetCare Fase 1)

As alterações abaixo **já foram aplicadas na working tree** do repositório
`sition-web` (sem commit) em 2026-07-04, e compilam (`mvn compile`). Este guia
serve de checklist de revisão/commit e de referência do contrato consumido
pelo app.

Sugestão de commit (Conventional Commits, no repo sition-web):

```
feat(pet): fatia de API PetCare — pets por usuário, VetAnimalAccess, supplies e expenses
```

## 1. Migration nova

| Arquivo | Conteúdo |
|---|---|
| `backend/src/main/resources/db/migration/V12__petcare_core.sql` | cria `vet_animal_access`, `pet_supplies`, `pet_expenses`; adiciona colunas opcionais em `animal_health_records` (`vaccine_batch`, `applied_by`, `dosage`, `frequency`, `end_date`) e `animals.photo_uri`. Sem `CREATE TYPE` (enums como VARCHAR, padrão de `animals.status`). Nenhuma coluna existente é alterada. |

Aplicação: sobe junto com o backend (`spring.flyway.enabled=true`, schema `sition`).

## 2. Arquivos **alterados** (revisar diff)

| Arquivo | Mudança |
|---|---|
| `model/animal/Animal.java` | + campo opcional `photoUri` (coluna `photo_uri`, V12) |
| `model/animal/AnimalHealthRecord.java` | + 5 campos opcionais de vacinação/medicação (V12) |

Nada mais do fluxo rural foi tocado — `AnimalController`, `AnimalService`,
`UserPropertyRole`, finance e subscription seguem intactos.

## 3. Arquivos **novos**

Modelo (`model/pet/`): `VetAnimalAccess` (padrão `UserPropertyRole`, unique
`user_id+animal_id`), `PetSupply`, `PetSupplyCategory`, `PetExpense`,
`PetExpenseCategory`.

Repositórios: `VetAnimalAccessRepository`, `PetSupplyRepository`,
`PetExpenseRepository`.

DTOs (`dto/pet/`): `PetCreateDTO`, `PetUpdateDTO`, `PetResponseDTO`,
`PetHealthRecordCreateDTO`, `PetHealthRecordResponseDTO`, `VetAccessCreateDTO`,
`VetAccessResponseDTO`, `PetSupplyCreateDTO`, `PetSupplyResponseDTO`,
`PetExpenseCreateDTO`, `PetExpenseResponseDTO`.

Services: `PetService` (CRUD owner-scoped; autorização tutor × vet; vet só
registra VACINA/MEDICACAO), `PetSupplyService`, `PetExpenseService`.

Controllers: `PetController` (`/api/v1/pets` + `/health` + `/vet-access` +
`/shared-with-me`), `PetSupplyController` (`/api/v1/pet-supplies`),
`PetExpenseController` (`/api/v1/pet-expenses`).

## 4. Segurança e regras respeitadas

- Nenhuma rota nova é pública: `SecurityConfig` já bloqueia tudo fora de
  `/api/v1/auth/**` (`anyRequest().authenticated()`), sem alteração.
- Autorização por dono no service (`findByIdAndOwnerId`), mesmo padrão dos
  demais domínios; veterinário via `VetAnimalAccess`.
- Sem caminho novo de registro; login continua o existente.
- Cotas: pets do PetCare **não** passam pelo `QuotaService` (cota atual é por
  propriedade — `countByPropertyIdAndIsActiveTrue`). Se o plano SaaS precisar
  limitar pets por usuário, tratar em sprint futura via `QuotaService`.
- Convenções Lombok mantidas (`@Getter @Setter @Builder @EqualsAndHashCode(of = "id")`,
  nunca `@Data`).

## 5. Docs do sition-web a atualizar no commit (padrão do repo)

`docs/architecture/current.md`, `docs/product/requirements.md` e, se desejado,
um ADR próprio referenciando o ADR 001 do PetCare
(`pet-care-android-app/docs/adr/001-pet-sem-propriedade.md`).
