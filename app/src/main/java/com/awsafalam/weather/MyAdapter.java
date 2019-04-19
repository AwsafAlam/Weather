package com.awsafalam.weather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Listitem> ListItems;
    private Context context;

    public MyAdapter(List<Listitem> listItems, Context context) {
        ListItems = listItems;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem , parent , false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Listitem listitem = ListItems.get(position);

        holder.Heading.setText(listitem.getHeading());
        holder.Description.setText(listitem.getDecription());
        if(listitem.getUrl().equals("01n")||listitem.getUrl().equals("01d"))
        {
            holder.Url.setImageResource(R.drawable.ic_clear);
        }
        else if(listitem.getUrl().equals("02n")||listitem.getUrl().equals("02d")){
            holder.Url.setImageResource(R.drawable.ic_sunny);
        }
        else if(listitem.getUrl().equals("03n")||listitem.getUrl().equals("03d")){
            holder.Url.setImageResource(R.drawable.ic_cloudy);
        }
        else if(listitem.getUrl().equals("04n")||listitem.getUrl().equals("04d")){
            holder.Url.setImageResource(R.drawable.ic_cloudy);
        }
//        else if(listitem.getUrl().equals("05n")||listitem.getUrl().equals("05d")){
//            holder.Url.setImageResource(R.drawable.ic_thunderrain);
//        }
//        else if(listitem.getUrl().equals("06n")||listitem.getUrl().equals("06d")){
//            holder.Url.setImageResource(R.drawable.ic_thunderrain);
//        }
        else if(listitem.getUrl().equals("50n")||listitem.getUrl().equals("50d")){
            holder.Url.setImageResource(R.drawable.ic_misty);
        }
        else if(listitem.getUrl().equals("11n")||listitem.getUrl().equals("11d")){
            holder.Url.setImageResource(R.drawable.ic_thunderrain);
        }
        else if(listitem.getUrl().equals("09n")||listitem.getUrl().equals("09d")){
            holder.Url.setImageResource(R.drawable.ic_rain);
        }
        else if(listitem.getUrl().equals("10n")||listitem.getUrl().equals("10d")){
            holder.Url.setImageResource(R.drawable.ic_showerrain);
        }
        else if(listitem.getUrl().equals("13n")||listitem.getUrl().equals("13d")){
            holder.Url.setImageResource(R.drawable.ic_snowy);
        }

    }

    @Override
    public int getItemCount() {
        return ListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView Heading;
        public TextView Description;
        public ImageView Url;

        public ViewHolder(View itemView) {
            super(itemView);

            Heading = (TextView) itemView.findViewById(R.id.Heading);
            Description = (TextView) itemView.findViewById(R.id.Description);
            Url = (ImageView)itemView.findViewById(R.id.imageView);
        }
    }


}
