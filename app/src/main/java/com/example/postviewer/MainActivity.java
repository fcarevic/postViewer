package com.example.postviewer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import com.example.postviewer.service.RestService;
import com.example.postviewer.ui_fragments.InterCommunicator;
import com.example.postviewer.ui_fragments.UI_Updater;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.os.IBinder;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static MainActivity mainActivity;
    private UI_Updater updater;
    private RestService.LocalBinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainActivity.this.binder= (RestService.LocalBinder) service;
            updater.setBinder(binder);
            updater.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        binder=null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainActivity = this;
        FloatingActionButton fab = findViewById(R.id.refresh);
        this.updater = new UI_Updater(this);
        InterCommunicator communicator = new ViewModelProvider(this).get(InterCommunicator.class);
        communicator.setUi_Updater(this.updater);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binder==null)  return;
                 binder.initiateRefresh();
               Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAndBindService();
    }

    private void startAndBindService() {
        try {
            Intent service = new Intent(this, RestService.class);
            startService(service);
            bindService(service, connection, Context.BIND_AUTO_CREATE );

        }

        catch (Exception e){
            Toast.makeText(this, e.toString(),Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.stop_service) {
            if(binder!=null) binder.stopService();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        this.updater.interrupt();
        super.onDestroy();
    }
}