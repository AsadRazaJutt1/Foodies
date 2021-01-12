package example.androidfoodie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import example.androidfoodie.Model.User;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;


public class VerifyUserNo extends AppCompatActivity {


    String verificationCodeBySystem;
    Button verify_btn;
    EditText phoneNoEnteredByTheUser;
    ProgressBar progressBar;
   // private FirebaseAuth mAuth;
    private Bundle bundle;
    private String bName,bEmail,bPhone,bPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user_no);


        //  final DatabaseReference table_admin = database.getReference("Admin");

        bundle = getIntent().getExtras();
        bName = bundle.getString("edtName");
        bEmail = bundle.getString("edtEmail");
        bPhone = bundle.getString("edtPhone");
        bPass = bundle.getString("edtPassword");

      //  mAuth = FirebaseAuth.getInstance();


        verify_btn = findViewById(R.id.verify_btn);
        phoneNoEnteredByTheUser = findViewById(R.id.verification_code_entered_by_user);
        progressBar = findViewById(R.id.progress_bar);


        progressBar.setVisibility(View.GONE);


        String phoneNo = getIntent().getStringExtra("edtPhone");


        sendVerificationCodeToUser(phoneNo);


        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String code = phoneNoEnteredByTheUser.getText().toString();


                if(code.isEmpty() || code.length()<6){
                    phoneNoEnteredByTheUser.setError("Wrong OTP...");
                    phoneNoEnteredByTheUser.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        });


    }


    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+92" + phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationCodeBySystem = s;
        }


        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {


            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyUserNo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    private void verifyCode(String codeByUser){


        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem,codeByUser);
        signInTheUserByCredentials(credential);


    }


    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        ;
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyUserNo.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //Intent intent = new Intent(getApplicationContext(),secondActivity.class);
                            //intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //startActivity(intent);
                            //etCode.setText("");
                            //Toast.makeText(PhoneAuthentication.this, "OK", Toast.LENGTH_LONG).show();
                            String bPhoneno=bPhone;
                            String fpassword=bPass;
                            firebaseAuth.createUserWithEmailAndPassword(bPhoneno, fpassword)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                //Log.d(TAG, "createUserWithEmail:success");
                                                //FirebaseUser user = mAuth.getCurrentUser();
                                                //updateUI(user);
                                                // Write a message to the database
                                                //Toast.makeText(PhoneAuthentication.this, "Account on FireBase Through phone", Toast.LENGTH_LONG).show();
                                                bundle = getIntent().getExtras();
                                                bName = bundle.getString("edtName");
                                                bEmail = bundle.getString("edtEmail");
                                                bPhone = bundle.getString("edtPhone");
                                                bPass = bundle.getString("edtPassword");
                                                //User user = new User(name,email,phone,pass);
                                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                final DatabaseReference table_user = database.getReference("User");
                                                //databaseReference.child(profile.getPhone()).setValue(profile);
                                                User user = new User(bName,bEmail,bPass);
////                                                table_user.child(.getText().toString()).setValue(user);
                                            table_user.child(bPhone).setValue(user);
                                                Toast.makeText(VerifyUserNo.this, "User Create", Toast.LENGTH_SHORT).show();
//                                                table_user.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                progressBar.setVisibility(View.INVISIBLE);
//                                                                //Toast.makeText(PhoneAuthentication.this, "Data Store on Database", Toast.LENGTH_SHORT).show();
//                                                                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                                                    // Apply activity transition
//                                                                    overridePendingTransition(R.anim.animation,R.anim.animation2);
//                                                                } else {
//                                                                    // Swap without transition
//                                                                }*/
////                                                                savePhoneNo();
//                                                                finish();
//                                                                startActivity(new Intent(getApplicationContext(),SignIn.class));
//                                                                FirebaseAuth.getInstance().signOut();
//                                                            }
//                                                        });
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                //updateUI(null);
                                                finish();
                                                startActivity(new Intent(VerifyUserNo.this , SignUp.class));
                                                Toast.makeText(VerifyUserNo.this, "Authentication failed.Try Again", Toast.LENGTH_SHORT).show();
                                            }

                                            // ...
                                        }

                                    });
                        }
                        else{
                            Toast.makeText(VerifyUserNo.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });


    }
//    private void savePhoneNo(){
//        String phone = bPhone;
//        SharedPreferences sp = getSharedPreferences(PHONE_CODE , 0);
//        sp.edit().putString(PHONE_NO , phone).apply();
//    }
}
