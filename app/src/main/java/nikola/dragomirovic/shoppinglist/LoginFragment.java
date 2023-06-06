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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private Button login_button;
    private EditText username;
    private EditText password;
    private TextView wrong;
    DatabaseHelper database_helper;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {

        LoginFragment fragment = new LoginFragment();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        database_helper = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        login_button = view.findViewById(R.id.button_login);

        username = view.findViewById(R.id.form_login_username);

        password = view.findViewById(R.id.form_login_password);

        wrong = view.findViewById(R.id.text_login_wrong_username_password);

        login_button.setOnClickListener(this);

        return view;

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_login) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String loginUrl = HttpHelper.ADDRESS + "/login";
                        JSONObject userJson = new JSONObject();
                        userJson.put("username", username.getText().toString());
                        userJson.put("password", password.getText().toString());

                        HttpHelper http_helper = new HttpHelper();
                        boolean login_success = http_helper.postJSONObjectFromURL(loginUrl, userJson);

                        if(login_success){
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
                        }else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    wrong.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }catch (IOException | JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }
}