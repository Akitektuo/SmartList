package com.akitektuo.smartlist.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.communicator.TotalUpdateNotifier;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.model.ListModel;
import com.akitektuo.smartlist.util.Preference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.akitektuo.smartlist.util.Constant.KEY_AUTO_FILL;
import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.KEY_RECOMMENDATIONS;
import static com.akitektuo.smartlist.util.Constant.KEY_TOTAL_COUNT;
import static com.akitektuo.smartlist.util.Constant.handler;

/**
 * Created by AoD Akitektuo on 22-Aug-17 at 19:27.
 */

public class LightListAdapter extends RecyclerView.Adapter<LightListAdapter.ViewHolder> {

    private Context context;
    private List<ListModel> listModels;
    private DatabaseHelper database;
    private Preference preference;
    private TotalUpdateNotifier notifierTotal;

    public LightListAdapter(Activity activity, List<ListModel> listModels) {
        this.context = activity;
        this.listModels = listModels;
        preference = new Preference(activity);
        database = new DatabaseHelper(activity);
        notifierTotal = (TotalUpdateNotifier) activity;
    }

    @Override
    public LightListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_list_light, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ListModel listModel = listModels.get(position);
        holder.textNumber.setText(changeNumber(listModel.getNumber()));
        holder.editValue.setText(listModel.getValue());
        holder.textCurrency.setText(listModel.getCurrency());
        holder.editAutoProduct.setText(listModel.getProduct());
        holder.editAutoProduct.setSingleLine();
        switch (listModel.getButtonType()) {
            case 0:
                holder.buttonSave.setVisibility(View.VISIBLE);
                holder.buttonDelete.setVisibility(View.GONE);
                break;
            case 1:
                holder.buttonSave.setVisibility(View.GONE);
                holder.buttonDelete.setVisibility(View.VISIBLE);
                break;
            default:
                holder.buttonSave.setVisibility(View.VISIBLE);
                holder.buttonDelete.setVisibility(View.VISIBLE);
        }
        refreshList(holder.editAutoProduct);
        holder.editAutoProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                if (holder.editValue.getText().toString().isEmpty() && preference.getPreferenceBoolean(KEY_AUTO_FILL)) {
                    handler.post(new Runnable() {
                        public void run() {
                            int mostUsedPrice = database.getCommonPriceForProduct(adapterView.getItemAtPosition(i).toString());
                            if (mostUsedPrice != 0) {
                                holder.editValue.setText(String.valueOf(mostUsedPrice));
                            }
                        }
                    });
                }
            }
        });
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderDelete = new AlertDialog.Builder(context);
                builderDelete.setTitle("Delete item");
                builderDelete.setMessage(String.format("Are you sure you want to delete %1$s, item number %2$d?", listModel.getProduct(), listModel.getNumber()));
                builderDelete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        preference.setPreference(KEY_TOTAL_COUNT, String.valueOf(Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT)) - Double.parseDouble(listModels.get(holder.getAdapterPosition()).getValue())));
                        database.deleteList(holder.getAdapterPosition() + 1);
                        updateDatabase(remove(holder.getAdapterPosition()));
                        notifierTotal.listChanged(false);
                    }
                });
                builderDelete.setNegativeButton("Cancel", null);
                AlertDialog dialogDelete = builderDelete.create();
                dialogDelete.show();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                holder.buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.editAutoProduct.clearFocus();
                        holder.editValue.clearFocus();
                        if (holder.editValue.getText().toString().isEmpty() || holder.editAutoProduct.getText().toString().isEmpty()) {
                            Toast.makeText(context, "Fill in all fields.", Toast.LENGTH_SHORT).show();
                        } else {
                            int lastItem = listModels.size();
                            String value = holder.editValue.getText().toString();
                            String product = holder.editAutoProduct.getText().toString();
                            if (holder.getAdapterPosition() + 1 == lastItem) {
                                database.addList(lastItem, value, product, new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));
                                preference.setPreference(KEY_TOTAL_COUNT, String.valueOf(Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT)) + Double.parseDouble(value)));
                                insert(listModels.size(), lastItem, value, product);
                                notifierTotal.listChanged(true);
                            } else {
                                database.updateList(holder.getAdapterPosition() + 1, listModel.getNumber(), value, product);
                                preference.setPreference(KEY_TOTAL_COUNT, String.valueOf(Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT)) + Double.parseDouble(value) - Double.parseDouble(listModels.get(holder.getAdapterPosition()).getValue())));
                                change(holder.getAdapterPosition(), listModel.getNumber(), value, product);
                                notifierTotal.listChanged(false);
                            }
                            database.updatePrices(holder.editAutoProduct.getText().toString(),
                                    holder.editValue.getText().toString());
                            refreshList(holder.editAutoProduct);
                        }
                    }
                });
            }
        }).start();
        holder.editValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (listModel.getButtonType() != 0) {
                    holder.buttonSave.setVisibility(View.VISIBLE);
                    holder.buttonDelete.setVisibility(View.VISIBLE);
                } else {
                    holder.buttonDelete.setVisibility(View.GONE);
                }
            }
        });
        holder.editAutoProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (listModel.getButtonType() != 0) {
                    holder.buttonSave.setVisibility(View.VISIBLE);
                    holder.buttonDelete.setVisibility(View.VISIBLE);
                } else {
                    holder.buttonDelete.setVisibility(View.GONE);
                }
            }
        });
    }

    private String changeNumber(int num) {
        if (num < 10) {
            return " " + num;
        }
        return String.valueOf(num);
    }

    private void updateDatabase(int position) {
        int lastIndex = listModels.size();
        for (int i = position; i < lastIndex; i++) {
            ListModel listModel = listModels.get(i);
            listModel.decrementNumber();
            database.updateList(i + 2, listModel.getNumber(), listModel.getValue(), listModel.getProduct());
            change(i, listModel);
        }
    }

    private void refreshList(final AutoCompleteTextView autoCompleteTextView) {
        if (preference.getPreferenceBoolean(KEY_RECOMMENDATIONS)) {
            handler.post(new Runnable() {
                public void run() {
                    ArrayList<String> list = new ArrayList<>();
                    Cursor cursor = database.getUsage();
                    if (cursor.moveToFirst()) {
                        do {
                            list.add(cursor.getString(0));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
                    autoCompleteTextView.setAdapter(adapter);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    public int remove(int position) {
        listModels.remove(position);
        notifyItemRemoved(position);
        return position;
    }

    public void insert(int position, int number, String value, String product) {
        change(position - 1, number, value, product);
        listModels.add(new ListModel(listModels.size() + 1, "", preference.getPreferenceString(KEY_CURRENCY), "", 0));
        notifyItemInserted(position);
    }

    public void change(int position, int number, String value, String product) {
        listModels.set(position, new ListModel(number, value, preference.getPreferenceString(KEY_CURRENCY), product, 1));
        notifyItemChanged(position);
    }

    public void change(int position, ListModel model) {
        listModels.set(position, model);
        notifyItemChanged(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNumber;
        EditText editValue;
        TextView textCurrency;
        AutoCompleteTextView editAutoProduct;
        Button buttonDelete;
        Button buttonSave;

        ViewHolder(View view) {
            super(view);
            textNumber = view.findViewById(R.id.text_item_light_number);
            editValue = view.findViewById(R.id.edit_item_light_value);
            textCurrency = view.findViewById(R.id.text_item_light_currency);
            editAutoProduct = view.findViewById(R.id.edit_auto_item_light_product);
            buttonDelete = view.findViewById(R.id.button_light_delete);
            buttonSave = view.findViewById(R.id.button_light_save);
        }
    }

}
