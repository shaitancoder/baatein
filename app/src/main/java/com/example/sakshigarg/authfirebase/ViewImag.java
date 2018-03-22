package com.example.sakshigarg.authfirebase;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
/**
 * Created by sakshi on 22-03-2017.
 */

public class ViewImag extends Activity {


 //   ImageButton imagedownload;
    URL url;
    StorageReference riversRef;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_imag);
        image=(ImageView)findViewById(R.id.image);
     //   imagedownload=(ImageButton)findViewById(R.id.download);

        final String path=getIntent().getStringExtra("path");
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        riversRef = storageReference.child(path);


        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {

                    url=new URL(uri.toString());

                }catch (Exception e)
                {
                    Toast.makeText(ViewImag.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(riversRef)
                .into(image);

//        imagedownload.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                try {
//
//
//                    Picasso.with(ViewImag.this)
//                            .load(url.toString())
//                            .into(new Target() {
//                                      @Override
//                                      public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                          try {
//                                              String root = Environment.getExternalStorageDirectory().toString();
//                                              File myDir = new File(root + "/ChatImage");
//
//                                              if (!myDir.exists()) {
//                                                  myDir.mkdirs();
//                                              }
//
//                                              String name = riversRef.getName();
//                                              myDir = new File(myDir, name);
//                                              FileOutputStream out = new FileOutputStream(myDir);
//                                              bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//
//                                              out.flush();
//                                              out.close();
//                                          } catch(Exception e){
//                                              // some action
//                                          }
//                                      }
//
//                                      @Override
//                                      public void onBitmapFailed(Drawable errorDrawable) {
//                                      }
//
//                                      @Override
//                                      public void onPrepareLoad(Drawable placeHolderDrawable) {
//                                      }
//                                  }
//                            );
//
//
//                    Toast.makeText(ViewImag.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();
//                }catch (Exception e)
//                {
//                    Toast.makeText(ViewImag.this, "Loading Link", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }
}
