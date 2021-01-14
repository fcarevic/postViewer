package com.example.postviewer.ui_fragments;

import androidx.lifecycle.ViewModel;

import com.example.postviewer.entities.Post;

public class InterCommunicator extends ViewModel {
    private Post post;
    private UI_Updater ui_updater;

    public void setPost(Post post) {
        this.post = post;
    }

    public Post getPost() {
        return this.post;
    }

    public void setUi_Updater(UI_Updater ui_updater) {
        this.ui_updater = ui_updater;
    }

    public UI_Updater getUI_Updater() {
        return this.ui_updater;
    }
}
