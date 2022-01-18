package com.example.accesslevels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity {
    private ImageView previousPage;
    private Button notRegistered;
    CardView signIn;
    TextInputLayout loginEmail,loginPassword,loginConfirmPwd;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        loginEmail=findViewById(R.id.loginEmail);
        loginPassword=findViewById(R.id.loginPassword);
        loginConfirmPwd=findViewById(R.id.loginConfirmPwd);

        previousPage =findViewById(R.id.backArrowLogin);
        signIn=findViewById(R.id.btnLogin);
        notRegistered=findViewById(R.id.textviewSignUpLgn);

        previousPage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(LogIn.this,Start.class);
                startActivity(intent);
            }
        });

        notRegistered.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(LogIn.this,Register.class);
                startActivity(intent);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            validateEmail(loginEmail);
            validatePassword(loginPassword);
            validateConfirmPwd(loginConfirmPwd);

            if (!validateEmail(loginEmail) |!validatePassword(loginPassword)|!validateConfirmPwd(loginConfirmPwd))
            {
                return;
            }
            else
                {
                    UserLoginDetails();
                }
            }
        });
    }

    private void UserLoginDetails()
    {
        fAuth.signInWithEmailAndPassword(loginEmail.getEditText().getText().toString(),loginPassword.getEditText().getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                {
                    @Override
                    public void onSuccess(AuthResult authResult)
                    {
                        checkUserType(authResult.getUser().getUid());
                       Toast.makeText(LogIn.this,"Logged in successfully",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(LogIn.this, new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(LogIn.this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUserType(String uid)
    {
        //Extract data from the document,this includes the email address,phone number,username
        DocumentReference documentReference=fStore.collection("Users").document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
               Log.d("TAG","onSuccess" + documentSnapshot.getData());
               //here,we identify the type of user,admin or normal user
                if (documentSnapshot.getString("isAdmin") !=null)
                {
                    //the user is an admin
                    startActivity(new Intent(getApplicationContext(),AdminPanel.class));
                    finish();
                }
                if (documentSnapshot.getString("isUser") !=null)
                {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(LogIn.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validateEmail(TextInputLayout loginEmail)
    {
        String val=loginEmail.getEditText().getText().toString().trim();
        String emailMatcher="a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+";
        if (val.isEmpty())
        {
            loginEmail.setError("This field cannot be empty!");
            return false;
        }
        if(val.matches(emailMatcher))
        {
            loginEmail.setError("Provide a valid email address");
            return false;
        }
        else
            {
                loginEmail.setError(null);
                loginEmail.setErrorEnabled(false);
                return true;
            }
    }
    public boolean validatePassword(TextInputLayout loginPassword)
    {
        String val=loginPassword.getEditText().getText().toString().trim();
        if (val.isEmpty())
        {
            loginPassword.setError("This field cannot be empty!");
            return false;
        }
        else if (val.length()<6 || val.length()>15)
        {
            loginPassword.setError("Password should be between 6-15 characters long");
            return  false;
        }
        else
            {
                loginPassword.setError(null);
                loginPassword.setErrorEnabled(false);
                return true;
            }
    }
    private boolean validateConfirmPwd(TextInputLayout confirmPwd)
    {
        String val=loginConfirmPwd.getEditText().getText().toString().trim();
        if (val.isEmpty())
        {
            loginConfirmPwd.setError("This field cannot be empty");
            return false;
        }

        else if (val.length()<6||val.length()>15)
        {
            loginConfirmPwd.setError("Password should be 6-15 characters long");
            return false;
        }
        else loginConfirmPwd.setError(null);
        loginConfirmPwd.setErrorEnabled(false);
        return true;

    }

}