package com.example.postviewer.ui_fragments;

import androidx.lifecycle.ViewModel;

import com.example.postviewer.entities.Post;
import com.example.postviewer.parser.Parser;

public class InterCommunicator extends ViewModel {
    private Post post;
    private Parser parser;

    public void setPost(Post post) {
        this.post = post;
    }

    public Post getPost() {
        return this.post;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public Parser getParser() {
        return this.parser;
    }
}
