package com.example.utils;

import com.epam.healenium.SelfHealingDriver;
import net.thucydides.core.webdriver.DriverSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SelfHealingDriverSource implements DriverSource {

    @Override
    public WebDriver newDriver() {
    	System.out.println("hello");
    	
    	String projectPath = System.getProperty("user.dir");
        String driverPath = projectPath + "\\chrome.exe"; // Windows
        // or use "/" for Linux/Mac: projectPath + "/drivers/chromedriver"

        System.setProperty("webdriver.chrome.driver", driverPath);
        ChromeOptions options = new ChromeOptions();
        // Add your own Chrome options if needed
        WebDriver original = new ChromeDriver(options);
        return SelfHealingDriver.create(original);
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }
}


