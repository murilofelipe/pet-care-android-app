# 🐾 PetCare - Gestão Rural e Familiar

O **PetCare** é um aplicativo Android desenvolvido em Jetpack Compose para a gestão completa de animais em propriedades rurais. O projeto foi estruturado para atender desde animais domésticos até lotes de produção (gado, aves e piscicultura), com foco em rastreabilidade e genealogia.

## 🚀 Funcionalidades

- **Cadastro Híbrido:** Registro de animais individuais (matrizes/pets) ou lotes (aves/peixes).
- **Genealogia e Pedigree:** Registro de pai e mãe para controle de consanguinidade.
- **Categorização Estruturada:** Separação por finalidade (Doméstico, Produção, Subsistência ou Lazer).
- **Identificação Visual:** Suporte para foto principal do animal ou lote.
- **Cálculo de Idade:** Lógica baseada em data de nascimento para manejo sanitário.
- **Arquitetura MVVM:** Código limpo, reativo e escalável.

## 🛠️ Stack Tecnológica

- **Linguagem:** Kotlin
- **Interface:** Jetpack Compose (Material 3)
- **Arquitetura:** MVVM + Repository Pattern
- **Estado:** StateFlow & Coroutines
- **Imagens:** Coil (Asynchronous Image Loading)

## 📂 Estrutura de Pastas

```text
src/main/kotlin/com/murilo/petcare/
├── model/          # Pet.kt e Enums de categoria
├── repository/     # PetRepository.kt (Gestão de fluxo de dados)
├── viewmodel/      # PetViewModel.kt (Lógica de estado da UI)
├── ui/
│   ├── components/ # PetCard.kt (Componentes reutilizáveis)
│   ├── screens/    # PetListScreen.kt e PetFormScreen.kt
│   └── theme/      # Configurações de cores e estilos
└── MainActivity.kt # Navegação e ponto de entrada
```
## 🚀 Como Rodar

1.  Clone o repositório:
```terminaloutput
git clone git@github.com:murilofelipe/pet-care-android-app.git
```   


2. Abra no Android Studio Panda 3+.

3. Instale no dispositivo:
   ./gradlew installDebug


## ⚙️ Configuração e Build
Ajuste de Memória (Heap Space)
Devido à complexidade dos componentes de UI e processamento de imagem, se o build falhar com erro de memória no Android Studio Panda 3, adicione ao seu gradle.properties:

```properties
org.gradle.jvmargs=-Xmx4096m
```


## 🏗 Estrutura
- `app/`: Módulo principal do aplicativo.
- `gradle/libs.versions.toml`: Fonte de verdade das versões.

## Dependências Core
```kotlin
implementation("io.coil-kt:coil-compose:2.6.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
```

## 📍 Roadmap de Desenvolvimento

1.  **Persistência Local (Room):** Migrar o repositório em memória para banco de dados local. 
2.  **Histórico de Vacinação:** Módulo para controle de datas e lembretes sanitários. 
3.  **Módulo de Piscicultura:** Gestão específica para tanques de tilápias e controle de ração. 
4.  **Integração Kafka/Data Engineering:** Streaming de dados para telemetria de sensores IoT na chácara.
---
Desenvolvido por Murilo Silva Felipe 🌿