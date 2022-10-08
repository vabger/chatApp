package com.example.chatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapp.R;

import java.util.List;

public class ImageItemAdapter extends BaseAdapter {
    class ViewHolderItem{
        TextView name;
        ImageView image;

        public ViewHolderItem(TextView name, ImageView image) {
            this.name = name;
            this.image = image;
        }

        public void setImage(int resource) {
            image.setImageResource(resource);
        }
        public void setName(String str){
            name.setText(str);
        }

    }

    private final Context context;
    private final List<ImageItem> imageItems;

    public ImageItemAdapter(Context context, List<ImageItem> imageItems){
        this.context = context;
        this.imageItems = imageItems;
    }
    @Override
    public int getCount() {
        return imageItems!=null?imageItems.size():0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolderItem viewHolderItem;
        if(view==null){
            view = LayoutInflater.from(context)
                    .inflate(R.layout.image_item, viewGroup, false);
            viewHolderItem = new ViewHolderItem(view.findViewById(R.id.name),view.findViewById(R.id.image));

            view.setTag(viewHolderItem);
        }
        else{
            viewHolderItem = (ViewHolderItem) view.getTag();
        }

        viewHolderItem.setImage(imageItems.get(i).getResource());
        viewHolderItem.setName(imageItems.get(i).getName());


        return view;
    }
}
