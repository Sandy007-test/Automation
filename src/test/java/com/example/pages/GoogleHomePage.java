package com.example.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class GoogleHomePage extends PageObject {

    public static final Target SEARCH_BOX = Target
            .the("Google search box")
            .located(By.name("q"));

    public static final Target AMAZON_LINK = Target
            .the("Amazon search result link")
            .located(By.partialLinkText("Amazon"));
}
