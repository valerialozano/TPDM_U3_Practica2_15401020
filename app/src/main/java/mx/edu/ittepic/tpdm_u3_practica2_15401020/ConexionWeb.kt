package mx.edu.ittepic.tpdm_u3_practica2_15401020

import android.app.ProgressDialog
import android.os.AsyncTask
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ConexionWeb(p:MainActivity) : AsyncTask<URL, Void, String>(){

    var puntero = p
    var variablesEnvio =  ArrayList<String>()
    var dialogo = ProgressDialog(puntero)

    override fun onPreExecute() {
        super.onPreExecute()
        dialogo.setTitle("Atención")
        dialogo.setMessage("Conectando con el servidor...")
        dialogo.show()
    }

    fun agregarVariablesEnvio(clave:String, valor:String){
        var cad = clave+"&"+valor
        variablesEnvio.add(cad)
    }

    override fun doInBackground(vararg params: URL?): String {
        var respuesta =""
        var cadenaEnvioPost =""

        //crear cadena de clave valor para post
        var total = variablesEnvio.size-1
        (0..total).forEach {
            try {
                var data = variablesEnvio.get(it).split("&")
                cadenaEnvioPost += data[0] + "=" + URLEncoder.encode(data[1], "utf-8") + " "

            }catch(err: UnsupportedEncodingException){
                respuesta = "No se pudo codificar URL"
            }
        }
        cadenaEnvioPost = cadenaEnvioPost.trim() //quitar espacios al inicio y al final de la cadena
        cadenaEnvioPost = cadenaEnvioPost.replace(" ", "&")
        println(cadenaEnvioPost)

        var conexion : HttpURLConnection ?= null
        try{
            //amarrar conexión con servidor web/lenguaje web
            conexion = params[0]?.openConnection() as HttpURLConnection
            conexion?.doOutput = true
            conexion?.setFixedLengthStreamingMode(cadenaEnvioPost.length)
            conexion?.requestMethod = "POST"
            conexion?.setRequestProperty("Content-Type","application/x-www-form-urlencoded")

            //envio variables ya codificadas
            var salida = BufferedOutputStream(conexion?.outputStream)
            salida.write(cadenaEnvioPost.toByteArray())
            salida.flush()
            salida.close()

            if(conexion?.responseCode==200){
                var flujoEntrada = InputStreamReader(conexion?.inputStream,"utf-8")
                var entrada = BufferedReader(flujoEntrada)

                // respuesta = entrada.readLine()
                respuesta = """${entrada.readLine()}"""
                entrada.close()
            }else
            {
                respuesta = "ERROR "+conexion?.responseCode
            }
        }catch(err: IOException){
            respuesta = "Error IOException"
        }finally {
            if(conexion!=null){
                conexion?.disconnect()
            }
        }

        return respuesta
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        dialogo.dismiss()
        puntero.mostrarResultado(result!!)
    }
}