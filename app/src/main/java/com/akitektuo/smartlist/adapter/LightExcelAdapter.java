package com.akitektuo.smartlist.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.communicator.ImportNotifier;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.model.ExcelModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

/**
 * Created by AoD Akitektuo on 22-Aug-17 at 19:27.
 */

public class LightExcelAdapter extends RecyclerView.Adapter<LightExcelAdapter.ViewHolder> {

    private List<ExcelModel> excelModels;
    private Context context;
    private DatabaseHelper database;
    private ImportNotifier importNotifier;

    public LightExcelAdapter(Activity activity, List<ExcelModel> excelModels) {
        this.context = activity;
        this.importNotifier = (ImportNotifier) activity;
        this.excelModels = excelModels;
        database = new DatabaseHelper(activity);
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
                openOptions(holder.getAdapterPosition());
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

    private void openOptions(final int position) {
        AlertDialog.Builder builderCurrency = new AlertDialog.Builder(context);
        builderCurrency.setTitle("Select action");
        builderCurrency.setItems(R.array.actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        openFile(position);
                        break;
                    case 1:
                        importFromExcel(position);
                        break;
                    case 2:
                        shareFile(position);
                        break;
                    case 3:
                        deleteFile(position);
                        break;
                    case 4:
                        showPath(position);
                        break;
                }
            }
        });
        builderCurrency.setNeutralButton("Cancel", null);
        AlertDialog alertDialogCurrency = builderCurrency.create();
        alertDialogCurrency.show();
    }

    @Override
    public int getItemCount() {
        return excelModels.size();
    }

    private void openFile(int position) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SmartList", excelModels.get(position).getName());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".com.akitektuo.smartlist", file), "application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showPath(int position) {
        AlertDialog.Builder builderDelete = new AlertDialog.Builder(context);
        builderDelete.setTitle("Path for " + excelModels.get(position).getName());
        builderDelete.setMessage(Environment.getExternalStorageDirectory() + File.separator + "SmartList");
        builderDelete.setNegativeButton("Close", null);
        AlertDialog dialogDelete = builderDelete.create();
        dialogDelete.show();
    }

    private void shareFile(int position) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SmartList", excelModels.get(position).getName());
        intentShareFile.setType("application/vnd.ms-excel");
        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }

    private void importFromExcel(int position) {
        final File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SmartList", excelModels.get(position).getName());
        final WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(Locale.getDefault());
        try {
            Workbook workbook = Workbook.getWorkbook(file, wbSettings);
            Sheet sheet = workbook.getSheet(0);
            int length = sheet.getRows();
            if (sheet.getCell(0, length - 1).getContents().equals("Total")) {
                length -= 2;
            }
            boolean isDate = sheet.getColumns() == 3;
            String date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
            for (int i = 1; i < length; i++) {
                if (isDate) {
                    date = sheet.getCell(2, i).getContents();
                }
                database.addList(database.getListNumberNew(), sheet.getCell(0, i).getContents(), sheet.getCell(1, i).getContents(), date);
                database.updatePrices(sheet.getCell(1, i).getContents(), sheet.getCell(0, i).getContents());
            }
            importNotifier.refreshList();
        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView textName;
        TextView textSize;

        ViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_item_excel);
            textName = view.findViewById(R.id.text_item_excel_name);
            textSize = view.findViewById(R.id.text_item_excel_size);
        }
    }

}
