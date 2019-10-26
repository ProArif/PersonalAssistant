package com.meraz.personalassistant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.meraz.personalassistant.R;

public class ResetPassActivity extends AppCompatActivity {

    private EditText edt_email;
    private Button btn_reset;
    private String email;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        mAuth = FirebaseAuth.getInstance();

        edt_email = findViewById(R.id.reset_email);
        btn_reset = findViewById(R.id.reset);



        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edt_email.getText().toString();
                if (!email.isEmpty()){
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            View view = findViewById(R.id.resetActivity);

                            if (task.isSuccessful()){
                                Snackbar.make(view,"Password Reset Email send.Please check your email",Snackbar.LENGTH_LONG)
                                        .show();
                            }else Snackbar.make(view,task.getException().toString(),Snackbar.LENGTH_LONG).show();
                        }
                    });
                }else
                {
                    Toast.makeText(ResetPassActivity.this,"Enter your email first",Toast.LENGTH_LONG).show();
                }

            }
        });


    }
}
