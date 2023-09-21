package com.example.testeplugpag

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import br.com.uol.pagseguro.plugpagservice.wrapper.IPlugPagWrapper
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagActivationData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagCustomPrinterLayout
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventListener
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagStyleData
import br.com.uol.pagseguro.plugpagservice.wrapper.exception.PlugPagException
import com.example.testeplugpag.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            val plugPag: IPlugPagWrapper = PlugPag(applicationContext)

            Log.i("TESTE", "Log imediatamente antes da chamada plugPag.isAuthenticated()")
            // Verifica se existe um usuário autenticado.
            try {
                if (plugPag.isAuthenticated()) {
                    // Existe um usuário autenticado.
                    Log.i("TESTE", "Autenticado")
                } else {
                    // Não existe um usuário autenticado.
                    Log.e("TESTE", "Não autenticado")
                }
            } catch (e: PlugPagException) {
                // Erro durante a validação.
                Log.e("TESTE", e.toString())
            }

            // Define os métodos a serem chamados quando existem novos eventos.
            val eventListener = object : PlugPagEventListener {
                override fun onEvent(data: PlugPagEventData) {
                    // Ação a ser executada quando um evento for disparado.
                    Log.i("TESTE", data.toString())
                }
            }

            plugPag.setEventListener(
                listener = eventListener
            )

            // Executa uma solicitação de definição de cores a serem usadas no design das telas fornecidas
            // pela PlugPagService.
            val styleData = PlugPagStyleData(
                headTextColor = 0x1,
                headBackgroundColor = 0xE13C70,
                contentTextColor = 0xDFDFE0,
                contentTextValue1Color = 0xFFE000,
                contentTextValue2Color = 0x100000,
                positiveButtonTextColor = 0x1,
                positiveButtonBackground = 0xFF358C,
                negativeButtonTextColor = 0x777778,
                negativeButtonBackground = 0x00FFFFFF,
                genericButtonBackground = 0x1,
                genericButtonTextColor = 0xFF358C,
                genericSmsEditTextBackground = 0x1,
                genericSmsEditTextTextColor = 0xFF358C,
                lineColor = 0x1000000,
            )

            try {
                if (plugPag.setStyleData(styleData = styleData)) {
                    // Cores definidas com sucesso.
                } else {
                    // Falha na definição de cores.
                }
            } catch (e: PlugPagException) {
                // Erro na definição de cores
            }

            // Executa a customização dos elementos da tela de impressão da via do cliente.
            val layout = PlugPagCustomPrinterLayout(
                title = "Título",
                titleColor = 0xFFE000.toString(),
                confirmTextColor = 0x1.toString(),
                cancelTextColor = 0x777778.toString(),
                windowBackgroundColor = 0xE13C70.toString(),
                buttonBackgroundColor = 0x1.toString(),
                buttonBackgroundColorDisabled = 0x1.toString(),
                sendSMSTextColor = 0xFFE000.toString(),
                maxTimeShowPopup = 10,
            )

            plugPag.setPlugPagCustomPrinterLayout(plugPagCustomPrinterLayout = layout)

            // Executa a solicitação de pagamento.
            val paymentData = PlugPagPaymentData(
                type = PlugPag.TYPE_DEBITO,
                amount = 2000, // R$ 20,00
                installmentType = PlugPag.INSTALLMENT_TYPE_A_VISTA,
                installments = PlugPag.A_VISTA_INSTALLMENT_QUANTITY,
                userReference = "Código da Venda",
                printReceipt = false,
                partialPay = false,
                isCarne = false,
            )
            val plugPagTransactionResult = plugPag.doPayment(paymentData = paymentData)

            if (plugPagTransactionResult.result == PlugPag.RET_OK) {
                // Venda efetuada com sucesso.
                Log.i("TESTE", plugPagTransactionResult.toString())
            } else {
                // Falha durante a venda.
                Log.e("TESTE", plugPagTransactionResult.toString())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    return navController.navigateUp(appBarConfiguration)
            || super.onSupportNavigateUp()
    }
}