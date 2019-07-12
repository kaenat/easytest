package setup4.com.simpletest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class LoginActivity extends AppCompatActivity {
    String emailText;
    String passwordText;
    EditText email;
    EditText password;

    FirebaseAuth mAuth;
    ToggleSwitch toggleSwitch;


    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(LoginActivity.this);


         toggleSwitch = (ToggleSwitch) findViewById(R.id.loginAsToggle);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Teacher");
        labels.add("Student");
        labels.add("Admin");
        labels.add("Manager");

        toggleSwitch.setLabels(labels);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        LinearLayout next = (LinearLayout) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*emailText = email.getText().toString();
                passwordText = password.getText().toString();

                Global go = new Global();
                go.setEmail(emailText);
                go.setPassword(passwordText);*/
                mGoogleSignInClient.signOut();
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1010);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1010) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            Toast.makeText(LoginActivity.this,"Success",Toast.LENGTH_LONG).show();

            if (toggleSwitch.getCheckedTogglePosition()==0){

                Intent i = new Intent(LoginActivity.this, TeacherActivity.class);
                startActivity(i);


            }
            else if (toggleSwitch.getCheckedTogglePosition()==1){

                Intent i = new Intent(LoginActivity.this, StudentActivity.class);
                startActivity(i);


            }
            if (toggleSwitch.getCheckedTogglePosition()==3){

                Intent i = new Intent(LoginActivity.this, ManagementActivity.class);
                startActivity(i);


            }
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            ///Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
            Toast.makeText(LoginActivity.this,"fail",Toast.LENGTH_LONG).show();

        }
    }

}
