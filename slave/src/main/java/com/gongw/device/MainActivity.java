package com.gongw.device;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.gongw.device.databinding.ActivityMainBinding;
import com.gongw.remote.communication.CommunicationKey;
import com.gongw.remote.communication.slave.CommandReceiver;
import com.gongw.remote.search.DeviceSearchResponser;

/**
 * Created by gongw on 2018/10/16.
 */

public class MainActivity extends AppCompatActivity {
    /**
     * 是否开启了搜索响应和通信响应
     */
    private boolean isOpen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.btnOpenResponser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen){
                    //停止响应搜索
                    DeviceSearchResponser.close();
                    isOpen = false;
                    binding.btnOpenResponser.setText("打开应答");
                    //停止接收通信命令
                    CommandReceiver.close();
                    Toast.makeText(MainActivity.this, "已经关闭响应程序！", Toast.LENGTH_SHORT).show();
                }else{
                    //开始响应搜索
                    DeviceSearchResponser.open();
                    isOpen = true;
                    binding.btnOpenResponser.setText("关闭应答");
                    //开始接受通信命令
                    CommandReceiver.open(new CommandReceiver.CommandListener() {
                        @Override
                        public String onReceive(final String command) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Receive:"+command, Toast.LENGTH_SHORT).show();
                                }
                            });
                            return CommunicationKey.RESPONSE_OK +
                                    "I am OK!" +
                                    CommunicationKey.EOF;
                        }
                    });
                    Toast.makeText(MainActivity.this, "已经打开响应程序！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.setIsOpen(isOpen);
        binding.executePendingBindings();
    }

}
