package com.example.bitmani.carbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {
    private Button offerRide;
    private Button searchRide;
    private Button registerCar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        offerRide = view.findViewById(R.id.offer_ride_button_id);
        searchRide = view.findViewById(R.id.search_ride_button_id);
        registerCar = view.findViewById(R.id.register_car_button_id);

        offerRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OfferRideActivity.class);
                startActivity(intent);
            }
        });


        searchRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchRideActivity.class);
                startActivity(intent);
            }
        });

        registerCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterCarActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
