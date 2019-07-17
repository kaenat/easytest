package setup4.com.simpletest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class ManagementActivity extends AppCompatActivity {

    private PieChartView chart;
    private PieChartData data;

    private boolean hasLabels = true;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;

    private int numberOfQuestions = 0;
    private int numberOfAnswers = 0;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        chart = findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        fetchData();
    }

    private void fetchData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot outerD = dataSnapshot.child("Questions");
                for (DataSnapshot innerD : outerD.getChildren()) {
                    numberOfQuestions++;
                }
                outerD = dataSnapshot.child("Answers");
                for (DataSnapshot innerD : outerD.getChildren()) {
                    numberOfAnswers++;
                }
                generateData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void generateData() {
        List<SliceValue> values = new ArrayList<SliceValue>();
        SliceValue sliceValue = new SliceValue((float) numberOfQuestions, ChartUtils.pickColor());
        sliceValue.setLabel("Total Questions: " + numberOfQuestions);
        values.add(sliceValue);
        sliceValue = new SliceValue((float) numberOfAnswers, ChartUtils.pickColor());
        sliceValue.setLabel("Questions Attempted:" + numberOfAnswers);
        values.add(sliceValue);

        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);

        if (isExploded) {
            data.setSlicesSpacing(24);
        }

        if (hasCenterText1) {
            data.setCenterText1("Hello!");

            // Get roboto-italic font.
            Typeface tf = Typeface.DEFAULT;
            data.setCenterText1Typeface(tf);

            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    40));
        }

        if (hasCenterText2) {
            data.setCenterText2("Charts (Roboto Italic)");

            Typeface tf = Typeface.SANS_SERIF;

            data.setCenterText2Typeface(tf);
            data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    40));
        }

        chart.setPieChartData(data);


        ImageButton logout = (ImageButton) findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.logout();
                Intent a= new Intent(ManagementActivity.this,LoginActivity.class);
                startActivity(a);
            }
        });
    }

    private class ValueTouchListener implements PieChartOnValueSelectListener {
        float val;
        String label;
        SliceValue selectedSlice;

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            /*selectedSlice = value;

            val = value.getValue();
            label = Arrays.toString(value.getLabelAsChars());
            Toast.makeText(getApplicationContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            value.setLabel(val + "");*/
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub
            //selectedSlice.setLabel(label);
        }
    }
}
