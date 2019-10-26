package com.meraz.personalassistant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.meraz.personalassistant.R;

public class LoginActivity extends AppCompatActivity {
    boolean valid = true;

    private TextView tvForgot,tvCreate;
    private EditText edtEmail,edtPassword,edtRegE,edtRegP;
    private Button btnLogin;

    private String email,password,emailReg,passwordReg;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvForgot = findViewById(R.id.forgot);
        tvCreate = findViewById(R.id.createAcc);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);



        mAuth = FirebaseAuth.getInstance();

        initialize();

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this,ResetPassActivity.class));
            }
        });

        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                // Get the layout inflater
                LayoutInflater inflater = LoginActivity.this.getLayoutInflater();

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                final View view = inflater.inflate(R.layout.registration_dialog, null);
                builder.setView(view)
                        // Add action buttons
                        .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                edtRegE = view.findViewById(R.id.emailReg);
                                edtRegP = view.findViewById(R.id.passwordReg);

                                emailReg = edtRegE.getText().toString();
                                passwordReg = edtRegP.getText().toString();

                                if (emailReg.isEmpty() && passwordReg.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emailReg).matches()){
                                    Toast.makeText(LoginActivity.this,"Please Enter email and password correctly",Toast.LENGTH_LONG).show();

                                }else {
                                    mAuth.createUserWithEmailAndPassword(emailReg,passwordReg)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(LoginActivity.this,"Registration Successful.Please Login",Toast.LENGTH_LONG).show();
                                                    }else {
                                                        Toast.makeText(LoginActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               dialog.dismiss();
                            }
                        }).create();
                builder.show();


            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize();
                if (validate()){

                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_LONG).show();

                        }
                    });
                } else Toast.makeText(LoginActivity.this,"Enter your info correctly",Toast.LENGTH_LONG).show();

            }
        });

    }

    private void initialize(){
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();

    }

    private boolean validate(){

        if (email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError("Please enter your email correctly");
            valid = false;
        }
        if (password.isEmpty()){
            edtPassword.setError("Please enter password");
            valid = false;
        }
        return valid;
    }
}
