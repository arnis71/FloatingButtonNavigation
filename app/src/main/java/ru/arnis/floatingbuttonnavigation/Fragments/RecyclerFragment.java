package ru.arnis.floatingbuttonnavigation.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.arnis.floatingbuttonnavigation.MainActivity;
import ru.arnis.floatingbuttonnavigation.R;
import ru.arnis.floatingbuttonnavigation.RecyclerViewAdapter;

public class RecyclerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_layout,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerViewAdapter(getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int last = 0;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy<0&&last>0)
                    ((MainActivity)getActivity()).showFbbn();
                else if (dy>0&&last<=0)
                    ((MainActivity)getActivity()).hideFbbn();

                last = dy;
            }
        });
        return view;
    }
}
