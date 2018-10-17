package com.gongw.remote.search;

import android.os.Handler;
import android.os.Looper;

import com.gongw.remote.Device;
import com.gongw.remote.RemoteConst;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用于搜索局域网中的设备
 */
public class DeviceSearcher {

	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	private static Handler uiHandler = new Handler(Looper.getMainLooper());

	/**
	 * 开始搜索
	 * @param onSearchListener
	 */
	public static void search(OnSearchListener onSearchListener){
		executorService.execute(new SearchRunnable(onSearchListener));
	}
	
	public static interface OnSearchListener{
		void onSearchStart();
		void onSearchedNewOne(Device device);
		void onSearchFinish();
	}
	
	private static class SearchRunnable implements Runnable {
		
		OnSearchListener searchListener;
		
		public SearchRunnable(OnSearchListener listener){
			this.searchListener = listener;
		}
		
		@Override
		public void run() {
			try {
				if(searchListener!=null){
					uiHandler.post(new Runnable() {
						@Override
						public void run() {
							searchListener.onSearchStart();
						}
					});
				}
				DatagramSocket socket = new DatagramSocket();
				//设置接收等待时长
				socket.setSoTimeout(RemoteConst.RECEIVE_TIME_OUT);
				byte[] sendData = new byte[1024];
				byte[] receData = new byte[1024];
                DatagramPacket recePack = new DatagramPacket(receData, receData.length);
                //使用广播形式（目标地址设为255.255.255.255）的udp数据包
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), RemoteConst.DEVICE_SEARCH_PORT);
                //用于存放已经应答的设备
                HashMap<String, Device> devices = new HashMap<>();
                //搜索指定次数
				for(int i=0;i<RemoteConst.SEARCH_DEVICE_TIMES;i++){
					sendPacket.setData(packSearchData(i+1));
					//发送udp数据包
					socket.send(sendPacket);
	                try {
	                	//限定搜索设备的最大数量
	                    int rspCount = RemoteConst.SEARCH_DEVICE_MAX;
	                    while (rspCount > 0) {
	                        socket.receive(recePack);
	                        final Device device = parseRespData(recePack);
	                        if(devices.get(device.getIp())==null){
	                        	//保存新应答的设备
		                        devices.put(device.getIp(), device);
								if(searchListener!=null){
									uiHandler.post(new Runnable() {
										@Override
										public void run() {
											searchListener.onSearchedNewOne(device);
										}
									});
								}
	                        }
							rspCount --;
	                    }
	                } catch (SocketTimeoutException e) {
	                }
				}
				socket.close();
				if(searchListener!=null){
					uiHandler.post(new Runnable() {
						@Override
						public void run() {
							searchListener.onSearchFinish();
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

        /**
         * 校验和解析应答的数据包
		 * @param pack udp数据包
		 * @return
         */
		private Device parseRespData(DatagramPacket pack) {
			if (pack.getLength() < 2) {
	            return null;
	        }
	        byte[] data = pack.getData();
	        int offset = pack.getOffset();
	        //检验数据包格式是否符合要求
	        if (data[offset++] != RemoteConst.PACKET_PREFIX || data[offset++] != RemoteConst.PACKET_TYPE_SEARCH_DEVICE_RSP) {
	            return null;
	        }
	        int length = data[offset++];
        	String uuid = new String(data, offset, length);
			return new Device(pack.getAddress().getHostAddress(), pack.getPort(), uuid);
		}

		/**
         * 生成搜索数据包
		 * 格式：$(1) + packType(1) + sendSeq(4) + dataLen(1) + [data]
		 *  packType - 报文类型
		 *  sendSeq - 发送序列
		 *  dataLen - 数据长度
		 *  data - 数据内容
		 * @param seq
         * @return
         */
		private byte[] packSearchData(int seq) {
			byte[] data = new byte[6];
			int offset = 0;
			data[offset++] = RemoteConst.PACKET_PREFIX;
			data[offset++] = RemoteConst.PACKET_TYPE_SEARCH_DEVICE_REQ;
			data[offset++] = (byte) seq;
			data[offset++] = (byte) (seq >> 8);
			data[offset++] = (byte) (seq >> 16);
			data[offset++] = (byte) (seq >> 24);
			return data;
		}
	}


}
