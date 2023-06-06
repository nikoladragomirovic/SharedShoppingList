package nikola.dragomirovic.shoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static String saved_username;
    private String init;
    private TextView username;
    private TextView new_list_button;
    private ListView list;
    private ListAdapter adapter;
    private DatabaseHelper database_helper;
    private ArrayList<List> all_lists = new ArrayList<>();
    private ArrayList<List> my_lists = new ArrayList<>();
    private Button see_my_lists_button;
    private Button see_all_lists_button;
    private Button see_shared_lists_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        username = findViewById(R.id.text_welcome_username);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        list = findViewById(R.id.list_welcome_list);

        Intent serviceIntent = new Intent(this, DatabaseSyncService.class);
        startService(serviceIntent);

        new_list_button = findViewById(R.id.button_welcome_new_list);
        new_list_button.setOnClickListener(this);

        see_my_lists_button = findViewById(R.id.button_welcome_see_my_lists);
        see_my_lists_button.setOnClickListener(this);

        see_all_lists_button = findViewById(R.id.button_welcome_see_all_lists);
        see_all_lists_button.setOnClickListener(this);

        see_shared_lists_button = findViewById(R.id.button_welcome_see_shared_lists);
        see_shared_lists_button.setOnClickListener(this);

        init = getIntent().getStringExtra("username");

        if (init != null) {
            saved_username = init;
        }

        username.setText(saved_username);

        adapter = new ListAdapter(this, saved_username);
        list.setAdapter(adapter);

        database_helper = new DatabaseHelper(this);

        all_lists = database_helper.loadLists(saved_username);

        for(int i = 0; i < all_lists.size(); i++){
            adapter.addItem(all_lists.get(i));
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_welcome_new_list) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New List Dialog");
            builder.setMessage("Are you sure you want to create new list?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface Interface, int i) {

                            Intent intent = new Intent(WelcomeActivity.this, NewListActivity.class);
                            intent.putExtra("username", saved_username);
                            startActivity(intent);
                        }
                    }
            );

            AlertDialog dialog = builder.create();
            dialog.show();

        }else if(view.getId() == R.id.button_welcome_see_my_lists){

                my_lists = database_helper.loadMyLists(saved_username);

                adapter.clearAllItems();

                for (int i = 0; i < my_lists.size(); i++) {
                    adapter.addItem(my_lists.get(i));
                }
        }else if (view.getId() == R.id.button_welcome_see_all_lists){
                all_lists = database_helper.loadLists(saved_username);

                adapter.clearAllItems();

                for (int i = 0; i < all_lists.size(); i++){
                    adapter.addItem(all_lists.get(i));
                }
              }
        else if (view.getId() == R.id.button_welcome_see_shared_lists){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String getListUrl = HttpHelper.ADDRESS + "/lists";

                        HttpHelper http_helper = new HttpHelper();
                        JSONArray shared_lists = http_helper.getJSONArrayFromURL(getListUrl);

                        if (shared_lists.length() > 0) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.clearAllItems();
                                }
                            });

                            for (int i = 0; i < shared_lists.length(); i++) {
                                JSONObject jsonObject = shared_lists.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                boolean shared = jsonObject.getBoolean("shared");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.addItem(new List(saved_username, name, shared));
                                    }
                                });
                            }
                        }
                    }catch (IOException | JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Handle the home button click
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {

        super.onResume();

        all_lists = database_helper.loadLists(saved_username);
        my_lists = database_helper.loadMyLists(saved_username);

        adapter.clearAllItems();

        for (int i = 0; i < all_lists.size(); i++) {
            adapter.addItem(all_lists.get(i));
        }

    }
}