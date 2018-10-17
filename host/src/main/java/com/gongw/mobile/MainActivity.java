package com.gongw.mobile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.gongw.mobile.databinding.ActivityMainBinding;
import com.gongw.remote.Device;
import com.gongw.remote.communication.host.Command;
import com.gongw.remote.communication.host.CommandSender;
import com.gongw.remote.search.DeviceSearcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongw on 2018/10/16.
 */

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<Device> deviceList = new ArrayList<>();
    private SimpleAdapter<Device> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        //开始搜索局域网中的设备
        startSearch();
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        adapter = new SimpleAdapter<Device>(deviceList, R.layout.item_device, BR.device){
            @Override
            public void addListener(View root, final Device itemData, int position) {
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击列表项时发送命令
                        sendCommand(itemData);
                    }
                });
            }
        };
        binding.rvDevices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvDevices.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        binding.rvDevices.setAdapter(adapter);
        binding.srlRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startSearch();
            }
        });
    }

    /**
     * 开始异步搜索局域网中的设备
     */
    private void startSearch(){
        DeviceSearcher.search(new DeviceSearcher.OnSearchListener() {
            @Override
            public void onSearchStart() {
                binding.srlRefreshLayout.setRefreshing(true);
                deviceList.clear();
            }

            @Override
            public void onSearchedNewOne(Device device) {
                binding.srlRefreshLayout.setRefreshing(false);
                deviceList.add(device);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onSearchFinish() {
                binding.srlRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void sendCommand(Device device){
        //发送命令，命令内容为"hello!"
        Command command = new Command("Are you OK!", new Command.Callback() {
            @Override
            public void onRequest(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Request: Are you OK!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSuccess(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Success:"+msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error:"+msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onEcho(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Echo："+msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        command.setDestIp(device.getIp());
        CommandSender.addCommand(command);
    }

}
