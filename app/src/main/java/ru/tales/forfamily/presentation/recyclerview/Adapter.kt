package ru.tales.forfamily.presentation.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.tales.forfamily.R
import ru.tales.forfamily.databinding.CardTaleBinding
import ru.tales.forfamily.domain.Tale


class Adapter : ListAdapter<Tale, Holder>(Callback()) {

    var click: ((Tale) -> Unit)? = null
    private val options = RequestOptions().error(R.drawable.ic_launcher_background)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            CardTaleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Holder(binding)
    }

    override fun onBindViewHolder(viewHolder: Holder, position: Int) {
        val item = getItem(position)

        with(viewHolder.binding) {
            tvName.text = item.name

            Glide.with(ivBackground).load(item.img).apply(options).into(ivBackground)

            if (item.isSaved){
                ivLoaded.setImageResource(R.drawable.ic_loaded)
            }else{
                ivLoaded.visibility = View.GONE
            }
            tvDuration.text = item.duration
            root.setOnClickListener {
                click?.invoke(item)
            }

        }
    }
}