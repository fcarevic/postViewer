package com.example.postviewer.parser;

import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.postviewer.MainActivity;
import com.example.postviewer.entities.Author;
import com.example.postviewer.entities.Database;
import com.example.postviewer.entities.Post;
import com.example.postviewer.ui_fragments.RecycleViewFragment;
import com.example.postviewer.ui_fragments.ViewAdaptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RestParser extends Thread implements Parser {
    private static final int SLEEP_TIME = 5 * 60 * 1000;
    private List<ViewAdaptor> adaptorSubscriptions;
    List<Post> posts;
    private String postUrl;
    private String authorURL;

    public RestParser(String postUrl, String authorURL) {
        this.postUrl = postUrl;
        this.authorURL = authorURL;
        this.adaptorSubscriptions = new LinkedList<>();
        this.posts = new ArrayList<>();
    }

    private Author parsetAuthorFromJSONobject(JSONObject obj) {

        try {
            int id = obj.getInt("id");
            String name = obj.getString("name");
            String email = obj.getString("email");
            return new Author(id, email, name);
        } catch (Exception e) {
            Toast.makeText(MainActivity.mainActivity, e.toString(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(MainActivity.mainActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public void setPostUrl(String url) {
        this.postUrl = url;
    }

    public void setAuthorURL(String url) {
        this.authorURL = url;
    }

    @Override
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
                        if (Database.getInstance(MainActivity.mainActivity).getDAO_Author().getById(author.getId()) != null)
                            return;
                        Database.getInstance(MainActivity.mainActivity).getDAO_Author().insertALl(author);
                    }
                },
                error -> {
                    Toast.makeText(MainActivity.mainActivity, error.toString(), Toast.LENGTH_LONG).show();
                });
        return request;

    }

    @Override
    public void refreshState() {

        Database.getInstance(MainActivity.mainActivity).getDAO_Author().deleteAll();
        Database.getInstance(MainActivity.mainActivity).getDAO_Post().deleteAll();
        RequestQueue queue = Volley.newRequestQueue(MainActivity.mainActivity);
        posts.clear();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, this.postUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Toast.makeText(MainActivity.mainActivity, "Refreshed", Toast.LENGTH_LONG).show();
                List<JsonObjectRequest> authorRequests = new LinkedList<>();
                for (int i = 0; i < response.length(); i++) {
                    Post post = null;

                    try {
                        post = parsePostFromJSONobject(response.getJSONObject(i));
                        if (post != null) {
                            RestParser.this.posts.add(post);

                            if (Database.getInstance(MainActivity.mainActivity).getDAO_Post().getById(post.getId()) == null) {
                                Database.getInstance(MainActivity.mainActivity).getDAO_Post().insertAll(post);
                                authorRequests.add(generateAuthorRequest(post.getUserId()));
                            }
                        }


                    } catch (Exception e) {
                        Toast.makeText(MainActivity.mainActivity, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                RequestQueue queue = Volley.newRequestQueue(MainActivity.mainActivity);
                for (JsonObjectRequest req : authorRequests) {
                    queue.add(req);
                }
                notifySubscriptions();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.mainActivity, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }


        );
        queue.add(request);

    }

    @Override
    public void run() {
        try {
            while (!interrupted()) {
                refreshState();
                sleep(SLEEP_TIME);
                  //  Toast.makeText(MainActivity.mainActivity, "Refreshing from thread", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //  Toast.makeText(MainActivity.mainActivity, e.toString(), Toast.LENGTH_LONG).show();

        }
        //  Toast.makeText(MainActivity.mainActivity, "Parser stopped", Toast.LENGTH_SHORT).show();

    }

    @Override
    synchronized public void subscribe(ViewAdaptor sub) {
        this.adaptorSubscriptions.add(sub);
        // refreshState();
    }

    @Override
    synchronized public void notifySubscriptions() {
        for (ViewAdaptor sub : adaptorSubscriptions) {
            sub.getLocalData().clear();
            sub.getLocalData().addAll(this.posts);
            sub.notifyDataSetChanged();
        }
    }
}
