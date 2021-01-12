package example.androidfoodie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Enumeration;
import java.util.UUID;

import example.androidfoodie.Common.Common;
import example.androidfoodie.Model.Complaint;
import example.androidfoodie.Model.User;

import static example.androidfoodie.OrderStatus.a;


public class MainActivity extends AppCompatActivity  {

    Button btnSignUp, btnSignIn, btnGuest;
    TextView txtSlogan;
    EditText edtPhone, edtPassword;

    TelephonyManager telephonyManager;

    static int PRMISSION_REQUEST = 0;
    final static  int REQUEST = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnGuest = (Button) findViewById(R.id.btnGuest);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        txtSlogan = (TextView) findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);
            }
        });


        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED){
                    requestRunTimePermissions();
                }else  if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_DENIED){
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Permission");
                    alertDialog.setMessage("Dear you need to allow phone permission");

                    alertDialog.setPositiveButton("Allow Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestRunTimePermissions();
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setNegativeButton("Deny Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    });
                    // add edit text to alert dialog

                    alertDialog.show();

                }
                else if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED){
                    telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    Common.IMEI = telephonyManager.getDeviceId();
                }
                else {
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE)
                            == PackageManager.PERMISSION_DENIED){
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        alertDialog.setTitle("Permission");
                        alertDialog.setMessage("Dear you need to allow phone permission");

                        alertDialog.setPositiveButton("Allow Permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestRunTimePermissions();
                                dialog.dismiss();
                            }
                        });
                        alertDialog.setNegativeButton("Deny Permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAffinity();
                            }
                        });
                        // add edit text to alert dialog

                        alertDialog.show();
                    }
                }
                if (Common.IMEI != null) {
                    Intent homeIntent = new Intent(MainActivity.this, Home.class);
                    Common.currentUser=null;
                    startActivity(homeIntent);
                    finish();
                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST:
                if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestRunTimePermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_PHONE_STATE
        }, PRMISSION_REQUEST);
    }
}
