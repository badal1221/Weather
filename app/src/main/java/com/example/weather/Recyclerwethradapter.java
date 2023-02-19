package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Recyclerwethradapter extends RecyclerView.Adapter<Recyclerwethradapter.viewholder>{
    Context context;
    ArrayList<wethr> arr;

    public Recyclerwethradapter(Context context, ArrayList<wethr> arr) {
        this.context = context;
        this.arr = arr;
    }
    @NonNull
    @Override
    public Recyclerwethradapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.myweather,parent,false);
        viewholder holder=new viewholder(v);
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull Recyclerwethradapter.viewholder holder, int position) {
        Picasso.get().load("https:".concat(arr.get(position).image)).into(holder.imageView);
        holder.texttemp.setText((arr.get(position).temp).concat("°C"));
        holder.textws.setText((arr.get(position).windspeed).concat("Km/h"));
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm");
        try{
             Date t=input.parse(arr.get(position).time);
             holder.texttime.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return arr.size();
    }
    public class viewholder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView texttime,texttemp,textws;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            texttime=itemView.findViewById(R.id.texttime);
            texttemp=itemView.findViewById(R.id.texttemp);
            textws=itemView.findViewById(R.id.textws);
        }
    }
}
