package setup4.com.simpletest;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import setup4.com.simpletest.models.Question;


/**
 * A fragment that launches other parts of the demo application.
 */
public class fragment_questions extends Fragment {
    int togglePosition;
    EditText questionLayout;
    RelativeLayout mcqOptions;

    ListView listView;
    SampleAdapter sa;
    ArrayList<String> mDataList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.zxc, container,
                false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final ToggleSwitch toggleSwitch = (ToggleSwitch) view.findViewById(R.id.questionToggle);
        final ArrayList<String> labels = new ArrayList<>();

        labels.add("Yes/No");
        labels.add("Short");
        labels.add("Long");
        labels.add("MCQs");
        labels.add("Picture");
        toggleSwitch.setLabels(labels);


        togglePosition = toggleSwitch.getCheckedTogglePosition();

        questionLayout = (EditText) view.findViewById(R.id.questionText);
        questionLayout.clearFocus();
        mcqOptions = (RelativeLayout) view.findViewById(R.id.mcqOptions);

        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener() {

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if (position == 3) {
                    mcqOptions.setVisibility(View.VISIBLE);
                } else {
                    mcqOptions.setVisibility(View.GONE);
                }
            }
        });

        if (togglePosition == 3) {
            mcqOptions.setVisibility(View.VISIBLE);
        } else {
            mcqOptions.setVisibility(View.GONE);
        }

        listView = (ListView) view.findViewById(R.id.listView);
        sa = new SampleAdapter();
        listView.setAdapter(sa);

        view.findViewById(R.id.addOption).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText txtUrl = new EditText(getContext());
                txtUrl.setHint("option");

                new AlertDialog.Builder(getContext())
                        .setTitle("MCQ Option")
                        .setMessage("Enter your option")
                        .setView(txtUrl)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String option = txtUrl.getText().toString();
                                if (option.equals("")) {
                                    Toast.makeText(getContext(), "option cannot be empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                sa.addOption(option);
                                txtUrl.clearFocus();
                                questionLayout.clearFocus();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
            }
        });

        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int togglePosition = toggleSwitch.getCheckedTogglePosition();
                String questionStatement = questionLayout.getText().toString();

                if (mDataList.isEmpty()) {
                    Toast.makeText(getContext(), "No Options", Toast.LENGTH_SHORT).show();
                }

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                Question question = new Question(togglePosition + "", questionStatement);
                if (togglePosition == 3) {
                    question.setOptions(mDataList);
                }
                String uniqueID = UUID.randomUUID().toString();
                mDatabase.child("Questions").child(uniqueID).setValue(question);

                questionLayout.setText("");
                sa.refresh();
            }
        });
    }

    private class SampleAdapter extends BaseAdapter {

        public void addOption(String option) {
            mDataList.add(option);
            notifyDataSetChanged();
        }

        public void removeOption(int i) {
            mDataList.remove(i);
            notifyDataSetChanged();
        }

        public void refresh() {
            mDataList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public String getItem(int position) {
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
                convertView = View.inflate(getActivity(), R.layout.list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String item = getItem(position);

            holder.question.setText("Option " + (position + 1) + ": " + item);

            holder.imageButton.setTag(position);
            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("MCQ Option")
                            .setMessage("Are you sure you want to delete ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    removeOption((Integer) view.getTag());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .show();
                }
            });

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

}