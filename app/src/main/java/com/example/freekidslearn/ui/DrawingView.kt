package com.example.freekidslearn.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * ============================================================================
 * DRAWINGVIEW.KT - Vue personnalisée pour le traçage des lettres avec le doigt
 * ============================================================================
 *
 * Ce fichier définit une vue personnalisée (Custom View) qui permet aux enfants
 * de dessiner avec leur doigt sur l'écran pour tracer les lettres.
 *
 * QU'EST-CE QU'UNE CUSTOM VIEW?
 * - Une Custom View est une vue Android créée de toutes pièces
 * - Elle hérite de la classe View et surcharge ses méthodes
 * - Elle permet de créer des composants UI uniques qui n'existent pas par défaut
 *
 * CONCEPTS GRAPHIQUES ANDROID:
 * - Canvas: Surface sur laquelle on dessine (comme une toile de peintre)
 * - Paint: Définit le style du dessin (couleur, épaisseur, etc.)
 * - Path: Représente une série de points connectés (le tracé)
 * - Bitmap: Image en mémoire où on stocke le dessin
 *
 * CYCLE DE VIE D'UN DESSIN:
 * 1. L'utilisateur touche l'écran (ACTION_DOWN)
 * 2. L'utilisateur déplace son doigt (ACTION_MOVE)
 * 3. L'utilisateur lève son doigt (ACTION_UP)
 * 4. Le tracé est sauvegardé et affiché
 *
 * FONCTIONNEMENT:
 * ┌─────────────────────────────────────────┐
 * │           DrawingView                   │
 * │  ┌───────────────────────────────────┐  │
 * │  │         Bitmap (canvasBitmap)     │  │
 * │  │  ┌─────────────────────────────┐  │  │
 * │  │  │    Canvas (drawCanvas)      │  │  │
 * │  │  │                             │  │  │
 * │  │  │    Tracé de l'utilisateur   │  │  │
 * │  │  │    ~~~~~~~~                 │  │  │
 * │  │  │                             │  │  │
 * │  │  └─────────────────────────────┘  │  │
 * │  └───────────────────────────────────┘  │
 * └─────────────────────────────────────────┘
 *
 * ============================================================================
 */
class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // =========================================================================
    // SECTION 1: DÉCLARATION DES VARIABLES DE DESSIN
    // =========================================================================

    /**
     * Path courant - représente le tracé en cours de dessin
     * Stocke les coordonnées des points du doigt pendant le déplacement
     * Réinitialisé après chaque lever de doigt (ACTION_UP)
     */
    private val drawPath = Path()

    /**
     * Paint pour le dessin - définit le style du trait
     *
     * Configuration:
     * - color: Orange (#FF6B35) - couleur vive et visible pour les enfants
     * - isAntiAlias: true - lissage des bords pour un trait plus fluide
     * - strokeWidth: 20f - épaisseur du trait (20 pixels)
     * - style: STROKE - dessine uniquement le contour (pas de remplissage)
     * - strokeJoin: ROUND - coins arrondis quand le trait change de direction
     * - strokeCap: ROUND - extrémités arrondies du trait
     */
    private val drawPaint = Paint().apply {
        color = Color.parseColor("#FF6B35")  // Orange vif
        isAntiAlias = true                    // Anti-aliasing pour trait lisse
        strokeWidth = 20f                     // Épaisseur du trait
        style = Paint.Style.STROKE            // Mode contour (pas de remplissage)
        strokeJoin = Paint.Join.ROUND         // Jonctions arrondies
        strokeCap = Paint.Cap.ROUND           // Extrémités arrondies
    }

    /**
     * Paint pour le canvas - utilisé pour dessiner le bitmap sur l'écran
     * DITHER_FLAG améliore la qualité des couleurs sur certains écrans
     */
    private val canvasPaint = Paint(Paint.DITHER_FLAG)

    /**
     * Canvas de dessin - surface sur laquelle on dessine les tracés
     * Lié au bitmap pour sauvegarder le dessin
     */
    private var drawCanvas: Canvas? = null

    /**
     * Bitmap du canvas - image en mémoire qui stocke le dessin
     * Permet de conserver le dessin entre les frames
     */
    private var canvasBitmap: Bitmap? = null

    /**
     * Liste de tous les tracés effectués
     * Chaque tracé est sauvegardé avec son style (Paint)
     * Permet de redessiner tous les tracés lors d'un rafraîchissement
     */
    private val paths = mutableListOf<PathData>()

    /**
     * Classe de données pour stocker un tracé avec son style
     *
     * @param path Le chemin graphique (coordonnées du tracé)
     * @param paint Le style appliqué à ce tracé (couleur, épaisseur)
     */
    data class PathData(val path: Path, val paint: Paint)

    // =========================================================================
    // SECTION 2: MÉTHODES DU CYCLE DE VIE DE LA VUE
    // =========================================================================

    /**
     * Appelée quand la taille de la vue change (création, rotation, etc.)
     *
     * @param w Nouvelle largeur en pixels
     * @param h Nouvelle hauteur en pixels
     * @param oldw Ancienne largeur
     * @param oldh Ancienne hauteur
     *
     * FONCTIONNEMENT:
     * - Crée un nouveau Bitmap de la taille de la vue
     * - Crée un Canvas lié à ce Bitmap
     * - Nécessaire car on doit connaître la taille pour créer le bitmap
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Créer un bitmap de la taille de la vue avec canal alpha (transparence)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        // Créer un canvas lié au bitmap pour dessiner dessus
        drawCanvas = Canvas(canvasBitmap!!)
    }

    /**
     * Méthode principale de dessin - appelée à chaque rafraîchissement
     *
     * @param canvas Le canvas système sur lequel dessiner
     *
     * ORDRE DE DESSIN:
     * 1. Le bitmap (contient les anciens tracés finalisés)
     * 2. Les chemins sauvegardés (historique des tracés)
     * 3. Le chemin courant (tracé en cours)
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Étape 1: Dessiner le bitmap avec les anciens tracés
        canvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, canvasPaint)
        }

        // Étape 2: Dessiner tous les tracés sauvegardés
        for (pathData in paths) {
            canvas.drawPath(pathData.path, pathData.paint)
        }

        // Étape 3: Dessiner le tracé en cours (pendant que le doigt bouge)
        canvas.drawPath(drawPath, drawPaint)
    }

    // =========================================================================
    // SECTION 3: GESTION DES ÉVÉNEMENTS TACTILES
    // =========================================================================

    /**
     * Gère les événements tactiles (toucher, déplacer, lever le doigt)
     *
     * @param event Contient les informations sur le toucher (position, action)
     * @return true si l'événement a été traité, false sinon
     *
     * TYPES D'ACTIONS:
     * - ACTION_DOWN: Le doigt touche l'écran (début du tracé)
     * - ACTION_MOVE: Le doigt se déplace (continuation du tracé)
     * - ACTION_UP: Le doigt quitte l'écran (fin du tracé)
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Récupérer les coordonnées X et Y du toucher
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            // Début du tracé - le doigt touche l'écran
            MotionEvent.ACTION_DOWN -> {
                // Déplacer le "stylo" au point de départ sans tracer
                drawPath.moveTo(touchX, touchY)
            }

            // Le doigt se déplace - dessiner une ligne
            MotionEvent.ACTION_MOVE -> {
                // Tracer une ligne depuis le dernier point jusqu'au nouveau
                drawPath.lineTo(touchX, touchY)
            }

            // Le doigt quitte l'écran - finaliser le tracé
            MotionEvent.ACTION_UP -> {
                // Dessiner le tracé final sur le canvas permanent
                drawCanvas?.drawPath(drawPath, drawPaint)

                // Sauvegarder le tracé dans l'historique
                // On crée des copies pour ne pas perdre les données quand on réinitialise
                val newPath = Path(drawPath)       // Copie du chemin
                val newPaint = Paint(drawPaint)    // Copie du style
                paths.add(PathData(newPath, newPaint))

                // Réinitialiser le chemin courant pour le prochain tracé
                drawPath.reset()
            }

            // Autre type d'action (non géré)
            else -> return false
        }

        // Demander un rafraîchissement de l'affichage
        // Cela appellera onDraw() pour afficher le nouveau tracé
        invalidate()
        return true
    }

    // =========================================================================
    // SECTION 4: MÉTHODES PUBLIQUES POUR CONTRÔLER LE DESSIN
    // =========================================================================

    /**
     * Efface tout le dessin et remet le canvas à zéro
     *
     * UTILISÉE PAR:
     * - Le bouton "Effacer" dans LetterTracingActivity
     * - Appelée aussi quand on change de lettre
     *
     * ÉTAPES:
     * 1. Vider la liste des tracés sauvegardés
     * 2. Réinitialiser le chemin courant
     * 3. Effacer le bitmap
     * 4. Rafraîchir l'affichage
     */
    fun clearCanvas() {
        // Vider l'historique des tracés
        paths.clear()
        // Réinitialiser le chemin courant
        drawPath.reset()

        // Effacer le bitmap en le rendant transparent
        canvasBitmap?.let {
            drawCanvas = Canvas(it)
            // PorterDuff.Mode.CLEAR efface tout en gardant la transparence
            drawCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }

        // Rafraîchir l'affichage
        invalidate()
    }

    /**
     * Change la couleur du trait de dessin
     *
     * @param color La nouvelle couleur (format entier ARGB)
     *
     * EXEMPLE D'UTILISATION:
     * drawingView.setDrawingColor(Color.BLUE)
     * drawingView.setDrawingColor(Color.parseColor("#FF0000"))
     *
     * NOTE: N'affecte que les nouveaux tracés, pas les anciens
     */
    fun setDrawingColor(color: Int) {
        drawPaint.color = color
    }

    /**
     * Change l'épaisseur du trait de dessin
     *
     * @param width La nouvelle épaisseur en pixels
     *
     * RECOMMANDATIONS:
     * - Enfants: 15-25 pixels (traits épais, faciles à voir)
     * - Précision: 5-10 pixels (traits fins)
     *
     * NOTE: N'affecte que les nouveaux tracés, pas les anciens
     */
    fun setStrokeWidth(width: Float) {
        drawPaint.strokeWidth = width
    }
}
