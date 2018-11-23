package me.thanongsine.myshutter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.thanongsine.myshutter.models.Photo;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.ViewHolder> {
    private List<Photo> photoList;
    private Context context;
    private List<String> photoKeyList;

    public PhotoListAdapter(Context context, List<Photo> photoList, List<String> photoKeyList) {
        this.photoList = photoList;
        this.context = context;
        this.photoKeyList = photoKeyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final int position = i;
        Photo photo = photoList.get(i);
        ImageView imageView = viewHolder.imageView;
        TextView textView = viewHolder.textView;

        Picasso.get()
                .load(photo.getUrl())
                .placeholder(R.drawable.placeholder_img)
                .into(imageView);
        textView.setText(photo.getTitle());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoInfoActivity.class);
                intent.putExtra("PHOTO_KEY", photoKeyList.get(position));
                AppCompatActivity activity = (AppCompatActivity) v.getContext();

                activity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.photo_title_text_view);
        }
    }
}
