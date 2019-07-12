package setup4.com.simpletest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import setup4.com.simpletest.models.Question;

public class StudentActivity extends AppCompatActivity {

    ListView listView;
    SampleAdapter sa;
    ArrayList<ListData> mDataList = new ArrayList<ListData>();
    int selectedOption;
    GoogleSignInClient mGoogleSignInClient;

    ImageButton logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        listView = (ListView) findViewById(R.id.listView);

        logout = (ImageButton) findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut();
                Intent a= new Intent(StudentActivity.this,LoginActivity.class);
                startActivity(a);
            }
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedOption = i;

            Intent intent = new Intent(getApplicationContext(), student_question_popup.class);
            intent.putExtra("ID", mDataList.get(i).ID);
            startActivityForResult(intent, 101);
            sa.refresh();
        });
        sa = new SampleAdapter();
        listView.setAdapter(sa);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot outerD = dataSnapshot.child("Questions");
                for (DataSnapshot innerD : outerD.getChildren()) {
                    ListData ld = new ListData(innerD.getKey(), (String) innerD.child("question").getValue());
                    sa.addOption(ld);
                }
                outerD = dataSnapshot.child("Answers");
                for (DataSnapshot innerD : outerD.getChildren()) {
                    String id = (String) innerD.child("questionID").getValue();
                  //  sa.removeOption(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private class SampleAdapter extends BaseAdapter {
        ArrayList<ListData> tempArr = new ArrayList<>();

        public void addOption(ListData option) {
            mDataList.add(option);
            notifyDataSetChanged();
        }

        public void removeOption(int i) {
            mDataList.remove(i);
            notifyDataSetChanged();
        }

        public void removeOption(String id) {
            int i = 0;
            for (; i <= mDataList.size(); i++) {
                if (mDataList.get(i).ID.equals(id))
                    break;
            }
            mDataList.remove(i);
        }

        public void refresh() {
            tempArr = (ArrayList<ListData>) mDataList.clone();
            mDataList.clear();
            notifyDataSetChanged();
        }

        public void restore() {
            mDataList = tempArr;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public ListData getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData item = getItem(position);

            holder.question.setText(item.question);
            holder.imageButton.setTag(position);
            holder.imageButton.setVisibility(View.GONE);

            return convertView;
        }
    }

    private static class ViewHolder {
        private View view;
        private TextView question;
        private ImageButton imageButton;

        private ViewHolder(View view) {
            this.view = view;
            question = (TextView) view.findViewById(R.id.textView);
            imageButton = (ImageButton) view.findViewById(R.id.cancelButton);
        }
    }

    private class ListData {
        String question;
        String ID;

        ListData(String ID, String question) {
            this.ID = ID;
            this.question = question;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode != Activity.RESULT_OK) {
            sa.restore();
        }
    }
}
