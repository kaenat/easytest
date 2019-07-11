package setup4.com.simpletest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import setup4.com.simpletest.models.Answer;


public class student_question_popup extends AppCompatActivity {

    TextView question;
    ListView lv;
    EditText editText;
    ArrayList<String> mOptions = new ArrayList<String>();
    int selectedOption = -1;

    LinearLayout pictureLayout;
    private Button btnChoose, btnUpload;
    private ImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    private static final int TAKE_PICTURE = 1;

    //Firebase
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;

    String questionID;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        final int width = dm.widthPixels;
        final int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .75), (int) (height * .5));
        questionID = getIntent().getStringExtra("ID");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Questions").child(questionID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String q = (String) dataSnapshot.child("question").getValue();
                type = (String) dataSnapshot.child("type").getValue();

                question.setText(q);
                if (type.equals("Yes/No")) {
                    lv.setVisibility(View.VISIBLE);

                    mOptions = new ArrayList<String>();
                    mOptions.add("Yes");
                    mOptions.add("No");
                } else if (type.equals("MCQs")) {
                    lv.setVisibility(View.VISIBLE);

                    mOptions = new ArrayList<String>();
                    List<String> options = (List<String>) dataSnapshot.child("options").getValue();
                    mOptions.addAll(options);
                } else if (type.equals("Short") || type.equals("Long")) {
                    editText.setVisibility(View.VISIBLE);
                } else if (type.equals("Picture")) {
                    getWindow().setLayout((int) (width * .75), (int) (height * .75));
                    pictureLayout.setVisibility(View.VISIBLE);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mOptions);
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        question = findViewById(R.id.question);
        lv = findViewById(R.id.listView);
        lv.setOnItemClickListener((adapterView, view, i, l) -> selectedOption = i);
        editText = findViewById(R.id.answerText);

        pictureLayout = findViewById(R.id.pictureQuestion);
        //Initialize Views
        btnChoose = findViewById(R.id.btnGallery);
        btnUpload = findViewById(R.id.btnCamera);
        imageView = findViewById(R.id.imgView);

        btnChoose.setOnClickListener(v -> chooseImage());

        btnUpload.setOnClickListener(v -> takePhoto(v));

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void takePhoto(View view) {
        new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {

            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            filePath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            startActivityForResult(intent, TAKE_PICTURE);
        }
    }

    public void submit(View view) {
        if (type.equals("Yes/No") || type.equals("MCQs")) {
            if (selectedOption == -1) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
            } else {
                addDatabaseEntry(mOptions.get(selectedOption));
            }
        } else if (type.equals("Short") || type.equals("Long")) {
            String ans = editText.getText().toString();
            if (ans.equals("")) {
                Toast.makeText(this, "Answer cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                addDatabaseEntry(ans);
            }
        } else if (type.equals("Picture")) {
            if (filePath == null) {
                Toast.makeText(this, "Answer cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImage = filePath;

            getContentResolver().notifyChange(selectedImage, null);
            ContentResolver cr = getContentResolver();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media
                        .getBitmap(cr, selectedImage);

                imageView.setImageBitmap(bitmap);
                Toast.makeText(this, selectedImage.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                        .show();
                Log.e("Camera", e.toString());
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString() + ".jpg");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            addDatabaseEntry(ref.getPath());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void addDatabaseEntry(String answer) {
        Answer a = new Answer(questionID, "student@gmail.com", answer);
        mDatabase.child("Answers").child(UUID.randomUUID().toString()).setValue(a);

        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }
}
