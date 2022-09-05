package com.neighbor.neighborsrefrigerator.scenarios.intro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.neighbor.neighborsrefrigerator.R
import com.neighbor.neighborsrefrigerator.scenarios.main.MainActivity
import com.neighbor.neighborsrefrigerator.viewmodels.LoginViewModel
import com.neighbor.neighborsrefrigerator.viewmodels.RegisterInfoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class StartActivity : ComponentActivity() {
    // Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1313
    private val viewModel by viewModels<LoginViewModel>()
    private val registerInfoViewModel by viewModels<RegisterInfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(color = Color.White) {
                Text(text = "로그인 확인중", fontSize = 30.sp)
            }
        }
        // 로그인 시도
        viewModel.tryLogin(this)

        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.loginResult.collect { isLogin ->
                    if (isLogin) {
                        Log.d("로그인 되어있음", "로그인 되어있음")
                        if (auth.currentUser != null) {
                            Log.d("token", auth.currentUser!!.getIdToken(true).toString())
                            val result = viewModel.hasId(auth.currentUser!!)
                            Log.d("아이디 있는지", result.toString())
                            if (result){
                                Log.d("아이디 있는지", "메인으로")
                                toMainActivity()
                            }else{
                                Log.d("아이디 없어서", "등록으로")
                                setContent {
                                    RegisterInfo()
                                }
                            }
                        }else{
                            Log.d("로그인 되어있음", "로그인 되어있지만? 유저가 없음")
                        }
                    } else {
                        Log.d("로그인 안되어있음", "로그인 안되어있음")
                        // 로그인 안되어있을 때 로그인 페이지 열림
                        setContent {
                            LoginScreen {
                                googleLogin()
                            }
                        }
                    }
                }
            }
            launch {
                registerInfoViewModel.event.collect{ event ->
                    when (event) {
                        RegisterInfoViewModel.RegisterEvent.ToMain -> toMainActivity()
                    }
                }
            }
        }

    }

    // 로그인 객체 생성
    private fun googleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // 빨간줄이지만 토큰 문제라 실행 가능
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignIn()
    }

    // 구글 회원가입
    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "구글 회원가입에 실패하였습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            /*no-op*/
        }
    }

    // account 객체에서 id 토큰 가져온 후 Firebase 인증
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            Log.d("로그인중", "로그인 되어야함1")
            if (task.isSuccessful) {
                Log.d("로그인중", "로그인 되어야함2")
                auth.currentUser?.let {
                    Log.d("로그인중", "로그인 되어야함3")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.setLoginResult(true)
                    }
                }
            }
        }
    }

    private fun toMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))

    }
}
