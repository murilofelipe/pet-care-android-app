# 🐾 PetCare — Gestão de pets para casas e apartamentos

O **PetCare** é um aplicativo Android (Kotlin + Jetpack Compose) para tutores
de pets, complementar ao **Sition Web** (SaaS de gestão rural). O app **não tem
backend próprio**: consome a mesma API REST do Sition Web, reaproveitando a
entidade `Animal` com vínculo direto ao usuário — sem propriedade rural.

## 🚀 Funcionalidades (Fase 1 — Core)

- **Autenticação**: login com e-mail/usuário + senha (JWT do Sition Web). O
  cadastro de conta continua no Sition Web (exige código de ativação).
- **Pets**: cadastro, edição, listagem e exclusão (nome, espécie, raça, sexo,
  nascimento, peso, foto, observações).
- **Vacinação**: tipo, data de aplicação, próxima dose, lote, local/vet
  responsável, custo.
- **Medicação**: nome, dosagem, frequência, início/fim do tratamento.
- **Utensílios/Ração**: itens com categoria, marca, quantidade e data prevista
  de reposição (alerta visual quando próxima).
- **Financeiro**: gastos por categoria com total do mês e marcação de
  recorrência.
- **Acesso do veterinário**: o tutor concede acesso por e-mail
  (`VetAnimalAccess`); o veterinário vê os pets compartilhados na aba
  "Como vet" e registra vacinas/medicações.
- **Cache offline**: Room espelha os dados da API; o último estado sincronizado
  fica disponível sem rede.

## 🛠️ Stack

Kotlin 2.3 · Jetpack Compose (Material 3) · MVVM (ViewModel + StateFlow) ·
Hilt · Retrofit + OkHttp + kotlinx.serialization · Room · DataStore · Coil.
Build: Gradle 9.4.1 + AGP 8.13.2 + KSP.

Documentação detalhada: [`docs/architecture/current.md`](docs/architecture/current.md) ·
[`docs/adr/001-pet-sem-propriedade.md`](docs/adr/001-pet-sem-propriedade.md) ·
[`docs/sition-web-changes.md`](docs/sition-web-changes.md) (mudanças no backend).

## ⚙️ Como rodar

1. **Backend** (repo `sition-web`): suba o docker-compose e o backend Spring —
   a migration `V12__petcare_core.sql` cria as tabelas do PetCare.
2. **App**: abra no Android Studio e rode no emulador, ou:

   ```bash
   ./gradlew installDebug
   ```

   O build **debug** aponta para `http://10.0.2.2:8080/` (backend local visto
   do emulador); o **release** aponta para `https://sition.murilofelipe.com/`.
   Ajuste em `app/build.gradle.kts` (`API_BASE_URL`) se necessário.
3. Faça login com um usuário existente do Sition Web.

## 📂 Estrutura

```text
app/src/main/kotlin/com/murilo/petcare/
├── di/            # Hilt (Retrofit, OkHttp, Room)
├── data/
│   ├── auth/      # TokenStore (DataStore) + AuthInterceptor (JWT)
│   ├── remote/    # PetCareApi + DTOs (contratos do Sition Web)
│   ├── local/     # Room (cache offline)
│   └── repository/
├── ui/
│   ├── login/ · home/ · pets/ · supplies/ · expenses/ · vet/
│   ├── navigation/ · session/ · common/ · theme/
└── MainActivity.kt
```

## 📍 Roadmap

1. **Fase 2 — Conexões externas**: pet shops, adestramento, praças, comunidade.
2. **Notificações/alarmes** (via Sition Web) para próximas doses e reposição.
3. **Receita médica estruturada** sincronizada do Sition Web.
4. Upload real de foto (câmera/galeria) e cadastro dentro do app.

---
Desenvolvido por Murilo Silva Felipe 🌿
