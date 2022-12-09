package edu.uiuc.cs427app;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uiuc.cs427app.database.user.UserDBHelper;
import edu.uiuc.cs427app.model.User;
import edu.uiuc.cs427app.utils.ToastMatcher;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    public static final String TEST_USERNAME = "thetestusername";
    public static final String TEST_PASSWORD = "thetestpassword0";
    public static final String WELCOME_INFO = "CS427 Project App - Team 23-";
    private final Context targetContext = InstrumentationRegistry.getInstrumentation()
            .getTargetContext();
    private final UserDBHelper db = new UserDBHelper(targetContext);
    @Rule
    public ActivityScenarioRule<UserLogin> activityScenarioRule
            = new ActivityScenarioRule<>(UserLogin.class);

    /**
     * To test happy flow of login success
     */
    @Test
    public void testLoginSuccess() throws InterruptedException {
        db.reset();
        User insertedUser = new User();
        insertedUser.setUserName(TEST_USERNAME);
        insertedUser.setPassword(TEST_PASSWORD);
        db.insertUserData(insertedUser, "");
        String s = targetContext.getResources().getString(R.string.city_application);
        onView(withId(R.id.user_login_welcome_text)).check(matches(withText(s)));
        onView(withId(R.id.username_login_text)).perform(typeText(TEST_USERNAME),
                closeSoftKeyboard());
        onView(withId(R.id.password_login_text)).perform(typeText(TEST_PASSWORD),
                closeSoftKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.login_confirm)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.textView3)).check(matches(withText(WELCOME_INFO + TEST_USERNAME)));
    }

    /**
     * To test failure in login
     */
    @Test
    public void testLoginFail() throws InterruptedException {
        db.reset();
        String s = targetContext.getResources().getString(R.string.city_application);
        onView(withId(R.id.user_login_welcome_text)).check(matches(withText(s)));
        onView(withId(R.id.username_login_text)).perform(typeText(TEST_USERNAME),
                closeSoftKeyboard());
        onView(withId(R.id.password_login_text)).perform(typeText(TEST_PASSWORD),
                closeSoftKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.login_confirm)).perform(click());
        Thread.sleep(1000);
        onView(withText("The username or password is incorrect!"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * To test forget password
     *
     * @throws InterruptedException
     */
    @Test
    public void testForgetPassword() throws InterruptedException {
        String s = targetContext.getResources().getString(R.string.city_application);
        String pwdRst = targetContext.getResources().getString(R.string.reset_password);
        Thread.sleep(1000);
        onView(withId(R.id.user_login_welcome_text)).check(matches(withText(s)));
        onView(withId(R.id.login_forget_password)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.password_reset)).check(matches(withText(pwdRst)));
    }

    /**
     * To test new user registration from login page
     *
     * @throws InterruptedException
     */
    @Test
    public void testShowSignUp() throws InterruptedException {
        String s = targetContext.getResources().getString(R.string.city_application);
        String wel = targetContext.getResources().getString(R.string.hello_and_welcome);
        Thread.sleep(1000);
        onView(withId(R.id.user_login_welcome_text)).check(matches(withText(s)));
        onView(withId(R.id.login_register)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.user_welcome_text)).check(matches(withText(wel)));
    }
}
