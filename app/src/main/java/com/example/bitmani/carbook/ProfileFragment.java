package com.example.bitmani.carbook;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private ImageView profilePicture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        profilePicture = view.findViewById(R.id.profile_image_id);

        if (currentUser != null) {
            Uri url = currentUser.getPhotoUrl();
            if (url != null) {
                Toast.makeText(getActivity(), url.toString(), Toast.LENGTH_SHORT).show();
            }
            //Picasso.get().load(url).into(profilePicture);
        }

        return view;
    }
}
