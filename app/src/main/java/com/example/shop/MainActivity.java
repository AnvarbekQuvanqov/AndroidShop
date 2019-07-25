package com.example.shop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyLog";
    private SearchView searchView;
    private ListView listView;
    private ListView listView2;
    private ArrayList<Product> list;
    private ArrayList<Product> list2;
    private ProductAdapter adapter;
    private ItemAdapter adapter2;
    private ImageView save;
    private ImageView calsel;
    private EditText price_product_count;
    private EditText price_inproduct_count;
    private Product selectProduct;
    private static Double selectProductSum=0.0;
    private static Integer selectedProduct=0;
    private static Double sum=0.0;
    private static Integer asosId;
    private Integer type;

    private static User thisuUser;

    private ProgressDialog progressDialog;
    private static String ip="192.168.43.57";
    private static String urlAsos="http://192.168.43.52:8080/application/json/asos";

    private Intent intent;
    private Intent typeIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        save=(ImageView)findViewById(R.id.save);
        calsel=(ImageView)findViewById(R.id.calsel);

        price_product_count=(EditText)findViewById(R.id.price_product_count);
        price_inproduct_count=(EditText)findViewById(R.id.price_inproduct_count);


        searchView=(SearchView)findViewById(R.id.searchView);
        listView=(ListView)findViewById(R.id.listView);
        listView2=(ListView)findViewById(R.id.listView2);

        list = new ArrayList<>();
        list2 = new ArrayList<>();
        Log.v(TAG,"Ha ina hohiray");
        intent=getIntent();
        thisuUser=(User)intent.getSerializableExtra("user");
        ip=intent.getStringExtra("ip");
        type=intent.getIntExtra("type",1);
        Log.v(TAG,ip+"");
        asosId=(Integer) intent.getIntExtra("asosId",0);
        Log.v(TAG,thisuUser.getId().toString());
        Log.v(TAG,intent.getStringExtra("ip"));
        new GetProducts().execute();
        Log.v(TAG,asosId+"");

        adapter2 = new ItemAdapter(this, R.layout.list_item,list2);
        listView2.setAdapter(adapter2);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
//                if (list.contains(query)) {
//                } else {
//                    Toast.makeText(MainActivity.this, "Topilmadi !!!", Toast.LENGTH_LONG).show();
//                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
//                if (list.contains(s)) {
//                    return  true;
//                }
                return false;
            }
        });

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Product product=(Product)view.getTag();
                        Log.v(TAG,product.getName()+" "+product.getId());
                        Log.v(TAG,product.getName()+" "+product.getPutId());
                        Log.v(TAG,product.getName()+" "+product.getCount());
                        Log.v(TAG,product.getName()+" "+product.getIncount());
                        Log.v(TAG,product.getName()+" "+product.getInprice());
                        Log.v(TAG,product.getName()+" "+product.getPrice());

//                        Log.v(TAG,product.getName()+" "+selectProduct.getName());
                        selectedProduct=1;
                        setProduct(product);
                    }
                }
        );

        save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(selectedProduct!=0){

                                            int price_product_count_int=tryParse(price_product_count.getText().toString());
                                            int price_inproduct_count_int=tryParse(price_inproduct_count.getText().toString());

                                            Log.v(TAG,price_inproduct_count_int+" "+price_product_count_int);

                                            if(price_product_count_int>0 || price_inproduct_count_int>0) {
                                                Log.v(TAG, "in 1:" + selectProduct.getName());
                                                selectProduct.setCount(price_product_count_int);
                                                selectProduct.setCount(price_inproduct_count_int);

                                                selectProductSum = (selectProduct.getPrice() * selectProduct.getCount() + selectProduct.getInprice() * selectProduct.getIncount());
                                                sum += selectProductSum;
                                                list2.add(selectProduct);
                                                adapter2.notifyDataSetChanged();
                                                new AddProduct().execute();
                                                selectedProduct=0;
                                                setProduct(selectProduct);
                                                Log.v(TAG, "list2.size:" + list2.size());
                                            }
                                            else{
                                                Toast.makeText(MainActivity.this,"Сонини киритинг !!!",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        else{
                                            Toast.makeText(MainActivity.this,"Махсулотни танланг",Toast.LENGTH_LONG).show();
                                        }


                                    }
                                }
        );
        calsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                selectedProduct=0;
                                setProduct(selectProduct);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Сиз ростан хам бекор қилишни истайсизми?").setPositiveButton("Ха", dialogClickListener)
                        .setNegativeButton("Йўқ", dialogClickListener).show();
            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item1) {
           downActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setProduct(Product product) {

        if(selectedProduct!=0){
            ((TextView)findViewById(R.id.select_product)).setText(product.getName());
            selectProduct=product;
        }
        else{
            ((TextView)findViewById(R.id.select_product)).setText(R.string.product);
            selectProduct=product;
        }
        price_product_count.getText().clear();
        price_inproduct_count.getText().clear();

    }

    public Integer tryParse(Object obj) {
        Integer retVal;
        try {
            retVal = Integer.parseInt((String) obj);
        } catch (NumberFormatException nfe) {
            retVal = 0; // or null if that is your preference
        }
        return retVal;
    }
    private void downActivity(){
        typeIntent = new Intent(MainActivity.this, TypeChangeActivity.class);
        typeIntent.putExtra("user",intent.getSerializableExtra("user"));
        typeIntent.putExtra("ip",intent.getStringExtra("ip"));
        typeIntent.putExtra("asosId",intent.getIntExtra("asosId",0));
        typeIntent.putExtra("type",intent.getIntExtra("type",0));
        startActivity(typeIntent);
        finish();
    }




    private class AddProduct extends AsyncTask<Void,Void,Void>{
        String urlRequest="http://"+ip+":8080/application/json/asosslave/asosid="+asosId+"/userid="+thisuUser.getId();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Малумот сақланяпти");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
           HttpHandler httpHandler=new HttpHandler();
           Log.v(TAG,"Ishla ahi");
            Log.v(TAG,"selectProduct Id:"+selectProduct.getId());
            Log.v(TAG,"selectProduct PutId:"+selectProduct.getPutId());
            Log.v(TAG,"selectProduct Count:"+selectProduct.getCount());
            Log.v(TAG,"selectProduct InCount:"+selectProduct.getIncount());
            Log.v(TAG,"selectProduct InPrice:"+selectProduct.getInprice());
            Log.v(TAG,"selectProduct Price:"+selectProduct.getPrice());
           httpHandler.makeServiceAddProduct(urlRequest,selectProduct);
           Log.v(TAG,"Ishlivar barakat tap");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }

    }


    private class GetProducts extends AsyncTask<Void, Void, Void> {
//        http://localhost:8080/application/json/clientid=4/4/products
        private String urlProducts="http://"+ip+":8080/application/json/clientid="+thisuUser.getClientId()+"/"+ type +"/products";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Малумот юкланяпти");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler httpHandler=new HttpHandler();
            String jsonStr=httpHandler.makeServiceCall(urlProducts);
            Log.v(TAG,"URL:"+urlProducts);
            if(jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Product product = new Product();
                        JSONObject object = jsonArray.getJSONObject(i);

                                /*
                                "id": 1,
                                "productId": 2,
                                "nameShort": "anvar",
                                "count": 4,
                                "incount": 5,
                                "price": 6,
                                "inprice": 7
                                */
                        product.setPutId(object.getInt("id"));
                        product.setId(object.getInt("productId"));
                        product.setName(object.getString("name"));
                        product.setCount(object.getInt("count"));
                        product.setIncount(object.getInt("incount"));
                        product.setPrice(object.getDouble("price"));
                        product.setInprice(object.getDouble("inprice"));
                        Log.v(TAG,"selectProduct Id:"+product.toString());

                        list.add(product);

                    }
                } catch (final JSONException e) {
                    Log.v("MyTag2", e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Хатолик юз берди", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
            else{
                Log.v("MyTag2", "serverdan galmadi");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"Сервер билан муамо бор",Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            adapter = new ProductAdapter(MainActivity.this,R.layout.products_item,list);
            listView.setAdapter(adapter);


        }
    }
}
