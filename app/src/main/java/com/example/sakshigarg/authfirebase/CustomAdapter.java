package com.example.sakshigarg.authfirebase;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * Created by sakshi on 22-03-2017.
 */
public class CustomAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<CustomObject> objects;
    String name;
    Context context;
    StorageReference storageReference;
    private  class ViewHolder
    {   LinearLayout rl;
        TextView name;
        TextView msg;
        TextView date;
        ImageView image;
    }
    public CustomAdapter(Context context, ArrayList<CustomObject> objects , String name)
    {
        inflater = LayoutInflater.from(context);
        this.context=context;
        this.objects=objects;
        this.name=name;
        storageReference = FirebaseStorage.getInstance().getReference();

    }

//why member functions
    public  int getCount()
    {
        return objects.size();
    }

    public CustomObject getItem(int position)
    {
        return  objects.get(position);
    }

    public  long getItemId(int position)
    {
        return  position;
    }

    public View getView(int position, View convertView, ViewGroup parent)//doubt
    {
        ViewHolder holder ;
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_list1, null);

            holder.rl=(LinearLayout)convertView.findViewById(R.id.rl);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.msg = (TextView) convertView.findViewById(R.id.msg);
            holder.date=(TextView)convertView.findViewById(R.id.date);
            holder.image=(ImageView)convertView.findViewById(R.id.image);

            convertView.setTag(holder);//means?

        RelativeLayout.LayoutParams params =(RelativeLayout.LayoutParams)holder.rl.getLayoutParams();
        try {

            if (objects.get(position).getName()!=null && objects.get(position).getName().equals(name)) {
                holder.rl.setBackground(ContextCompat.getDrawable(context, R.drawable.button_back_blue));

                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.setMarginStart(200);
                holder.rl.setLayoutParams(params);

            }
                else {
                holder.rl.setBackground(ContextCompat.getDrawable(context, R.drawable.button_back_green));
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                params.setMarginEnd(200);
                holder.rl.setLayoutParams(params);
            }
        }catch (Exception e)
        {
            Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();

        }
        if(objects.get(position).getIm()==1)
        {
            try {
                StorageReference riversRef = storageReference.child(objects.get(position).getMsg());
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(riversRef)
                        .into(holder.image);
            }catch(Exception e)
            {
                Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();

            }

        }
        else
            holder.msg.setText(objects.get(position).getMsg());
            holder.date.setText(objects.get(position).getDate());
            holder.name.setText(objects.get(position).getName());



            return convertView;
        }
    }

