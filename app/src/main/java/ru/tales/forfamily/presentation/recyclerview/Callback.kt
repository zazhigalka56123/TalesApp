package ru.tales.forfamily.presentation.recyclerview

import androidx.recyclerview.widget.DiffUtil
import ru.tales.forfamily.domain.Tale

class Callback : DiffUtil.ItemCallback<Tale>() {

    override fun areItemsTheSame(p1: Tale, p2: Tale) =
        p1.name == p2.name

    override fun areContentsTheSame(p1: Tale, p2: Tale) = p1 == p2
}