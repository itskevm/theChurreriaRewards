package com.mancia.thechurreriarewards;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Rewards extends AppCompatActivity {

    public static TextView resultTextView = null;
    Button scan_btn = null;
    Button force;
    Button redeem = null;
    private static final int REQUEST_CAMERA = 1;
    int currentPurchaseCount = 0;
    boolean currentRedemptionState = true;
    public static final int DEF = 0;

    List<ImageView> churroView = new ArrayList<>();
    String[] redemptionCode = new String[]
            {"155wmsab", "i83440ab", "2#3375ab",
                    "open18ab", "loco17ab"};

    //ArrayList<Integer> array_image = new ArrayList<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
            } else {
                Toast.makeText(this,"Permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        // Populate the list of ImageView created at the start
        churroView.add((ImageView)findViewById(R.id.imageView1)); // slot 0
        churroView.add((ImageView)findViewById(R.id.imageView2)); // slot 1
        churroView.add((ImageView)findViewById(R.id.imageView3)); // ...
        churroView.add((ImageView)findViewById(R.id.imageView4));
        churroView.add((ImageView)findViewById(R.id.imageView5));
        churroView.add((ImageView)findViewById(R.id.imageView6));
        churroView.add((ImageView)findViewById(R.id.imageView7));
        churroView.add((ImageView)findViewById(R.id.imageView8));
        churroView.add((ImageView)findViewById(R.id.imageView9));
        churroView.add((ImageView)findViewById(R.id.imageView10));
        churroView.add((ImageView)findViewById(R.id.imageView11)); // ...
        churroView.add((ImageView)findViewById(R.id.imageView12)); // slot 11

        // Loading saved data: number of churros previously purchase, and coupon was redeemed
        SharedPreferences sharedPreferences=getSharedPreferences("userData",Context.MODE_PRIVATE);
        int savedPurchaseCount=sharedPreferences.getInt("storedPurchaseCount", DEF);
        boolean savedRedemptionState=sharedPreferences.getBoolean("storedRedemptionState", false);
        Log.i("Saved.PurchaseCount", "Activity began with count: " + savedPurchaseCount);
        Log.i("Saved.RedemptionState", "Activity began with state: " + savedRedemptionState);

        // Draw a certain amount of good churros based on previous purchase count
        for (int i = 0; i < savedPurchaseCount; i++) {
            // Valid up to savedPurchaseCount == 11 to draw 12 churros
            churroView.get(i).setImageResource(R.drawable.goodchurro);
        }

        // Set button state based on previous purchase count and previous redemption state
        redeem = (Button) findViewById(R.id.redeem_btn); // allows for manipulation of this button
        scan_btn = (Button) findViewById(R.id.btn_scan); // allows for manipulation of this button
        setButtonState(savedPurchaseCount, savedRedemptionState); // cannot come earlier in the code

        // Set local variables from saved data
        currentPurchaseCount = savedPurchaseCount;
        currentRedemptionState = savedRedemptionState;

        // activating result text view as variable and verifying the text its holding
        resultTextView = (TextView)findViewById(R.id.result_text);
        System.out.println("text currently is: " + resultTextView.getText());

        // Telling the scan button what to do upon being pressed
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Will ask for permission if not already granted (every first installation)
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Rewards.this);
                    builder.setMessage("Please allow for camera access on next screen.");
                    builder.setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        // Telling the redeem button what to do upon being pressed
        redeem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                new AlertDialog.Builder(Rewards.this)
                        .setMessage("Are you sure you want to use your coupon?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // The following line ensures that the program will temporarily
                                // remember that you have redeemed your last coupon. Since there
                                // is only one load function in onCreate, the program will never
                                // read the saved data until next time the app starts, therefore
                                // this is a very important part of the logic for the app to keep
                                // the Redeem text as N/A (disabled state handled elsewhere).
                                currentRedemptionState = true;
                                displayRedemptionUse(redeem.getText().toString().toLowerCase());
                                setButtonState(currentPurchaseCount,true);
                                Log.i("Clicked", "You have pressed yes");
                                saveRedemptionState(true);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        // This method simulates the purchase of a churro
        force = (Button) findViewById(R.id.forcechange);
        force.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                recordPurchase();
            }
        });
    }

    @Override
    protected void onResume() {
        System.out.println("resume");
        System.out.println("text currently is: " + resultTextView.getText());
        super.onResume();
    }

    @Override
    protected void onStart() {
        System.out.println("start");
        System.out.println("text currently is: " + resultTextView.getText());
        super.onStart();
    }

    @Override
    protected void onRestart() {
        System.out.println("restart");
        System.out.println("PRE text currently is: " + resultTextView.getText());
        super.onRestart();

        // Check if a successful made has been with redemption codes
            if (resultTextView.getText().toString() != getText(R.string.rewardsPrompt)) {
                System.out.println("Change in string detected");
                if (verifyScannedCode()) {
                    Toast.makeText(this, "You have recorded one purchase!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Incorrect code scanned!", Toast.LENGTH_LONG).show();
                }
            }

        // Resets the text before the user can see the screen again (would display scanned code).
        resultTextView.setText(R.string.rewardsPrompt);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onPause() {
        System.out.println("pause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        System.out.println("stop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.out.println("destroy");
        super.onDestroy();
    }

    /********************************************************
     * Method: recordPurchase
     * Called each time a QR code has been successfully scanned.
     * Iterates to the next slot in the list of ImageView slots
     * to set the next slot as a coloured churro (purchased).
     * It also sets the state of the redeem button and saves
     * the user's purchase count.
     * If the user has somehow exceeded the amount of purchases
     * allowed, the count will be reset.
     ********************************************************/
    public void recordPurchase() {
        // This resets the churro count if you buy without resetting the count.
        if (currentPurchaseCount > 11) {
            currentPurchaseCount = resetPurchaseCount();
            Log.i("Current.PurchaseCount","Count was just set to: " + currentPurchaseCount);
            scan_btn.setEnabled(true);
        } else { // This counts your churro purchase and colours in a churro.
            churroView.get(currentPurchaseCount).setImageResource(R.drawable.goodchurro);
            currentPurchaseCount++;
        }
        // This ensures that when your purchase count reaches a coupon checkpoint (4, 8, or 12),
        // the app acknowledges that you have not yet redeemed the coupon you have just earned.
        // This data is saved incase the user chooses not to use it during this instance.
        if (currentPurchaseCount == 4 || currentPurchaseCount == 8 || currentPurchaseCount == 12) {
            currentRedemptionState = false;
            saveRedemptionState(currentRedemptionState);
        }

        Log.i("setButtonState","User about to start method. User redeemed? " + currentRedemptionState);
        setButtonState(currentPurchaseCount, currentRedemptionState); // alerts you if you have a coupon to redeem
        savePurchaseCount(currentPurchaseCount);
    }

    /********************************************************
     * Method: setButtonState
     * @param userCount     is a number that takes in the amount
     *                      of churros purchased.
     * @param userRedeemed  is a boolean value indicating whether or
     *                      not the user has redeemed their coupon
     *                      already.
     ********************************************************/
    public void setButtonState(int userCount, boolean userRedeemed) {
        Log.i("setButtonState","This method was called. User count? " + userCount);
        Log.i("setButtonState","This method was called. User redeemed? " + userRedeemed);
        switch (userCount) {
            case 4:
                // 25% off
                Log.i("SwitchC.4", "Count: " + userCount + " State: " + userRedeemed);
                if (!userRedeemed) {
                    redeem.setEnabled(true);
                    redeem.setText(R.string.discount25);
                } else {
                    redeem.setEnabled(false);
                    redeem.setText(R.string.redeem);
                }
                break;
            case 8:
                // 50% off
                Log.i("SwitchC.8", "Count: " + userCount + " State: " + userRedeemed);
                if (!userRedeemed) {
                    redeem.setEnabled(true);
                    redeem.setText(R.string.discount50);
                } else {
                    redeem.setEnabled(false);
                    redeem.setText(R.string.redeem);
                }
                break;
            case 12:
                // free churro
                Log.i("SwitchC.12", "Count: " + userCount + " State: " + userRedeemed);
                if (!userRedeemed) {
                    redeem.setEnabled(true);
                    redeem.setText(R.string.discount100);
                    scan_btn.setEnabled(false);
                } else {
                    redeem.setEnabled(false);
                    scan_btn.setEnabled(true);
                    redeem.setText(R.string.redeem);
                    currentPurchaseCount = resetPurchaseCount();
                    savePurchaseCount(currentPurchaseCount);
                }
                break;
            default:
                Log.i("SwitchC.Default","Occurred at count: " + userCount);
                Log.i("SwitchC.Default","Occurred at state: " + userRedeemed);

                // Range to set button state and text based on the userCount and userRedeemed
                if (userRedeemed || userCount < 4) {
                    System.out.println("if #1");
                    redeem.setEnabled(false);
                    redeem.setText(R.string.redeem);
                } else if (userCount > 4 && userCount < 8 && !userRedeemed) {
                    System.out.println("if #2");
                    //Toast.makeText(this, "enabled 4-8 range", Toast.LENGTH_LONG).show();
                    //Log.i("SC.Def","supposed to toast 1");
                    redeem.setText(R.string.discount25);
                } else if (userCount > 8 && userCount < 12 && !userRedeemed) {
                    System.out.println("if #3");
                    //Toast.makeText(this, "enabled 8-12 range", Toast.LENGTH_LONG).show();
                    //Log.i("SC.Def","supposed to toast 2");
                    redeem.setText(R.string.discount50);
                }
                break;
        }
    }

    /********************************************************
     * Method: saveRedemptionState
     * Takes in a boolean and saves it to SharedPreferences as
     * whether or not the user has redeemed a coupon.
     * @param saveState is a passed boolean value indicating at what
     *                  state the user has left the redeem status.
     ********************************************************/
    public void saveRedemptionState(boolean saveState) {
        SharedPreferences sharedPreferences =
                getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("storedRedemptionState", saveState);
        editor.apply();
        Log.i("Data Saved", "RedemptionState was saved as: " + saveState);
    }

    /********************************************************
     * Method: savePurchaseCount
     * Takes in a number and saves it to SharedPreferences as
     * the amount of churros the user has purchased.
     * @param saveCount is an integer passed to this function for
     *                  the app to know what number to save.
     ********************************************************/
    public void savePurchaseCount(int saveCount) {
        SharedPreferences sharedPreferences =
                getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("storedPurchaseCount", saveCount);
        editor.apply();
        Log.i("Data Saved", "PurchaseCount was saved as: " + saveCount);
    }

    /********************************************************
     * Method: resetPurchaseCount
     * Resets all of the ImageViews on the screen that have churro
     * icons to their initial source, which is a black and white
     * cartoon churro. This will indicate the user has restarted
     * their purchase count.
     *
     * @return the number zero, to which the purchase count is set.
     ********************************************************/
    public int resetPurchaseCount() {
        for (int k = 0; k < 12; k++) {
            churroView.get(k).setImageResource(R.drawable.badchurro);
        }
        return 0;
    }

    /********************************************************
     * Method: displayRedemptionUse
     * Displays a longer lasting toast notification confirming that
     * the user has just redeemed using the redeem button.
     * @param couponName    a string that the redeem button holds
     *                      just before it is reset to its disabled
     *                      value (No coupon available).
     ********************************************************/
    public void displayRedemptionUse(String couponName) {
        Toast.makeText(this, "Coupon used for " + couponName, Toast.LENGTH_LONG).show();
    }

    /********************************************************
     * Method: verifyScannedCode
     * Runs a verification FOR loop that iterates through the
     * apps array of accepted string values for redemption
     * codes. Only if a match is found, the loop will break and
     * the method's boolean will be changed from false to                                                                                                                                                                                                                   true.
     * @return a boolean value based on match success.
     ********************************************************/
    public boolean verifyScannedCode() {
        System.out.println("you just verified");
        boolean match = false;
        for (int i = 0; i < redemptionCode.length - 1; i++) {
            if (redemptionCode[i].contentEquals(resultTextView.getText())) {
                System.out.println("match found");
                match = true;
                System.out.println("POST text currently is: " + resultTextView.getText());
                recordPurchase();
                break;
            }
        }
        return match;
    }
}
