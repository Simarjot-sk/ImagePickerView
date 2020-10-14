package com.simarjot.mulltiple_image_picker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.simarjot.mulltiple_image_picker.utils.dpToPx
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import java.util.*

class ImagePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : HorizontalScrollView(context, attrs, defStyle) {

    companion object {
        const val DEFAULT_IMAGE_COUNT = 5
        const val IMAGE_PICKER_REQUEST_CODE = 445
    }

    private var maxSelectable = DEFAULT_IMAGE_COUNT
    private lateinit var linearLayout: LinearLayout
    private val _selectedUris = LinkedList<Uri>()
    val selectedUris
        get() = _selectedUris.toList()
    var addImagesButtonClickListener: ((ImagePickerView) -> Unit)? = null

    init {
        //inflate the layout
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_image_picker, this)

        //load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.ImagePickerView, defStyle, 0)
        maxSelectable = a.getInteger(R.styleable.ImagePickerView_image_count, DEFAULT_IMAGE_COUNT)
        a.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        linearLayout = findViewById(R.id.root_ll)
        val addImageButton = findViewById<ImageButton>(R.id.add_image_button)

        addImageButton.setOnClickListener {
            addImagesButtonClickListener?.invoke(this)
        }
    }

    fun startPickerActivity(fragment: Fragment, requestCode: Int = IMAGE_PICKER_REQUEST_CODE) {
        val selectableCount = maxSelectable - _selectedUris.size
        if (selectableCount > 0) {
            Matisse.from(fragment)
                .choose(MimeType.ofImage())
                .imageEngine(GlideEngine())
                .showPreview(false)
                .maxSelectable(selectableCount)
                .forResult(requestCode)
        } else {
            Toast.makeText(
                context,
                "You can only select up to $maxSelectable media files.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun onActivityResult(data: Intent) {
        val urlList = Matisse.obtainResult(data)
        urlList.forEach(this::addImageViewToLinearLayout)
    }

    private fun addImageViewToLinearLayout(uri: Uri) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, null, false)

        view.findViewById<ImageView>(R.id.image_view).apply {
            Glide.with(context).load(uri).override(150, 150).thumbnail(0.1f).into(this)
            tag = uri
        }

        view.findViewById<ImageView>(R.id.cross_icon).apply {
            setOnClickListener {
                linearLayout.removeView(view)
                _selectedUris.remove(uri)
            }
        }

        val size = context.dpToPx(100)
        val layoutParams = LinearLayout.LayoutParams(size, size).apply {
            val margin = context.dpToPx(10)
            setMargins(margin, margin, margin, margin)
        }

        linearLayout.addView(view, layoutParams)
        _selectedUris.add(uri)
    }
}
