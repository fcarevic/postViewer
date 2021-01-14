package com.example.postviewer.ui_fragments;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.example.postviewer.MainActivity;
import com.example.postviewer.entities.DAO_Author;
import com.example.postviewer.entities.DAO_Post;
import com.example.postviewer.entities.Database;
import com.example.postviewer.service.RestService;

import java.util.LinkedList;
import java.util.List;

public class UI_Updater  {
    private RestService.LocalBinder binder;
    private Worker worker;
    private List<ViewAdaptor> adaptorSubscriptions;



    public UI_Updater( Context act){
        adaptorSubscriptions= new LinkedList<>();
    }

    public void setBinder(RestService.LocalBinder binder) {
        this.binder = binder;
      }

    public RestService.LocalBinder getBinder() {
        return binder;
    }

    synchronized public void subscribe(ViewAdaptor sub) {
        this.adaptorSubscriptions.add(sub);
    }


    synchronized public void notifySubscriptions() {
        for (ViewAdaptor sub : adaptorSubscriptions) {

            notifyDataChanged(sub);
        }
    }

    private void postMessage( String msg){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.mainActivity, msg, Toast.LENGTH_LONG).show();
            }
        });

    }
private void notifyDataChanged(ViewAdaptor sub){
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
        @Override
        public void run() {
            DAO_Post dao_post = Database.getInstance(MainActivity.mainActivity).getDAO_Post();
            sub.getLocalData().clear();
            sub.getLocalData().addAll(dao_post.getAllPosts());
            sub.notifyDataSetChanged();
        }
    });

}

    public void start(){
        interrupt();
        worker = new Worker();
        worker.start();
    }

    public void interrupt(){
        if(worker!=null)
            worker.interrupt();
    }

    class Worker extends Thread {

        @Override
        public void run() {
            try {
                while (!interrupted()) {
                    binder.awaitRefresh();
                    notifySubscriptions();
                }
            } catch (Exception e) {
               // postMessage(e.toString());

            }
        }
    }
}

