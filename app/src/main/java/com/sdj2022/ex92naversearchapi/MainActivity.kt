package com.sdj2022.ex92naversearchapi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.sdj2022.ex92naversearchapi.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity() {

    val binding:ActivityMainBinding by lazy {ActivityMainBinding.inflate(layoutInflater)}

    var apiResponse:NaverSearchResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btn.setOnClickListener { searchItems() }
    }

    fun searchItems(){

        // 기존에 있던 데이터클래스의 데이터들 초기화**
        apiResponse?.items?.clear()

        // 검색버튼 누르면 키보드 사라지게
        val imm:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        // Naver 검색 API 를 사용하여 검색어의 쇼핑결과를 받아오기

        // Retrofit 을 이용하여 HTTP 통신시작
        val retrofit:Retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/v1/search/shop.json/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val retrofitService = retrofit.create(NaverApiRetrofitService::class.java)


//        val call:Call<String> = retrofitService.searchDataToString(binding.et.text.toString(), "5H0rKGox40W4dzHyutlR", "3MlesIkxEO")
//        call.enqueue(object:Callback<String>{
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                val s:String? = response.body()
//                Log.i("TAG", s.toString())
//                AlertDialog.Builder(this@MainActivity).setMessage(s).show()
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Toast.makeText(this@MainActivity, "error : ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })


        retrofitService.searchData(binding.et.text.toString()).enqueue(object : Callback<NaverSearchResponse>{
            override fun onResponse(
                call: Call<NaverSearchResponse>,
                response: Response<NaverSearchResponse>
            ) {
                apiResponse = response.body()
                // apply, let 등은 null 이 아닐때만 작동함 (if문과 비슷한 효과)
                apiResponse?.items?.let { binding.recyclerView.adapter = NaverSearchAdapter(this@MainActivity, it) }
            }

            override fun onFailure(call: Call<NaverSearchResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "error : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}