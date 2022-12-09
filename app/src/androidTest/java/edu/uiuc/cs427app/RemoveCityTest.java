package edu.uiuc.cs427app;

import static androidx.core.util.Preconditions.checkNotNull;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uiuc.cs427app.database.user.UserDBHelper;
import edu.uiuc.cs427app.model.User;
import edu.uiuc.cs427app.model.UserSelectedLocation;
import edu.uiuc.cs427app.utils.MyViewAction;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RemoveCityTest {

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final String testName = "testRemoveCity";
    private final UserSelectedLocation testChampaign = new UserSelectedLocation("Champaign", "40.1164204", "-88.2433829");
    private final UserSelectedLocation testChicago = new UserSelectedLocation("Chicago", "41.8781136", "-87.6297982");
    private final String mockUserSelectedLocations = testChampaign.serialize() + "\t" + testChicago.serialize() + "\t";
    private UserDBHelper dbMock;

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    /**
     * set up test temp db before each test, and insert a fake user with 2 locations in her list.
     */
    @Before
    public void createDb() {
        //set up a test user with 2 locations in her list, Champaign and Chicago
        dbMock = new UserDBHelper(context);
        User userDao = new User();
        userDao.setUserName(testName);
        String testPassword = "testPassword";
        userDao.setPassword(testPassword);
        dbMock.insertUserData(userDao, mockUserSelectedLocations);
    }

    /**
     * close test temp db after each test.
     */
    @After
    public void finish() {
        dbMock.close();
    }

    /**
     * This method is used to do location mocking and geo info checking
     */
    @Test
    public void checkRemoveCity() throws InterruptedException {

        //in the app, start the activity from an existed intent, which provided the user name
        ActivityScenario.launch(new Intent(
                context, MainActivity.class)
                .putExtra("username", testName));
        //Validating the first element in the city list is Champaign
        onView(withId(R.id.locationRecView))
                .check(matches(atPosition(0, hasDescendant(withText("Champaign")))));

        //in the app, remove the city of Champaign
        Thread.sleep(1500);
        onView(withId(R.id.locationRecView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Champaign")),
                        MyViewAction.clickChildViewWithId(R.id.locationImageRemove)));
        Thread.sleep(3000);
        //validating if Chicago is the only city available on the list
        onView(withId(R.id.locationRecView))
                .check(matches(atPosition(0, hasDescendant(withText("Chicago")))));
        Thread.sleep(3000);

    }
}
