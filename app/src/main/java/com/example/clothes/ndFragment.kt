package com.example.clothes


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clothes.ndSonActivity.ndSonAboutActivity
import com.example.clothes.ndSonActivity.ndSonSettingsActivity


class ndFragment : Fragment() {

    companion object {
        fun newInstance() = ndFragment()
    }

    private val optionList = ArrayList<ndFragmentOption>()

    private lateinit var viewModel: NdViewModel

    class MyLinearLayoutManager : LinearLayoutManager {
        private var isScrollEnabled = true

        constructor(context: Context?) : super(context) {}
        /*调用这个方法控制滑动*/
        fun setScrollEnabled(flag: Boolean) {
            isScrollEnabled = flag
        }

        override fun canScrollVertically(): Boolean {
            return isScrollEnabled && super.canScrollVertically()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val newView : View = inflater.inflate(R.layout.nd_fragment, container, false)
        initRecyclerView(newView)
        return newView
    }

    private fun initRecyclerView(newView : View) {
        initOptions()
        val layoutManager = MyLinearLayoutManager(this.activity).apply {
            setScrollEnabled(false)
        }  //使RecyclerView不可滑动

        val ndFragmentrecyclerView : RecyclerView = newView.findViewById(R.id.ndFragmentRecyclerView)
        ndFragmentrecyclerView.addItemDecoration(
            DividerItemDecoration(
                this.activity,
                DividerItemDecoration.VERTICAL
            )
        )
        ndFragmentrecyclerView.layoutManager = layoutManager
        val adapter = ndFragmentAdapter(optionList)
        ndFragmentrecyclerView.adapter = adapter
    }

    private fun initOptions() {
        optionList.add(
            ndFragmentOption(
                "设置", R.drawable.icon_setting, Intent(
                    activity,
                    ndSonSettingsActivity::class.java
                )
            )
        )
        optionList.add(
            ndFragmentOption(
                "关于", R.drawable.icon_information, Intent(
                    activity,
                    ndSonAboutActivity::class.java
                )
            )
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NdViewModel::class.java)
        // TODO: Use the ViewModel
    }
}




class ndFragmentOption(val name: String, val imageId: Int, val intent: Intent)

class ndFragmentAdapter(val optionList: List<ndFragmentOption>) :
    RecyclerView.Adapter<ndFragmentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val optionImage: ImageView = view.findViewById(R.id.optionImage)
        val optionText: TextView = view.findViewById(R.id.optionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.nd_fragment_options, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val option = optionList[position]
            startActivity(parent.context, option.intent, null)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = optionList[position]
        holder.optionImage.setImageResource(option.imageId)
        holder.optionText.text = option.name
    }

    override fun getItemCount(): Int = optionList.size

}