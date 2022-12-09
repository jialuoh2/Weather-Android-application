package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

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
public class MockLocationTests {

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final String testName = "testNameMockLocation";
    private final String testPassword = "testPassword";
    private User userMock;
    private final UserSelectedLocation testChampaign = new UserSelectedLocation("Champaign", "40.1164204", "-88.2433829");
    private final UserSelectedLocation testChicago = new UserSelectedLocation("Chicago", "41.8781136", "-87.6297982");
    private final String mockUserSelectedLocations = testChampaign.serialize() + "\t" + testChicago.serialize() + "\t";
    private UserDBHelper dbMock;

    /**
     * set up test temp db before each test, and insert a fake user with 2 locations in her list.
     */
    @Before
    public void createDb() {
        //set up a test user with 2 locations in her list, Champaign and Chicago
        dbMock = new UserDBHelper(context);
        userMock = new User();
        userMock.setUserName(testName);
        userMock.setPassword(testPassword);
        dbMock.insertUserData(userMock, mockUserSelectedLocations);
    }

    /**
     * close test temp db after each test.
     */
    @After
    public void finish() {

        ////remove the test user when the test is finished
        dbMock.removeUserData(userMock);
        dbMock.close();
    }

    /**
     * This method is used to do location mocking using gps and geo info checking
     */
    @Test
    public void mockLocationUsingGPS() throws InterruptedException {

        //in the app, start the activity from an existed intent, which provided the user name
        ActivityScenario.launch(new Intent(
                context, MainActivity.class)
                .putExtra("username", testName));


        //mock location Champaign
        Location mockLocationChampaign = new Location(LocationManager.GPS_PROVIDER);
        mockLocationChampaign.setLatitude(40.1164204);
        mockLocationChampaign.setLongitude(-88.2433829);
        mockLocationChampaign.setAccuracy(1f);
        mockLocationChampaign.setTime(System.currentTimeMillis());

        //in the app, open the map of Chicago
        Thread.sleep(1500);

        onView(withId(R.id.locationRecView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Chicago")),
                        MyViewAction.clickChildViewWithId(R.id.locationMap)));
        Thread.sleep(3000);


        //get the geo info of mocked location Champaign
        LocationManager mockLm = mock(LocationManager.class);
        when(mockLm.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(mockLocationChampaign);
        Location curLocation = mockLm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Double curLat = curLocation.getLatitude();
        Double curLng = curLocation.getLongitude();
        String geoMock = "Latitude: " + curLat + ", Longitude: " + curLng;

        //check if the latitude and longitude of mocked location(Champaign) are not the same with shown on map
        onView(withId(R.id.map_location_latlng)).check(matches(not(withText(geoMock))));

        //in the app, click back and open the map of Champaign
        pressBack();
        Thread.sleep(1500);
        onView(withId(R.id.locationRecView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Champaign")),
                        MyViewAction.clickChildViewWithId(R.id.locationMap)));
        Thread.sleep(3000);

        //check if the latitude and longitude of mocked location(Champaign) are the same with shown on the map
        onView(withId(R.id.map_location_latlng)).check(matches(withText(geoMock)));

    }

}