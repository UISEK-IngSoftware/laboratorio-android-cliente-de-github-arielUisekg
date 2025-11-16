package ec.edu.uisek.githubclient


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

class ReposViewHolder(val binding: FragmentRepoItemBinding) : // binding publico
    RecyclerView.ViewHolder(binding.root) {
    fun bind(repo: Repo) {
        binding.repoName.text = repo.name
        binding.repoDescription.text = repo.description
        binding.repoLang.text = if (repo.language.isNullOrBlank()) "No especificado" else repo.language // <-- CAMBIADO
        Glide.with(binding.root.context)
            .load(repo.owner.avatarUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .circleCrop()
            .into(binding.reportOwnerImage)
    }
}

class ReposAdapter: RecyclerView.Adapter<ReposViewHolder>() {
    private var repositorios: List<Repo> = emptyList()

    // Lambdas para eventos de clic
    var onEditClick: ((Repo) -> Unit)? = null
    var onDeleteClick: ((Repo) -> Unit)? = null

    override fun getItemCount(): Int = repositorios.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReposViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        val repo = repositorios[position]
        holder.bind(repo)

        holder.binding.editRepoButton.setOnClickListener {
            onEditClick?.invoke(repo)
        }

        holder.binding.deleteRepoButton.setOnClickListener {
            onDeleteClick?.invoke(repo)
        }
    }

    fun updateRepositorios(newRepositorios: List<Repo>) {
        repositorios = newRepositorios
        notifyDataSetChanged()
    }
}
