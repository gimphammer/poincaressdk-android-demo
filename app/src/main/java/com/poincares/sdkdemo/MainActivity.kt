/**
 * @file MainActivity.kt
 * @author wuliang (wuliang@poincares.com)
 * @brief
 * @version 0.1
 * @date 2024-07-26
 *
 * @copyright Copyright (c) 2024 @poincares.com
 *
 */
package com.poincares.sdkdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity(), View.OnClickListener,
                     PoincaresSessionWrapper.UIStatusSwitcher
{
    @Suppress("SpellCheckingInspection")
    private lateinit var poincaresWrapper: PoincaresSessionWrapper

    private lateinit var btnStartBySvConfig: Button
    private lateinit var btnAddPing:    Button
    private lateinit var btnAddHttp:    Button
    private lateinit var btnAddTcpPing: Button
    private lateinit var btnAddMtr:     Button


    private lateinit var etPingUrl:      EditText
    private lateinit var etPingCount:    EditText
    private lateinit var etHttpUrl:      EditText
    private lateinit var etHttpCount:    EditText
    private lateinit var etTcpPingUrl:   EditText
    private lateinit var etTcpPingCount: EditText
    private lateinit var etMtrUrl:       EditText
    private lateinit var etMtrCount:     EditText

    companion object {
        init {
            System.loadLibrary("poincaressdk") // 确保库名和你的实际库名匹配
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initPoincaresSDK()
        initWidgets()
    }

    override fun onDestroy() {
        uninitPoincaresSDK()
        super.onDestroy()
    }


    @SuppressLint("SetTextI18n")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnStartBySvConfig -> {
                if(!poincaresWrapper.isSessionStarted()
                    && poincaresWrapper.startBySvConfig())
                {
                    switchToStarted()
                }
                else if(poincaresWrapper.isSessionStarted() ) {
                    poincaresWrapper.stopSession()
                    switchToStopped()
                }
            }

            R.id.btnAddPing -> {
                poincaresWrapper.addPing(etPingUrl.text.toString(),
                                         etPingCount.text.toString().toInt())
                btnAddPing.isEnabled = false
            }

            R.id.btnAddHttp -> {
                poincaresWrapper.addHttp(etHttpUrl.text.toString(),
                                         etHttpCount.text.toString().toInt())
                btnAddHttp.isEnabled = false
            }

            R.id.btnAddTcpPing -> {
                poincaresWrapper.addTcpPing(etTcpPingUrl.text.toString(),
                                            etTcpPingCount.text.toString().toInt())
                btnAddTcpPing.isEnabled = false
            }

            R.id.btnAddMtr -> {
                poincaresWrapper.addMtr(etMtrUrl.text.toString(),
                                        etMtrCount.text.toString().toInt())
                btnAddMtr.isEnabled = false
            }
        }
    }


    private fun initWidgets() {
        etPingUrl   = findViewById(R.id.etPingUrl)
        etPingCount = findViewById(R.id.etPingCount)

        etHttpUrl   = findViewById(R.id.etHttpUrl)
        etHttpCount = findViewById(R.id.etHttpCount)

        etTcpPingUrl   = findViewById(R.id.etTcpPingUrl)
        etTcpPingCount = findViewById(R.id.etTcpPingCount)

        etMtrUrl   = findViewById(R.id.etMtrUrl)
        etMtrCount = findViewById(R.id.etMtrCount)

        btnStartBySvConfig = findViewById(R.id.btnStartBySvConfig)
        btnStartBySvConfig.setOnClickListener(this)

        btnAddPing = findViewById(R.id.btnAddPing)
        btnAddPing.setOnClickListener(this)
        btnAddPing.isEnabled = false

        btnAddHttp = findViewById(R.id.btnAddHttp)
        btnAddHttp.setOnClickListener(this)
        btnAddHttp.isEnabled = false

        btnAddTcpPing = findViewById(R.id.btnAddTcpPing)
        btnAddTcpPing.setOnClickListener(this)
        btnAddTcpPing.isEnabled = false

        btnAddMtr = findViewById(R.id.btnAddMtr)
        btnAddMtr.setOnClickListener(this)
        btnAddMtr.isEnabled = false
    }

    private fun initPoincaresSDK() {
        poincaresWrapper = PoincaresSessionWrapper()
        poincaresWrapper.init(this, this)
    }

    private fun uninitPoincaresSDK() {
        poincaresWrapper.uninit()
    }


    override fun switchToStarted() {
        btnAddPing.isEnabled = true
        btnAddHttp.isEnabled = true
        btnAddTcpPing.isEnabled = true
        btnAddMtr.isEnabled = true

        btnStartBySvConfig.text = "Stop Session"
    }

    override fun switchToStopped() {

        btnAddPing.isEnabled = false
        btnAddHttp.isEnabled = false
        btnAddTcpPing.isEnabled = false
        btnAddMtr.isEnabled = false

        btnStartBySvConfig.text = "Start by Server Config"
    }
}