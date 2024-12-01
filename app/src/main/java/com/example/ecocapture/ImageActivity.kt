package com.example.ecocapture

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ecocapture.databinding.ActivityImageBinding
import java.io.ByteArrayOutputStream
import android.Manifest

class ImageActivity : AppCompatActivity()
{
    lateinit var binding : ActivityImageBinding
    lateinit var cameraLauncher : ActivityResultLauncher<Intent>
    var CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val contract1 = ActivityResultContracts.StartActivityForResult()
        cameraLauncher = registerForActivityResult(contract1)
        {
            if (it?.resultCode == RESULT_OK)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                {
                    val bitmap = it.data?.getParcelableExtra("data", Bitmap::class.java)!!
                    binding.image.setImageBitmap(bitmap)
                }
                else
                {
                    val bitmap = it.data?.getParcelableExtra<Bitmap>("data")!!
                    binding.image.setImageBitmap(bitmap)
                }
            }
        }

        initEvents()
    }

    // 이벤트 초기화
    private fun initEvents()
    {
        // 카메라 앱 실행
        binding.buttonUploadImage.setOnClickListener {
            if (isCameraPermissionGranted()) // 카메라 권한 승인이 되면
            {
                launchCamera() // 카메라 앱 실행
            }
            else
            {
                requestCameraPermission() // 아니면 카메라 권한 승인 요청
            }
        }

        // 이미지를 결과 액티비티로 전송
        binding.buttonTransferImage.setOnClickListener {
            val bitmap = getImageFromImageView()
            if (bitmap != null)
            {
                val base64 = encodeBitmapToBase64(bitmap)
                val intent = Intent(this, ImageResultActivity::class.java)
                intent.putExtra("imageBase64", base64)
                startActivity(intent)
            }
        }
    }

    // 이미지 가져오기
    private fun getImageFromImageView(): Bitmap?
    {
        val drawable = binding.image.drawable
        return if (drawable is BitmapDrawable)
        {
            (drawable as BitmapDrawable).bitmap
        }
        else
        {
            null
        }
    }

    // Bitmap 이미지를 Base64 형식으로 변환
    private fun encodeBitmapToBase64(bitmap: Bitmap): String
    {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // 카메라 실행
    private fun launchCamera() {
        val newIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(newIntent)
    }

    // 카메라 권한 요청 확인
    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 카메라 권한 요청
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
                launchCamera()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required to use this feature",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}