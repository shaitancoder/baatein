package com.example.sakshigarg.authfirebase;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends Activity {


    public static final String MyPREFERENCES="Myprefs";
    public static final String NAME="name";
    public static final String EMAIL="email";

    SharedPreferences sharedPreferences;

    public String name1,email1,password1,message;
    FirebaseAuth firebaseAuth;
    EditText email,password,name;
    Button add;
    Button googlesign;
    ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            name1 = intent.getStringExtra("NAME");
            email1 = intent.getStringExtra("EMAIL");
            password1 = intent.getStringExtra("PASSWORD");
            message = intent.getStringExtra("MESSAGE");
        }

        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        name=(EditText)findViewById(R.id.name);

        if (intent != null) {
            email.setText(email1);
            name.setText(name1);
            password.setText(password1);
        }
        add=(Button) findViewById(R.id.add);
        //googlesign=(Button)findViewById(R.id.googlesign);
        progress=(ProgressBar)findViewById(R.id.progress) ;
        progress.setVisibility(View.INVISIBLE);

        sharedPreferences=getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        String temp=sharedPreferences.getString(NAME,null);
        if(temp!=null)
        {
            Intent intent1=new Intent(MainActivity.this,ProfileActivity.class);
            intent1.putExtra("MESSAGE",message);
            startActivity(intent1);
            finish();
        }


        firebaseAuth= FirebaseAuth.getInstance();



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard(MainActivity.this);
                progress.setVisibility(View.VISIBLE);
                registerUser();}});



    }

    public static void hideSoftKeyboard(Activity activity)
    {
        InputMethodManager inputMethodManager=(InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
    }

    private void signIn()
    {
        final String textemail=email.getText().toString();
        String textpassword=password.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(textemail,textpassword)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            SharedPreferences.Editor editor =getSharedPreferences(MyPREFERENCES,MODE_PRIVATE).edit();
                            if(name.getText().length()==0)
                                editor.putString(NAME,"UNKNOWN");
                            else
                                editor.putString(NAME,name.getText().toString());
                            editor.putString(EMAIL,textemail);
                            editor.commit();
                            finish();
                            Intent i = new Intent(MainActivity.this,ProfileActivity.class);
                            i.putExtra("MESSAGE",message);
                            startActivity(i);
                        }
                        else
                        {
                            progress.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this,"LOGIN FAILED",Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }
    private  void registerUser()
    {

        final String textemail=email.getText().toString();
        String textpassword=password.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(textemail,textpassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            SharedPreferences.Editor editor =getSharedPreferences(MyPREFERENCES,MODE_PRIVATE).edit();
                            if(name.getText().length()==0)
                                editor.putString(NAME,"UNKNOWN");
                            else
                                editor.putString(NAME,name.getText().toString());
                            editor.putString(EMAIL,textemail);
                            editor.commit();
                            Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                            intent.putExtra("MESSAGE",message);
                            startActivity(intent);
                            finish();
                            Toast.makeText(MainActivity.this, "SUCCESSFULLY LOGGED IN", Toast.LENGTH_LONG).show();

                        }
                        else {
                            signIn();
                        }
                    }
                });

    }
}
