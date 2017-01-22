package ru.arnis.nav;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnis on 23/12/2016.
 */

class FloatingButtonBottomNavigationItem {
    private static ItemManager itemManager = new FloatingButtonBottomNavigationItem.ItemManager();
    private static final int ITEM_HEIGHT = 80;
    static int getItemHeight(float density) {
        return (int) (ITEM_HEIGHT*density);
    }

    private static boolean highlightSelectedItem;
    private static int textNotSelected;
    private static int textSelected;
    private static int highlightItem;

    static void setHighlightSelectedItem(boolean value){
        highlightSelectedItem = value;
    }
    static void setColorTheme(Context context, int colorTheme){
        if (colorTheme==0){
            textNotSelected = context.getResources().getColor(R.color.textNotSelectedDark);
            textSelected = context.getResources().getColor(R.color.textSelectedDark);
            highlightItem = context.getResources().getColor(R.color.highlightItemDark);
        } else {
            textNotSelected = context.getResources().getColor(R.color.textNotSelectedLight);
            textSelected = context.getResources().getColor(R.color.textSelectedLight);
            highlightItem = context.getResources().getColor(R.color.highlightItemLight);
        }
    }

    private TextView title;
    private ImageView dash;
    private ImageView highlight;
    private boolean active;

    private void setActive(boolean active) {
        this.active = active;
    }
    void setTitle(String text) {
        title.setText(text);
    }

    FloatingButtonBottomNavigationItem(View view) {
        dash = (ImageView) view.findViewById(R.id.dash);
        title = (TextView) view.findViewById(R.id.title);
        if (highlightSelectedItem){
            highlight = (ImageView) view.findViewById(R.id.highlight);
            highlight.setColorFilter(highlightItem);
        }
        title.setTextColor(textNotSelected);
        dash.setColorFilter(textNotSelected);
        itemManager.newItem(this);
    }

    private void press(){
        PropertyValuesHolder pvhXin = PropertyValuesHolder.ofFloat(View.SCALE_X,1f,0.8f);
        PropertyValuesHolder pvhYin = PropertyValuesHolder.ofFloat(View.SCALE_Y,1f,0.8f);
        ObjectAnimator animatorIN = ObjectAnimator.ofPropertyValuesHolder(title,pvhXin,pvhYin);
        animatorIN.setDuration(100);
        animatorIN.setInterpolator(new DecelerateInterpolator());

        PropertyValuesHolder pvhXout = PropertyValuesHolder.ofFloat(View.SCALE_X,0.8f,1f);
        PropertyValuesHolder pvhYout = PropertyValuesHolder.ofFloat(View.SCALE_Y,0.8f,1f);
        ObjectAnimator animatorOut = ObjectAnimator.ofPropertyValuesHolder(title,pvhXout,pvhYout);
        animatorOut.setDuration(100);
        animatorOut.setInterpolator(new AccelerateInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animatorIN,animatorOut);
        set.start();
    }
    private void showBackground(){
        if (highlightSelectedItem)
            highlight.animate().alpha(1f).setDuration(100).setInterpolator(new AccelerateInterpolator()).start();
    }
    private void hideBackground(){
        if (highlightSelectedItem)
            highlight.animate().alpha(0f).setDuration(20).setInterpolator(new DecelerateInterpolator()).start();
    }

    void animateItemTransition(final boolean showing, long delay){
        if (!showing){
            dash.animate().setStartDelay(delay+100).alpha(0f).setDuration(300);
            title.animate().setStartDelay(delay).alpha(1f).scaleX(1f).scaleY(1f).setDuration(500).setInterpolator(new DecelerateInterpolator());
            if (active)
                selection(true);
        } else {
            dash.animate().setStartDelay(delay).alpha(1f).setDuration(350).start();
            title.animate().setStartDelay(delay).alpha(0f).scaleX(0f).scaleY(0f).setDuration(400)
                    .withStartAction(new Runnable() {
                @Override
                public void run() {
                    if (active)
                        selection(false);
                }
            });
        }
    }

    private void selection(boolean select){
        if (select){
            showBackground();
            title.setTypeface(title.getTypeface(),Typeface.BOLD);
            title.setTextColor(textSelected);
        } else {
            hideSelection();
        }
    }
    private void hideSelection(){
        hideBackground();
        title.setTypeface(title.getTypeface(),Typeface.NORMAL);
        title.setTextColor(textNotSelected);
    }
    void setSelection(){
        press();
        itemManager.setSelection(this);
    }

    private void clear(){
        title = null;
        dash = null;
        highlight = null;
    }
    static void clearAll(){
        itemManager.clear();
    }
    static FloatingButtonBottomNavigationItem getItem(int index){
        return itemManager.items.get(index);
    }

    private static class ItemManager{
        private List<FloatingButtonBottomNavigationItem> items;
        private FloatingButtonBottomNavigationItem activeItem;
        private FloatingButtonBottomNavigationItem lastItem;

        private ItemManager() {
            items = new ArrayList<>();
        }

        private void clear(){
            for (FloatingButtonBottomNavigationItem item:items){
                item.clear();
            }
            items.clear();
        }

        private void newItem(FloatingButtonBottomNavigationItem item){
            if (items.size()==0){
                activeItem = item;
                lastItem = item;
                item.setActive(true);
            }
            items.add(item);
        }

        private void setSelection(FloatingButtonBottomNavigationItem item){
            activeItem = item;
            activeItem.setActive(true);
            activeItem.selection(true);
            boolean isNewItemClick = lastItem != null && lastItem != activeItem;
            if (lastItem!=null&&isNewItemClick){
                lastItem.selection(false);
                lastItem.setActive(false);
            }
            lastItem = activeItem;
        }
    }
}
