package edu.uiuc.cs427app.utils;

import android.view.View;

import androidx.test.espresso.IdlingResource;

import org.hamcrest.Matcher;

/**
 *  A custom class to listen to layout change callbacks
 */
public class LayoutChangeCallback implements IdlingResource, View.OnLayoutChangeListener {

    private Matcher<View> matcher;
    private IdlingResource.ResourceCallback callback;
    private boolean matched = false;

    public LayoutChangeCallback(Matcher<View> matcher) {
        this.matcher = matcher;
    }

    @Override public String getName() {
        return "Layout change callback";
    }

    @Override public boolean isIdleNow() {
        return matched;
    }

    @Override public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    @Override public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        matched = matcher.matches(v);
        callback.onTransitionToIdle();
    }
}