package com.akitektuo.smartlist.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.model.ExcelModel;

import java.io.File;
import java.util.List;

/**
 * Created by AoD Akitektuo on 22-Aug-17 at 19:27.
 */

public class LightExcelAdapter extends RecyclerView.Adapter<LightExcelAdapter.ViewHolder> {

    private List<ExcelModel> excelModels;
    private Context context;

    public LightExcelAdapter(Context context, List<ExcelModel> excelModels) {
        this.context = context;
        this.excelModels = excelModels;
    }

    @Override
    public LightExcelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_excel_light, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ExcelModel excelModel = excelModels.get(position);
        holder.textName.setText(excelModel.getName());
        holder.textSize.setText(excelModel.getSize());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile(holder.getAdapterPosition());
            }
        });
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteFile(holder.getAdapterPosition());
                return true;
            }
        });
    }

    private void deleteFile(final int position) {
        AlertDialog.Builder builderDelete = new AlertDialog.Builder(context);
        builderDelete.setTitle("Delete File");
        builderDelete.setMessage("Are you sure you want to delete this file?");
        builderDelete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SmartList", excelModels.get(position).getName());
                if (file.delete()) {
                    excelModels.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
        builderDelete.setNegativeButton("Cancel", null);
        AlertDialog dialogDelete = builderDelete.create();
        dialogDelete.show();
    }

    @Override
    public int getItemCount() {
        return excelModels.size();
    }

    private void openFile(int position) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SmartList", excelModels.get(position).getName());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView textName;
        TextView textSize;

        ViewHolder(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.layout_item_excel);
            textName = (TextView) view.findViewById(R.id.text_item_excel_name);
            textSize = (TextView) view.findViewById(R.id.text_item_excel_size);
        }
    }

}
