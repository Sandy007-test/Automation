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

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.JavascriptExecutor;
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
import com.example.steps.ClickAndHeal;
import com.example.steps.GoogleHomePageSteps;
import com.example.utils.HealLocator;
import com.example.utils.waitTimeSeconds;

@ExtendWith(SerenityJUnit5Extension.class)
public class AppTest {

//	WebDriver delegate = new ChromeDriver();
//	//create Self-healing driver
//	SelfHealingDriver driver = SelfHealingDriver.create(delegate);
	
	WebDriver driver;
	Actor user = Actor.named("AdminUser");

	boolean enablehealenium = true;
	@BeforeEach
	public void setUp() {
		System.out.println("inside setup");
		
//		EnvironmentVariables env = SystemEnvironmentVariables.createEnvironmentVariables();
//        String remoteUrl = env.getProperty("webdriver.remote.url");
//
        ChromeOptions options = new ChromeOptions();
//
//        try {
//        	URI uri = URI.create(remoteUrl);
//        	options.setCapability("healenium:healingEnabled", true);
//            options.setCapability("healenium:recoveryTries", 3);
//            System.setProperty("HLM_SERVER_URL", "http://localhost:7878");
//            System.setProperty("HLM_IMITATOR_URL", "http://localhost:8000");
//            System.setProperty("HLM_ENABLED", "true");
//            WebDriver delegate = new RemoteWebDriver(uri.toURL(), options);
//            if(enablehealenium == true) {
//            	System.out.println("Healenium enabled");
//            driver = SelfHealingDriver.create(delegate); // Wrap with Healenium
//            }
//            else {
//            	System.out.println("Healenium disabled");
//            	driver = delegate;
//            }
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("Invalid remote WebDriver URL: " + remoteUrl, e);
//        }
        
		options.addArguments("--start-maximized");
	    WebDriver delegate = new ChromeDriver(options);
	    
	    // 2. Configure Healenium client directly
	    System.setProperty("HLM_SERVER_URL", "http://localhost:7878");
	    System.setProperty("HLM_IMITATOR_URL", "http://localhost:8000");
	    System.setProperty("HLM_ENABLED", "true");
	    
	    // 3. Create self-healing driver
	    driver = SelfHealingDriver.create(delegate);

        user.can(BrowseTheWeb.with(driver));
    }
	

	@Test
	public void search_for_amazon() {
	    user.attemptsTo(
	        Open.url("https://duckduckgo.com"),
	        Enter.theValue("amazon").into(SearchPage.SEARCH_BOX),
	        Hit.the(Keys.ENTER).into(SearchPage.SEARCH_BOX));
	    //    WaitUntil.the(SearchPage.AMAZON_LINK.resolveFor(user).isVisible(), is(true)),    String pageSource = Serenity.getDriver().getPageSource();
	    JavascriptExecutor js = (JavascriptExecutor) driver;
	    js.executeScript("document.healeniumCapture=true;");
	   
	   
	   
	    user.attemptsTo( 
	    		 waitTimeSeconds.on("Waiting", 3),
	    		Click.on(SearchPage.AMAZON_LINK )
	    );
	   
	}
	
	@Test
	public void search_for_amazon_Gemini() {
	    user.attemptsTo(
	        Open.url("https://duckduckgo.com"),
	        Enter.theValue("amazon").into(SearchPage.SEARCH_BOX),
	        Hit.the(Keys.ENTER).into(SearchPage.SEARCH_BOX));
	   driver.get("https://duckduckgo.com/?t=h_&q=amazon&ia=web");
	    //    WaitUntil.the(SearchPage.AMAZON_LINK.resolveFor(user).isVisible(), is(true)),
	   
	    user.attemptsTo( 
	    		 waitTimeSeconds.on("Waiting", 10),
	    		ClickAndHeal.on(SearchPage.AMAZON_LINK)
	    );
	   
	}
	
	@BeforeAll // or @BeforeEach
	public static void printHealeniumProperties() {
	    // Option 1: Directly from System Properties (Serenity might set it)
	    String similarity = System.getProperty("HLM_SIMILARITY_COEFFICIENT");
	    if (similarity != null) {
	        System.out.println("HLM_SIMILARITY_COEFFICIENT (from System.getProperty): " + similarity);
	    } else {
	        System.out.println("HLM_SIMILARITY_COEFFICIENT not found as System Property directly.");
	    }

	    // Option 2: Read the properties file directly (for debugging only)
	    try {
	        Properties prop = new Properties();
	        prop.load(new FileInputStream("serenity.properties"));
	        String fileSimilarity = prop.getProperty("HLM_SIMILARITY_COEFFICIENT");
	        if (fileSimilarity != null) {
	            System.out.println("HLM_SIMILARITY_COEFFICIENT (from serenity.properties file): " + fileSimilarity);
	        } else {
	            System.out.println("HLM_SIMILARITY_COEFFICIENT not found in serenity.properties file.");
	        }
	    } catch (Exception e) {
	        System.err.println("Error reading serenity.properties: " + e.getMessage());
	    }
	}
}
