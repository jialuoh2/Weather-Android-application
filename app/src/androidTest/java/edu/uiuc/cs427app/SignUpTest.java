package edu.uiuc.cs427app;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import edu.uiuc.cs427app.database.user.UserDBHelper;
import edu.uiuc.cs427app.model.User;

@RunWith(AndroidJUnit4.class)
public class SignUpTest{

    public static final String SIGNUP_WELCOME = "Create Account";
    public static final String TEST_USERNAME = "thetestusername";
    public static final String TEST_PASSWORD_0 ="thetestpassword0";
    public static final String TEST_PASSWORD_1 ="thetestpassword1";
    public static final String TEST_PASSWORD_2 ="thete";
    private final UserDBHelper db = new UserDBHelper(InstrumentationRegistry.getInstrumentation()
            .getTargetContext());
    @Rule public ActivityScenarioRule<UserRegister> activityScenarioRule
            = new ActivityScenarioRule<>(UserRegister.class);

    /**
     * To test happy flow of user sign up
     */
    @Test
    public void testUserSignUp() throws InterruptedException {
        db.reset();
        onView(withId(R.id.user_welcome_text)).check(matches(withText(SIGNUP_WELCOME)));
        onView(withId(R.id.username_text)).perform(typeText(TEST_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.password_text)).perform(typeText(TEST_PASSWORD_0), closeSoftKeyboard());
        onView(withId(R.id.password_text_2)).perform(typeText(TEST_PASSWORD_0),
                closeSoftKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.select_ui_theme)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.signup_confirm)).perform(click());
        onView(withId(R.id.username_login_text)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        assert db.verifyUserData(TEST_USERNAME, TEST_PASSWORD_0);
    }

    /**
     * To test flow when user already exists in database
     * @throws InterruptedException
     */
    @Test
    public void testUserSignUpDuplicated() throws InterruptedException {
        db.reset();
        User insertedUser = new User();
        insertedUser.setUserName(TEST_USERNAME);
        insertedUser.setPassword(TEST_PASSWORD_0);
        db.insertUserData(insertedUser, "");
        onView(withId(R.id.user_welcome_text)).check(matches(withText(SIGNUP_WELCOME)));
        onView(withId(R.id.username_text)).perform(typeText(TEST_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.password_text)).perform(typeText(TEST_PASSWORD_0), closeSoftKeyboard());
        onView(withId(R.id.password_text_2)).perform(typeText(TEST_PASSWORD_0),
                closeSoftKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.select_ui_theme)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.username_text)).check(matches(hasErrorText("This Username has been used!")));
    }

    /**
     * To test error message with empty username
     * @throws InterruptedException
     */
    @Test
    public void testUserSignUpEmptyUsername() throws InterruptedException {
        onView(withId(R.id.user_welcome_text)).check(matches(withText(SIGNUP_WELCOME)));
        onView(withId(R.id.select_ui_theme)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.username_text)).check(matches(hasErrorText("DO NOT Enter An Empty Username!")));
    }

    /**
     * To test flow with empty password value
     * @throws InterruptedException
     */
    @Test
    public void testUserSignUpEmptyPassword() throws InterruptedException {
        onView(withId(R.id.user_welcome_text)).check(matches(withText(SIGNUP_WELCOME)));
        onView(withId(R.id.username_text)).perform(typeText(TEST_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.select_ui_theme)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.password_text)).check(matches(hasErrorText("DO NOT Enter An Empty Password!")));
    }

    /**
     * To test flow with empty confirm password value
     * @throws InterruptedException
     */
    @Test
    public void testUserSignUpEmptyConfirmPassword() throws InterruptedException {
        onView(withId(R.id.user_welcome_text)).check(matches(withText(SIGNUP_WELCOME)));
        onView(withId(R.id.username_text)).perform(typeText(TEST_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.password_text)).perform(typeText(TEST_PASSWORD_0), closeSoftKeyboard());
        onView(withId(R.id.select_ui_theme)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.password_text_2)).check(matches(hasErrorText("Please Confirm Your Password!")));
    }

    /**
     * To test for password being too short
     * @throws InterruptedException
     */
    @Test
    public void testUserSignUpPasswordTooShort() throws InterruptedException {
        onView(withId(R.id.user_welcome_text)).check(matches(withText(SIGNUP_WELCOME)));
        onView(withId(R.id.username_text)).perform(typeText(TEST_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.password_text)).perform(typeText(TEST_PASSWORD_2), closeSoftKeyboard());
        onView(withId(R.id.password_text_2)).perform(typeText(TEST_PASSWORD_2),
                closeSoftKeyboard());
        onView(withId(R.id.select_ui_theme)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.password_text)).check(matches(hasErrorText("The Minimum Length For Password Is 6!")));
    }

    /**
     * To test if the two password values entered do not match
     * @throws InterruptedException
     */
    @Test
    public void testUserSignUpPasswordNotMatch() throws InterruptedException {
        onView(withId(R.id.user_welcome_text)).check(matches(withText(SIGNUP_WELCOME)));
        onView(withId(R.id.username_text)).perform(typeText(TEST_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.password_text)).perform(typeText(TEST_PASSWORD_0), closeSoftKeyboard());
        onView(withId(R.id.password_text_2)).perform(typeText(TEST_PASSWORD_1),
                closeSoftKeyboard());
        onView(withId(R.id.select_ui_theme)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.password_text_2)).check(matches(hasErrorText("Please Enter The Same Password!")));
    }

    /**
     * To test dark mode
     * @throws InterruptedException
     */
    @Test
    public void testUserSignUpDarkMode() throws InterruptedException {
        db.reset();
        onView(withId(R.id.user_welcome_text)).check(matches(withText(SIGNUP_WELCOME)));
        onView(withId(R.id.username_text)).perform(typeText(TEST_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.password_text)).perform(typeText(TEST_PASSWORD_0), closeSoftKeyboard());
        onView(withId(R.id.password_text_2)).perform(typeText(TEST_PASSWORD_0),
                closeSoftKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.select_ui_theme)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.themeDark)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.signup_confirm)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.username_login_text)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        assert db.verifyUserData(TEST_USERNAME, TEST_PASSWORD_0);
    }

    /**
     * To test light mode
     * @throws InterruptedException
     */
    @Test
    public void testUserSignUpLightMode() throws InterruptedException {
        db.reset();
        onView(withId(R.id.user_welcome_text)).check(matches(withText(SIGNUP_WELCOME)));
        onView(withId(R.id.username_text)).perform(typeText(TEST_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.password_text)).perform(typeText(TEST_PASSWORD_0), closeSoftKeyboard());
        onView(withId(R.id.password_text_2)).perform(typeText(TEST_PASSWORD_0),
                closeSoftKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.select_ui_theme)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.themeLight)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.signup_confirm)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.username_login_text)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        assert db.verifyUserData(TEST_USERNAME, TEST_PASSWORD_0);
    }

}
