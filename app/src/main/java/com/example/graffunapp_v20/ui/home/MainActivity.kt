package com.example.graffunapp_v20.ui.home
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.graffunapp_v20.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        initListeners()
    }

    private fun initListeners() {

        // Pulsacion Tecla ENTER
        binding.etFuncion.pulsacionTeclaEnter{

        }
    }

    //##############################################################
    // Funciones de Inicializacion
    //###############################################################
    private fun initUi() {
        binding.etLimiteInferior.isEnabled = false
        binding.etLimiteSuperior.isEnabled = false
        binding.btnGraficar.isEnabled = false
        binding.etFuncion.requestFocus()
    }

    //######################################################
    // Funciones Auxiliares (Helpers)
    //######################################################

    // MEJORA: Función de extensión para eliminar código duplicado de los listeners
    private fun EditText.pulsacionTeclaEnter(onEnterPressed: () -> Unit){
        this.setOnEditorActionListener { _, actionId, ev ->
            val esAccionIme = actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_SEARCH

            val esEnterFisico = ev != null &&
                    ev.keyCode == KeyEvent.KEYCODE_ENTER &&
                    ev.action == KeyEvent.ACTION_DOWN

            if (esAccionIme || esEnterFisico) {
                onEnterPressed()
                true
            } else {
                false
            }
        }
    }
}