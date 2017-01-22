package ru.arnis.floatingbuttonnavigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.arnis.nav.FloatingButtonBottomNavigation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingButtonBottomNavigation fbbn = (FloatingButtonBottomNavigation) findViewById(R.id.fbbn);

        fbbn.setTitles("Item 1","Item 2","Item 3","Item 4","Item 5");
    }
}
