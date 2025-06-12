package com.example.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.example.pages.SearchPage;
import com.google.gson.Gson;

public class HealingUtils {
    public static void prepareForHealing(WebDriver driver) {
        // 1. Capture DOM
        String pageSource = driver.getPageSource();
        
        // 2. Inject healing hooks
        ((JavascriptExecutor)driver).executeScript(
            "window._healenium = {" +
            "  capture: true," + 
            "  metadata: " + new Gson().toJson(SearchPage.HEALING_ATTRIBUTES) +
            "};"
        );
    }
}
