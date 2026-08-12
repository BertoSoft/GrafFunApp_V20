package com.example.graffunapp_v20.domain

import com.example.graffunapp_v20.domain.models.DatosXY
import net.objecthunter.exp4j.ExpressionBuilder

class MatUseCase {

    fun getAllDatosUseCase(strFuncion: String, xMin: Double, xMax: Double): List<DatosXY>{
        val listaDatos = mutableListOf<DatosXY>()

        try {
            // 2. Configuramos el motor matemático indicando que la variable del texto será "x"
            val expresion = ExpressionBuilder(strFuncion)
                .variable("x")
                .build()

            // Mantengo tu excelente resolución de 1000 puntos para que la curva sea ultrasuave
            val paso = (xMax - xMin) / 1000
            var x = xMin

            // 3. Bucle de cálculo dinámico basado en la función del usuario
            while (x <= xMax) {
                // Asignamos el valor de X actual al motor matemático
                expresion.setVariable("x", x)

                // Calculamos el valor real de Y en base al string ingresado
                val y = expresion.evaluate()

                // MEJORA: Validamos que el resultado sea un número real válido (evita divisiones por 0 o infinitos)
                if (!y.isNaN() && !y.isInfinite()) {
                    listaDatos.add(DatosXY(x, y))
                }
                x += paso
            }
        } catch (e: Exception) {
            // Si el usuario escribe algo inválido sintácticamente, atrapamos el error para evitar un Crash
            e.printStackTrace()
            return emptyList()
        }
        return listaDatos
    }
}