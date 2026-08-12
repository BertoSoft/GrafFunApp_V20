package com.example.graffunapp_v20.ui.home
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.graffunapp_v20.databinding.ActivityMainBinding
import com.example.graffunapp_v20.domain.MatUseCase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        initListeners()
    }
    //##############################################################
    // Funciones de Inicializacion
    //###############################################################
    private fun initListeners() {

        // Pulsacion Tecla ENTER
        binding.etFuncion.pulsacionTeclaEnter{
            etFuncionKeyEnter(binding.etFuncion.text.toString().trim())
        }
        binding.etLimiteInferior.pulsacionTeclaEnter {
            etLimiteInferiorKeyEnter()
        }
        binding.etLimiteSuperior.pulsacionTeclaEnter {
            etLimiteSuperiorKeyEnter()
        }

        //Click
        binding.btnGraficar.setOnClickListener {
            btnGraficaClick()
        }
    }
    private fun initUi() {
        binding.etLimiteInferior.isEnabled = false
        binding.etLimiteSuperior.isEnabled = false
        binding.btnGraficar.isEnabled = false
        binding.etFuncion.requestFocus()
    }

    //######################################################
    // Lógica de Negocio / Validaciones
    //######################################################
    private fun etFuncionKeyEnter(strFuncion: String) {
        if (strFuncion.isEmpty()) {
            mostrarMensaje("Debes de especificar una funcion")
            return
        }

        binding.etLimiteInferior.isEnabled = true
        binding.etLimiteSuperior.isEnabled = true
        binding.etLimiteInferior.requestFocus()
    }
    private fun etLimiteInferiorKeyEnter() {
        val str = binding.etLimiteInferior.text.toString()
        if (str.isEmpty()) {
            mostrarMensaje("Debes de especificar un límite inferior")
            return
        }
        binding.etLimiteSuperior.requestFocus()
    }
    private fun etLimiteSuperiorKeyEnter() {
        val strFuncion = binding.etFuncion.text.toString().trim()
        val dLimiteInferior = binding.etLimiteInferior.text.toString().toDoubleOrNull()
        val dLimiteSuperior = binding.etLimiteSuperior.text.toString().toDoubleOrNull()

        // MEJORA: Validaciones en cascada limpias usando cláusulas de guarda
        if (strFuncion.isEmpty()) {
            mostrarErrorEditText(binding.etFuncion, "Debes de especificar una funcion")
            return
        }
        if (dLimiteInferior == null) {
            mostrarErrorEditText(binding.etLimiteInferior, "Debes de especificar un límite inferior")
            return
        }
        if (dLimiteSuperior == null) {
            mostrarErrorEditText(binding.etLimiteSuperior, "Debes de especificar un límite superior")
            return
        }
        if (dLimiteInferior >= dLimiteSuperior) {
            mostrarErrorEditText(binding.etLimiteInferior, "El límite inferior tiene que ser menor que el límite superior")
            return
        }

        // --- TODO CORRECTO ---
        ocultarTeclado()
        if(!binding.btnGraficar.isEnabled){
            binding.btnGraficar.isEnabled = true
        }
        binding.btnGraficar.performClick()
    }
    private fun btnGraficaClick(){
        val listaDatos = MatUseCase().getAllDatos(
            binding.etFuncion.text.toString().trim(),
            binding.etLimiteInferior.text.toString().toDouble(),
            binding.etLimiteSuperior.text.toString().toDouble()
        )

        if(listaDatos.isEmpty()){
            mostrarMensaje("Lista Vacia")
        }
    }

    //######################################################
    // Funciones Auxiliares (Helpers)
    //######################################################
    private fun EditText.pulsacionTeclaEnter(teclaEnterPulsada: () -> Unit){
        this.setOnEditorActionListener { _, actionId, ev ->
            val esAccionIme = actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_SEARCH

            val esEnterFisico = ev != null &&
                    ev.keyCode == KeyEvent.KEYCODE_ENTER &&
                    ev.action == KeyEvent.ACTION_DOWN

            if (esAccionIme || esEnterFisico) {
                teclaEnterPulsada()
                true
            } else {
                false
            }
        }
    }
    private fun mostrarErrorEditText(editText: EditText, mensaje: String) {
        mostrarMensaje(mensaje)
        editText.requestFocus()
    }
    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
    private fun ocultarTeclado() {
        currentFocus?.let { vista ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(vista.windowToken, 0)
        }
    }
}