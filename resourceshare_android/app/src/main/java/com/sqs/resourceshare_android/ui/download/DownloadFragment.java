package com.sqs.resourceshare_android.ui.download;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bin.david.form.annotation.SmartTable;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sqs.resourceshare_android.R;
import com.sqs.resourceshare_android.ScanActivity;
import com.sqs.resourceshare_android.databinding.FragmentDownloadBinding;
import com.sqs.resourceshare_android.entity.File;
import com.sqs.resourceshare_android.entity.ResponseDto;
import com.sqs.resourceshare_android.util.Data;
import com.sqs.resourceshare_android.util.FileUtils;
import com.sqs.resourceshare_android.util.HttpCallBack;
import com.sqs.resourceshare_android.util.HttpClientUtil;

import java.util.List;
@SmartTable(name="表名")
public class DownloadFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 100;
    private FragmentDownloadBinding binding;
    AlertDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDownloadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        //获取返回的结果
                        String data = result.getData().getStringExtra("data");
                        Log.d("DownloadFragment", "onCreate: " + data);



                        //下载
                        queryFile(data);
                        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
                    }
                });


        binding.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getContext(), "点击", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ScanActivity.class);
                activityResultLauncher.launch(intent);
            }
        });

        binding.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shareCode = binding.shareCodeEt.getText().toString();
                Log.d("DownloadViewModel", shareCode);
                if (shareCode == null || shareCode.length() != 3) {
                    Toast.makeText(getContext(), "文件共享码为三位数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                queryFile(shareCode);
            }
        });


        // 检查权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 权限未被授予，需要请求权限
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        } else {

        }

        binding.settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(R.layout.dialog_input);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogTemp, int id) {
                        // 处理确定按钮点击事件
                        EditText editText = dialog.findViewById(R.id.editText);
                        String userInput = editText.getText().toString();
                        Data.SERVERURL="http://"+userInput+":8989/v1";
                        Toast.makeText(getActivity(), "服务器设置成功！", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 处理取消按钮点击事件
                    }
                });
                dialog = builder.create();
                dialog.setTitle("设置服务器IP");
                dialog.show();
                String temp=Data.SERVERURL.replace("http://","").replace(":8989/v1","");
                ((EditText)dialog.findViewById(R.id.editText)).setText(temp);
            }
        });

        // final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // 如果权限被用户同意，则执行存储操作
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //doStorageOperation();
                } else {
                    // 用户拒绝权限请求，需要提示用户或者引导他们去设置页面手动开启
                    Toast.makeText(getContext(), "需要存储权限", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void queryFile(String shareCode) {
        HttpClientUtil.doGet(Data.SERVERURL + "/file/queryByShareCode/" + shareCode, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                if (result == null || result.length() == 0 || result.equals("error")) {
                    showToast("对应文件共享码不存在");
                    return;
                }
                System.out.println("=====>" + result);
                ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto.getMsg().equals("success")) {
                    LinkedTreeMap<String, Object> fileMap = (LinkedTreeMap<String, Object>) responseDto.getData();
                    File file = new File();
                    file.setId(Double.valueOf(fileMap.get("id").toString()).longValue());
                    file.setType(fileMap.get("type").toString());
                    file.setFileName(fileMap.get("fileName").toString());
                    if ("folder".equals(file.getType())) {
                        file.setSize(null);
                    } else {
                        file.setSize(fileMap.get("size").toString());
                    }
                    file.setDownloadProgress(0.0);
                    file.setStatue(1);
                    Log.d("DownloadViewModel", file.toString());

                    downloadFile(file);
                    //1.加入下载列表
                    //MainFrame.getContext().addDownLoadFile(file);
                    //2.跳转到下载列表界面
                    //MainFrame.getContext().getLeftPanel().clickMenu("fdl_button");
                } else {
                    showToast("对应文件共享码不存在");
                }
            }

            @Override
            public void error(String error) {
                showToast("获取文件失败：" + error);
            }
        });


    }


    private void downloadFile(File file) {
        //下载文件
        String fileUrl = null;
        if (file.getOwner() != null) {
            fileUrl = Data.SERVERURL + "/personfile/" + file.getId() + "/download/" + file.getOwner();
        } else {
            fileUrl = Data.SERVERURL + "/file/" + file.getId() + "/download";
        }
        String path = FileUtils.getExternalStoragePath() + java.io.File.separator + file.getFileName();
        if (file.getType().equals("folder")) {
            path += ".zip";
        }
        Log.d("DownloadFragment", "文件路径:" + path);
        String finalPath = path;
        HttpClientUtil.downloadFile(fileUrl, path, new HttpCallBack() {
            @Override
            public void response(String result) {
                showToast("文件路径：" + finalPath);
            }

            @Override
            public void error(String error) {
                showToast("下载文件失败：" + error);
            }
        });


    }

    private void showToast(String message) {
        Looper.prepare(); // 初始化Looper
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        };
        Message msg = Message.obtain(handler, 0);
        handler.sendMessage(msg); // 发送消息到消息队列
        Looper.loop(); // 启动消息循环
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}