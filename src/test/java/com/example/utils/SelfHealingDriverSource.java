package com.example.utils;

import com.epam.healenium.SelfHealingDriver;
import net.thucydides.core.webdriver.DriverSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import net.thucydides.model.environment.SystemEnvironmentVariables; // Import this

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class SelfHealingDriverSource implements DriverSource {

    @Override
    public WebDriver newDriver() {
        String remoteUrl = SystemEnvironmentVariables.createEnvironmentVariables().getProperty("webdriver.remote.url");
        if (remoteUrl == null || remoteUrl.isEmpty()) {
            throw new IllegalStateException("webdriver.remote.url property must be set in serenity.properties to point to Healenium Proxy.");
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless=new");
        // Add any other desired capabilities here if needed
        // DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        // capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        try {
            URI uri = URI.create(remoteUrl);
            WebDriver delegate = new RemoteWebDriver(uri.toURL(), options); // Or capabilities
            return SelfHealingDriver.create(delegate); // Wrap with Healenium
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid remote WebDriver URL for Healenium Proxy: " + remoteUrl, e);
        }
    }

    @Override
    public boolean takesScreenshots() {
        return true; // Or false based on your preference
    }
}