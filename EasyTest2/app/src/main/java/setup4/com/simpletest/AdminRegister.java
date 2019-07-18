package setup4.com.simpletest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

import setup4.com.simpletest.models.User;

public class AdminRegister extends AppCompatActivity {
    ArrayList list;
    Spinner roleSpinner;
    String emailText;
    EditText email;
    DatabaseReference mDatabase;
    ArrayList<String> mDatalist=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        roleSpinner= (Spinner) findViewById(R.id.role);
        email= (EditText) findViewById(R.id.email);



        list = new ArrayList<String>();
        list.add("Student");
        list.add("Teacher");
        list.add("Management");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminRegister.this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void registerUser(View view) {

        String roleText=roleSpinner.getSelectedItem().toString();
        emailText= email.getText().toString();

        addDatabaseEntry(roleText,emailText);
    }

    private void addDatabaseEntry(String role,String email) {
        User user = new User(role, email);
        mDatabase.child("Users").child(UUID.randomUUID().toString()).setValue(user);

        setResult(Activity.RESULT_OK, new Intent());

        Toast.makeText(this, "User registered successfully", Toast.LENGTH_LONG).show();


    }


    public void logoutAdmin(View view) {

        Intent in= new Intent(this,LoginActivity.class);
        startActivity(in);
    }
}








