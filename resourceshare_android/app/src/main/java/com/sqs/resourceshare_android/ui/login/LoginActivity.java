package com.sqs.resourceshare_android.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sqs.resourceshare_android.entity.User;
import com.sqs.resourceshare_android.databinding.ActivityLoginBinding;
import com.sqs.resourceshare_android.util.Data;
import com.sqs.resourceshare_android.util.HttpCallBack;
import com.sqs.resourceshare_android.util.HttpClientUtil;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEditText.getText().toString() == null || passwordEditText.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "用户名或密码不能为空!", Toast.LENGTH_LONG).show();
                    return;
                }
               // binding.loading.setVisibility(View.VISIBLE);
                login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

    }

    private void login(String name, String pwd) {
        User user = new User(name, pwd, 0);
        HttpClientUtil.doPostJson(Data.SERVERURL + "/loginClient", new Gson().toJson(user), new HttpCallBack() {
            @Override
            public void response(String result) {
                //binding.loading.setVisibility(View.GONE);
                System.out.println("登录返回======" + result);
                if (result != null) {
                    if (result.indexOf("error") != -1) {
                        showToast(result);
                       /* JOptionPane.showMessageDialog(null, result, "提示", JOptionPane.WARNING_MESSAGE);
                        statusLabel.setVisible(true);
                        loginButton.setEnabled(true);
                        usernameField.setEditable(true);
                        passwordField.setEditable(true);*/
                        return;
                    }
                    User loginuser = new Gson().fromJson(result, User.class);
                    Data.loginUser = loginuser;
                    //mainFrame.hasLogin(Loginuser);
                    //登录成功
                    //JOptionPane.showMessageDialog(null, "登录成功", "提示", JOptionPane.WARNING_MESSAGE);
                    showToast("登录成功");
                }
            }

            @Override
            public void error(String error) {
                //binding.loading.setVisibility(View.GONE);
                //JOptionPane.showMessageDialog(null, "服务器访问异常:" + error, "提示", JOptionPane.WARNING_MESSAGE);
                showToast("服务器访问异常:" + error);
            }
        });
    }

    private void showToast(String message) {
        Looper.prepare(); // 初始化Looper
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (LoginActivity.this == null || LoginActivity.this.getPackageName() == null)
                    return;

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                if("登录成功".equals(message)){
                    LoginActivity.this.finish();

                }

            }
        };
        Message msg = Message.obtain(handler, 0);
        handler.sendMessage(msg); // 发送消息到消息队列
        Looper.loop(); // 启动消息循环
    }


}