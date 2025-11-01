# Enregistreur de Réunions pour Android

Cette application est une version Android native de l'enregistreur de réunions web. Elle permet d'enregistrer de l'audio, de le transcrire automatiquement avec identification des interlocuteurs, et de générer un compte rendu structuré à l'aide d'une intelligence artificielle.

## Fonctionnalités

*   **Enregistrement Audio :** Enregistrez vos réunions directement depuis le microphone de votre appareil.
*   **Stockage Sécurisé :** Vos clés API pour les services externes sont chiffrées et stockées de manière sécurisée sur l'appareil.
*   **Liste des Enregistrements :** Une base de données locale conserve la liste de vos enregistrements passés avec leur date et leur durée.
*   **Transcription par IA :** Utilise l'API d'AssemblyAI pour une transcription précise, incluant la séparation des différents interlocuteurs.
*   **Résumé par IA :** Utilise l'API de Mistral AI pour générer un compte rendu de réunion professionnel et structuré à partir de la transcription.

## Technologies Utilisées

*   **Langage :** Kotlin
*   **Interface Utilisateur :** Jetpack Compose
*   **Architecture :** MVVM (Model-View-ViewModel)
*   **Programmation Asynchrone :** Kotlin Coroutines
*   **Base de Données Locale :** Room
*   **Réseau :** OkHttp & Kotlinx.Serialization
*   **Stockage Sécurisé :** Jetpack Security (EncryptedSharedPreferences)

## Instructions de Fonctionnement

### 1. Prérequis

*   [Android Studio](https://developer.android.com/studio) (dernière version recommandée)
*   Un appareil Android ou un émulateur avec Android 7.0 (API 24) ou supérieur.

### 2. Configuration des Clés API

Pour utiliser les fonctionnalités de transcription et de résumé, vous devez obtenir des clés API auprès des services suivants :

*   **AssemblyAI :**
    1.  Créez un compte gratuit sur [assemblyai.com](https://www.assemblyai.com/).
    2.  Accédez à votre tableau de bord pour trouver votre clé API. Le compte gratuit inclut plusieurs heures de transcription audio par mois.
*   **Mistral AI :**
    1.  Créez un compte sur [console.mistral.ai](https://console.mistral.ai/).
    2.  Générez une clé API depuis la section "API Keys" de votre plateforme.

Une fois l'application lancée, saisissez ces clés dans les champs prévus à cet effet dans la section "Configuration des API" et appuyez sur "Sauvegarder les clés".

### 3. Compilation et Exécution

1.  Clonez ce dépôt sur votre machine locale.
2.  Ouvrez le projet dans Android Studio.
3.  Laissez Gradle synchroniser et télécharger toutes les dépendances nécessaires.
4.  Sélectionnez un appareil (physique ou émulateur) dans la barre d'outils.
5.  Cliquez sur le bouton **Run 'app'** (icône verte en forme de triangle) pour compiler et installer l'application sur l'appareil sélectionné.
