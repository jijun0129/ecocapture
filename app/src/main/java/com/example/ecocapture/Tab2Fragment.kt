package com.example.ecocapture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Tab2Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tab2, container, false)

        // RecyclerView 초기화
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 데이터베이스에서 데이터 가져오기
        val dbHelper = MyDatabaseHelper(requireContext())
        val results = dbHelper.getAllResults()

        // 어댑터 연결
        val adapter = ResultAdapter(results)
        recyclerView.adapter = adapter

        return view
    }
}

class ResultAdapter(private val data: List<Pair<String, String>>) :
    RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textSearch: TextView = itemView.findViewById(R.id.textSearch)
        val textResult: TextView = itemView.findViewById(R.id.textResult)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (searchText, resultText) = data[position]
        holder.textSearch.text = searchText
        holder.textResult.text = resultText
    }

    override fun getItemCount(): Int = data.size
}
