package com.example.tests;

import com.example.steps.GoogleHomePageSteps;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class GoogleHomePageTest {

    @net.serenitybdd.annotations.Steps
    GoogleHomePageSteps googleSteps;

    @Test
    public void open_google_homepage_successfully() {
        // This test opens Google's homepage
        googleSteps.openGoogle();
    }
}
