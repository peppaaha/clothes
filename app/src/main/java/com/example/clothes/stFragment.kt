package com.example.clothes

import android.content.Intent
import android.media.Image
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.clothes.stSonActivity.stSonClothesDetailActivity


class stFragment : Fragment() {

    companion object {
        fun newInstance() = stFragment()
    }

    private val clothesList = ArrayList<stFragmentClothes>()

    private lateinit var viewModel: StViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val newView : View = inflater.inflate(R.layout.st_fragment, container, false)
        initClothes()
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val stFragmentrecyclerView : RecyclerView = newView.findViewById(R.id.stFragmentRecyclerView)
        stFragmentrecyclerView.layoutManager = layoutManager
        val adapter = stFragmentAdapter(clothesList)
        stFragmentrecyclerView.adapter = adapter
        return newView
    }

    private fun initClothes() {
        repeat(100) {
            clothesList.add(stFragmentClothes("衣物1", R.drawable.ic_launcher_background))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StViewModel::class.java)
        // TODO: Use the ViewModel
    }

}

class stFragmentClothes(val name: String, val imageId: Int)

class stFragmentAdapter(val clothesList: List<stFragmentClothes>) :
    RecyclerView.Adapter<stFragmentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clothesText: TextView = view.findViewById(R.id.clothesText)
        val clothesImage : ImageView = view.findViewById(R.id.clothesImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.st_fragment_clothes, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val clothes = clothesList[position]
            val intent = Intent(parent.context, stSonClothesDetailActivity::class.java)
            startActivity(parent.context, intent, null)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothes = clothesList[position]
        holder.clothesImage.setImageResource(clothes.imageId)
        holder.clothesText.text = clothes.name
    }

    override fun getItemCount(): Int = clothesList.size

}
