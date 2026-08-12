package com.example.graffunapp_v20.ui.grafica

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.graffunapp_v20.domain.models.DatosXY

class VisorGrafica @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    private var listaDatos: List<DatosXY> = emptyList()
    private val pincelFuncion = Paint().apply {
        color           = Color.parseColor("#F63D03")
        strokeWidth     = 6f
        style           = Paint.Style.STROKE
        isAntiAlias     = true
    }
    private val pincelEjes = Paint().apply {
        color           = Color.BLACK
        strokeWidth     = 2f
        style           = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)

        if(listaDatos.size < 2) return

        val ancho = canvas.width.toFloat()
        val alto = canvas.height.toFloat()

        val xMin = listaDatos.first().x
        val xMax = listaDatos.last().x
        val yMin = listaDatos.minOf { it.y }
        val yMax = listaDatos.maxOf { it.y }

        // calcular los rangos matematicos, evitando ceros
        val rangoX = if(xMax - xMin == 0.0) 1.0 else xMax - xMin
        val rangoY = if(yMax - yMin == 0.0) 1.0 else yMax - yMin

        // 5. Calcular los factores de escala (Píxeles por unidad matemática)
        val escalaX = ancho / rangoX.toFloat()
        val escalaY = alto / rangoY.toFloat()

        // 6. DIBUJAR LOS EJES CARTESIANOS (Opcional, si caen dentro del rango visible)
        // Eje Y matemático (donde X = 0)
        if (xMin <= 0 && xMax >= 0) {
            val pixelEjeY = ((0 - xMin) * escalaX).toFloat()
            canvas.drawLine(pixelEjeY, 0f, pixelEjeY, alto, pincelEjes)
        }
        // Eje X matemático (donde Y = 0)
        if (yMin <= 0 && yMax >= 0) {
            val pixelEjeX = (alto - (0 - yMin) * escalaY).toFloat()
            canvas.drawLine(0f, pixelEjeX, ancho, pixelEjeX, pincelEjes)
        }

        // 7. BUCLE DE DIBUJO DE LA GRÁFICA
        // Recorremos los puntos uniendo el actual con el siguiente mediante una línea
        for (i in 0 until listaDatos.size - 1) {
            val pActual = listaDatos[i]
            val pSiguiente = listaDatos[i + 1]

            // Convertimos las coordenadas matemáticas del punto ACTUAL a píxeles
            val x1 = ((pActual.x - xMin) * escalaX).toFloat()
            val y1 = (alto - (pActual.y - yMin) * escalaY).toFloat()

            // Convertimos las coordenadas matemáticas del punto SIGUIENTE a píxeles
            val x2 = ((pSiguiente.x - xMin) * escalaX).toFloat()
            val y2 = (alto - (pSiguiente.y - yMin) * escalaY).toFloat()

            // Dibujamos el segmento de recta en el Canvas
            canvas.drawLine(x1, y1, x2, y2, pincelFuncion)
        }
    }

    fun setDatos(listaDatos: List<DatosXY>){
        this.listaDatos = listaDatos
        invalidate()
    }
}