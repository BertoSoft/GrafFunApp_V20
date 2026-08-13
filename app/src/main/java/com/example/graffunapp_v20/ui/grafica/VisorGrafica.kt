package com.example.graffunapp_v20.ui.grafica

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.example.graffunapp_v20.domain.models.DatosXY
import java.lang.Math.abs

class VisorGrafica @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){
    private var listaDatos: List<DatosXY> = emptyList()
    // --- Variables para el control de Zoom y Desplazamiento ---
    private var factorZoom = 1.0f
    private var desplazarX = 0.0f
    private var desplazarY = 0.0f
    private var ultimoToqueX = 0.0f
    private var ultimoToqueY = 0.0f

    // Detector para el gesto de pinza (dos dedos)
    private val detectorZoom = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            factorZoom *= detector.scaleFactor
            // Limitamos el zoom entre 0.5x y 10x para evitar que se rompa la vista
            factorZoom = factorZoom.coerceIn(0.5f, 10.0f)
            invalidate() // Redibuja el lienzo con el nuevo zoom
            return true
        }
    })
    private val pincelFuncion = Paint().apply {
        color       = Color.parseColor("#F63D03")
        strokeWidth = 6f
        style       = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val pincelEjes = Paint().apply {
        color       = Color.WHITE
        strokeWidth = 2f
        style       = Paint.Style.STROKE
    }
    private val pincelTexto = Paint().apply {
        color       = Color.WHITE
        textSize    = 35f
        isAntiAlias = true
    }
    // Captura los eventos táctiles de la pantalla
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Primero, dejamos que el detector de zoom procese el evento
        detectorZoom.onTouchEvent(event)

        // Si se está usando un solo dedo, gestionamos el desplazamiento (Pan)
        if (!detectorZoom.isInProgress) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    ultimoToqueX = event.x
                    ultimoToqueY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.x - ultimoToqueX
                    val deltaY = event.y - ultimoToqueY

                    desplazarX += deltaX
                    desplazarY += deltaY

                    ultimoToqueX = event.x
                    ultimoToqueY = event.y
                    invalidate() // Redibuja con el nuevo desplazamiento
                }
            }
        }
        return true
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (listaDatos.size < 2) return

        val ancho = canvas.width.toFloat()
        val alto = canvas.height.toFloat()

        val xMin = listaDatos.first().x
        val xMax = listaDatos.last().x
        val yMin = listaDatos.minOf { it.y }
        val yMax = listaDatos.maxOf { it.y }

        val rangoX = if (xMax - xMin == 0.0) 1.0 else xMax - xMin
        val rangoY = if (yMax - yMin == 0.0) 1.0 else yMax - yMin

        val escalaX = ancho / rangoX.toFloat()
        val escalaY = alto / rangoY.toFloat()

        // --- APLICAR TRANSFORMACIONES DE ZOOM Y PAN ---
        canvas.save()
        // Aplicamos el desplazamiento arrastrado por el usuario
        canvas.translate(desplazarX, desplazarY)
        // Escalamos tomando como punto de pivote el centro del Canvas
        canvas.scale(factorZoom, factorZoom, ancho / 2f, alto / 2f)

        // Coordenadas de los ejes
        val pixelEjeY = if (xMin <= 0 && xMax >= 0) ((0 - xMin) * escalaX).toFloat() else 50f
        val pixelEjeX = if (yMin <= 0 && yMax >= 0) (alto - (0 - yMin) * escalaY).toFloat() else alto - 50f

        // DIBUJAR LÍNEAS DE LOS EJES
        if (xMin <= 0 && xMax >= 0) canvas.drawLine(pixelEjeY, -alto * 5, pixelEjeY, alto * 5, pincelEjes)
        if (yMin <= 0 && yMax >= 0) canvas.drawLine(-ancho * 5, pixelEjeX, ancho * 5, pixelEjeX, pincelEjes)

        val divisiones = 9
        val espacioX = rangoX / divisiones
        val espacioY = rangoY / divisiones

        // ESCALA EJE X (Textos rotados)
        for (i in 0..divisiones) {
            val actualX = xMin + (i * espacioX)
            val pixelX = ((actualX - xMin) * escalaX).toFloat()
            val texto = String.format("%.2f", actualX)

            canvas.drawLine(pixelX, pixelEjeX - 10f, pixelX, pixelEjeX + 10f, pincelEjes)

            if (abs(actualX) > (rangoX / 20)) {
                canvas.save()
                canvas.translate(pixelX, pixelEjeX + 25f)
                canvas.rotate(-90f)
                pincelTexto.textAlign = Paint.Align.RIGHT
                // Compensamos el tamaño del texto para que no se deforme exageradamente con el zoom
                pincelTexto.textSize = 35f / factorZoom
                canvas.drawText(texto, 0f, 10f / factorZoom, pincelTexto)
                canvas.restore()
            }
        }

        // ESCALA EJE Y
        pincelTexto.textAlign = Paint.Align.LEFT
        for (i in 0..divisiones) {
            val actualY = yMin + (i * espacioY)
            val pixelY = (alto - (actualY - yMin) * escalaY).toFloat()
            val texto = String.format("%.2f", actualY)

            canvas.drawLine(pixelEjeY - 10f, pixelY, pixelEjeY + 10f, pixelY, pincelEjes)

            if (abs(actualY) > (rangoY / 20)) {
                pincelTexto.textSize = 35f / factorZoom
                canvas.drawText(texto, pixelEjeY + (20f / factorZoom), pixelY + (10f / factorZoom), pincelTexto)
            }
        }

        // DIBUJO DE LA LÍNEA DE LA FUNCIÓN
        for (i in 0 until listaDatos.size - 1) {
            val p1 = listaDatos[i]
            val p2 = listaDatos[i + 1]

            canvas.drawLine(
                ((p1.x - xMin) * escalaX).toFloat(),
                (alto - (p1.y - yMin) * escalaY).toFloat(),
                ((p2.x - xMin) * escalaX).toFloat(),
                (alto - (p2.y - yMin) * escalaY).toFloat(),
                pincelFuncion
            )
        }

        // Restauramos el estado original del lienzo
        canvas.restore()
    }
    // Método público para cuando el usuario grafique una nueva función
    fun setDatos(listaDatos: List<DatosXY>) {
        this.listaDatos = listaDatos
        // Resetear el zoom y desplazamiento cada vez que se calcula una gráfica nueva
        factorZoom = 1.0f
        desplazarX = 0.0f
        desplazarY = 0.0f
        invalidate()
    }
}