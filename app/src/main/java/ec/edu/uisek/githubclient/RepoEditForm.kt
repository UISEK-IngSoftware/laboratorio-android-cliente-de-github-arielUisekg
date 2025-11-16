package ec.edu.uisek.githubclient

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoEditForm : AppCompatActivity() {

    private var currentRepoOwner: String? = null
    private var currentRepoName: String? = null

    private lateinit var repoDescriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private val apiService: GithubApiService by lazy {
        RetrofitClient.gitHubApiService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_edit_form)

        // Inicializar vistas
        repoDescriptionEditText = findViewById(R.id.edit_repo_description_input)
        saveButton = findViewById(R.id.edit_save_button)
        cancelButton = findViewById(R.id.edit_cancel_button)

        // conseguir data de intent
        currentRepoOwner = intent.getStringExtra("REPO_OWNER")
        currentRepoName = intent.getStringExtra("REPO_NAME")
        val repoDescription = intent.getStringExtra("REPO_DESCRIPTION")


        repoDescriptionEditText.setText(repoDescription)

        // listeners de clic
        saveButton.setOnClickListener {
            saveChanges()
        }

        cancelButton.setOnClickListener {
            finish() // Cerrar actividad
        }
    }

    private fun saveChanges() {
        val owner = currentRepoOwner
        val repoName = currentRepoName
        val newDescription = repoDescriptionEditText.text.toString()

        if (owner == null || repoName == null) {
            showMessage("Error: No se pudo identificar el repositorio.")
            return
        }

        val repoRequest = RepoRequest(name = repoName, description = newDescription)
        val call = apiService.updateRepo(owner, repoName, repoRequest)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio actualizado con éxito")
                    finish()
                } else {
                    showMessage("Error al actualizar: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showMessage("Fallo en la conexión: ${t.message}")
            }
        })
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
