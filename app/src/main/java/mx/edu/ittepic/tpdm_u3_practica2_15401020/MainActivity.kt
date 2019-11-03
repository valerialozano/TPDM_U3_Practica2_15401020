package mx.edu.ittepic.tpdm_u3_practica2_15401020

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.net.URL

class MainActivity : AppCompatActivity() {

    var descripcion : EditText ?= null
    var monto : EditText ?= null
    var fecha : EditText ?= null
    var pagado : CheckBox ?= null
    var insertar : Button ?= null
    var mostrarJSON : Button ?=null
    var buscarPago : Button ?= null
    var etiqueta : TextView ?= null
    var id : EditText ?= null
    var jsonRespuesta = ArrayList<org.json.JSONObject>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        descripcion = findViewById(R.id.descripcion)
        monto = findViewById(R.id.monto)
        fecha = findViewById(R.id.fecha)
        pagado = findViewById(R.id.pagado)
        insertar = findViewById(R.id.insertar)
        mostrarJSON = findViewById(R.id.mostrarJson)
        buscarPago = findViewById(R.id.buscar)
        etiqueta = findViewById(R.id.etiqueta)

        var p =0
        pagado?.setOnCheckedChangeListener { buttonView, isChecked ->
            if(pagado?.isChecked==true){
                p=1
            }else{
                p=0
            }
        }

        insertar?.setOnClickListener {
            var conexionWeb = ConexionWeb(this)
            conexionWeb.agregarVariablesEnvio("descripcion",descripcion?.text.toString())
            conexionWeb.agregarVariablesEnvio("monto", monto?.text.toString())
            conexionWeb.agregarVariablesEnvio("fecha", fecha?.text.toString())
            conexionWeb.agregarVariablesEnvio("pagado",p.toString())
            conexionWeb.execute(URL("https://tpdmtec2019agbase1.herokuapp.com/insertarReciboPago.php"))

        }
        mostrarJSON?.setOnClickListener {
            var conexionWeb = ConexionWeb(this)
            conexionWeb.execute(URL("https://tpdmtec2019agbase1.herokuapp.com/consultarPagos.php"))

        }
        buscarPago?.setOnClickListener {
            val posicion = id?.text.toString().toInt()
            val jsonObjeto = jsonRespuesta.get(posicion)
            etiqueta?.setText("Descripcion: "+jsonObjeto.getString("descripcion")+"\n"+
                    "Monto: "+jsonObjeto.getString("monto")+"\n"+
                    "Fecha: "+jsonObjeto.getString("fechaVencimiento")+"\n"+
                    "Pagado: "+jsonObjeto.getString("pagado"))
        }
    }

    fun mostrarResultado(result:String){
        var alerta = AlertDialog.Builder(this)
        alerta.setTitle("Respuesta del servidor").setMessage(result).setPositiveButton("Aceptar"){dialog,which->}.show()

        val jsonarray = org.json.JSONArray(result)
        var total = jsonarray.length()-1
        (0..total).forEach {
            jsonRespuesta.add(jsonarray.getJSONObject(it))
        }

        etiqueta?.setText(result)

    }
}
