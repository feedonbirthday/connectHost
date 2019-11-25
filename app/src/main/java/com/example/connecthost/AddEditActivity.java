package com.example.connecthost;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AddEditActivity extends AppCompatActivity implements View.OnClickListener {
    //view物件與儲存變數的宣告
    private Bundle bData;
    private EditText editName, editTel, editEmail, editBirth;
    private Button btnConfirm,btnSelect;
    private String type;
    private Intent intent;
    private String newName,queryName,newPhone,newEmail,newBirth,oldPic;
    private ImageView image;

    private String id;
    private int index;
    private boolean isEdit = false;
    private Boolean isDeleted = false;
    private Bitmap bitmap;
    private String picturePath;
    private String filename;
    private Uri selectedImage;
    // number of images to select
    private static final int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        initView();
        bData = this.getIntent().getExtras();
        type = bData.getString("type");
        if(type.equals("edit")){
            id = bData.getString("id");
            editName.setText(bData.getString("name"));
            editTel.setText(bData.getString("phone"));
            editEmail.setText(bData.getString("email"));
            editBirth.setText(bData.getString("birthday"));
            image.setImageBitmap((Bitmap) bData.getParcelable("image"));

//            (new ConnectMysql()).execute("https://mysqlcontact.000webhostapp.com/query.php");
//            (new ConnectMysql()).execute("https://clvsc41118101.000webhostapp.com/query.php");
        }
    }
    private void initView(){

        editName = findViewById(R.id.name);
        editTel = findViewById(R.id.phone);
        editEmail = findViewById(R.id.email);
        editBirth = findViewById(R.id.birthday);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnSelect = findViewById(R.id.btnSelect);
        image = findViewById(R.id.image);

        btnConfirm.setOnClickListener(this);
        btnSelect.setOnClickListener(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_back:
                intent = new Intent(AddEditActivity.this, MainActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.action_delete:

//                (new ConnectMysql()).execute("https://mysqlcontact.000webhostapp.com/delete.php");
                (new ConnectMysql()).execute("https://clvsc41118101.000webhostapp.com/delete.php");

                break;
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnConfirm:
                oldPic = bData.getString("pic_filename");
                newName = editName.getText().toString();
                newPhone = editTel.getText().toString();
                newEmail = editEmail.getText().toString();
                newBirth = editBirth.getText().toString();
                Log.v("newName1",newName);
                if(type.equals("new")){
//                    (new ConnectMysql()).execute("https://mysqlcontact.000webhostapp.com/insert.php");
                    (new ConnectMysql()).execute("https://clvsc41118101.000webhostapp.com/insert.php");
                }else{
                    Log.i("edit=","start edit");
//                    (new ConnectMysql()).execute("https://mysqlcontact.000webhostapp.com/update.php");
                    (new ConnectMysql()).execute("https://clvsc41118101.000webhostapp.com/update.php");
                }
                break;
            case R.id.btnSelect:
                selectImageFromGallery();
                break;
        }
    }

    private void selectImageFromGallery() {
        if (Build.VERSION.SDK_INT <19) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "請選擇圖片"), PICK_IMAGE);
        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        if (resultCode == Activity.RESULT_OK && data != null) {

            selectedImage = data.getData();
            image.setImageURI(selectedImage);
            Log.v("getData",data.getData().toString());
            String id = selectedImage.getLastPathSegment().split(":")[1];
            final String[] imageColumns = {MediaStore.Images.Media.DATA };
            if (Build.VERSION.SDK_INT >= 23) {
                int REQUEST_CODE_IMAGE = 101;
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET};
                //验证是否许可权限
                for (String str : permissions) {
                    if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                        //申请权限
                        this.requestPermissions(permissions, REQUEST_CODE_IMAGE);
                        return;
                    }
                }
            }
            Uri uri = getUri();
            picturePath = "path";

            Cursor imageCursor = getContentResolver().query(uri, imageColumns,
                    MediaStore.Images.Media._ID + "="+id, null, null);

            if (imageCursor.moveToFirst()) {

                picturePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            Log.v("test=",picturePath);
        }

    }
    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
    private class ConnectMysql extends AsyncTask<String, Void, String> {
        String attachmentFileName = picturePath;
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        private final ProgressDialog dialog = new ProgressDialog(AddEditActivity.this);
        @Override
        protected String doInBackground(String... strings) {
            int result = 0;
            URL u = null;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            try{
                u = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                    //新增資料程式碼
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);
                    conn.setRequestProperty("Charset", "UTF-8");

                    //宣告輸出串流
                    DataOutputStream request = new DataOutputStream(conn.getOutputStream());
                    if (this.attachmentFileName != null) {
                        File sourceFile = new File(attachmentFileName);
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
                        request.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + this.attachmentFileName + "\"" + this.crlf);
                        request.writeBytes(this.crlf);

                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];
                        // 讀取圖片資料並寫入表單資訊
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        while (bytesRead > 0) {
                            request.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        }
                        request.write(buffer);
                        if(type.equals("edit")) {
                            request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
                            request.writeBytes("Content-Disposition: form-data; name=\"Picture\"" + "\"" + this.crlf);
                            request.writeBytes(this.crlf);
                            request.writeBytes(oldPic);
                            request.writeBytes(this.crlf);
                        }
                    }
                    if(type.equals("edit")) {
                        //讀取目前資料的id
                        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
                        request.writeBytes("Content-Disposition: form-data; name=\"ContactID\"" + "\"" + this.crlf);
                        request.writeBytes(this.crlf);
                        request.writeBytes(id);
                        request.writeBytes(this.crlf);
                    }
                    //姓名欄位與資料
                    request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"Name\"" + "\"" + this.crlf);
                    request.writeBytes(this.crlf);
                    request.writeBytes(newName);
                    request.writeBytes(this.crlf);

                    //電話欄位與資料設定
                    request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"Phone\"" + "\"" + this.crlf);
                    request.writeBytes(this.crlf);
                    request.writeBytes(newPhone);
                    request.writeBytes(this.crlf);

                    //Email欄位與資料
                    request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"Email\"" + "\"" + this.crlf);
                    request.writeBytes(this.crlf);
                    request.writeBytes(newEmail);
                    request.writeBytes(this.crlf);

                    //生日欄位與資料
                    request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"Birthday\"" + "\"" + this.crlf);
                    request.writeBytes(this.crlf);
                    request.writeBytes(newBirth);
                    request.writeBytes(this.crlf);
                    request.writeBytes(this.twoHyphens + this.boundary + this.twoHyphens + this.crlf);

                    request.flush();
                    request.close();
                    Log.i("postString=", request.toString());
                    conn.connect();
                    //End 新增資料程式碼


                    InputStream is = conn.getInputStream();
                    // Read the stream
                    byte[] b = new byte[1024];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while (is.read(b) != -1)
                        baos.write(b);

                    String response = new String(baos.toByteArray());
                    Log.i("response=", response);
                    return response;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            int isSuccess = 0;
            String result_msg;
            if(type.equals("new")){
                isSuccess = s.indexOf("add Success");
                if(isSuccess != -1) {
                    Toast.makeText(AddEditActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                }
            }else if(type.equals("edit")) {
                isSuccess = s.indexOf("edit Success");
                if(isSuccess != -1) {
                    Toast.makeText(AddEditActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                }
            }else{
                isSuccess = s.indexOf("delete Success");
                if(isSuccess != -1) {
                    Toast.makeText(AddEditActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
        private void loadContact(JSONObject obj) throws JSONException {

            if(obj.getString("Picture") != null) {
                bitmap = LoadImage("https://clvsc41118101.000webhostapp.com/upload/" + obj.getString("Picture").toString());
            }else{
                bitmap = LoadImage("https://clvsc41118101.000webhostapp.com/upload/girl.png");
            }

            Log.v("jsonObj=",obj.getString("Picture").toString());
            oldPic = obj.getString("Picture");
            index = obj.getInt("ContactID");
            newName = obj.getString("Name");
            newPhone = obj.getString("Phone");
            newEmail = obj.getString("Email");
            newBirth = obj.getString("Birthday");

        }
        //連線網路圖片位址並轉成bitmap
        private Bitmap LoadImage(String imageUrl){
            Bitmap bitmap = null;
            try{
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = conn.getInputStream();

                    bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    return bitmap;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}
