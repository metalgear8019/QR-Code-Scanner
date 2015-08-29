package com.citu.qrcodescanner.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.citu.qrcodescanner.R;
import com.citu.qrcodescanner.adapter.EntryAdapter;
import com.citu.qrcodescanner.dao.EntryDao;
import com.citu.qrcodescanner.model.Entry;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EntryDao dao;
    private ListView lv;
    private ArrayList<Entry> entries;
    private EntryAdapter adapter;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        lv = (ListView) findViewById(R.id.listView1);
        et = (EditText) findViewById(R.id.editText1);
        dao = new EntryDao(this.getApplicationContext());

        entries = (ArrayList) dao.getAllEntries();

        adapter = new EntryAdapter(MainActivity.this, entries);
        lv.setAdapter(adapter);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                dao = new EntryDao(this.getApplicationContext());
                dao.addEntry(new Entry(result.getContents()));
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned code successfully!", Toast.LENGTH_LONG).show();
                updateHistory();
            }
        } else {
            Log.d("MainActivity", "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void scanCode(View paramView) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(false);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.initiateScan();
    }

    public void updateHistory() {
        adapter.updateView((ArrayList) dao.getAllEntries());
    }
}
