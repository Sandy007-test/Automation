package com.example.google_homepage_automation;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Hit;
import net.serenitybdd.screenplay.actions.Open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import com.example.pages.SearchPage;
import com.example.steps.GoogleHomePageSteps;

@ExtendWith(SerenityJUnit5Extension.class)
public class AppTest {

	@Managed(driver = "chrome")
	WebDriver driver;

	Actor user = Actor.named("AdminUser");

	@BeforeEach
	public void setUp() {
		System.out.println("inside setup");
	    user.can(BrowseTheWeb.with(driver));
	}

	@Test
	public void search_for_amazon() {
	    user.attemptsTo(
	        Open.url("https://duckduckgo.com"),
	        Enter.theValue("amazon").into(SearchPage.SEARCH_BOX),
	        Hit.the(Keys.ENTER).into(SearchPage.SEARCH_BOX),
	        Click.on(SearchPage.AMAZON_LINK)
	    );
	}
}
