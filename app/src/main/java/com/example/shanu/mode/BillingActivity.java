package com.example.shanu.mode;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BillingActivity extends AppCompatActivity {

    public int REQUEST_MEDICINE_NAME = 1;
    private RecyclerView mRecyclerView;
    private List<Item> mItemList = new ArrayList<>();
    private ItemAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        mAdapter = new ItemAdapter(mItemList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);


        Button addItemButton = (Button) findViewById(R.id.btn_add_item);
        Button paymentButton = (Button) findViewById(R.id.btn_payment);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BillingActivity.this, TestActivity.class);
                startActivityForResult(intent, REQUEST_MEDICINE_NAME);
            }
        });
    }

    @Override
    public void onBackPressed() {

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDICINE_NAME) {
            if (resultCode == RESULT_OK) {
                String result = data.getData().toString();
                try {
                    JSONObject jResult = new JSONObject(result);
                    String flag = jResult.getString("status");
                    if (flag.equalsIgnoreCase("Success"))
                    {
                        final String firstAnswer = jResult.getString("first");
                        final String secondAnswer = jResult.getString("second");
                        final String thirdAnswer = jResult.getString("third");

                        CharSequence options[] = new CharSequence[] {firstAnswer, secondAnswer, thirdAnswer,"None of the above"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Pick the correct medicine :");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0)
                                {
                                    Item item = new Item(firstAnswer, 10,105.75);
                                    mItemList.add(item);
                                    mAdapter.notifyDataSetChanged();
                                }
                                else if(which==1)
                                {
                                    Item item = new Item(secondAnswer, 20,411.24);
                                    mItemList.add(item);
                                    mAdapter.notifyDataSetChanged();
                                }
                                else if (which==2)
                                {
                                    Item item = new Item(thirdAnswer, 30,545.10);
                                    mItemList.add(item);
                                    mAdapter.notifyDataSetChanged();
                                }
                                else if (which==3)
                                {
                                    Toast.makeText(getApplicationContext(), "Try Again",
                                            Toast.LENGTH_LONG).show();
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                        builder.show();


                    }
                    else if(flag.equalsIgnoreCase("Failed")){


                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
