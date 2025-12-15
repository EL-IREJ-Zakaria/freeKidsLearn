# Kids Learning - Application Android Éducative

Une application Android pour aider les enfants à apprendre et écrire les alphabets arabe et
français.

## 📱 Fonctionnalités

### Alphabet Arabe

- ✅ Affichage complet des 28 lettres arabes
- ✅ Son prononcé pour chaque lettre
- ✅ Interface de traçage avec le doigt
- ✅ Bouton pour répéter le son
- ✅ Navigation entre les lettres

### Alphabet Français

- ✅ Affichage complet des 26 lettres françaises
- ✅ Prononciation du nom de chaque lettre
- ✅ Interface de traçage interactive
- ✅ Effacer et recommencer le tracé
- ✅ Progression entre les lettres

### Interface Enfant

- 🎨 Couleurs vives et attrayantes
- 🖼️ Grandes icônes et boutons
- 📱 Navigation simple et intuitive
- 🎵 Réactions sonores encourageantes

## 🏗️ Architecture

### Technologies Utilisées

- **Langage**: Kotlin
- **Base de données**: Room (pour la progression)
- **UI**: Material Design 3
- **Audio**: TextToSpeech pour les sons des lettres
- **Architecture**: MVVM-ready avec LiveData

### Structure du Projet

```
app/src/main/
├── java/com/example/freekidslearn/
│   ├── data/
│   │   ├── AlphabetType.kt          # Enum pour les types d'alphabet
│   │   ├── Letter.kt                # Modèle de données pour une lettre
│   │   ├── LetterProgress.kt        # Entity Room pour la progression
│   │   ├── LetterProgressDao.kt     # DAO pour les opérations DB
│   │   └── AppDatabase.kt           # Configuration Room
│   ├── ui/
│   │   ├── DrawingView.kt           # Vue personnalisée pour tracer
│   │   └── LetterAdapter.kt         # Adapter RecyclerView
│   ├── utils/
│   │   ├── AlphabetLoader.kt        # Chargement des données JSON
│   │   └── SoundManager.kt          # Gestion des sons
│   ├── MainActivity.kt              # Écran d'accueil
│   ├── AlphabetListActivity.kt      # Liste des lettres
│   └── LetterTracingActivity.kt     # Écran de traçage
├── assets/
│   ├── arabic_alphabet.json         # Données alphabet arabe
│   └── french_alphabet.json         # Données alphabet français
└── res/
    ├── layout/                      # Layouts XML
    ├── values/
    │   ├── colors.xml              # Couleurs de l'app
    │   ├── strings.xml             # Textes
    │   └── dimens.xml              # Dimensions
    └── drawable/                    # Icônes et images
```

## 🎨 Design

### Couleurs Principales

- **Vert primaire** (#4CAF50) - Couleur principale
- **Violet** (#9B59B6) - Bouton alphabet arabe
- **Bleu** (#3498DB) - Bouton alphabet français
- **Orange** (#FF6B35) - Accent et tracé

### Fonctionnalités Techniques

- ✅ Support téléphones et tablettes
- ✅ Design responsive
- ✅ Performance optimisée
- ✅ Fonctionne hors-ligne
- ✅ Sauvegarde automatique de la progression
- ✅ Architecture propre et modulaire

## 🚀 Installation

1. Cloner le projet
2. Ouvrir dans Android Studio
3. Sync Gradle
4. Lancer l'application sur un émulateur ou appareil

### Prérequis

- Android Studio Giraffe ou supérieur
- Kotlin 2.0+
- Android SDK 24+ (Android 7.0)
- Target SDK 36

## 📋 Dépendances Principales

```kotlin
// Room Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// Material Design
com.google.android.material:material:1.13.0

// Gson pour JSON
com.google.code.gson:gson:2.10.1

// Lifecycle Components
androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2
androidx.lifecycle:lifecycle-livedata-ktx:2.6.2
```

## 🎯 Utilisation

1. **Écran d'accueil**: Choisir entre l'alphabet arabe ou français
2. **Liste des lettres**: Sélectionner une lettre pour commencer
3. **Traçage**:
    - Écouter la prononciation
    - Tracer la lettre avec le doigt
    - Utiliser "Effacer" pour recommencer
    - "Suivant" pour passer à la lettre suivante
    - "Répéter" pour réentendre le son

## 📝 Caractéristiques Pédagogiques

- **Apprentissage multisensoriel**: Vue + Son + Toucher
- **Répétition espacée**: Suivi de la progression
- **Feedback positif**: Sons encourageants
- **Autonomie**: Navigation simple pour les enfants
- **Sans publicité**: Expérience d'apprentissage pure

## 🔧 Configuration

Le projet utilise:

- **ViewBinding** pour l'accès aux vues
- **Coroutines** pour les opérations asynchrones
- **Room** pour la persistance des données
- **TextToSpeech** pour la synthèse vocale

## 📱 Screenshots

L'application comprend:

- Écran d'accueil avec sélection de langue
- Grille de lettres avec RecyclerView
- Interface de traçage avec Canvas
- Boutons de contrôle adaptés aux enfants

## 🤝 Contribution

Ce projet est développé dans un cadre éducatif. Les contributions sont les bienvenues!

## 📄 Licence

Projet éducatif - Free Kids Learn

---

Développé avec ❤️ pour l'éducation des enfants
