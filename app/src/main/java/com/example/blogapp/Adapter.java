package com.example.blogapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blogapp.Fragments.Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    ArrayList<Model> list;

    public Adapter(ArrayList<Model> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }
    public void filter_list(ArrayList<Model> filter_list){
        list=filter_list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model model =list.get(position);
        holder.title.setText(model.getTitle());
        holder.date.setText(model.getDate());
        holder.share_count.setText(model.getShare_count());
        holder.author.setText(model.getAuthor());


        Glide.with(holder.author.getContext()).load(model.getImg()).into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.author.getContext(), BlogDetail.class);
                intent.putExtra("id",model.getId());
                holder.author.getContext().startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.author.getContext());
                builder.setTitle("What you want do?");
                builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Dialog u_dialog=new Dialog(holder.author.getContext());
                        u_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        u_dialog.setCancelable(false);
                        u_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        u_dialog.setContentView(R.layout.update_dialog);


                        EditText title=u_dialog.findViewById(R.id.b_title);
                        EditText desc=u_dialog.findViewById(R.id.b_desc);
                        EditText author=u_dialog.findViewById(R.id.b_author);

                        title.setText(model.getTitle());
                        desc.setText((model.getDesc()));
                        author.setText(model.getAuthor());

                        TextView dialogButton=u_dialog.findViewById(R.id.btn_publish);
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (title.getText().toString().isEmpty()){
                                    title.setError("Field is required!!");
                                }else if (desc.getText().toString().isEmpty()){
                                    desc.setError("Field is required!!");
                                }else if (author.getText().toString().isEmpty()){
                                    author.setError("Field is required!!");
                                }else{
                                    HashMap<String,Object> map = new HashMap<>();
                                    map.put("title",title.getText().toString());
                                    map.put("desc",desc.getText().toString());
                                    map.put("author",author.getText().toString());

                                    FirebaseFirestore.getInstance().collection("Blogs").document(model.getId()).update(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        dialog.dismiss();
                                                        u_dialog.dismiss();
                                                    }
                                                }
                                            });

                                }
                            }
                        });

                        u_dialog.show();
                    }
                });
                builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder deleteBuilders =new AlertDialog.Builder(holder.author.getContext());
                        deleteBuilders.setTitle("Are you sure to Delete it??");
                        deleteBuilders.setPositiveButton("Yest! I am Sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseFirestore.getInstance().collection("Blogs").document(model.getId()).delete();

                            }
                        });
                        deleteBuilders.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        deleteBuilders.show();
                    }
                });
                builder.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView date,title,share_count,author;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            img=itemView.findViewById(R.id.imageView3);
            date=itemView.findViewById(R.id.t_date);
            title=itemView.findViewById(R.id.textView9);
            share_count=itemView.findViewById(R.id.textView10);
            author=itemView.findViewById(R.id.textView8);


        }
    }
}
