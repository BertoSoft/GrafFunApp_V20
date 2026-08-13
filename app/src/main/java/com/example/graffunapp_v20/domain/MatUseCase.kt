package com.example.graffunapp_v20.domain

import com.example.graffunapp_v20.domain.models.DatosXY
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.tokenizer.FunctionToken
import kotlin.math.exp

class MatUseCase {

    fun getAllDatosUseCase(strFuncion: String, min: Double, max: Double): List<DatosXY>{
        val listaDatos = mutableListOf<DatosXY>()
        var xMax = max
        var xMin = min
        var isTrigonometrica = false
        val limiteYMaximo = 100.0 * (max - min)
        val limiteYMinimo = -100.0 * (max - min)

        try {
            val strFuncionLimpia = strFuncion.lowercase().replace(" ", "")
            var expresion = ExpressionBuilder(strFuncionLimpia)
                .variable("x")
                .build()

            isTrigonometrica = isTrigonometrica(expresion)
            if (isTrigonometrica){
                xMax = Math.toRadians(max)
                xMin = Math.toRadians(min)
            }

            // Mantengo tu excelente resolución de 1000 puntos para que la curva sea ultrasuave
            val paso = (xMax - xMin) / 1000
            var x = xMin

            // 3. Bucle de cálculo dinámico basado en la función del usuario
            while (x <= xMax) {
                // Asignamos el valor de X actual al motor matemático
                expresion.setVariable("x", x)

                // Calculamos el valor real de Y en base al string ingresado
                var y = expresion.evaluate()

                // MEJORA: Validamos que el resultado sea un número real válido (evita divisiones por 0 o infinitos)
                if (!y.isNaN() && !y.isInfinite()) {
                    // Si el valor se dispara por la asíntota, lo limitamos para que no rompa la escala del Visor
                    if (y > limiteYMaximo) y = limiteYMaximo
                    if (y < limiteYMinimo) y = limiteYMinimo
                    if(isTrigonometrica){
                        val xGrados = Math.toDegrees(x)
                        listaDatos.add(DatosXY(xGrados, y))
                    }
                    else{
                        listaDatos.add(DatosXY(x, y))
                    }
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

    private fun isTrigonometrica(expresion: Expression): Boolean{
        var isTrigonometrica = false
        val campos = expresion.javaClass.getDeclaredField("tokens")
        campos.isAccessible = true
        val tokens = campos.get(expresion) as Array<*>

        val funcionesTrig = setOf("sin", "cos", "tan")
        isTrigonometrica = tokens.any { token ->
            token is FunctionToken && funcionesTrig.contains(token.function.name)
        }
        return isTrigonometrica
    }
}