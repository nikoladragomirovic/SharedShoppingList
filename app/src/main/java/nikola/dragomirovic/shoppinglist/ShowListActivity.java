package nikola.dragomirovic.shoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ShowListActivity extends AppCompatActivity {
    private String title;
    private boolean shared;
    private TextView view_title;
    private TaskAdapter adapter;
    private ListView list;
    private EditText title_form;
    private Button button_add;
    private DatabaseHelper database_helper;
    private ArrayList<Task> tasks = new ArrayList<>();
    private Button refresh;
    private HttpHelper http_helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        database_helper = new DatabaseHelper(this);

        http_helper = new HttpHelper();

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        view_title = findViewById(R.id.text_show_list_title);

        refresh = findViewById(R.id.button_show_list_refresh);

        title = getIntent().getStringExtra("title");
        shared = getIntent().getBooleanExtra("shared", false);

        if (shared) {
            refresh.setVisibility(View.VISIBLE);
        } else {
            refresh.setVisibility(View.GONE);
        }

        view_title.setText(title);

        adapter = new TaskAdapter(this);

        list = findViewById(R.id.list_show_list_tasks);
        list.setAdapter(adapter);

        title_form = findViewById(R.id.form_show_list_title);
        button_add = findViewById(R.id.button_show_list_add);

        tasks = database_helper.loadTasks(title);

        for (int i = 0; i < tasks.size(); i++) {
            adapter.addItem(tasks.get(i));
        }

        button_add.setOnClickListener(view -> {
            if (title_form.getText().toString().isEmpty()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Title empty!");
                builder.setPositiveButton("Ok", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {

                String rand_id = generateRandomString(16);

                database_helper.addTask(title_form.getText().toString(), title, rand_id, 0);

                if (shared) {
                    http_helper.addTask(title_form.getText().toString(), title, rand_id);
                }

                tasks = database_helper.loadTasks(title);

                adapter.clearTasks();

                for (int i = 0; i < tasks.size(); i++) {
                    adapter.addItem(tasks.get(i));
                }

            }
        });

        refresh.setOnClickListener(view -> {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clearTasks();
                        }
                    });
                    try {

                        HttpHelper http_helper = new HttpHelper();
                        JSONArray all_tasks = http_helper.getJSONArrayFromURL("http://192.168.0.27:3000/tasks/" + title);

                        for (int i = 0; i < all_tasks.length(); i++) {

                            JSONObject jsonObject = all_tasks.getJSONObject(i);
                            boolean check = jsonObject.getBoolean("done");
                            String taskId = jsonObject.getString("taskId");
                            String name = jsonObject.getString("name");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.addItem(new Task(title, taskId, name, check));
                                }
                            });
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        });
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
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            View v = getCurrentFocus();

            if (v instanceof EditText) {

                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {

                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                }
            }
        }

        return super.dispatchTouchEvent(event);

    }

    public static String generateRandomString(int length) {

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }
        return sb.toString();

    }

}