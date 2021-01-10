package com.example.postviewer.ui_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.postviewer.MainActivity;
import com.example.postviewer.R;
import com.example.postviewer.entities.Post;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ViewAdaptor extends RecyclerView.Adapter<ViewAdaptor.ViewHolder> {
    private Fragment parentFragment;
    private List<Post> localData;
    private RecyclerView recyclerView;
    private HashMap<View, View> mapButtonToParentView = new HashMap<>();

    public ViewAdaptor(List<Post> localData) {
        setLocalData(localData);
    }

    public ViewAdaptor() {
        this.localData = new LinkedList<Post>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Button btn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.btn = (Button) itemView.findViewById(R.id.button);
            mapButtonToParentView.put(this.btn, itemView);
            this.btn.setOnClickListener(l -> {
                InterCommunicator communicator = new ViewModelProvider(ViewAdaptor.this.parentFragment.requireActivity()).get(InterCommunicator.class);
                int ind = recyclerView.getChildLayoutPosition(mapButtonToParentView.get(l));
                communicator.setPost(localData.get(ind));
                if (ViewAdaptor.this.parentFragment == null) return;
                NavHostFragment.findNavController(ViewAdaptor.this.parentFragment)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            });
        }

        public Button getButton() {
            return this.btn;
        }

    }

    public List<Post> getLocalData() {
        return this.localData;
    }

    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }

    public void setParentFragment(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setLocalData(List<Post> localData) {

        if (localData == null)
            this.localData = new LinkedList<>();
        else
            this.localData = localData;
    }

    @NonNull
    @Override
    public ViewAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdaptor.ViewHolder holder, int position) {
        holder.getButton().setText(this.localData.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return this.localData.size();
    }
}
