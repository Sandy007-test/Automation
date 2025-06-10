package com.example.google_homepage_automation;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Hit;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.matchers.statematchers.IsVisibleMatcher;
import net.serenitybdd.screenplay.waits.Wait;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.epam.healenium.SelfHealingDriver;
import com.epam.healenium.SelfHealingDriverWait;
import com.example.pages.SearchPage;
import com.example.steps.GoogleHomePageSteps;
import com.example.utils.waitTimeSeconds;

@ExtendWith(SerenityJUnit5Extension.class)
public class AppTest {

//	WebDriver delegate = new ChromeDriver();
//	//create Self-healing driver
//	SelfHealingDriver driver = SelfHealingDriver.create(delegate);
	

	WebDriver driver;
	Actor user = Actor.named("AdminUser");

	@BeforeEach
	public void setUp() {
		System.out.println("inside setup");
		
		EnvironmentVariables env = SystemEnvironmentVariables.createEnvironmentVariables();
        String remoteUrl = env.getProperty("webdriver.remote.url");

        ChromeOptions options = new ChromeOptions();

        try {
        	URI uri = URI.create(remoteUrl);
            WebDriver delegate = new RemoteWebDriver(uri.toURL(), options);
            driver = SelfHealingDriver.create(delegate); // Wrap with Healenium
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid remote WebDriver URL: " + remoteUrl, e);
        }

        user.can(BrowseTheWeb.with(driver));
    }
	

	@Test
	public void search_for_amazon() {
	    user.attemptsTo(
	        Open.url("https://duckduckgo.com"),
	        Enter.theValue("amazon").into(SearchPage.SEARCH_BOX),
	        Hit.the(Keys.ENTER).into(SearchPage.SEARCH_BOX),
	        waitTimeSeconds.on("Waiting", 5));
	    //    WaitUntil.the(SearchPage.AMAZON_LINK.resolveFor(user).isVisible(), is(true)),
	   
	    user.attemptsTo( 
	    		Click.on(SearchPage.AMAZON_LINK)
	    );
	   
	}
}
