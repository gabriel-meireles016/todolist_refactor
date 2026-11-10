# ToDo List — Kotlin

App ToDo List em Kotlin (MVVM + StateFlow) com testes unitários de ViewModel e análise de mutação via PIT.

## Pré-requisitos


- **JDK 17** (ou compatível com o Gradle do projeto)
- **Android Studio** (recomendado) ou Gradle instalado
- Gradle Wrapper incluído (`./gradlew`)

```bash
app/
 └── src/
     ├── main/    → código de produção
     └── test/    → testes unitários
         ├── com/meireles/todolist/ui/screens/add/AddViewModelTest.kt
         └── com/meireles/todolist/ui/screens/home/HomeViewModelTest.kt
```

## Cobertura dos testes:

- AddViewModel — criação/edição de tarefas, validação, loading, erros, gates de suspensão.
- HomeViewModel — listagem, filtro, contagens, delete, toggleStatus, erros.
- FakeTaskRepository — fake em memória com flags de falha, gates (CompletableDeferred), contadores e lastFilter.

## Executar testes unitários
```
./gradlew test
```

## Executar o PIT (Pitest)
```
./gradlew pitestDebug
```

## Resultados e Relatórios
```
app/build/reports/pitest/index.html    → HTML
app/build/reports/pitest/mutations.xml → XML
```
