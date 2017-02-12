package ru.arnis.floatingbuttonnavigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ru.arnis.floatingbuttonnavigation.Fragments.InspiredByFragment;
import ru.arnis.floatingbuttonnavigation.Fragments.ScrollViewFragment;
import ru.arnis.floatingbuttonnavigation.Fragments.RecyclerFragment;
import ru.arnis.nav.FloatingButtonBottomNavigation;
import ru.arnis.nav.OnItemClickListener;

public class MainActivity extends AppCompatActivity {

    private FloatingButtonBottomNavigation fbbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbbn = (FloatingButtonBottomNavigation) findViewById(R.id.fbbn);

        fbbn.setTitles("RecyclerView Demo","ScrollView Demo","Inspired By","Library info","Settings");
        fbbn.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                switch (index){
                    case 0: replaceFragment(new RecyclerFragment());break;
                    case 1: replaceFragment(new ScrollViewFragment());break;
                    case 2: replaceFragment(new InspiredByFragment());break;
                }
            }
        });

        addFragment(new RecyclerFragment());
    }

    private void addFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_frame, fragment).commit();
    }
    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_frame,fragment).addToBackStack(null).commit();
    }

    public void hideFbbn(){
        fbbn.hideButton();
    }
    public void showFbbn(){
        fbbn.showButton();
    }
}
