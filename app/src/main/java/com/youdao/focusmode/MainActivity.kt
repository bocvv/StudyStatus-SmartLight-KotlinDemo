package com.youdao.focusmode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.youdao.ocr.AladdinStudyStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File

class MainActivity : AppCompatActivity() {

    private val channel = Channel<ByteArray>(capacity = Channel.CONFLATED)
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isStop = false
    private var isFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bt0).setOnClickListener {
            Log.d("bowei", "stop1")
            isStop = true
            /*
            while(true) {
                if (isFinished) {
                    break
                }
            }
             */
            Log.d("bowei", "stop2")
            // isStop = false
            // isFinished = false
            AladdinStudyStatus.release()
            Log.d("bowei", "stop3")
        }

        AladdinStudyStatus.init("/system/etc/youdao_resource/focus_mode/data/study_status.cfg", true, true, 1536, 1536)
        startImageReader()
    }

    fun startImageReader() {
        recognize()
        findViewById<CameraKitView>(R.id.cameraView).startNV21ImageReader(object :
                CameraKitView.FrameCallback {
            override fun onFrame(frame: ByteArray?) {
                //Log.d("lijiwei", "onFrame: ++++++++++++++++")
                frame?.let {
                    runBlocking {
                        channel.send(it)
                    }
                }
            }
        })
    }

    private fun recognize() {
        val fileDir = "/sdcard/focus_mode/"
        val freqFilePath = fileDir + "temp_log.txt"
        val file = File(freqFilePath)
        if (!file.exists()) {
            file.parentFile.mkdirs()
        }

        scope.launch {
            while (true) {
                channel.receive().apply {
                    val startTime = System.currentTimeMillis()

                    /*
                    var picData = PicUtil.rotateYUV420Degree270(this, 1536, 1536)
                    picData = PicUtil.NV21MirrorFlip(picData, 1536, 1536)

                    try {
                        val image = YuvImage(picData, ImageFormat.NV21, 1536, 1536, null)
                        val outputSteam = ByteArrayOutputStream()
                        image.compressToJpeg(Rect(0, 0, image.width, image.height), 70, outputSteam) // 将NV21格式图片，以质量70压缩成Jpeg，并得到JPEG数据流
                        val jpegData = outputSteam.toByteArray() //从outputSteam得到byte数据
                        val file = File(getDir("image", Context.MODE_PRIVATE), "camera1_" + System.currentTimeMillis() + ".jpg")
                        val os: OutputStream = FileOutputStream(file)
                        os.write(jpegData)
                        os.flush()


                        os.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    */

                    /*
                    val result = StudyStatus.getStudyStatusYUV(this, 1536, 1536)
                    val endTime = System.currentTimeMillis()
                    Log.d("lijiwei", "recognize: getStudyStatusYUV TIME IS ${endTime - startTime} ms")
                    var backStr = " "
                    var headStr = " "
                    var actionStr = " "
                    withContext(Dispatchers.Main) {
                        val stringArray = result?.map { it.toString() }?.toTypedArray()
                        val back = result?.get(0)
                        val head = result?.get(1)
                        val action = result?.get(2)
                        when (back) {
                            0 -> backStr = "upright"
                            1 -> backStr = "hump"
                            2 -> backStr = "other_sitting_posture"
                        }
                        when (head) {
                            0 -> headStr = "up_head"
                            1 -> headStr = "tilt_head"
                            2 -> headStr = "hold_head"
                            3 -> headStr = "grovel"
                            4 -> headStr = "other_head_posture"
                        }
                        when (action) {
                            0 -> actionStr = "learn"
                            1 -> actionStr = "look_other"
                            2 -> actionStr = "computer"
                            3 -> actionStr = "phone"
                            4 -> actionStr = "face_down"
                            5 -> actionStr = "nobody"
                            6 -> actionStr = "other_action_posture"
                        }
                        findViewById<TextView>(R.id.tvOcrText).text = backStr + "\n" + headStr + "\n" + actionStr

                     */

                    if (!isStop) {
                        val result = AladdinStudyStatus.getSmartLightResultYUV(this, 1536, 1536)
                        val statusResult = AladdinStudyStatus.getStudyStatusYUV(this, 1536, 1536)
                        val shadingScore = AladdinStudyStatus.getShadingScore()
                        val endTime = System.currentTimeMillis()
                        Log.d(
                            "lijiwei",
                            "recognize: getStudyStatusYUV TIME IS ${endTime - startTime} ms"
                        )
                        var backStr = " "
                        var headStr = " "
                        var actionStr = " "
                        var smartStr = " "
                        var shadingStr = " "
                        var shadingMean = shadingScore?.get(0)
                        var shadingStddev = shadingScore?.get(1)
                        withContext(Dispatchers.Main) {
                            when (result) {
                                0 -> smartStr = "textbook"
                                1 -> smartStr = "cartoon"
                                2 -> smartStr = "online_class"
                                3 -> smartStr = "relax"
                                4 -> smartStr = "ignore"
                            }

                            val stringArray = statusResult?.map { it.toString() }?.toTypedArray()
                            val back = statusResult?.get(0)
                            val head = statusResult?.get(1)
                            val action = statusResult?.get(2)
                            val shading = statusResult?.get(3)
                            when (back) {
                                0 -> backStr = "upright"
                                1 -> backStr = "hump"
                                2 -> backStr = "other_sitting_posture"
                            }
                            when (head) {
                                0 -> headStr = "up_head"
                                1 -> headStr = "tilt_head"
                                2 -> headStr = "hold_head"
                                3 -> headStr = "grovel"
                                4 -> headStr = "other_head_posture"
                            }
                            when (action) {
                                0 -> actionStr = "learn"
                                1 -> actionStr = "look_around"
                                2 -> actionStr = "pad"
                                3 -> actionStr = "computer"
                                4 -> actionStr = "phone"
                                5 -> actionStr = "playing"
                                6 -> actionStr = "eating_drinking"
                                7 -> actionStr = "sleep"
                                8 -> actionStr = "nobody"
                            }
                            when (shading) {
                                0 -> shadingStr = "no shading"
                                1 -> shadingStr = "shading"
                            }
                            findViewById<TextView>(R.id.tvOcrText).text =
                                backStr + "\n" + headStr + "\n" + actionStr + "\n\n" + smartStr + "\n\n" + shadingStr + "\n" + "mean = " + shadingMean + "\n" + "stddev = " + shadingStddev
                        }
                    } else {
                        isFinished = true
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<CameraKitView>(R.id.cameraView).cameraStart()
    }

    override fun onPause() {
        super.onPause()
        findViewById<CameraKitView>(R.id.cameraView).cameraStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        channel.close()
        AladdinStudyStatus.release()
    }

}