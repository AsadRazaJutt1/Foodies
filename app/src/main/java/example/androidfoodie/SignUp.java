package example.androidfoodie;

//import androidx.annotation.NonNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import example.androidfoodie.Common.Common;
import example.androidfoodie.Database.ForDirectOrder;
import example.androidfoodie.Model.Admin;
import example.androidfoodie.Model.Request;
import example.androidfoodie.Model.User;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtName, edtEmail, edtPhone, edtPassword, secretBox;
    Button btnSignUp;
    TextView term, code;
    CheckBox checkBox;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        term = (TextView) findViewById(R.id.term);
        checkBox = (CheckBox) findViewById(R.id.checked);
        code = (TextView) findViewById(R.id.code);

        term.setText("Terms \u0026 Conditions");
        term.setTextColor(this.getResources().getColor(R.color.colorAccent));
        // in it firebase

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Please wait..");
                    mDialog.show();

                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // if already user has phone

                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                mDialog.dismiss();

                                Toast.makeText(SignUp.this, "Phone number already register as a User", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();

                                User user = new User(edtName.getText().toString(), edtPassword.getText().toString(),"false");
                                table_user.child(code.getText().toString() + "" + edtPhone.getText().toString()).setValue(user);
//
                                Toast.makeText(SignUp.this, "Sign Up successfully!!", Toast.LENGTH_SHORT).show();
//                                            finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setMessage("You must agreed on the Terms and Conditions.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
        term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, TermAndCondition.class);
                startActivity(intent);

            }

        });
    }
}
