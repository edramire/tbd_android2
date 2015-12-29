package com.usach.tbdgrupo7.iservifast.Views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.usach.tbdgrupo7.iservifast.Controllers.OfrecerPost;
import com.usach.tbdgrupo7.iservifast.Model.Oferta;
import com.usach.tbdgrupo7.iservifast.R;
import com.usach.tbdgrupo7.iservifast.utilities.JsonHandler;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OfrecerActivity extends AppCompatActivity {

    private Button btn_tomar_foto;
    private Button btn_seleccionar_foto;
    private Button btn_enviar;
    private int PICK_IMAGE_REQUEST = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri = null;
    public String mPath;
    public Spinner sp;
    public ArrayAdapter<String> adapter;
    public ArrayList<String> listItems=new ArrayList<>();
    private BroadcastReceiver br = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofrecer);

        /*
        sp=(Spinner)findViewById(R.id.spinner_categorias);
        adapter=new ArrayAdapter<String>(this,R.layout.activity_ofrecer,R.id.spinner_categorias,listItems);
        sp.setAdapter(adapter);
        */

        btn_tomar_foto = (Button) findViewById(R.id.btn_tomar_foto);
        btn_seleccionar_foto = (Button) findViewById(R.id.btn_seleccionar_foto);
        btn_enviar = (Button) findViewById(R.id.btn_enviar);

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

                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                } else {
                    Toast.makeText(getApplication(), "El celular no soporta cámara.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_enviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                IntentFilter intentFilter = new IntentFilter("httpPost");

                String titulo = ((EditText) findViewById(R.id.input_titulo)).getText().toString();
                String descripcion = ((EditText) findViewById(R.id.input_descripcion)).getText().toString();
                String categoria = ((EditText) findViewById(R.id.input_categoria)).getText().toString();
                String precio = ((EditText) findViewById(R.id.input_precio)).getText().toString();
                Date d = new Date();

                // no tiene id asignado actualmente
                Oferta a = new Oferta();
                a.setTitulo(titulo);
                a.setDescripcion(descripcion);
                a.setCategoria_idCategoria(0);
                a.setComunidad_idComunidad(0);
                a.setDuracion(123);
                a.setPrecio(Integer.parseInt(precio));
                a.setFecha(d);
                //a.setUsuario_idUsuario(Integer.parseInt(id_usuario));
                a.setUsuario_idUsuario(0);

                JsonHandler jh = new JsonHandler();
                JSONObject jObject = jh.setOferta(a);

                br = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String result = intent.getStringExtra("post");
                        if(result.equals("OK")){
                            Toast.makeText(getApplicationContext(),"Servicio creado exitosamente",Toast.LENGTH_SHORT);
                            Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent1);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Problemas al crear el servicio",Toast.LENGTH_SHORT);
                        }
                    }
                };
                getApplicationContext().registerReceiver(br, intentFilter);
                new OfrecerPost(getApplicationContext().getApplicationContext()).execute(getResources().getString(R.string.servidor)+"Oferta/crear",jObject.toString());
            }
        });
    }

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mPath = mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg";
        } else if(type == MEDIA_TYPE_VIDEO) {
            mPath = mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4";
        } else {
            return null;
        }
        mediaFile = new File(mPath);
        return mediaFile;
    }

    public static Bitmap rotateBitmap(String src, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(src);

            if (orientation == 1) {
                return bitmap;
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2:
                    matrix.setScale(-1, 1);
                    break;
                case 3:
                    matrix.setRotate(180);
                    break;
                case 4:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case 5:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case 6:
                    matrix.setRotate(90);
                    break;
                case 7:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case 8:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;

        try {
            /**
             * if your are targeting only api level >= 5
             * ExifInterface exif = new ExifInterface(src);
             * orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
             */
            if (Build.VERSION.SDK_INT >= 5) {
                Class<?> exifClass = Class.forName("android.media.ExifInterface");
                Constructor<?> exifConstructor = exifClass.getConstructor(new Class[] { String.class });
                Object exifInstance = exifConstructor.newInstance(new Object[] { src });
                Method getAttributeInt = exifClass.getMethod("getAttributeInt", new Class[] { String.class, int.class });
                Field tagOrientationField = exifClass.getField("TAG_ORIENTATION");
                String tagOrientation = (String) tagOrientationField.get(null);
                orientation = (Integer) getAttributeInt.invoke(exifInstance, new Object[] { tagOrientation, 1});
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return orientation;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.output_photo);
                bitmap = rotateBitmap(mPath,bitmap);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == 100 && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(mPath, options);
            ImageView imageView = (ImageView) findViewById(R.id.output_photo);
            imageView.setImageBitmap(bitmap);
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
