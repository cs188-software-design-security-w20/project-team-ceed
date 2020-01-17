package com.ceed.tripster;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class LogInFragment extends Fragment implements View.OnClickListener {

    private Context _context;
    private NavController _navController;
    private Button _buttonLogIn;
    private EditText _editTextEmailLogIn;
    private EditText _editTextPasswordLogIn;
    private TextView _textViewRegister;

    private ProgressBar _progressBarRegister;


    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this._context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        _buttonLogIn = (Button) view.findViewById(R.id.buttonLogIn);
        _editTextEmailLogIn = (EditText) view.findViewById(R.id.editTextEmailLogIn);
        _editTextPasswordLogIn = (EditText) view.findViewById(R.id.editTextPasswordLogIn);
        _textViewRegister = (TextView) view.findViewById(R.id.textViewRegister);

        _buttonLogIn.setOnClickListener(this);
        _textViewRegister.setOnClickListener(this);
        _progressBarRegister = new ProgressBar(_context);

        _navController = Navigation.findNavController(view);

    }

    @Override
    public void onClick(View view) {
        if (view == _buttonLogIn) {
            logIn();
        } else if (view == _textViewRegister) {
            // open sign-in activity
            _navController.navigate(R.id.action_logInFragment_to_signUpFragment);
        }
    }

    private void logIn() {
        String email = _editTextEmailLogIn.getText().toString().trim();
        String password = _editTextPasswordLogIn.getText().toString().trim();

        // Validate the email and password inputted by the user
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(_context, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        // Show the progress bar
        _progressBarRegister.setVisibility(View.VISIBLE);


    }

}
