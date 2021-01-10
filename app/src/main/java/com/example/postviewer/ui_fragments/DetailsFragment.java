package com.example.postviewer.ui_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.postviewer.R;
import com.example.postviewer.entities.Author;
import com.example.postviewer.entities.Database;
import com.example.postviewer.entities.Post;

public class DetailsFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.details_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleTv = (TextView) (view.findViewById(R.id.textview_title));
        TextView bodyTv = (TextView) (view.findViewById(R.id.textview_body));
        TextView authorTv = (TextView) (view.findViewById(R.id.textview_author));
        TextView emailTv = (TextView) (view.findViewById(R.id.textview_email));
        InterCommunicator interCommunicator = new ViewModelProvider(DetailsFragment.this.requireActivity()).get(InterCommunicator.class);
        Post post = interCommunicator.getPost();
        if (post != null) {
            titleTv.setText(post.getTitle());
            bodyTv.setText(post.getBody());
            Author author = Database.getInstance(DetailsFragment.this.requireContext()).getDAO_Author().getById(post.getUserId());
            if (author != null) {
                authorTv.setText(author.getName());
                emailTv.setText(author.getEmail());
            }
        }

        view.findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(DetailsFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        view.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InterCommunicator interCommunicator = new ViewModelProvider(DetailsFragment.this.requireActivity()).get(InterCommunicator.class);
                Post post = interCommunicator.getPost();
                if (post == null) return;
                Database.getInstance(DetailsFragment.this.requireContext()).getDAO_Post().delete(post);
                Toast.makeText(DetailsFragment.this.requireContext(), "Successfully removed post", Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(DetailsFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);

            }
        });
    }
}