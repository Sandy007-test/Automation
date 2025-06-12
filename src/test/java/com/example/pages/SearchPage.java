package com.example.pages;

import java.util.Map;

import org.openqa.selenium.support.FindBy;

import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;

public class SearchPage extends PageObject {

	public static final Target SEARCH_BOX = Target.the("Google Search Box")
	        .located(By.xpath("//input[@aria-label='Search with DuckDuckGo']"));
	
	public static final Map<String, String> HEALING_ATTRIBUTES = Map.of(
	        "class", "result__title",
	        "tag", "h3"
	    );

	@FindBy(xpath = "//h3[contains(text(),'Amazon')]")
	    public static final Target AMAZON_LINK = Target.the("Amazon link")
	        .locatedBy("//h3[contains(text(),'Amazonn')]");
}
