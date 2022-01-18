package com.example.accesslevels;

import static com.example.accesslevels.R.color.design_default_color_primary;
import static com.example.accesslevels.R.color.design_default_color_primary_variant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    Button buttonSignUpReg;
    TextInputLayout username,email,phone,password;
    CardView registerBtn;
    ImageView backBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CheckBox buy,sell;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        registerBtn = findViewById(R.id.btnRegister);
        buttonSignUpReg = findViewById(R.id.buttonSignUpRegister);

        username = findViewById(R.id.regUsername);
        email = findViewById(R.id.regEmail);
        phone = findViewById(R.id.regPhone);
        password = findViewById(R.id.regPwd);

        buy=findViewById(R.id.isBuyer);
        sell=findViewById(R.id.isAdmin);

        backBtn = findViewById(R.id.backArrowRegister);

        //check if the checkboxes are checked via this method
        isBuyer();
        isAdmin();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Register.this,Start.class);
                startActivity(intent);
            }
        });

        buttonSignUpReg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            startActivity(new Intent(getApplicationContext(), LogIn.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validateUsername(username);
                validateEmail(email);
                validatePhone(phone);
                validatePassword(password);
                validateCheckboxes();

                if (!validateUsername(username) | !validateEmail(email) | !validatePhone(phone)
                        | !validatePassword(password)|!validateCheckboxes())
                {
                    return;
                }

                else {
                    UserRegistrationFunction();
                }


            }
        });

    }

    private void isBuyer() //if the buy checkbox is checked,the sell checkbox cannot be checked
    {
        buy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
               sell.setChecked(false);
            }
        });
    }

    private void isAdmin() //if the sell checkbox is checked,the buy checkbox should be unchecked
    {
        sell.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                buy.setChecked(false);
            }
        });
    }

    private void UserRegistrationFunction()
    {
       fAuth.createUserWithEmailAndPassword(email.getEditText().getText().toString(),password.getEditText().getText().toString())
            .addOnSuccessListener(Register.this, new OnSuccessListener<AuthResult>()
            {
                @Override
                public void onSuccess(AuthResult authResult)
                {
                    //Here,we register the user and his credentials are saved to our collection through the document reference
                    FirebaseUser firebaseUser=fAuth.getCurrentUser();
                    DocumentReference df=fStore.collection("Users").document(firebaseUser.getUid());
                    Map<String,Object> userDetails=new HashMap<>();

                    userDetails.put("Username",username.getEditText().getText().toString());
                    userDetails.put("Email address",email.getEditText().getText().toString());
                    userDetails.put("Phone",phone.getEditText().getText().toString());

                   if (buy.isChecked())
                   {
                       userDetails.put("isUser","1");
                       mainActivityMethod();
                   }
                   if (sell.isChecked())
                   {
                       userDetails.put("isAdmin","1");
                       adminPanelMethod();
                   }

                   df.set(userDetails).addOnSuccessListener(Register.this, new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void unused)
                        {
                           Toast.makeText(Register.this,"User details were created successfully",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(Register.this, new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(Register.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });


                Toast.makeText(Register.this,"Account created successfully!",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                finish();
                }
            }).addOnFailureListener(Register.this, new OnFailureListener()
       {
           @Override
           public void onFailure(@NonNull Exception e)
           {
            Toast.makeText(Register.this,e.getMessage(),Toast.LENGTH_SHORT).show();
           }
       });

    }//Here,we create user account and assign him/her to the specific activities

    private void adminPanelMethod()
    {
        if (sell.isChecked())
        {
            startActivity(new Intent(getApplicationContext(),AdminPanel.class));
            finish();
        }
    }//if the sell checkbox is checked,the user is directed to the admin panel

    private void mainActivityMethod()
    {       if (buy.isChecked())
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }//if the buy checkbox is checked,the user is directed to the main activity panel

    //validate user inputs
    private boolean validateUsername(TextInputLayout username)
    {
    String val=username.getEditText().getText().toString().trim();
    String checkSpaces="\\A\\w{5,20}\\z";

    if (val.isEmpty()|| val.length()== 0)
    {
        username.setError("Field is empty!");
        return false;
    }
    else if(val.length()< 5 || val.length()>15)
    {
        username.setError("Username should have 5-15 characters");
        return false;
    }
        else if (!val.matches(checkSpaces))
    {
        username.setError("White spaces are not allowed");
        return false;
    }
    else
    {
        username.setError(null);
        username.setErrorEnabled(false);
        return  true;
    }
    }
    private boolean validateEmail(TextInputLayout email)
    {
        String val=email.getEditText().getText().toString().trim();
        if (val.isEmpty()|| val.length()== 0)
        {
            email.setError("Field is empty!");
            return false;
        }

        else
        {
            email.setError(null);
            email.setErrorEnabled(false);
            return  true;
        }
    }
    private boolean validatePhone(TextInputLayout phone)
    {
        String val=phone.getEditText().getText().toString().trim();

        if (val.isEmpty())
        {
            phone.setError("Field empty,enter phone number");
            return false;
        }
        else
            {
                phone.setError(null);
                phone.setErrorEnabled(false);
                return true;
            }
    }

    private boolean validatePassword(TextInputLayout password) {
        String passwordVal = password.getEditText().getText().toString().trim();

        if (passwordVal.isEmpty())
        {
            password.setError("Field is empty!");
            return false;
        }
        //Checks if the password is more than 6 characters
        if (passwordVal.length() < 6 || passwordVal.length() > 15)
        {
            password.setError("Password should be between 6-15 characters!");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCheckboxes()
    {
        if (!(buy.isChecked()||sell.isChecked()))
        {
            Toast.makeText(Register.this,"Please select account type",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    }


