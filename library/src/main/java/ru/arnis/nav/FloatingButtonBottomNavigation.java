package ru.arnis.nav;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import io.codetail.animation.ViewAnimationUtils;


/**
 * Created by arnis on 23/12/2016.
 */

public class FloatingButtonBottomNavigation extends RelativeLayout{
    private static final int REVEAL_DURATION = 350;

    private LinearLayout itemsList;
    private View revealView;
    private FloatingButton floatingButton;
    private ImageView overlayView;
    private GestureDetector gd;
    private boolean showing;
    private boolean animating;

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    private void setAnimating(boolean animating) {
        this.animating = animating;
    }

    public FloatingButtonBottomNavigation(Context context) {
        super(context);
        init(context, null);
    }

    public FloatingButtonBottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingButtonBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        inflate(context,R.layout.main_layout,this);
        itemsList = (LinearLayout) findViewById(R.id.items_list);
        revealView = findViewById(R.id.reveal);
        overlayView = (ImageView) findViewById(R.id.overlay);
        floatingButton = new FloatingButton();
        gd = new GestureDetector(context, new GestureListener());

        applyAttributes(context,attrs);
    }

    public void setTitles(final String... titles){
        adjustRevealLayout(titles.length);

        for (String title:titles){
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_item,null);
            final FloatingButtonBottomNavigationItem item = new FloatingButtonBottomNavigationItem(view);
            item.setTitle(title);

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.setSelection();
                    hideNav(260);

                    if (onItemClickListener!=null)
                        onItemClickListener.onItemClick(itemsList.indexOfChild(view));
                }
            });
            itemsList.addView(view);
        }
    }
    public void forceHide(){
        hideNav(0);
    }
    public void hideButton(){
        floatingButton.hide();
    }
    public void showButton(){
        floatingButton.show();
    }

    private void adjustRevealLayout(int totalItems){
        int navHeight = FloatingButtonBottomNavigationItem.getItemHeight(getResources().getDisplayMetrics().density)*totalItems;
        LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, navHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        findViewById(R.id.reveal_wrap).setLayoutParams(params);
    }

    private void applyAttributes(Context context, AttributeSet attrs){
        if (attrs!=null){

            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.FloatingButtonBottomNavigation,
                    0, 0);

            try {
                int mainColor = a.getColor(R.styleable.FloatingButtonBottomNavigation_mainColor, getResources().getColor(R.color.main));
                int colorTheme = a.getInt(R.styleable.FloatingButtonBottomNavigation_colorTheme, 0);
                FloatingButtonBottomNavigationItem.setColorTheme(context,colorTheme);
                FloatingButtonBottomNavigationItem.setHighlightSelectedItem(a.getBoolean(R.styleable.FloatingButtonBottomNavigation_highlightSelectedItem, true));

                revealView.setBackgroundColor(mainColor);
                floatingButton.setCustomColor(mainColor);
                if (colorTheme!=0){
                    floatingButton.setIconColor(getResources().getColor(R.color.textSelectedLight));
                }
                Log.d("happy", "applyAttributes: ");
            } finally {
                a.recycle();
            }
        }
    }

    private void showNav(){
        long delay = 0;
        if (!showing){
            setAnimating(true);
            reveal(delay);
            animateList(delay);
            dim(delay);
        }
    }
    private void hideNav(long delay){
        if (showing){
            setAnimating(true);
            reveal(delay);
            animateList(delay);
            dim(delay);
            floatingButton.restore(delay);
        }
    }

    private void reveal(long delay){
        floatingButton.setButtonCenter();

        int cx = (revealView.getLeft() + revealView.getRight()) / 2;
        int cy = floatingButton.getButtonCenter();

        int dx = Math.max(cx, this.getWidth() - cx);
        int dy = Math.max(cy, this.getHeight() - cy);
        final float finalRadius = (float) Math.hypot(dx, dy);

        Animator animator;
        if (!showing){
            animator = ViewAnimationUtils.createCircularReveal(revealView, cx, cy, floatingButton.getRadius(), finalRadius);
        }
        else{
            animator = ViewAnimationUtils.createCircularReveal(revealView, cx, cy, finalRadius, 0);
        }

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(REVEAL_DURATION);
        animator.setStartDelay(delay);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                revealView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setAnimating(false);
                if (showing){
                    revealView.setVisibility(INVISIBLE);
                    itemsList.setVisibility(INVISIBLE);
                }

                showing = !showing;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private void dim(long delay){
        if (!showing)
            overlayView.animate().setStartDelay(delay).alpha(.7f).setDuration(300).start();
        else overlayView.animate().setStartDelay(delay).alpha(0).setDuration(300).start();
    }

    private void animateList(long delay){
        itemsList.setVisibility(VISIBLE);
        for (int i = 0; i < itemsList.getChildCount(); i++) {
            FloatingButtonBottomNavigationItem.getItem(i).animateItemTransition(showing,delay);
            View child = itemsList.getChildAt(i);
            animateItem(child,i,delay);
        }
    }
    private void animateItem(View view, int iter, long delay){
        PropertyValuesHolder pvhY;
        if (!showing){
            pvhY = PropertyValuesHolder.ofFloat(Y,floatingButton.getAdjustedCenter(),iter*FloatingButtonBottomNavigationItem.getItemHeight(getResources().getDisplayMetrics().density));
        } else {
            pvhY = PropertyValuesHolder.ofFloat(Y,iter*FloatingButtonBottomNavigationItem.getItemHeight(getResources().getDisplayMetrics().density),floatingButton.getAdjustedCenter());
        }

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view,pvhY);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setStartDelay(delay);
        animator.setDuration(REVEAL_DURATION);
        animator.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        gd.onTouchEvent(ev);
        return animating;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FloatingButtonBottomNavigationItem.clearAll();
    }

    private class FloatingButton {
        private RelativeLayout parentView;
        private ImageView background;
        private ImageView icon;
        private float buttonY;
        private int buttonCenter;

        FloatingButton() {
            parentView = (RelativeLayout) findViewById(R.id.button_parent);
            background = (ImageView) findViewById(R.id.background);
            icon = (ImageView) findViewById(R.id.icon);
            background.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN: press(); break;
                        case MotionEvent.ACTION_UP: release(); showNav(); break;
                    }
                    return false;
                }
            });
        }

        void press(){
            background.animate().scaleX(.8f).scaleY(0.8f).setDuration(170).setInterpolator(new DecelerateInterpolator()).start();
        }
        void release(){
            background.animate().scaleX(1f).scaleY(1f).setDuration(0).start();
            parentView.setVisibility(INVISIBLE);
        }
        void restore(long delay){
            parentView.setScaleY(0f);
            parentView.setScaleX(0f);
            parentView.animate().setStartDelay(delay+200).scaleX(1f).scaleY(1f).setInterpolator(new OvershootInterpolator()).setDuration(400).start();
            background.setVisibility(VISIBLE);
            parentView.setVisibility(VISIBLE);
        }
        void hide(){
            setButtonY(parentView.getY());
            parentView.animate().y(overlayView.getHeight()).start();
        }
        void show(){
            parentView.animate().y(buttonY).start();
        }
        void setButtonCenter(){
            if (buttonCenter==0)
                buttonCenter = (int) ((parentView.getY()+(parentView.getHeight()/2)-(overlayView.getHeight()- revealView.getHeight())));
        }
        void setCustomColor(int color){
            background.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        void setIconColor(int color){
            icon.setColorFilter(color,PorterDuff.Mode.SRC_ATOP);
        }
        int getButtonCenter() {
            return buttonCenter;
        }
        int getRadius(){
            return background.getHeight()/2;
        }
        int getAdjustedCenter(){
            return getButtonCenter()-getRadius();
        }

        private void setButtonY(float buttonY) {
            if (this.buttonY==0)
                this.buttonY = buttonY;
        }

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float yDif = e2.getY()-e1.getY();
            float xDif = e2.getX()-e1.getX();

            if (Math.abs(xDif)<yDif&&yDif>0){
                hideNav(0);
            }
            return false;
        }
    }
}
