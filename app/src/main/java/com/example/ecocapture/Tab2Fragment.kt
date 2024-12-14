package com.example.ecocapture

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Tab2Fragment : Fragment() {

    private lateinit var adapter: ResultAdapter
    private lateinit var dbHelper: MyDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tab2, container, false)

        // RecyclerView 초기화
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 데이터베이스 초기화
        dbHelper = MyDatabaseHelper(requireContext())
        val results = dbHelper.getAllResults()

        // 어댑터 연결
        adapter = ResultAdapter(results) { item ->
            val (image, searchText, resultText) = item

            val intent = Intent(requireContext(), LogDetailActivity::class.java).apply {
                putExtra("image", image)
                putExtra("searchText", searchText)
                putExtra("resultText", resultText)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()

        // 데이터베이스에서 최신 데이터를 가져와 어댑터 갱신
        val updatedResults = dbHelper.getAllResults()
        adapter.updateData(updatedResults)
    }
}



class ResultAdapter(
    private var data: List<Triple<ByteArray?, String, String>>,
    private val onItemClick: (Triple<ByteArray?, String, String>) -> Unit
) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textSearch: TextView = itemView.findViewById(R.id.textSearch)
        val textResult: TextView = itemView.findViewById(R.id.textResult)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val (image, searchText, resultText) = item

        holder.textSearch.text = searchText
        holder.textResult.text = resultText

        if (image != null) {
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            holder.imageView.setImageBitmap(bitmap)
            holder.imageView.visibility = View.VISIBLE
        } else {
            holder.imageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = data.size

    // 데이터 갱신 메서드 추가
    fun updateData(newData: List<Triple<ByteArray?, String, String>>) {
        this.data = newData
        notifyDataSetChanged()
    }
}



