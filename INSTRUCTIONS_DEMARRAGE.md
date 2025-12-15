# 🚀 Instructions de Démarrage - Kids Learning

## Première Configuration

### 1️⃣ Synchroniser les Dépendances

Après avoir ouvert le projet dans Android Studio:

1. **Sync Gradle**
    - Cliquez sur l'icône "Sync Project with Gradle Files" dans la barre d'outils
    - Ou: `File → Sync Project with Gradle Files`
    - Attendez que toutes les dépendances se téléchargent

2. **Vérifier les Erreurs**
    - Les erreurs de linter concernant Room et Gson devraient disparaître après le sync
    - Si des erreurs persistent, faites: `Build → Clean Project` puis `Build → Rebuild Project`

### 2️⃣ Configuration de l'Émulateur

**Option A: Créer un nouvel émulateur**

```
Tools → Device Manager → Create Device
- Choisir: Pixel 5 ou Pixel Tablet
- API Level: 34 (Android 14) ou supérieur
- RAM: 2048 MB minimum
```

**Option B: Utiliser un appareil physique**

```
1. Activer le mode développeur sur votre appareil
2. Activer le débogage USB
3. Connecter via USB
```

### 3️⃣ Premier Lancement

1. **Lancer l'application**
    - Cliquez sur le bouton ▶️ (Run) dans Android Studio
    - Ou: `Shift + F10`

2. **Vérifications au premier lancement**
    - L'app devrait afficher l'écran d'accueil avec 2 boutons
    - Tester la sélection de l'alphabet arabe
    - Tester la sélection de l'alphabet français
    - Vérifier que les lettres s'affichent dans la grille

### 4️⃣ Tester les Fonctionnalités

#### Test de Base

✅ Navigation depuis l'accueil vers la liste
✅ Clic sur une lettre pour ouvrir le traçage
✅ Dessiner avec le doigt sur le canvas
✅ Bouton "Effacer" pour nettoyer le canvas
✅ Boutons "Suivant" et "Précédent" pour naviguer

#### Test Audio

✅ Son automatique à l'ouverture d'une lettre
✅ Bouton "Répéter" pour rejouer le son
✅ Vérifier que le TTS fonctionne en arabe et français

#### Test de Persistance

✅ Tracer plusieurs lettres
✅ Passer à la lettre suivante
✅ Fermer et rouvrir l'app
✅ La progression devrait être sauvegardée dans Room

## 🔧 Résolution des Problèmes Courants

### ❌ Problème: Erreurs de compilation Gradle

**Solution:**

```bash
1. File → Invalidate Caches → Invalidate and Restart
2. Supprimer le dossier .gradle dans le projet
3. Relancer le sync Gradle
```

### ❌ Problème: Room annotations non reconnues

**Solution:**

```
- Vérifier que KSP est bien configuré dans build.gradle.kts
- Rebuild le projet: Build → Rebuild Project
- Les classes générées par Room seront dans build/generated/
```

### ❌ Problème: TextToSpeech ne fonctionne pas

**Solutions possibles:**

```
1. Vérifier que les langues TTS sont installées sur l'appareil
   - Paramètres → Système → Langues → Synthèse vocale
   
2. Pour l'émulateur:
   - Installer Google TTS depuis le Play Store de l'émulateur
   - Ou utiliser un appareil physique
   
3. Tester avec ce code dans logcat:
   - Chercher "TTS initialized" dans les logs
```

### ❌ Problème: Le canvas ne dessine pas

**Solution:**

```kotlin
- Vérifier que DrawingView.kt n'a pas d'erreurs
- Le canvas doit avoir une taille > 0
- Vérifier dans onSizeChanged que le bitmap est créé
```

### ❌ Problème: Les fichiers JSON ne se chargent pas

**Solution:**

```
1. Vérifier que les fichiers sont dans app/src/main/assets/
2. Rebuild le projet pour inclure les assets
3. Vérifier les logs pour les erreurs de parsing JSON
```

### ❌ Problème: L'app crash au démarrage

**Solutions:**

```
1. Vérifier le Logcat pour l'erreur exacte
2. Vérifier que minSdk est >= 24
3. S'assurer que tous les fichiers de layout existent
4. Clean et Rebuild le projet
```

## 📱 Test sur Appareil Physique

### Android 7.0 - 13 (API 24-33)

```
✅ Toutes les fonctionnalités supportées
✅ Performance optimale
```

### Android 14+ (API 34+)

```
✅ Support complet
✅ Nouvelles optimisations Material Design 3
```

## 🎨 Personnalisation Rapide

### Changer les Couleurs

Éditez `app/src/main/res/values/colors.xml`:

```xml
<color name="primary">#VotreCouleur</color>
<color name="accent">#VotreCouleur</color>
```

### Ajouter des Lettres

Éditez les fichiers JSON dans `assets/`:

```json
{"id": 29, "letter": "ء", "name": "Hamza", "type": "ARABIC"}
```

### Modifier les Textes

Éditez `app/src/main/res/values/strings.xml`:

```xml
<string name="welcome_title">Votre Texte</string>
```

## 📊 Vérifier la Base de Données

### Utiliser Database Inspector

```
1. Lancer l'app en mode debug
2. View → Tool Windows → App Inspection
3. Sélectionner l'onglet "Database Inspector"
4. Explorer la table "letter_progress"
```

### Voir les Données

```sql
SELECT * FROM letter_progress;
```

## 🧪 Mode Debug

### Activer les Logs Détaillés

Ajouter dans les classes:

```kotlin
private val TAG = "KidsLearning"

Log.d(TAG, "Message de debug")
Log.e(TAG, "Message d'erreur", exception)
```

### Logcat Filters

```
- Tag: KidsLearning
- Package: com.example.freekidslearn
- Log Level: Debug
```

## 📦 Générer l'APK

### Debug APK

```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

Fichier généré: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (pour distribution)

```
1. Build → Generate Signed Bundle / APK
2. Créer ou sélectionner un keystore
3. Suivre l'assistant
```

## 🔍 Checklist de Vérification

Avant de considérer le projet terminé:

- [ ] L'app se lance sans crash
- [ ] Les 2 alphabets sont accessibles depuis l'accueil
- [ ] Les lettres s'affichent dans la grille (RecyclerView)
- [ ] Le clic sur une lettre ouvre le traçage
- [ ] Le son se joue automatiquement
- [ ] Le bouton "Répéter" fonctionne
- [ ] On peut dessiner sur le canvas
- [ ] Le bouton "Effacer" nettoie le canvas
- [ ] Navigation "Suivant/Précédent" fonctionne
- [ ] Les données sont sauvegardées en Room
- [ ] L'app fonctionne hors-ligne
- [ ] Pas d'erreur dans le Logcat
- [ ] L'interface est responsive (test tablette)

## 📝 Notes Importantes

### Permissions

L'app n'a besoin d'aucune permission spéciale. Tout fonctionne en local.

### Taille de l'APK

Environ 3-5 MB (debug), 2-3 MB (release avec minification)

### Performance

- Testé sur appareils avec 2GB RAM minimum
- Fonctionne fluidement sur Android 7.0+

### Langues TTS

Si le TTS ne fonctionne pas:

1. Vérifier que les données de langue sont téléchargées
2. Alternative: utiliser des fichiers audio MP3 dans res/raw/

## 🎓 Pour les Débutants

### Structure d'un Projet Android

```
app/
├── src/main/
│   ├── java/          → Code Kotlin
│   ├── res/           → Resources (layouts, colors, etc.)
│   ├── assets/        → Fichiers raw (JSON, images)
│   └── AndroidManifest.xml → Configuration app
├── build.gradle.kts   → Dépendances
└── proguard-rules.pro → Règles d'obfuscation
```

### Commandes Gradle Utiles

```bash
./gradlew clean           # Nettoyer le build
./gradlew assembleDebug   # Compiler debug APK
./gradlew installDebug    # Installer sur appareil
./gradlew dependencies    # Voir les dépendances
```

## 💡 Conseils Pro

1. **Toujours sync Gradle** après avoir modifié build.gradle.kts
2. **Utiliser Logcat** pour débugger les problèmes
3. **Tester sur plusieurs appareils** (différentes tailles)
4. **Faire des commits Git réguliers** pour sauvegarder votre travail
5. **Lire les erreurs attentivement** - elles donnent souvent la solution

## 🆘 Besoin d'Aide?

### Documentation Android

- [Developer Guide](https://developer.android.com/guide)
- [Kotlin Language](https://kotlinlang.org/docs/home.html)
- [Material Design](https://material.io/develop/android)

### Ressources Utiles

- Stack Overflow: `[android] [kotlin] votre question`
- Android Developers Discord
- Reddit: r/androiddev

---

## ✅ Prêt à Commencer!

Suivez ces étapes dans l'ordre:

1. ✅ Sync Gradle
2. ✅ Lancer l'app
3. ✅ Tester toutes les fonctionnalités
4. ✅ Vérifier les logs
5. ✅ Faire des modifications si nécessaire

**Bon développement! 🚀**
