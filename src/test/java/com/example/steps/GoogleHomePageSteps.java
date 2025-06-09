package com.example.steps;

import com.example.pages.GoogleHomePage;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.steps.UIInteractionSteps;

import static com.example.pages.GoogleHomePage.*;

public class GoogleHomePageSteps extends UIInteractionSteps {

    @Step
    public void openGoogle() {
        openUrl("https://www.google.com");
    }

    @Step
    public void searchForAmazon() {
        SEARCH_BOX.resolveFor(this).typeAndEnter("Amazon");
    }

    @Step
    public void clickAmazonLink() {
        AMAZON_LINK.resolveFor(this).click();
    }
}
