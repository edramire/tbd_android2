package com.usach.tbdgrupo7.iservifast.Views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.usach.tbdgrupo7.iservifast.R;

import java.io.IOException;

public class OfrecerActivity extends AppCompatActivity {

    private Button btn_tomar_foto;
    private Button btn_seleccionar_foto;
    private int PICK_IMAGE_REQUEST = 1;
    private int CAPTURE_IMAGE_REQUEST = 1;
    private static final String TAG = "CallCamera";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 0;
    private Uri fileUri = null;
    private ImageView photoImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofrecer);

        btn_tomar_foto = (Button) findViewById(R.id.btn_tomar_foto);
        btn_seleccionar_foto = (Button) findViewById(R.id.btn_seleccionar_foto);

        btn_seleccionar_foto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);


            }
        });


        btn_tomar_foto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                /*
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getOutputPhotoFile();
                fileUri = Uri.fromFile(getOutputPhotoFile());
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQ );
*/

                if (getApplicationContext().getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 100);

                } else {
                    Toast.makeText(getApplication(), "El celular no soporta camara.", Toast.LENGTH_LONG).show();
                }

                // Show only images, no videos or anything else
                //intent.setType("image/*");
                //intent.setAction(Intent.ACTION_CAMERA_BUTTON);
                // Always show the chooser (if there are multiple options available)
                //startActivityForResult(Intent.createChooser(intent, "Capturar imagen"), CAPTURE_IMAGE_REQUEST);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                ImageView imageView = (ImageView) findViewById(R.id.output_photo);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == 100 && resultCode == RESULT_OK) {
            System.out.println("DESPUES CAMERA");
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);



            photo = imageOreintationValidator(photo,filePath);

            ImageView imageView = (ImageView) findViewById(R.id.output_photo);
            imageView.setImageBitmap(photo);
        }
    }

    private Bitmap rotateImage(Bitmap src, float degree) {
        // create new matrix object
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        // return new bitmap rotated using matrix
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }


    private Bitmap imageOreintationValidator(Bitmap bitmap, String path) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
