package com.rmit.cloudcomputing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_ResultList extends RecyclerView.Adapter<Adapter_ResultList.GeneralViewHolder> {
    private Context context;
    private ArrayList<Model_Product> products;

    public Adapter_ResultList(Context context, ArrayList<Model_Product> products) {
        this.context=context;
        this.products=products;
    }

    public static class GeneralViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView price;
        private ImageView image;
        private ImageButton menu;
        private TextView date;

        public GeneralViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            price=itemView.findViewById(R.id.price);
            image=itemView.findViewById(R.id.imageView);
            menu=itemView.findViewById(R.id.menu);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public GeneralViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(context).inflate(R.layout.item_result, parent, false);
        return new GeneralViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final GeneralViewHolder holder, final int position) {

        Model_Product product=products.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(String.valueOf(product.getPrice()));
        if(DataHolder.usertype.equals("admin")){
            holder.menu.setVisibility(View.VISIBLE);
            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu pm = new PopupMenu(context, v);
                    pm.getMenuInflater().inflate(R.menu.resultadmin, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())   {
                                case R.id.edit:
                                    Intent intent=new Intent(context,Activity_CreateProduct.class);
                                    intent.putExtra("id",products.get(position).getId());
                                    intent.putExtra("name",products.get(position).getName());
                                    intent.putExtra("price",products.get(position).getPrice());
                                    intent.putExtra("image",products.get(position).getImage());
                                    intent.putExtra("position",position);
                                    intent.putExtra("from","edit");
//                                    intent.putExtra("product",products.get(position));
                                    context.startActivity(intent);
                                    break;
                                case R.id.delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete Product");
                                    builder.setMessage("Are you sure you want to delete this product?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AsyncTask_Delete asyncTask_delete =new AsyncTask_Delete(context,Adapter_ResultList.this,position);
                                            asyncTask_delete.execute(products.get(position).getId());
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    builder.show();
                                    break;

                                case R.id.pricehistory:
                                    Intent intent1=new Intent(context,Activity_PriceHistory.class);
                                    intent1.putExtra("id",products.get(position).getId());
                                    context.startActivity(intent1);
                            }
                            return false;
                        }
                    });
                    pm.show();
                }
            });
        }
        Picasso.get()
                .load(product.getImage())
                .placeholder(R.drawable.noimage)
                .resize(100, 100)
                .error(R.drawable.noimage)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
