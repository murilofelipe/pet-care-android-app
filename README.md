# Pet Care Android App 🐾

Projeto moderno para gestão e monitoramento de pets (Snow, Sky, Nox e Aura), integrando tecnologias mobile de ponta.

## 🛠 Tech Stack

* **Kotlin 2.3.0**: Utilizando o novo compilador K2.
* **Jetpack Compose**: UI declarativa com Material 3.
* **Gradle 9.4.1**: Configurado com Kotlin DSL.
* **Version Catalogs**: Gestão centralizada de dependências.

## 🚀 Como Rodar

1. Clone o repositório:
   git clone git@github.com:murilofelipe/pet-care-android-app.git

2. Abra no Android Studio Panda 3+.

3. Instale no dispositivo:
   ./gradlew installDebug

## 🏗 Estrutura
- `app/`: Módulo principal do aplicativo.
- `gradle/libs.versions.toml`: Fonte de verdade das versões.

## 📍 Roadmap de Desenvolvimento

1.  **Conectividade Wireless**: Finalizar o pareamento via ADB over Wi-Fi para deploy contínuo no smartphone.
2.  **CRUD de Pets**: Implementar o gerenciamento (Criar, Ler, Atualizar, Deletar) dos perfis de Snow, Sky, Nox e Aura.
3.  **Autenticação Google**: Integrar o Google Sign-In para segurança e sincronização de conta.
4.  **Histórico de Vacinação**: Módulo dedicado para controle de datas e lembretes de saúde animal.
5.  **Persistência Local**: Implementar o Room Database para garantir que os dados estejam acessíveis mesmo sem internet na chácara.
6.  **Integração Kafka**: Configurar o streaming de dados para telemetria e eventos em tempo real dos sensores IoT.
---
Desenvolvido por Murilo Silva Felipe
