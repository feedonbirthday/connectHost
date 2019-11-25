package com.example.connecthost;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ContactAdapter adapter;
    private ListView contactlist;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new ContactAdapter(new ArrayList<Contact>(), this);
        contactlist = findViewById(R.id.listview);
        contactlist.setAdapter(adapter);
//        (new ConnectMysql()).execute("https://mysqlcontact.000webhostapp.com/list_contacts.php");
        (new ConnectMysql()).execute("https://clvsc41118101.000webhostapp.com/list_contacts.php");
        contactlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent();
                intent.putExtra("id",adapter.getItem(position).getId());
                intent.putExtra("pic_filename",adapter.getItem(position).getPic_filename());
                intent.putExtra("name",adapter.getItem(position).getName());
                intent.putExtra("phone",adapter.getItem(position).getPhoneNum());
                intent.putExtra("email",adapter.getItem(position).getEmail());
                intent.putExtra("birthday",adapter.getItem(position).getBirthday());
                intent.putExtra("image",adapter.getItem(position).getPic());
                intent.putExtra("title","編輯資料");
                intent.putExtra("type","edit");
                intent.setClass(MainActivity.this,AddEditActivity.class);
                startActivity(intent);
            }
        });
    }
    private class ConnectMysql extends AsyncTask<String, Void, List<Contact>>{
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("資料下載中...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            super.onPostExecute(contacts);
            dialog.dismiss();
            adapter.setItemList(contacts);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected List<Contact> doInBackground(String... strings) {
            List<Contact> result = new ArrayList<Contact>();
            URL u = null;
            try{
                u = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                //讀取網頁上的資料
                InputStream is = conn.getInputStream();
                byte[] b = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ( is.read(b) != -1)
                    baos.write(b);
                String JSONResp = new String(baos.toByteArray());
                Log.i("JSONResp=", JSONResp);
                //將資料轉換成陣列並加入result
                JSONArray arr = new JSONArray(JSONResp);
                for(int i = 0; i < arr.length(); i++){
                    if(arr.getJSONObject(i) != null){
                        result.add(convertContact(arr.getJSONObject(i)));
                        Log.v("data=", arr.getJSONObject(i).toString());
                    }
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        //將json物件轉成自訂的Contact資料格式
        private Contact convertContact(JSONObject obj) throws JSONException {
            Bitmap bitmap;
            if(obj.getString("Picture") != null){
                bitmap = LoadImage("https://clvsc41118101.000webhostapp.com/upload/" + obj.getString("Picture").toString());
            }else{
                bitmap = LoadImage("https://clvsc41118101.000webhostapp.com/upload/girl.png");
            }
            String id = obj.getString("ContactID");
            String pic_filename = obj.getString("Picture");
            String name = obj.getString("Name");
            String phone = obj.getString("Phone");
            String email = obj.getString("Email");
            String birthday = obj.getString("Birthday");
           return new Contact(id, pic_filename, bitmap, name, phone, email, birthday);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch(id){
            case R.id.action_add:
                intent = new Intent();
                intent.putExtra("title","新增好友");
                intent.putExtra("type","new");

                changeView(this, AddEditActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    //intent跳轉activity共用函式
    public void changeView(Context context, Class<?> cla){

        intent = intent.setClass(context, cla);
        startActivity(intent);
        this.finish();
    }
}
