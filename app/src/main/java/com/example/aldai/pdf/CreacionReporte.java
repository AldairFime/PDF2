package com.example.aldai.pdf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;


public class CreacionReporte extends AppCompatActivity {
    String NOMBRE_DIRECTORIO = "MisPDFs";

    String NOMBRE_DOCUMENTO = null;
    String item;
    EditText etTexto;
    EditText Descripcion;
    EditText Telefono;
    EditText Direccion;
    Button btnGenerar;
    Button btnUbicacio;
    private Spinner spinner;
    String currentDateandTime;
    Button btnCamara;
    Button btnSave;
    ImageView imageView2;
    private static final int CAM_REQUEST = 123;
    Bitmap bitmap;
    OutputStream outputStream;
    String fileName = "";
    Button Enviar;
    TextView DireccionReporte;
    String message;
    public String mEmail = "reportesjuarezfime@gmail.com";
    public TextView mSubject;
    public EditText mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creacion_reporte);
        btnCamara = (Button) findViewById(R.id.btnCamara);
        imageView2 = (ImageView) findViewById(R.id.Imagen);
        btnSave = (Button) findViewById(R.id.btnCamaraSave);
        DireccionReporte = (TextView) findViewById(R.id.DireccionReporte);
        Intent intent = getIntent();
        message = intent.getStringExtra("Value");
        DireccionReporte.setText(message);
        mMessage = (EditText)findViewById(R.id.Mensaje);
        Enviar = (Button) findViewById(R.id.btnEnviarCorreo);
        Enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapDrawable drawable = (BitmapDrawable) imageView2.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                File file = Environment.getExternalStorageDirectory();
                File dir = new File(file.getAbsolutePath()+"/Downloads/");
                dir.mkdirs();
                fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir,fileName);
                try {
                    outputStream = new FileOutputStream(outFile);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                btnGenerar.setEnabled(true);

                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
               Toast.makeText(getApplicationContext(),"Se guardo" , Toast.LENGTH_LONG).show();

                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, CAM_REQUEST);
                btnSave.setEnabled(true);

            }
        });


        etTexto = findViewById(R.id.etTexto);
        Descripcion = findViewById(R.id.Descripcion);

        Direccion = findViewById(R.id.etDireec);
        btnGenerar = findViewById(R.id.btnGenerar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setTitle("Generar Reportes");
        BotonLocation();
        spinner = findViewById(R.id.spinner);
        List<String> Categorias = new ArrayList<>();
        Categorias.add(0, "Categoria del problema");
        Categorias.add("Baches");
        Categorias.add("Alumbrado");
        Categorias.add("Mantenimiento y Limpieza");
        Categorias.add("Seguridad Publica");
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Categorias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Selecciones una Option")) {

                } else {
                    item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Tipo: " + item, Toast.LENGTH_SHORT).show();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                    currentDateandTime = sdf.format(new Date());
                    Toast.makeText(getApplicationContext(),"Hora: " + currentDateandTime,Toast.LENGTH_SHORT).show();
                    NOMBRE_DOCUMENTO = "Reporte_" + currentDateandTime + "_" + item + ".pdf";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    1000);
        }
        // Genera el documento
        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubject = (TextView)findViewById(R.id.Subjet);
                crearPDF();
                mSubject.append(NOMBRE_DOCUMENTO);
                Toast.makeText(CreacionReporte.this, "Reporte Generado", Toast.LENGTH_LONG).show();
                Enviar.setEnabled(true);

            }
        });
    }
    private void sendMail() {

        String recipientList = mEmail;
        String[] recipients = recipientList.split(",");
        String subject = mSubject.getText().toString();
        String message = mMessage.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Send email..."));

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAM_REQUEST) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView2.setImageBitmap(bitmap);
        }
    }
    public void crearPDF() {
        Document documento = new Document();

        documento.setPageSize(PageSize.A1
        );


        try {

            File file = crearFichero(NOMBRE_DOCUMENTO);

            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());



            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);


            documento.open();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a ");
            currentDateandTime = sdf.format(new Date());




            documento.add(new Paragraph(  "\t\t\t\t\t\t\t\t\t\t\t\t"+"REPORTES MUNICIPIO DE JUAREZ NUEVO LEON"+  "\n\n\n\n") );

            documento.add(new Paragraph("Persona que realizo el reporte : " + etTexto.getText().toString() + "\n\n"));
            documento.add(new Paragraph("Direccion " + "\n\n"+Direccion.getText().toString()+"\n\n"));
            documento.add(new Paragraph("Descripcion del problema: " + "\n\n"+Descripcion.getText().toString()+"\n\n"));
            documento.add(new Paragraph("Reporte de : " + item + "\n\n"));
            documento.add(new Paragraph("Fecha del reporte: " + currentDateandTime + "\n\n"));


            if(fileName != null){
             documento.add(new Paragraph("Imagen del reporte"));
            documento.add(com.lowagie.text.Image.getInstance("/storage/emulated/0/Downloads/"+fileName));
            }
            else{
                Toast.makeText(getApplicationContext(), "No encontre la foto", Toast.LENGTH_LONG).show();
            }

            if(message != null){

                documento.add(new Paragraph("Mapa"));
                documento.add(com.lowagie.text.Image.getInstance("/mnt/sdcard/"+message+ ".jpg"));


            }else {
                Toast.makeText(getApplicationContext(), "No encontre la foto", Toast.LENGTH_LONG).show();
            }

            documento.add(new Paragraph("Direccion del Reporte: " + message + "\n\n"));

        } catch (DocumentException e) {
        } catch (IOException e) {
        } finally {
            documento.close();
        }
    } //Se creoPDF
    public File crearFichero(String nombreFichero) {
        File ruta = getRuta();

        File fichero = null;
        if (ruta != null) {
            fichero = new File(ruta, nombreFichero);
        }

        return fichero;
    } //Se crea un fichero
    public File getRuta() {
        File ruta = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), NOMBRE_DIRECTORIO);

            if (ruta != null) {
                if (!ruta.mkdirs()) {
                    if (!ruta.exists()) {
                        return null;
                    }
                }
            }

        }
        return ruta;
    } //Obtenemos ruta
    private void BotonLocation(){
         btnUbicacio = (Button) findViewById(R.id.btnUbicacion);
       btnUbicacio.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(CreacionReporte.this,PermisosMapa.class));


           }
       });
    }


    /*


    @SuppressLint("MissingPermission")
    public void call_action(){

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:8118788000" ));
        startActivity(callIntent);
    }

    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    call_action();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

*/

}