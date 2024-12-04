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
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log

class ImageActivity : AppCompatActivity()
{
    lateinit var binding : ActivityImageBinding
    lateinit var cameraLauncher : ActivityResultLauncher<Intent>
    lateinit var galleryLauncher : ActivityResultLauncher<Intent>
    val CAMERA_PERMISSION_CODE = 100
    val GALLERY_PERMISSION_CODE = 200
    var imageUri : Uri? = null
    var isImageOnCamera = 0 // 이미지 소스 여부 (0 : 카메라, 1 : 갤러리)

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
                isImageOnCamera = 0
            }
        }

        galleryLauncher = registerForActivityResult(contract1)
        {
            if (it?.resultCode == RESULT_OK && it.data != null)
            {
                imageUri = it.data?.data
                imageUri?.let { uri ->
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // API 28 이상
                        val source = ImageDecoder.createSource(contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    } else { // API 27 이하
                        MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    }
                    binding.image.setImageBitmap(bitmap)
                }
                isImageOnCamera = 1
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

        // 갤러리 실행
        binding.buttonSelectImage.setOnClickListener {
            if (isGalleryPermissionGranted()) // 갤러리(외부 저장소) 접근 권한 승인이 되면
            {
                launchGallery() // 갤러리 앱 실행
            }
            else
            {
                requestGalleryPermission() // 아니면 갤러리 권한 승인 요청
            }
        }

        // 이미지를 결과 액티비티로 전송
        binding.buttonTransferImage.setOnClickListener {
            if (isImageOnCamera == 0) // 카메라로 찍은 이미지일 경우
            {
                val bitmap = getImageFromImageView()
                if (bitmap != null)
                {
                    val base64 = encodeBitmapToBase64(bitmap)
                    val intent = Intent(this, ImageResultActivity::class.java)
                    intent.putExtra("imageBase64", base64)
                    startActivity(intent)
                }
            }
            else // 갤러리에서 선택한 이미지일 경우
            {
                val intent = Intent(this, ImageResultActivity::class.java)
                intent.putExtra("imageUri", imageUri.toString())
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

    // 갤러리 실행
    private fun launchGallery() {
        val newIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        galleryLauncher.launch(newIntent)
    }

    // 갤러리 권한 요청 확인
    private fun isGalleryPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) // API 33 이상
        {
            return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        }
        else // API 32 이하
        {
            return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 갤러리 권한 요청
    private fun requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) // API 33 이상
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                GALLERY_PERMISSION_CODE
            )
        }
        else // API 32 이하
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                GALLERY_PERMISSION_CODE
            )
        }
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
        if (requestCode == CAMERA_PERMISSION_CODE) { // 카메라 권한 요청 처리
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
        else if (requestCode == GALLERY_PERMISSION_CODE) { // 갤러리(외부 저장소) 권한 요청 처리
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Gallery permission granted", Toast.LENGTH_SHORT).show()
                launchGallery()
            } else {
                Toast.makeText(
                    this,
                    "Gallery permission is required to use this feature",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}