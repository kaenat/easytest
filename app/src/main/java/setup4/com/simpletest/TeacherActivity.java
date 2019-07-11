package setup4.com.simpletest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;


public class TeacherActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.main_tab_content);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        /*test t = new test("1", "2");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                test post = dataSnapshot.getValue(test.class);
                Log.w("", post.name);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        myRef.child("Questions").addValueEventListener(postListener);*/
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPageAdapter Adapter = new ViewPageAdapter(getSupportFragmentManager());
        Adapter.addFragments(new fragment_questions(), "Questions");
        Adapter.addFragments(new fragment_answers(), "Answers");
        viewPager.setAdapter(Adapter);
    }


    public class ViewPageAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> tabT = new ArrayList<>();

        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragments(Fragment fragment, String titles) {
            this.fragments.add(fragment);
            this.tabT.add(titles);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public CharSequence getPageTitle(int position) {
            return tabT.get(position);
        }

    }

}