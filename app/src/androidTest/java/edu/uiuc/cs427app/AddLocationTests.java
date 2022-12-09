package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.actionWithAssertions;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.greaterThan;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uiuc.cs427app.database.user.UserDBHelper;
import edu.uiuc.cs427app.model.User;
import edu.uiuc.cs427app.model.UserSelectedLocation;
import edu.uiuc.cs427app.utils.LayoutChangeCallback;
import edu.uiuc.cs427app.utils.ToastMatcher;

@RunWith(AndroidJUnit4.class)
public class AddLocationTests {

    private final String testName = "testNameAddLocation";
    private final String testPassword = "testPassword";
    private final UserSelectedLocation testChampaign = new UserSelectedLocation(
            "Champaign", "40.1164204", "-88.2433829");
    private final String mockUserSelectedLocations = testChampaign.serialize() + "\t";
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private UserDBHelper dbAddLocationTest;
    private User userAddLocationTest;

    /**
     * Checks if the recycler view has a matching item count
     *
     * @param matcher a Matcher for matching the item counts
     * @return recycler view when the item count in view matches
     */
    private static Matcher<View> hasItemCount(Matcher<Integer> matcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has item count: ");
                matcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView view) {
                return matcher.matches(view.getAdapter().getItemCount());
            }
        };
    }

    /**
     * Method to wait until all the data has been loaded by the async request
     *
     * @param matcher a Matcher for matching views
     * @return a ViewAction when the matcher matches the condition
     */
    private static ViewAction waitUntil(Matcher<View> matcher) {
        return actionWithAssertions(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(View.class);
            }

            @Override
            public String getDescription() {
                StringDescription description = new StringDescription();
                matcher.describeTo(description);
                return String.format("wait until: %s", description);
            }

            @Override
            public void perform(UiController uiController, View view) {
                if (!matcher.matches(view)) {
                    LayoutChangeCallback callback = new LayoutChangeCallback(matcher);
                    try {
                        IdlingRegistry.getInstance().register(callback);
                        view.addOnLayoutChangeListener(callback);
                        uiController.loopMainThreadUntilIdle();
                    } finally {
                        view.removeOnLayoutChangeListener(callback);
                        IdlingRegistry.getInstance().unregister(callback);
                    }
                }
            }
        });
    }

    /**
     * set up test temp db before each test, and insert a fake user with 1 location in her list.
     */
    @Before
    public void createUserInDb() {
        dbAddLocationTest = new UserDBHelper(context);
        userAddLocationTest = new User();
        userAddLocationTest.setUserName(testName);
        userAddLocationTest.setPassword(testPassword);
        dbAddLocationTest.insertUserData(userAddLocationTest, mockUserSelectedLocations);
    }

    /**
     * close test temp db after each test.
     */
    @After
    public void finish() {
        //remove the test user when the test is finished
        dbAddLocationTest.removeUserData(userAddLocationTest);
        dbAddLocationTest.close();
    }

    /**
     * This method is used to test part of the AddLocationActivity
     * including searching location, selecting the right location from google place widget
     * and adding it into the user location list correctly
     */
    @Test
    public void checkAddLocation() throws InterruptedException {

        ActivityScenario.launch(new Intent(
                context, MainActivity.class)
                .putExtra("username", testName));

        String addChicago = "Chicago";

        Thread.sleep(2000);

        //start to add a location, which is to click the add location button on Main page
        onView(withId(R.id.buttonAddLocation)).perform(click());
        Thread.sleep(2000);

        //jump to AddLocationActivity and start to search locations, first test adding Chicago
        onView(withId(R.id.search_location_text)).perform(typeText(addChicago));
        Thread.sleep(2000);

        //in the place prediction list built up by Google Place API, pick the first item and click
        //so that this place is filled into the search location text field
        onView(ViewMatchers.withId(com.google.android.libraries.places.R.id.places_autocomplete_list))
                .perform(waitUntil(hasItemCount(greaterThan(0))), RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);

        //click confirm button to add this location to the location list on the main page.
        onView(withId(R.id.add_location_confirm)).perform(click());
        Thread.sleep(2000);

        //check if the shown location list include the location we just picked.
        onView(withText(addChicago)).check(matches((isDisplayed())));

    }

    /**
     * This method is used to test part of the AddLocationActivity
     * specially for empty input, which will trigger a warning toast message
     */
    @Test
    public void checkAddEmptyLocation() throws InterruptedException {

        ActivityScenario.launch(new Intent(
                context, MainActivity.class)
                .putExtra("username", testName));

        String toastMessage = "You have not entered anything!";

        Thread.sleep(2000);

        //start to add a location, which is to click the add location button on Main page
        onView(withId(R.id.buttonAddLocation)).perform(click());
        Thread.sleep(2000);

        //jump to AddLocationActivity, but do not input anything, just click add location button directly
        onView(withId(R.id.add_location_confirm)).perform(click());
        Thread.sleep(500);

        //check if the toast notification is displayed
        onView(withText(toastMessage))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * This method is used to test part of the AddLocationActivity
     * specially for clear text in the input field
     */
    @Test
    public void checkClearLocation() throws InterruptedException {

        ActivityScenario.launch(new Intent(
                context, AddLocationActivity.class));

        String addChicago = "Chicago";

        Thread.sleep(2000);

        //start to search a location
        onView(withId(R.id.search_location_text)).perform(typeText(addChicago));
        Thread.sleep(3000);

        //choose the one back from google place api
        onView(ViewMatchers.withId(com.google.android.libraries.places.R.id.places_autocomplete_list))
                .perform(waitUntil(hasItemCount(greaterThan(0))), RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);

        //perform clear text
        onView(withId(R.id.add_location_clear)).perform(click());
        Thread.sleep(1000);

        //check if the text is cleared
        onView(withId(R.id.search_location_text))
                .check(matches(withText("")));
    }

}
