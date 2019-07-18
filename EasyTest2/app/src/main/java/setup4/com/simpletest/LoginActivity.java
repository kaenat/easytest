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
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class LoginActivity extends AppCompatActivity {
    String emailText;
    String passwordText;
    EditText email;
    EditText password;

    FirebaseAuth mAuth;
    ToggleSwitch toggleSwitch;
DatabaseReference mDatabase;

    static GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(LoginActivity.this);


         /*toggleSwitch = (ToggleSwitch) findViewById(R.id.loginAsToggle);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Teacher");
        labels.add("Student");
        labels.add("Admin");
        labels.add("Manager");

        toggleSwitch.setLabels(labels);*/

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        LinearLayout next = (LinearLayout) findViewById(R.id.next);

      //  mGoogleSignInClient.signOut();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (email.getText().toString().equals("admin")&&password.getText().toString().equals("admin")){

                    Intent in= new Intent(LoginActivity.this,AdminRegister.class);
                    startActivity(in);
                }
                else {
                    Toast.makeText(LoginActivity.this,"Admin email/password incorrect",Toast.LENGTH_LONG).show();
                }

               /* emailText = email.getText().toString();
                passwordText = password.getText().toString();

                Global go = new Global();
                go.setEmail(emailText);
                go.setPassword(passwordText);


                ActionCodeSettings actionCodeSettings =
                        ActionCodeSettings.newBuilder()
                                // URL you want to redirect back to. The domain (www.example.com) for this
                                // URL must be whitelisted in the Firebase Console.
                                .setUrl("easytest-17c2e.firebaseapp.com ")
                                // This must be true
                                .setHandleCodeInApp(true)

                                .setAndroidPackageName(
                                        "setup4.com.simpletest",
                                        false, *//* installIfNotAvailable *//*
                                        "12"    *//* minimumVersion *//*)
                                .build();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendSignInLinkToEmail(emailText, actionCodeSettings)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("result", "Email sent.");
                                }
                            }
                        });

                // FirebaseAuth auth = FirebaseAuth.getInstance();
                Intent intent = getIntent();
                String emailLink = intent.getData().toString();

                Global got= new Global();

// Confirm the link is a sign-in with email link.
                if (auth.isSignInWithEmailLink(emailLink)) {
                    // Retrieve this from wherever you stored it


                    // The client SDK will parse the code from the link for you.
                    auth.signInWithEmailLink(got.getEmail(), emailLink)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("msg", "Successfully signed in with email link!");
                                        AuthResult result = task.getResult();
                                        // You can access the new user via result.getUser()
                                        // Additional user info profile *not* available via:
                                        // result.getAdditionalUserInfo().getProfile() == null
                                        // You can check if the user is new or existing:
                                        // result.getAdditionalUserInfo().isNewUser()
                                    } else {
                                        Log.e("msg", "Error signing in with email link", task.getException());
                                    }
                                }
                            });
                }*/

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

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot outerD = dataSnapshot.child("Users");
                    if (outerD.exists()) {


                    for (DataSnapshot innerD : outerD.getChildren()) {
                        String ld = innerD.getKey();
                        String email = (String) innerD.child("UserEmail").getValue();
                        String role = (String) innerD.child("UserRole").getValue();


                        Toast.makeText(LoginActivity.this, "" + account.getDisplayName(), Toast.LENGTH_LONG).show();


                         if (account.getEmail().equals(email)&&role.equals("Student")) {

                            Intent i = new Intent(LoginActivity.this, StudentActivity.class);
                            startActivity(i);
                        }
                        else if (account.getEmail().equals(email)&&role.equals("Teacher")) {

                            Intent i = new Intent(LoginActivity.this, TeacherActivity.class);
                            startActivity(i);
                        }
                        else if (account.getEmail().equals(email)&&role.equals("Management")) {

                            Intent i = new Intent(LoginActivity.this, ManagementActivity.class);
                            startActivity(i);
                        }
                        else{
                             Toast.makeText(LoginActivity.this,"fail",Toast.LENGTH_LONG).show();

                         }


                    }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            ///Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
            Toast.makeText(LoginActivity.this,"fail",Toast.LENGTH_LONG).show();

        }
    }

    public static void logout(){

        mGoogleSignInClient.signOut();




    }

}
