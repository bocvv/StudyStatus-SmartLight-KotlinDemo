package com.youdao.focusmode

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.wonderkiln.camerakit.CameraKit
import com.wonderkiln.camerakit.CameraView

class CameraKitView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var view: View? = null
    private var cameraView: CameraView? = null

    init {
        view = LayoutInflater.from(context).inflate(R.layout.base_layout_camera, this, true)
        cameraView = view?.findViewById(R.id.cameraKitView)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CameraKitView)
        val facing =
            typedArray.getInteger(R.styleable.CameraKitView_cameraFacing, COMMON_DEFAULT_VALUE)
        val previewWidth = typedArray.getInteger(
            R.styleable.CameraKitView_cameraPreviewWidth,
            COMMON_DEFAULT_VALUE
        )
        val previewHeight = typedArray.getInteger(
            R.styleable.CameraKitView_cameraPreviewHeight,
            COMMON_DEFAULT_VALUE
        )
        val captureWidth = typedArray.getInteger(
            R.styleable.CameraKitView_cameraCaptureWidth,
            COMMON_DEFAULT_VALUE
        )
        val captureHeight = typedArray.getInteger(
            R.styleable.CameraKitView_cameraCaptureHeight,
            COMMON_DEFAULT_VALUE
        )
        if (facing != COMMON_DEFAULT_VALUE) {
            setFacing(
                if (facing == 0) {
                    Facing.BACK
                } else {
                    Facing.FRONT
                }
            )
        }
        if (previewWidth != COMMON_DEFAULT_VALUE && previewHeight != COMMON_DEFAULT_VALUE) {
            setPreviewSize(previewWidth, previewHeight)
        }
        if (captureWidth != COMMON_DEFAULT_VALUE && captureHeight != COMMON_DEFAULT_VALUE) {
            setCaptureSize(captureWidth, captureHeight)
        }
        typedArray.recycle()
    }

    /**
     * 启动相机
     */
    fun cameraStart() {
        cameraView?.start()
    }

    /**
     * 关闭相机
     */
    fun cameraStop() {
        cameraView?.stop()
    }

    /**
     * 拍照
     */
    fun takePic(imageCallback: ImageCallback?) {
        cameraView?.captureImage {
            imageCallback?.onImage(it.jpeg)
        }
    }

    /**
     * 获取实时帧数据（NV21格式。 ps：默认格式）
     */
    fun startNV21ImageReader(frameCallback: FrameCallback?) {
        cameraView?.setPreviewCallback { data, _ ->
            frameCallback?.onFrame(data)
        }
    }

    /**
     * 获取实时帧数据（JPEG格式）
     */
    fun startJpegImageReader(frameCallback: FrameCallback?) {
        cameraView?.setJpegPreviewCallback { data, _ ->
            frameCallback?.onFrame(data)
        }
    }

    /**
     * 切换摄像头
     */
    fun toggleFacing() {
        cameraView?.toggleFacing()
    }

    /**
     * 设置使用的摄像头
     */
    fun setFacing(facing: Facing) {
        cameraView?.facing = if (facing == Facing.FRONT) {
            CameraKit.Constants.FACING_FRONT
        } else {
            CameraKit.Constants.FACING_BACK
        }
    }

    /**
     * 设置预览尺寸
     */
    fun setPreviewSize(width: Int, height: Int) {
        cameraView?.previewWidth = width
        cameraView?.previewHeight = height
    }

    /**
     * 设置拍照尺寸
     */
    fun setCaptureSize(width: Int, height: Int) {
        cameraView?.captureWidth = width
        cameraView?.captureHeight = height
    }

    interface ImageCallback {
        fun onImage(image: ByteArray)
    }

    interface FrameCallback {
        fun onFrame(frame: ByteArray?)
    }

    interface PreviewListener {
        fun onStart()
        fun onStop()
    }

    enum class Facing {
        FRONT, BACK
    }

    companion object {
        const val COMMON_DEFAULT_VALUE = -1
    }
}