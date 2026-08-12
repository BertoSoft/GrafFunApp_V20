package com.example.graffunapp_v20.ui.home
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.graffunapp_v20.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        initListeners()
        initObservers()
    }

    //##############################################################
    // Funciones de Inicializacion
    //###############################################################
    private fun initUi() {
        with(binding){
            etLimiteInferior.isEnabled = false
            etLimiteSuperior.isEnabled = false
            btnGraficar.isEnabled = false
            etFuncion.requestFocus()
        }
    }
    private fun initListeners() {
        with(binding){
            etFuncion.pulsacionTeclaEnter{
                etFuncionKeyEnter(etFuncion.text.toString().trim())
            }
            etLimiteInferior.pulsacionTeclaEnter {
                etLimiteInferiorKeyEnter()
            }
            etLimiteSuperior.pulsacionTeclaEnter {
                etLimiteSuperiorKeyEnter()
            }
            btnGraficar.setOnClickListener {
                btnGraficaClick()
            }
        }
    }
    private fun initObservers() {
        homeViewModel.listaDatos.observe(this){ listaDatosNueva ->
            if(!listaDatosNueva.isNullOrEmpty()) {
                binding.viewGrafica.setDatos(listaDatosNueva)
            }
        }
    }

    //######################################################
    // Lógica de Negocio / Validaciones
    //######################################################
    private fun etFuncionKeyEnter(strFuncion: String) {
        if (strFuncion.isEmpty()) {
            mostrarErrorEditText(binding.etFuncion, "Debes de especificar una funcion")
            return
        }
        with(binding){
            etLimiteInferior.isEnabled = true
            etLimiteSuperior.isEnabled = true
            etLimiteInferior.requestFocus()
        }
    }
    private fun etLimiteInferiorKeyEnter() {
        val str = binding.etLimiteInferior.text.toString()
        if (str.isEmpty()) {
            mostrarErrorEditText(binding.etLimiteInferior, "Debes de especificar un límite inferior")
            return
        }
        binding.etLimiteSuperior.requestFocus()
    }
    private fun etLimiteSuperiorKeyEnter() {
        if(validarEditText()){
            // --- TODO CORRECTO ---
            ocultarTeclado()
            with(binding){
                btnGraficar.isEnabled = true
                btnGraficar.performClick()
            }
        }
    }
    private fun btnGraficaClick(){
        if(validarEditText()){
            homeViewModel.setDatos(
                binding.etFuncion.text.toString().trim(),
                binding.etLimiteInferior.text.toString().toDouble(),
                binding.etLimiteSuperior.text.toString().toDouble()
            )
        }
    }
    private fun validarEditText(): Boolean{
        val strFuncion = binding.etFuncion.text.toString().trim()
        val dLimiteInferior = binding.etLimiteInferior.text.toString().toDoubleOrNull()
        val dLimiteSuperior = binding.etLimiteSuperior.text.toString().toDoubleOrNull()
        if (strFuncion.isEmpty()) {
            mostrarErrorEditText(binding.etFuncion, "Debes de especificar una funcion")
            return false
        }
        if (dLimiteInferior == null) {
            mostrarErrorEditText(binding.etLimiteInferior, "Debes de especificar un límite inferior")
            return false
        }
        if (dLimiteSuperior == null) {
            mostrarErrorEditText(binding.etLimiteSuperior, "Debes de especificar un límite superior")
            return false
        }
        if (dLimiteInferior >= dLimiteSuperior) {
            mostrarErrorEditText(binding.etLimiteInferior, "El límite inferior tiene que ser menor que el límite superior")
            return false
        }
        return true
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
        setMensaje(mensaje)
        editText.requestFocus()
    }
    private fun setMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
    private fun ocultarTeclado() {
        currentFocus?.let { vista ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(vista.windowToken, 0)
        }
    }
}