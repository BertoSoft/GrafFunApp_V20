package com.example.graffunapp_v20.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.graffunapp_v20.domain.MatUseCase
import com.example.graffunapp_v20.domain.models.DatosXY

class HomeViewModel: ViewModel() {

    private val matUseCase = MatUseCase()
    private val _listaDatos = MutableLiveData<List<DatosXY>>()
    val listaDatos: LiveData<List<DatosXY>> get() = _listaDatos

    fun setDatos(strFuncion: String, xMin: Double, xMax: Double){
        val resultado = matUseCase.getAllDatosUseCase(strFuncion, xMin, xMax)
        _listaDatos.value = resultado
    }

}