package ru.arnis.floatingbuttonnavigation.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ru.arnis.floatingbuttonnavigation.MainActivity;
import ru.arnis.floatingbuttonnavigation.R;

public class ListviewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_layout,container,false);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,android.R.id.text1 ,getResources().getStringArray(R.array.listview_items)));
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP: ((MainActivity)getActivity()).showFbbn();break;
                    case MotionEvent.ACTION_DOWN: ((MainActivity)getActivity()).hideFbbn();break;
                }
                return false;
            }
        });
        return view;
    }
}
