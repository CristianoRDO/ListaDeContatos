package br.edu.ifsp.dmo.listadecontatos.view

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import br.edu.ifsp.dmo.listadecontatos.R
import br.edu.ifsp.dmo.listadecontatos.databinding.ActivityMainBinding
import br.edu.ifsp.dmo.listadecontatos.databinding.NewContactDialogBinding
import br.edu.ifsp.dmo.listadecontatos.model.Contact
import br.edu.ifsp.dmo.listadecontatos.model.ContactDao

class MainActivity : AppCompatActivity(), OnItemClickListener {

    private val TAG = "CONTACTS"
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ListContactAdapter
    private val listDataSource = ArrayList<Contact>()
    private var dialogName: String = ""
    private var dialogPhone: String = ""
    private var isDialogOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            dialogName = savedInstanceState.getString("dialogName", "")
            dialogPhone = savedInstanceState.getString("dialogPhone", "")

            if(savedInstanceState.getBoolean("isDialogOpen")) {
                handleNewContactDialog()  // Recria o diálogo com os valores salvos
            }
        }

        Log.v(TAG, "Executando o onCreate()")
        configClickListener()
        configListView()
    }

    override fun onStart() {
        Log.v(TAG, "Executando o onStart()")
        super.onStart()
    }

    override fun onResume() {
        Log.v(TAG, "Executando o onResume()")
        super.onResume()
    }

    override fun onPause() {
        Log.v(TAG, "Executando o onPause()")
        super.onPause()
    }

    override fun onStop() {
        Log.v(TAG, "Executando o onStop()")
        super.onStop()
    }

    override fun onRestart() {
        Log.v(TAG, "Executando o onRestart()")
        super.onRestart()
    }

    override fun onDestroy() {
        Log.v(TAG, "Executando o onDestroy()")
        Log.v(TAG, "Lista de contatos de que será perdida")
        for (contact in ContactDao.findAll()) {
            Log.v(TAG, contact.toString())
        }
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Salve o estado do diálogo e os textos dos campos que poderiam estar preenchidos.
        outState.putString("dialogName", dialogName)
        outState.putString("dialogPhone", dialogPhone)
        outState.putBoolean("isDialogOpen", isDialogOpen)
    }

    override fun onItemClick(parent: AdapterView<*>?, p1: View?, position: Int, id: Long) {
        val selectContact = binding.listviewContacts.adapter.getItem(position) as Contact
        val uri = "tel:${selectContact.phone}"
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse(uri)
        startActivity(intent)
    }

    private fun configClickListener() {
        binding.buttonNewContact.setOnClickListener {
            handleNewContactDialog()
        }
    }

    private fun configListView() {
        listDataSource.addAll(ContactDao.findAll())
        adapter = ListContactAdapter(this, listDataSource)
        binding.listviewContacts.adapter = adapter
        binding.listviewContacts.onItemClickListener = this
    }

    private fun updateListDataSource() {
        listDataSource.clear();
        listDataSource.addAll(ContactDao.findAll())
        adapter.notifyDataSetChanged()
    }

    private fun handleNewContactDialog() {
        val bindingDialog = NewContactDialogBinding.inflate(layoutInflater)

        // Adicionado "escutador" aos campos de texto da caixa de diálogo para registrar alterações de conteúdo.
        bindingDialog.edittextName.addTextChangedListener {
            dialogName = bindingDialog.edittextName.text.toString()
        }

        bindingDialog.edittextPhone.addTextChangedListener {
            dialogPhone = bindingDialog.edittextPhone.text.toString()
        }

        // Se os dados de contato foram restaurados, os campos serão preenchidos com eles.
        bindingDialog.edittextName.setText(dialogName)
        bindingDialog.edittextPhone.setText(dialogPhone)

        val builderDialog = AlertDialog.Builder(this)
        builderDialog.setView(bindingDialog.root)
            .setTitle(R.string.new_contact)
            .setPositiveButton(
                R.string.btn_dialog_save,
                DialogInterface.OnClickListener{dialog, which ->
                    Log.v(TAG, "Salvar Contato")
                    ContactDao.insert(
                        Contact(
                            bindingDialog.edittextName.text.toString(),
                            bindingDialog.edittextPhone.text.toString()
                        )
                    )
                    updateListDataSource()
                    clearDialogState()
                    dialog.dismiss()
                })
            .setNegativeButton(
                R.string.btn_dialog_cancel,
                DialogInterface.OnClickListener {dialog, which ->
                    Log.v(TAG, "Cancelar novo Contato")
                    clearDialogState()
                    dialog.cancel()
                })
            .setOnDismissListener{ // Adicionado "escutador" ao dialog para verificar se foi fechado.
                if(isDialogOpen) { // Se for true, significa que o diálogo foi fechado acidentalmente (clicando fora), então os dados são recuperados
                    dialogName = bindingDialog.edittextName.text.toString()
                    dialogPhone = bindingDialog.edittextPhone.text.toString()
                    isDialogOpen = false
                }
            }
        builderDialog.create().show()

        isDialogOpen = true  // Define o estado do diálogo como aberto
    }

    private fun clearDialogState() {
        isDialogOpen = false
        dialogName = ""
        dialogPhone = ""
    }

}
