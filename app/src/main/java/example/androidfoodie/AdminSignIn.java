package example.androidfoodie;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import example.androidfoodie.Common.Common;
import example.androidfoodie.Model.User;

public class AdminSignIn extends AppCompatActivity {
    EditText edtPhone, edtPassword;
    Button btnSignIn;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_in);

        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        textView = (TextView) findViewById(R.id.forget_password);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog mDialog = new ProgressDialog(AdminSignIn.this);
                mDialog.setMessage("Please wait..");
                mDialog.show();

                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //check if user does not exist
                        if (dataSnapshot.child("+"+edtPhone.getText().toString()).exists()) {
                            // get user info
                            mDialog.dismiss();
                            User user = dataSnapshot.child("+"+edtPhone.getText().toString()).getValue(User.class);
                            user.setPhone("+"+edtPhone.getText().toString());  //set Phone
//                            Toast.makeText(SignIn.this, "User", Toast.LENGTH_SHORT).show();

                            if (user.getPassword().equals(edtPassword.getText().toString())) {

                                if (Boolean.parseBoolean(user.getIsStaff())) {
                                    Toast.makeText(AdminSignIn.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AdminSignIn.this, "Sig in With Staff Account", Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                }
                            } else {
                                Toast.makeText(AdminSignIn.this, "Wrong password!!", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(AdminSignIn.this, "Not Exist", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void forget_password(View view) {
        Toast.makeText(this, "Add Soon", Toast.LENGTH_SHORT).show();
    }
}