package id.overlogic.storify.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.overlogic.storify.R
import id.overlogic.storify.data.source.remote.response.ListStoryItem
import id.overlogic.storify.databinding.ItemStoryBinding
import id.overlogic.storify.ui.detail.DetailActivity
import id.overlogic.storify.util.DateFormatter
import java.util.*


class StoryAdapter :
    ListAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val anime = getItem(position)
        holder.bind(anime)
    }


    class MyViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name

            binding.cvStory.setOnClickListener {
                val intentDetail = Intent(it.context, DetailActivity::class.java)
                intentDetail.putExtra(DetailActivity.EXTRA_ID, story.id)
                it.context.startActivity(intentDetail)
            }
            val readableDate = DateFormatter(story.createdAt ?: "").format()

            binding.tvDate.text = readableDate

            binding.tvDescription.text = story.description


            Glide.with(itemView.context)
                .load(story.photoUrl)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error)
                )
                .into(binding.ivItemPhoto)
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListStoryItem> =
            object : DiffUtil.ItemCallback<ListStoryItem>() {
                override fun areItemsTheSame(
                    oldItem: ListStoryItem,
                    newItem: ListStoryItem
                ): Boolean {
                    return oldItem.name == newItem.name
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: ListStoryItem,
                    newItem: ListStoryItem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}