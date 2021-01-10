package com.example.postviewer.parser;

import androidx.recyclerview.widget.RecyclerView;

import com.example.postviewer.entities.Post;
import com.example.postviewer.ui_fragments.ViewAdaptor;

import java.util.List;

public interface Parser {
    List<Post> getPosts();
    void subscribe(ViewAdaptor subscription);
    void notifySubscriptions();
    public void refreshState();
}
