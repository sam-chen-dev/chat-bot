# chat-bot

#### A modern Android AI chat application built with Jetpack Compose and OpenAI Responses API.

## Screenshot & Demo
<img width="2880" height="2880" alt="20260514_153133" src="https://github.com/user-attachments/assets/b4dc2717-4a7d-46e5-8639-3c3cc57c27bb" />

Watch demo [here](https://youtube.com/shorts/vZoK0pZdXj0). 

## Technologies Used
- [Retrofit](https://square.github.io/retrofit/): Used for API call and streaming API integration.
- [OpenAI Responses API](https://developers.openai.com/api/reference/responses/overview): Utilized for chat functionality.
- [Jetpack Compose](https://developer.android.com/compose): Modern Android UI toolkit.
- [Navigation3](https://developer.android.com/guide/navigation/navigation-3): Modern navigation framework for Jetpack Compose.
- [Room](https://developer.android.com/training/data-storage/room): Made chat history persistent.
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore): Made preferences data persistent.
- [Koin](https://insert-koin.io/docs/quickstart/android/): Dependency Injection framework for Kotlin.
- [MVVM Architecture](https://developer.android.com/topic/architecture): Modern architectural pattern for structuring Android project.

## Features
- Support for both standard and streaming responses using Retrofit and Server-Sent Events (SSE).
- Context-aware chat functionality using OpenAI Responses API.
- Offline support for chat history.
- Clean architecture for a modular and maintainable codebase.
- Dependency Injection with Koin for efficient and scalable development.

## Architecture
```
UI (Compose)
    ↓
ViewModel
    ↓
Repository
    ↓
OpenAI Responses API / Room Database
```

## Setup
1. Clone the repository.
2. Add your OpenAI API key to `local.properties` file:
```
OPENAI_API_KEY="xxx"
```
3. Run the app.
