package com.example.bitmani.carbook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRideResultAdapter extends RecyclerView.Adapter<SearchRideResultAdapter.ViewHolder> {
    private static final String TAG = "SearchRideResultAdapter";

    private Context mContext;

    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mName = new ArrayList<>();
    private ArrayList<String> mPhone = new ArrayList<>();
    private ArrayList<String> mFrom = new ArrayList<>();
    private ArrayList<String> mTo = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();


    public SearchRideResultAdapter(Context mContext, ArrayList<String> mImages, ArrayList<String> mName, ArrayList<String> mPhone, ArrayList<String> mFrom, ArrayList<String> mTo, ArrayList<String> mDate, ArrayList<String> mTime) {
        this.mContext = mContext;
        this.mImages = mImages;
        this.mName = mName;
        this.mPhone = mPhone;
        this.mFrom = mFrom;
        this.mTo = mTo;
        this.mDate = mDate;
        this.mTime = mTime;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ride_search_results, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG,"OnBindViewHolder : called");

        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(i))
                .into(viewHolder.image);

        viewHolder.name.setText(mName.get(i));
        viewHolder.phone.setText(mPhone.get(i));
        viewHolder.from.setText(mFrom.get(i));
        viewHolder.to.setText(mTo.get(i));
        viewHolder.date.setText(mDate.get(i));
        viewHolder.time.setText(mTime.get(i));

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Item Clicked");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        TextView name;
        TextView phone;
        TextView from;
        TextView to;
        TextView date;
        TextView time;
        Button bookCar;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.owner_name);
            phone = itemView.findViewById(R.id.owner_phone_number);

            image = itemView.findViewById(R.id.circular_image_id);
            from = itemView.findViewById(R.id.recycler_view_from_text);
            to = itemView.findViewById(R.id.recycler_view_to_text);
            date = itemView.findViewById(R.id.recycler_view_date);
            time = itemView.findViewById(R.id.recycler_view_time);

            bookCar = itemView.findViewById(R.id.book_button_id);
            parentLayout = itemView.findViewById(R.id.search_ride_parent_layout);

            bookCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //..send notification
                }
            });
        }
    }
}
