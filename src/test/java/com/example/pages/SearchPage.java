package com.example.pages;

import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;

public class SearchPage extends PageObject {

	public static final Target SEARCH_BOX = Target.the("Google Search Box")
	        .located(By.xpath("//input[@aria-label='Search with DuckDuckGo']"));

	    public static final Target AMAZON_LINK = Target.the("Amazon link")
	        .locatedBy("//p[contains(text(),'Amazonn')]");
}
