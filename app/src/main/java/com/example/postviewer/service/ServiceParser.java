package com.example.postviewer.service;

import android.app.Service;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.postviewer.MainActivity;
import com.example.postviewer.entities.Author;
import com.example.postviewer.entities.Database;
import com.example.postviewer.entities.Post;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ServiceParser extends Thread {
    private Service service;
    private static final int SLEEP_TIME = 5 * 60* 1000;
    private List<Post> posts;
    private String postUrl;
    private String authorURL;
    private boolean isUpdateRequest;

    public ServiceParser(String postUrl, String authorURL, Service service) {
        this.postUrl = postUrl;
        this.service=service;
        this.authorURL = authorURL;
        this.posts = new ArrayList<>();
    }

    private Author parsetAuthorFromJSONobject(JSONObject obj) {

        try {
            int id = obj.getInt("id");
            String name = obj.getString("name");
            String email = obj.getString("email");
            return new Author(id, email, name);
        } catch (Exception e) {
            postMessage(e.toString());
        }
        return null;
    }

    private Post parsePostFromJSONobject(JSONObject obj) {
        try {
            int id = obj.getInt("id");
            int userId = obj.getInt("userId");
            String title = obj.getString("title");
            String body = obj.getString("body");
            return new Post(id, title, body, userId);
        } catch (Exception e) {
         postMessage(e.toString());
        }
        return null;
    }

    public void setPostUrl(String url) {
        this.postUrl = url;
    }

    public void setAuthorURL(String url) {
        this.authorURL = url;
    }


    public List<Post> getPosts() {
        return this.posts;
    }

    private JsonObjectRequest generateAuthorRequest(int id) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, this.authorURL + id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Author author = parsetAuthorFromJSONobject(response);
                        if (author == null) return;
                        if (Database.getInstance(service).getDAO_Author().getById(author.getId()) != null)
                            return;
                        Database.getInstance(service).getDAO_Author().insertALl(author);
                    }
                },
                error -> {
              postMessage(error.toString());
            });
        return request;

    }
    private void postMessage( String msg){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(service.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void refreshState() {

         RequestQueue queue = Volley.newRequestQueue(service);
        posts.clear();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, this.postUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Database.getInstance(service).getDAO_Author().deleteAll();
                Database.getInstance(service).getDAO_Post().deleteAll();


                List<JsonObjectRequest> authorRequests = new LinkedList<>();
                for (int i = 0; i < response.length(); i++) {
                    Post post = null;

                    try {
                        post = parsePostFromJSONobject(response.getJSONObject(i));
                        if (post != null) {
                            ServiceParser.this.posts.add(post);

                            if (Database.getInstance(service).getDAO_Post().getById(post.getId()) == null) {
                                Database.getInstance(service).getDAO_Post().insertAll(post);
                                authorRequests.add(generateAuthorRequest(post.getUserId()));
                            }
                        }


                    } catch (Exception e) {
                        postMessage(e.toString());
                    }
                }
                RequestQueue queue = Volley.newRequestQueue(service);
                for (JsonObjectRequest req : authorRequests) {
                    queue.add(req);
                }
                synchronized (service) { //awakes ui_updater thread
                    service.notifyAll();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        postMessage(error.toString());
                    }
                }


        );
        queue.add(request);

    }

    @Override
    public void run() {

       postMessage("Service started");
        try {
            while (!interrupted()) {
                try {
                    refreshState();
                    postMessage("Refreshed from service");

                    sleep(SLEEP_TIME);
                } catch (InterruptedException e){
                    if(!isUpdateRequest) throw  e;

                }
            }
        } catch (Exception e) {
                postMessage(e.toString());
        }
       postMessage("Service thread finished");
    }

    public void setUpdateRequest(boolean b)
    {

        this.isUpdateRequest = b;
    }
}
