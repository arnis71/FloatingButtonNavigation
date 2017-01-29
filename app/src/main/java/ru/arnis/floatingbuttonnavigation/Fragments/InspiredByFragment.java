package ru.arnis.floatingbuttonnavigation.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import ru.arnis.floatingbuttonnavigation.R;

public class InspiredByFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.insipred_by_layout,container,false);
        WebView webView = (WebView) view.findViewById(R.id.gif_view);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.loadUrl("file:///android_asset/inspiration.gif");
        return view;
    }
}
