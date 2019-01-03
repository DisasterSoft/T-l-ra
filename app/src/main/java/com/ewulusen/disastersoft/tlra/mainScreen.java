package com.ewulusen.disastersoft.tlra;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class mainScreen extends AppCompatActivity implements  CompoundButton.OnCheckedChangeListener {
    databaseHelper db;
    TextView ora,mai;
    Button hozzad,nullaz,email;
    EditText input;
    Switch kapcs;
    Boolean kapcsolo_be=false;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        db=new databaseHelper(this);
        hozzad=findViewById(R.id.bekuld);
        nullaz=findViewById(R.id.nullaz);
        ora=findViewById(R.id.ora);
        mai=findViewById(R.id.mai);
        kapcs=findViewById(R.id.switch1);
        if (kapcs != null) {
            kapcs.setOnCheckedChangeListener(this);
        }
        email=(Button) findViewById(R.id.email);
        email.setVisibility(View.INVISIBLE);

        if(!db.login().equals("default@d.d")) {
            email.setText(db.login());
            email.setVisibility(View.VISIBLE);
            kapcs.setChecked(true);
            kapcsolo_be=true;
        }
        else
        {
            email.setText(getString(R.string.email));
            email.setVisibility(View.INVISIBLE);
            kapcs.setChecked(false);
            kapcsolo_be=false;
        }
        ora.setText(db.getHour(db.login()));
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              shohAllertDialog();
            }
        });
        hozzad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    db.addHour(email.getText().toString(), mai.getText().toString());
                    ora.setText(db.getHour(db.login()));
                    if(isNetworkAvailable() && !db.login().equals("default@d.d") && kapcsolo_be) {
                        sendEmail(email.getText().toString(), mai.getText().toString(), ora.getText().toString());
                    }
                    mai.setText("");

            }
        });
        nullaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.nullaz(db.login());
                ora.setText(db.getHour(db.login()));
            }
        });

        mAdView = findViewById(R.id.adView);
        MobileAds.initialize(this, getString(R.string.admode));
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        mAdView.loadAd(adRequest);
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    /**
    Method use to chek if networ awible is.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    /**
     * Sending email
     */
    protected void sendEmail(String TO,String hour,String tulora) {
        String[] recipients = {TO};
        Intent emailIntent = new Intent(Intent.ACTION_SEND,Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_CC, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.emailSubjeckt));
        emailIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.emailBody1)+" "+hour+" "+
        this.getString(R.string.emailBody2)+" "+tulora+" "+this.getString(R.string.emailBody3));

        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(Intent.createChooser(emailIntent, this.getString(R.string.emailsend)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mainScreen.this, this.getString(R.string.emailError), Toast.LENGTH_SHORT).show();
        }
    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(isChecked) {
            email.setVisibility(View.VISIBLE);
            kapcsolo_be=true;
        } else {
            kapcsolo_be=false;
            email.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    public void shohAllertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.rename);
        builder.setMessage(R.string.itemNotNamed);
        input = new EditText(this);
        builder.setView(input);
        //Positiv button
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String txt = input.getText().toString();
                if(isEmailValid(txt)) {
                    db.addUser(txt);
                    email.setText(db.login());
                    ora.setText(db.getHour(db.login()));
                }
                else
                {
                    Toast.makeText(mainScreen.this, getString(R.string.notValidEmail), Toast.LENGTH_SHORT).show();

                }
            }
        });
        //Negatív button
        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //Create dialog
        AlertDialog ad = builder.create();
        ad.show();
    }
}
