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
        val adapter = ResultAdapter(results) { item ->
            val (image, searchText, resultText) = item

            // 새로운 액티비티로 데이터 전달
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
}



class ResultAdapter(
    private val data: List<Triple<ByteArray?, String, String>>,
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

        // 텍스트 설정
        holder.textSearch.text = searchText
        holder.textResult.text = resultText

        // 이미지가 있는 경우 설정
        if (image != null) {
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            holder.imageView.setImageBitmap(bitmap)
            holder.imageView.visibility = View.VISIBLE // 이미지 표시
        } else {
            holder.imageView.visibility = View.GONE // 이미지 숨김
        }

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = data.size
}


