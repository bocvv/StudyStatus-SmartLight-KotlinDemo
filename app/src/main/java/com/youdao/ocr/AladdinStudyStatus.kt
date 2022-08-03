package com.youdao.ocr

import android.util.Log

object AladdinStudyStatus {

    init {
        System.loadLibrary("study_status")
    }

    external fun init(configPath: String?, useSmartLight: Boolean, useDVT2: Boolean, width: Int, height: Int): Boolean
    external fun getStudyStatusYUV(data: ByteArray, width: Int, height: Int): IntArray?
    external fun getCurrStudyStatusResult(): IntArray?
    external fun getShadingScore(): FloatArray?
    external fun getSmartLightResultYUV(data: ByteArray, width: Int, height: Int): Int
    external fun release(): Boolean
}
