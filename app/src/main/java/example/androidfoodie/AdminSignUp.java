package example.androidfoodie;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import example.androidfoodie.Model.User;

public class AdminSignUp extends AppCompatActivity {
    MaterialEditText edtName, edtEmail, edtPhone, edtPassword, secretBox;
    Button btnSignUp;
    TextView term, code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);
        edtName = (MaterialEditText) findViewById(R.id.edtName);

        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        code = (TextView) findViewById(R.id.code);
        // in it firebase

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    final ProgressDialog mDialog = new ProgressDialog(AdminSignUp.this);
                    mDialog.setMessage("Please wait..");
                    mDialog.show();

                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // if already user has phone

                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                mDialog.dismiss();

                                Toast.makeText(AdminSignUp.this, "Phone number already register as a User", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();

                                User user = new User(edtName.getText().toString(), edtPassword.getText().toString(), "true");
                                table_user.child(code.getText().toString() + "" + edtPhone.getText().toString()).setValue(user);
//
                                Toast.makeText(AdminSignUp.this, "Sign Up successfully!!", Toast.LENGTH_SHORT).show();
//                                            finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }
        });
    }
}