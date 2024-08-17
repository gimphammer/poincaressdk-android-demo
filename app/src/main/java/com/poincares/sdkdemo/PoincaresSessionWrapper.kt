/**
 * @file PoincaresSessionWrapper.kt
 * @author wuliang (wuliang@poincares.com)
 * @brief
 * @version 0.1
 * @date 2024-07-26
 *
 * @copyright Copyright (c) 2024 @poincares.com
 *
 */

package com.poincares.sdkdemo

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.poincares.sdk.NDAppTag
import com.poincares.sdk.NDOperationObserver
import com.poincares.sdk.NDOperationResult
import com.poincares.sdk.NDTaskDescriptionBase
import com.poincares.sdk.NDTaskDescriptionHttp
import com.poincares.sdk.NDTaskDescriptionMtr
import com.poincares.sdk.NDTaskDescriptionPing
import com.poincares.sdk.NDTaskDescriptionTcpPing
import com.poincares.sdk.PoincaresFactory
import com.poincares.sdk.PoincaresSession


@Suppress("SpellCheckingInspection")
class PoincaresSessionWrapper{
    private lateinit var mainActivity: Context
    private var isInitOK : Boolean = false
    private var sessionStarted : Boolean = false
    private var session : PoincaresSession? = null

    private var appKey : String    = "to apply on www.poincares.com"
    private var appSecret : String = "to apply on www.poincares.com"
    private var schedulingServerUrl : String = "http://8.149.142.50:81/config/center/info"
    private var appTag : NDAppTag = NDAppTag()

    private class TaskDescRecord(initId : Long, initVer : Int) {
        public var id : Long = initId
        public var version : Int = initVer
    }

    private var pingRecord : TaskDescRecord = TaskDescRecord(0, 0)
    private var httpRecord : TaskDescRecord = TaskDescRecord(1000, 1000)
    private var tcpPingRecord : TaskDescRecord = TaskDescRecord(2000, 2000)
    private var mtrRecord : TaskDescRecord = TaskDescRecord(3000, 3000)

    private var strBuilder : StringBuilder = StringBuilder()

    // Handler定义
    private val handler: Handler = Handler(Looper.getMainLooper())

    private fun handleOperationResult(opResult: NDOperationResult) {

        strBuilder.clear()
        strBuilder.append("opType=").append(opResult.type)
            .append(", err=0x").append(Integer.toHexString(opResult.errCode))

        if (null != opResult.extraMsg && !opResult.extraMsg.isEmpty())
            strBuilder.append(", extraMsg=").append(opResult.extraMsg)

        if (PoincaresSession.NDOperationType.kOperationTaskAdd  == opResult.type) {
            strBuilder.append(", taskType=").append(opResult.type)
                .append("taskID=").append(opResult.taskId)
                .append("taskVersion=").append(opResult.taskVersion)
        }

        val resultStr = strBuilder.toString()
        Toast.makeText(mainActivity, resultStr, Toast.LENGTH_SHORT).show()

//        if (needToStopSession(opResult.errCode, opResult.type)) {
//            stopSession()
//        }
    }

    private fun needToStopSession(error: Int, opType : PoincaresSession.NDOperationType ): Boolean {
        var isKeyOperation = false
        isKeyOperation = (PoincaresSession.NDOperationType.kOperationSchedulingRequest == opType
                || PoincaresSession.NDOperationType.kOperationNDTaskDispatchRequest == opType
                || PoincaresSession.NDOperationType.kOperationDataReport == opType)

        if (0!= error && isKeyOperation) {
            return true
        }
        return false
    }

    private val operationObserver = object : NDOperationObserver {
        override fun onOperationEnd(opResult: NDOperationResult) {
            var opResultCopy = NDOperationResult(opResult)
            //don't block the JNI thred. post to UI thread to do the job much more overloaded
            handler.post {
                handleOperationResult(opResultCopy)
            }
        }
    }


    fun init(activity: Context) {
        appTag.version = "1.0.0"
        appTag.id   = "PoincarsSDKDemo_android_id#1"
        appTag.name = "PoincarsSDKDemo_android"
        appTag.versionCode = "1.0.0"

        session = PoincaresFactory.createSesion()
        val res = session?.init(appKey, appSecret, appTag, schedulingServerUrl, operationObserver, activity)
        if (null != res && 0 == res)
            isInitOK = true

        mainActivity = activity
    }

    fun uninit() {
        val res = session?.uninit()
    }

    fun isSessionStarted(): Boolean {
        return sessionStarted
    }

    fun startBySvConfig() : Boolean {
        if (!isInitOK) {
            Toast.makeText(mainActivity, "session init failed, can't start", Toast.LENGTH_SHORT).show()
            return false
        }

        val res = session!!.start()
        if (0 == res) {
            sessionStarted = true
            return true
        }

        return false
    }



    fun stopSession() {
        if (!isInitOK || null == session) {
            Toast.makeText(mainActivity, "session init failed, no stop!", Toast.LENGTH_SHORT).show()
            return
        }

        val res = session!!.stop();
        if (0 == res) {
            sessionStarted = false
            Toast.makeText(mainActivity, "session stopped", Toast.LENGTH_SHORT).show()
        }
        return;
    }

    fun addPing(url: String, count: Int ) {
        if (!isInitOK) {
            Toast.makeText(mainActivity, "check init first: can't add Ping!", Toast.LENGTH_SHORT).show()
            return
        }

        if (url.isEmpty() || null == session) {
            Toast.makeText(mainActivity, "addPing: url is empty or session is null!", Toast.LENGTH_SHORT).show()
            return
        }

        val pingDesc = NDTaskDescriptionPing()
        pingDesc.packetSize = 64
        pingDesc.packetNum  = 30
        pingDesc.perTimeout = 1000
        pingDesc.perInterval= 1000
        pingDesc.id = pingRecord.id
        pingDesc.version = pingRecord.version++
        pingDesc.host = url
        pingDesc.jobCount = NDTaskDescriptionBase.kCyclicMode
        pingDesc.jobInterval = 1000 * 60 //one minute


        val res = session!!.addTask(pingDesc)
        Toast.makeText(mainActivity, "Ping-Task add, res=$res", Toast.LENGTH_SHORT).show()
    }

    fun addHttp(url: String, count: Int ) {
        if (!isInitOK) {
            Toast.makeText(mainActivity, "check init() first: can't add Http!", Toast.LENGTH_SHORT).show()
            return
        }

        if (url.isEmpty() || null == session) {
            Toast.makeText(mainActivity, "addHttp: url is empty or session is null!", Toast.LENGTH_SHORT).show()
            return
        }

        val httpDesc = NDTaskDescriptionHttp()
        httpDesc.link = url;
//        httpDesc.port = 80;
//        val res = session?.
        httpDesc.protocolVersion = 2
        httpDesc.perInterval = 10000
        httpDesc.id = httpRecord.id
        httpDesc.version = httpRecord.version++

        httpDesc.jobInterval = 10000
        httpDesc.jobCount = NDTaskDescriptionBase.kCyclicMode

        val res = session!!.addTask(httpDesc)

        Toast.makeText(mainActivity, "Http-Task add, res=$res", Toast.LENGTH_SHORT).show()
    }

    fun addTcpPing(url: String, count: Int ) {
        if (!isInitOK) {
            Toast.makeText(mainActivity, "check init() first: can't add TcpPing!", Toast.LENGTH_SHORT).show()
            return
        }

        if (url.isEmpty() || null == session) {
            Toast.makeText(mainActivity, "addTcpPing: url is empty or session is null!", Toast.LENGTH_SHORT).show()
            return
        }

        val tcpPing = NDTaskDescriptionTcpPing()
        tcpPing.port = 80
        tcpPing.packetNum  = 1
        tcpPing.perTimeout = 1000*10
        tcpPing.perInterval= 1000

        tcpPing.id = tcpPingRecord.id
        tcpPing.version = tcpPingRecord.version++
        tcpPing.host = url
        tcpPing.jobInterval = 1000*30
        tcpPing.jobCount = 50  //any positive number or NDTaskDescriptionBase.kCyclicMode

        val res = session!!.addTask(tcpPing)
        Toast.makeText(mainActivity, "TcpPing-Task add res=$res", Toast.LENGTH_SHORT).show()
    }


    fun addMtr(url: String, count: Int ) {
        if (!isInitOK) {
            Toast.makeText(mainActivity, "session init failed, can't addMtr!", Toast.LENGTH_SHORT).show()
            return
        }

        if (url.isEmpty() || null == session) {
            Toast.makeText(mainActivity, "addMtr: url is empty or session is null!", Toast.LENGTH_SHORT).show()
            return
        }

        val mtr = NDTaskDescriptionMtr()
        mtr.packetSize = 6
        mtr.packetNum  = 30
        mtr.perTimeout = 1000
        mtr.perInterval = 1000
        mtr.id = mtrRecord.id
        mtr.version = mtrRecord.version++
        mtr.host = url
        mtr.jobCount = 80 //any positive number or NDTaskDescriptionBase.kCyclicMode
        mtr.jobInterval = 1000*60

        val res = session!!.addTask(mtr)

        Toast.makeText(mainActivity, "Mtr-Task add=$res", Toast.LENGTH_SHORT).show()
    }


}


