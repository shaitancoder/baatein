package com.example.sakshigarg.authfirebase;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
/**
 * Created by sakshi on 22-03-2017.
 */
public class ProfileActivity extends Activity {

    private static int RESULT_LOAD_IMAGE = 1;
    String picturePath;
    Uri selectedImage;
    private static final int REQ_CODE_SPEECH_INPUT = 100;


    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    String name = "";
    EditText msg;
    ImageButton send, camera,mic;
    FirebaseUser user;
    ListView list;
    ArrayList<CustomObject> chats = new ArrayList<CustomObject>();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);
        msg = (EditText) findViewById(R.id.msg);
        send = (ImageButton) findViewById(R.id.send);
        camera = (ImageButton) findViewById(R.id.camera);
       mic = (ImageButton) findViewById(R.id.mic);

       Intent intent = getIntent();
       if (intent != null) {
           String message = intent.getStringExtra("MESSAGE");
           msg.setText(message);
//           if (msg.length() > 0) {
//               saveUserInformation(msg.getText().toString(), 0);
//               msg.setText("");
//           }
       }

        list = (ListView) findViewById(R.id.list);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        sharedPreferences = getSharedPreferences("Myprefs", MODE_PRIVATE);
        name = sharedPreferences.getString("name", null);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(msg.length()>0) {
                    saveUserInformation(msg.getText().toString(), 0);
                    msg.setText("");
                }
            }
        });
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceInput();
            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    CustomAdapter customAdapter = new CustomAdapter(ProfileActivity.this, chats,name);
                    if(customAdapter.getItem(position).getIm()==1) {
                    String path = customAdapter.getItem(position).getMsg();

                        Intent intent = new Intent(ProfileActivity.this, ViewImag.class);
                        intent.putExtra("path", path);
                        startActivity(intent);
                    }
                }catch (Exception e)
                {
                    Toast.makeText(ProfileActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    try {
                        People people = noteDataSnapshot.getValue(People.class);
                        CustomObject ob = new CustomObject(people.getName(), people.getMessage(),people.getIm(),people.getDate());
                        chats.add(ob);
                    } catch (Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
                CustomAdapter customAdapter = new CustomAdapter(ProfileActivity.this, chats, name);
                list.setAdapter(customAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "SPEAK YOUR MSG");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }


    public void saveUserInformation(String message, int im) {
        String date = new SimpleDateFormat("dd MMM hh:mm aaa").format(new Date());
//        String date = DateFormat.getDateTimeInstance().format(new Date());
        People people = new People(name,message,im,date);
        databaseReference.push().setValue(people);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            uploadFile();
        }


    }

    private void uploadFile() {
        //if there is a file to upload
        if (selectedImage != null) {

            File f= new File(picturePath);
            StorageReference riversRef = storageReference.child("images/"+f.getName());
//doubt
            riversRef.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(getApplicationContext(), "Image Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        saveUserInformation(riversRef.getPath(),1);
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }

    }

