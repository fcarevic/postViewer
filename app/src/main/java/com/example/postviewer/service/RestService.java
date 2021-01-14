package com.example.postviewer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class RestService extends Service {
    private static final String POST_URL = "http://jsonplaceholder.typicode.com/posts";
    private static  final  String AUTHOR_URL = "http://jsonplaceholder.typicode.com/users/";

    private ServiceParser serviceParser;


    public class LocalBinder extends Binder {
        public void initiateRefresh(){
            serviceParser.setUpdateRequest(true);
            serviceParser.interrupt();
        }

        public Service getService(){ return RestService.this;}
        public void stopService(){
            stopSelf();
        }

        public void awaitRefresh() throws InterruptedException {
            synchronized (RestService.this) {
                RestService.this.wait();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serviceParser=new ServiceParser(POST_URL, AUTHOR_URL, this);
        serviceParser.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

                return Service.START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }


    @Override
    public void onDestroy() {
        serviceParser.setUpdateRequest(false);
        serviceParser.interrupt();
        super.onDestroy();
    }


}
