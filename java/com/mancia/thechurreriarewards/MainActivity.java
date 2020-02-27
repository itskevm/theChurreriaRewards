package com.mancia.thechurreriarewards;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**************************************************************
    Executed with the rewards button, brings up the rewards screen.
     **************************************************************/
    public void goToRewards(View view) {
        Intent intent = new Intent(this, Rewards.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**************************************************************
     Executed with the rewards button, brings up the rewards screen.
     **************************************************************/
    public void goToBuild(View view) {
        Intent intent = new Intent(this, BuildYourOwn.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**************************************************************
     Executed with the phone icon, sends Churreria phone number to
     the user's dialer of choice
     **************************************************************/
    public void goToDialer(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:(208) 227-3375"));
        startActivity(intent);

    }

    /**************************************************************
     Executed with the navigation button, sends the address to Google Maps.
     **************************************************************/
    public void goToNavigation(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("geo:43.825385,-111.788611?q=the+churreria"));
        startActivity(intent);
    }

    /**************************************************************
     Executed with the deals button, brings user to either Glasscard or Play Store.
     **************************************************************/
    public void goToGlasscard(View view) {
        if (isPackageInstalled("org.glasscard.glasscard")) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("org.glasscard.glasscard");
            startActivity(launchIntent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(
                    "https://play.google.com/store/apps/details?id=org.glasscard.glasscard"));
            intent.setPackage("com.android.vending");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /********************************************************
     * Method: isPackageInstalled
     * @param packageName is a string containing the name of the
     *                    package in question (ie: Glass Card).
     *
     * @return indicates if the package is available on the device.
     ********************************************************/
    private boolean isPackageInstalled(String packageName) {
        boolean available = true;
        try {
            getPackageManager().getPackageInfo(packageName,0);
        }
        catch (PackageManager.NameNotFoundException e) {
            available = false;
        }
        return available;
    }
}
