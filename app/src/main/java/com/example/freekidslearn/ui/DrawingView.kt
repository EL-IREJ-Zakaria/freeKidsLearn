package com.example.freekidslearn.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * ============================================================================
 * DRAWINGVIEW.KT - Vue personnalisée pour le traçage des lettres
 * ============================================================================
 *
 * Zone de dessin où l'enfant peut tracer les lettres avec son doigt.
 * La lettre à tracer est affichée en arrière-plan en gris clair pour
 * guider l'enfant pendant le traçage.
 *
 * ============================================================================
 */
class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // =========================================================================
    // SECTION 1: Variables pour le dessin du tracé
    // =========================================================================

    /** Chemin actuel en cours de dessin */
    private val drawPath = Path()

    /** Style du trait de dessin (couleur, épaisseur, etc.) */
    private val drawPaint = Paint().apply {
        color = Color.parseColor("#FF6B6B")  // Rouge-rose vif
        isAntiAlias = true
        strokeWidth = 24f                     // Trait épais pour les enfants
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    /** Paint pour le canvas */
    private val canvasPaint = Paint(Paint.DITHER_FLAG)

    /** Canvas de dessin */
    private var drawCanvas: Canvas? = null

    /** Bitmap pour stocker le dessin */
    private var canvasBitmap: Bitmap? = null

    /** Liste des tracés sauvegardés */
    private val paths = mutableListOf<PathData>()

    /** Classe pour stocker un tracé avec son style */
    data class PathData(val path: Path, val paint: Paint)

    // =========================================================================
    // SECTION 2: Variables pour la lettre en arrière-plan
    // =========================================================================

    /** La lettre à afficher en arrière-plan */
    private var backgroundLetter: String = ""

    /** Style pour la lettre en arrière-plan (gris clair, très grande) */
    private val letterPaint = Paint().apply {
        color = Color.parseColor("#E0E0E0")  // Gris clair
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    /** Taille de la lettre en arrière-plan (sera calculée dynamiquement) */
    private var letterSize = 300f

    // =========================================================================
    // SECTION 3: Méthodes du cycle de vie
    // =========================================================================

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Créer le bitmap pour le dessin
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)

        // Calculer la taille de la lettre en fonction de la taille de la vue
        // La lettre occupe 70% de la plus petite dimension
        letterSize = minOf(w, h) * 0.7f
        letterPaint.textSize = letterSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // ─────────────────────────────────────────────────────────────────────
        // Étape 1: Dessiner la lettre en arrière-plan (guide pour l'enfant)
        // ─────────────────────────────────────────────────────────────────────
        if (backgroundLetter.isNotEmpty()) {
            // Calculer la position centrale
            val xPos = width / 2f

            // Calculer la position Y pour centrer verticalement le texte
            // On utilise les métriques de la police pour un centrage précis
            val fontMetrics = letterPaint.fontMetrics
            val textHeight = fontMetrics.descent - fontMetrics.ascent
            val yPos = (height / 2f) + (textHeight / 2f) - fontMetrics.descent

            // Dessiner la lettre centrée
            canvas.drawText(backgroundLetter, xPos, yPos, letterPaint)
        }

        // ─────────────────────────────────────────────────────────────────────
        // Étape 2: Dessiner le bitmap avec les anciens tracés
        // ─────────────────────────────────────────────────────────────────────
        canvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, canvasPaint)
        }

        // ─────────────────────────────────────────────────────────────────────
        // Étape 3: Dessiner tous les tracés sauvegardés
        // ─────────────────────────────────────────────────────────────────────
        for (pathData in paths) {
            canvas.drawPath(pathData.path, pathData.paint)
        }

        // ─────────────────────────────────────────────────────────────────────
        // Étape 4: Dessiner le tracé en cours
        // ─────────────────────────────────────────────────────────────────────
        canvas.drawPath(drawPath, drawPaint)
    }

    // =========================================================================
    // SECTION 4: Gestion des événements tactiles
    // =========================================================================

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            // Début du tracé
            MotionEvent.ACTION_DOWN -> {
                drawPath.moveTo(touchX, touchY)
            }

            // Le doigt se déplace
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX, touchY)
            }

            // Fin du tracé
            MotionEvent.ACTION_UP -> {
                drawCanvas?.drawPath(drawPath, drawPaint)

                // Sauvegarder le tracé
                val newPath = Path(drawPath)
                val newPaint = Paint(drawPaint)
                paths.add(PathData(newPath, newPaint))

                drawPath.reset()
            }

            else -> return false
        }

        invalidate()
        return true
    }

    // =========================================================================
    // SECTION 5: Méthodes publiques
    // =========================================================================

    /**
     * Définit la lettre à afficher en arrière-plan
     *
     * @param letter La lettre à tracer (ex: "A", "ب")
     */
    fun setBackgroundLetter(letter: String) {
        backgroundLetter = letter
        invalidate()  // Redessiner la vue
    }

    /**
     * Efface le dessin mais garde la lettre en arrière-plan
     */
    fun clearCanvas() {
        paths.clear()
        drawPath.reset()

        canvasBitmap?.let {
            drawCanvas = Canvas(it)
            drawCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }

        invalidate()
    }

    /**
     * Change la couleur du trait de dessin
     */
    fun setDrawingColor(color: Int) {
        drawPaint.color = color
    }

    /**
     * Change l'épaisseur du trait de dessin
     */
    fun setStrokeWidth(width: Float) {
        drawPaint.strokeWidth = width
    }

    /**
     * Change la couleur de la lettre en arrière-plan
     */
    fun setLetterColor(color: Int) {
        letterPaint.color = color
        invalidate()
    }
}
