package com.citu.qrcodescanner.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citu.qrcodescanner.R;
import com.citu.qrcodescanner.dao.EntryDao;
import com.citu.qrcodescanner.model.Entry;
import com.citu.qrcodescanner.util.ScannedItemUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by metalgear8019 on 8/21/2015.
 */
public class EntryAdapter extends BaseAdapter implements Filterable {

    private int action;
    private EntryDao dao;
    private ArrayList<Entry> mOriginalValues;
    private ArrayList<Entry> mDisplayedValues;
    private DateFormat dateFormat = DateFormat.getDateInstance();
    LayoutInflater inflater;

    public void updateView(ArrayList allEntries) {
        this.mOriginalValues = allEntries;
        this.mDisplayedValues = allEntries;
        notifyDataSetChanged();
    }


    private class ViewHolder {
        LinearLayout container;
        TextView data, date;
    }

    public EntryAdapter(Context context, ArrayList<Entry> allEntries) {
        this.mOriginalValues = allEntries;
        this.mDisplayedValues = allEntries;
        inflater = LayoutInflater.from(context);
        dao = new EntryDao(context);
    }

    @Override
    public int getCount() {
        return mDisplayedValues.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.view_entries, null);
            holder.container = (LinearLayout)convertView.findViewById(R.id.entryContainer);
            holder.data = (TextView) convertView.findViewById(R.id.textData);
            holder.date = (TextView) convertView.findViewById(R.id.textDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.data.setText(mDisplayedValues.get(position).getData());
        holder.date.setText(DateFormat.getDateInstance().format(new Date(mDisplayedValues.get(position).getDate())));

        holder.container.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final Entry clickedEntry = mDisplayedValues.get(position);
                action = ScannedItemUtil.getAction(clickedEntry.getData());
                Log.d("HistoryActivity", "action = " + action + " :: data = " + clickedEntry.getData());
                new AlertDialog.Builder(inflater.getContext())
                        .setTitle(DateFormat.getDateTimeInstance().format(new Date(mDisplayedValues.get(position).getDate())))
                        .setMessage(mDisplayedValues.get(position).getData())
                        .setPositiveButton("Delete Entry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                dao.deleteEntry(mDisplayedValues.get(position));
                                mDisplayedValues = (ArrayList) dao.getAllEntries();
                                mOriginalValues = (ArrayList) dao.getAllEntries();
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setNeutralButton(ScannedItemUtil.ACTIONS[action], new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                switch (action) {
                                    case 0:
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickedEntry.getData()));
                                        inflater.getContext().startActivity(browserIntent);
                                        break;
                                    default:
                                        ClipboardManager clipboard = (ClipboardManager) inflater.getContext().getSystemService(inflater.getContext().CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("label", clickedEntry.getData());
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(inflater.getContext(), "Data copied to clipboard!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setIcon(R.drawable.ic_action_name)
                        .show();
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mDisplayedValues = (ArrayList<Entry>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Entry> FilteredArrList = new ArrayList<Entry>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Entry>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).getData();
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(new Entry(mOriginalValues.get(i).getData()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

}
