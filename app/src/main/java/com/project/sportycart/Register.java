package com.project.sportycart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.project.sportycart.entity.AccessTokenDTO;
import com.project.sportycart.entity.RegisterUser;
import com.project.sportycart.retrofit.GetProductsService;
import com.project.sportycart.retrofit.RetrofitClientInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    private RegisterUser registerUser=new RegisterUser();
    private EditText registerUsername;
    private EditText registerEmail;
    private EditText registerPassword;
    private EditText registerConfirmPassword;
    private EditText registerPhone;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button signUp=findViewById(R.id.register);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                registerUsername = findViewById(R.id.registerusername);
                String userName = registerUsername.getText().toString();

                registerEmail = findViewById(R.id.registeremail);
                String email = registerEmail.getText().toString();

                registerPassword = findViewById(R.id.registerpassword);
                String password = registerPassword.getText().toString();

                registerConfirmPassword = findViewById(R.id.registerconfirmpassword);
                String confirmPassword = registerConfirmPassword.getText().toString();

                registerPhone = findViewById(R.id.registerphone);
                long phone = Long.parseLong(String.valueOf(registerPhone.getText()));

//                System.out.println(userName+email+password+confirmPassword+phone);

                Boolean nullFlag = true;
                Boolean passwordCheckFail = true;
                long largestNo=9999999999L;
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (userName != null && email != null && password != null && confirmPassword != null && phone != 0 && phone>=1000000000L && phone<=largestNo && email.matches(emailPattern)) {
                    nullFlag = false;
                    if (confirmPassword.equals(password)) {
                        passwordCheckFail = false;
                        registerUser.setEmail(email);
                        registerUser.setPassword(password);
                        registerUser.setPhoneNo(phone);
                        registerUser.setUserName(userName);
                        message = "Registered";
                    } else {
                        passwordCheckFail=true;
                        message = "Confirm Password Failed";
                        Toast.makeText(getApplicationContext(), "Registered!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    nullFlag=true;
                    message = "Enter all fields appropriately!";
                }

                if(nullFlag==false && passwordCheckFail==false){
                    GetProductsService getProductsService = RetrofitClientInstance.getRetrofitInstance().create(GetProductsService.class);
                    Call<AccessTokenDTO> call= getProductsService.addUser(registerUser);
                    call.enqueue(new Callback<AccessTokenDTO>() {
                        @Override
                        public void onResponse(Call<AccessTokenDTO> call, Response<AccessTokenDTO> response) {
                            if (response.body().getCheck() == false) {
                                Toast.makeText(getApplicationContext(), "Already Registered, Please Login!", Toast.LENGTH_SHORT).show();
                                System.out.println("ALREADY REGISTERED");
                                message="ALREADY REGISTERED";
                                Intent loginNow= new Intent(Register.this,Login.class);
                                startActivity(loginNow);
                                finish();
                            }
                            else {
//                            registerUser.setUserId("userId");
                                System.out.println("REGISTERED");
                                Toast.makeText(getApplicationContext(), "Registered!", Toast.LENGTH_SHORT).show();
                                message="Registered Successfully!";
                                Intent loginNow= new Intent(Register.this,Login.class);
                                startActivity(loginNow);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<AccessTokenDTO> call, Throwable t) {
                            System.out.println("Invalid Backend Response"+t.getMessage());
                        }
                    });
                }

                Snackbar snackbar = Snackbar.make(findViewById(R.id.register_layout),
                        message, Snackbar.LENGTH_SHORT);
                snackbar.show();

            }
        });
    }
}
