package nikola.dragomirovic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterFragment extends Fragment implements View.OnClickListener {
    Button register;
    EditText username;
    EditText email;
    EditText password;
    TextView empty_form;
    TextView wrong_email;
    DatabaseHelper database_helper;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance() {

        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database_helper = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        username = view.findViewById(R.id.form_register_username);

        email = view.findViewById(R.id.form_register_email);

        password = view.findViewById(R.id.form_register_password);

        empty_form = view.findViewById(R.id.text_register_warning_empty);

        wrong_email = view.findViewById(R.id.text_register_warning_email_format);

        register = view.findViewById(R.id.button_register);

        register.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_register) {

            if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {

                empty_form.setVisibility(View.VISIBLE);
                wrong_email.setVisibility(View.GONE);

            } else if (!email.getText().toString().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")) {

                wrong_email.setVisibility(View.VISIBLE);
                empty_form.setVisibility(View.GONE);

            } else {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String registrationUrl = HttpHelper.ADDRESS + "/users";
                            JSONObject userJson = new JSONObject();
                            userJson.put("username", username.getText().toString());
                            userJson.put("password", password.getText().toString());
                            userJson.put("email", email.getText().toString());

                            HttpHelper http_helper = new HttpHelper();
                            boolean registrationSuccessful = http_helper.postJSONObjectFromURL(registrationUrl, userJson);

                            if (registrationSuccessful) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getActivity(), WelcomeActivity.class);

                                        Bundle bundle = new Bundle();
                                        bundle.putString("username", username.getText().toString());

                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity().getApplicationContext(), "Username already exists!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        }
    }
}