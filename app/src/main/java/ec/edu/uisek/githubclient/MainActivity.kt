package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog // <-- IMPORTADO
import androidx.appcompat.app.AppCompatActivity


import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter
    private val apiService: GithubApiService by lazy { // <-- API Service
        RetrofitClient.gitHubApiService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        binding.newRepoFab.setOnClickListener {
            displayNewRepoFform()
        }

    }
    override fun onResume() {
        super.onResume()
        fetchRepositorios()
    }

    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter()

        // manejar edit
        reposAdapter.onEditClick = { repo ->
            val intent = Intent(this, RepoEditForm::class.java).apply {
                putExtra("REPO_OWNER", repo.owner.login)
                putExtra("REPO_NAME", repo.name)
                putExtra("REPO_DESCRIPTION", repo.description)
            }
            startActivity(intent)
        }

        // manejar el delete
        reposAdapter.onDeleteClick = { repo ->
            showDeleteConfirmationDialog(repo) // <-- LLAMADA A LA NUEVA FUNCIÓN
        }

        binding.reposRecyclerView.adapter = reposAdapter
    }

    private fun showDeleteConfirmationDialog(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar el repositorio '${repo.name}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteRepository(repo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteRepository(repo: Repo) {
        val call = apiService.deleteRepo(repo.owner.login, repo.name)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio '${repo.name}' eliminado con éxito")
                    fetchRepositorios() // Refresh the list
                } else {
                    showMessage("Error al eliminar: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Fallo en la conexión: ${t.message}")
            }
        })
    }

    private fun fetchRepositorios(){
        val call = apiService.getRepos()
        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if(response.isSuccessful){
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()){
                        reposAdapter.updateRepositorios(repos)
                    }
                    else {
                        showMessage("No se encontraron repositorios")
                    }
                }else{
                    val errorMessage = when(response.code()){
                        401 -> "No autorizado"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error ${response.code()}"
                    }
                    showMessage("Error: $errorMessage")

                }


            }
            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("No se pudieron cargar lo repositorios")

            }
        })
    }
    private fun showMessage (message:String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }
    private fun displayNewRepoFform(){
        Intent(this, RepoForm::class.java).apply {
            startActivity(this)
        }
    }
}
