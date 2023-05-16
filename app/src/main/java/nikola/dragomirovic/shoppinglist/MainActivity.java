package nikola.dragomirovic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login_button;
    private Button register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        login_button = findViewById(R.id.button_main_login);
        register_button = findViewById(R.id.button_main_register);

        login_button.setOnClickListener(this);
        register_button.setOnClickListener(this);

        String dbFilePath = getApplicationContext().getDatabasePath("shared_list_app.db").getAbsolutePath();
        Log.d("Database Path", dbFilePath);
    }

    @Override
    public void onClick(View view) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (view.getId() == R.id.button_main_login) {

            login_button.setVisibility(View.INVISIBLE);
            register_button.setVisibility(View.INVISIBLE);

            transaction.add(R.id.layout_main, LoginFragment.newInstance(), "fragment_login").commit();

        }

        if (view.getId() == R.id.button_main_register) {

            login_button.setVisibility(View.INVISIBLE);
            register_button.setVisibility(View.INVISIBLE);

            transaction.add(R.id.layout_main, RegisterFragment.newInstance(), "fragment_register").commit();

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
    public void onBackPressed() {

        if (getSupportFragmentManager().findFragmentByTag("fragment_login") != null || getSupportFragmentManager().findFragmentByTag("fragment_register") != null) {

            login_button.setVisibility(View.VISIBLE);
            register_button.setVisibility(View.VISIBLE);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (getSupportFragmentManager().findFragmentByTag("fragment_login") != null) {
                transaction.remove(getSupportFragmentManager().findFragmentByTag("fragment_login")).commit();
            }

            if (getSupportFragmentManager().findFragmentByTag("fragment_register") != null) {
                transaction.remove(getSupportFragmentManager().findFragmentByTag("fragment_register")).commit();
            }

        } else {

        }

    }
}