package com.example.steps; // Adjust package as needed

import com.example.utils.GeminiHealingUtil;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By; // Import Selenium's By class
import org.openqa.selenium.TimeoutException; // Import TimeoutException as it's often linked to element not found

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ClickAndHeal implements Task {

    private final Target target;
    private final int maxHealingAttempts; // To prevent infinite loops

    // Constructor for the task (reverted to single Target parameter)
    protected ClickAndHeal(Target target) {
        this.target = target;
        this.maxHealingAttempts = 2; // Default to 1 attempt with Gemini
    }

    // Constructor to specify max attempts (optional)
    protected ClickAndHeal(Target target, int maxHealingAttempts) {
        this.target = target;
        this.maxHealingAttempts = maxHealingAttempts;
    }

    // Static factory method for readability in tests: user.attemptsTo(ClickAndHeal.on(target));
    public static ClickAndHeal on(Target target) {
        return instrumented(ClickAndHeal.class, target);
    }

    // Static factory method with custom attempts: user.attemptsTo(ClickAndHeal.on(target).retrying(2));
    public ClickAndHeal retrying(int maxHealingAttempts) {
        return instrumented(ClickAndHeal.class, this.target, maxHealingAttempts);
    }


    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();  // Get the WebDriver instance from the Actor

        for (int attempt = 0; attempt <= maxHealingAttempts; attempt++) { // Loop for initial attempt + healing attempts
            try {
                if (attempt == 0) { // Initial attempt
                    System.out.println("Attempt " + (attempt + 1) + ": Clicking element with original locator: " + target.getName());
                    actor.attemptsTo(Click.on(target));
                } else { // Healing attempt
                    System.out.println("Attempt " + (attempt + 1) + ": Original locator failed. Initiating Gemini AI healing...");
                    // Pass target.toString() to GeminiHealingUtil
                    By healedByLocator = GeminiHealingUtil.healLocatorWithGemini(driver, target.toString());

                    if (healedByLocator != null) {
                        System.out.println("Gemini suggested healed locator: " + healedByLocator.toString());
                        // Create a temporary Target from the healed By locator
                        Target healedTarget = Target.the(target.getName() + " (healed)").located(healedByLocator);
                        actor.attemptsTo(Click.on(healedTarget));
                        System.out.println("Successfully clicked element with Gemini-healed locator!");
                        return; // Element clicked, task completed
                    } else {
                        System.err.println("Gemini Healing failed for: " + target.getName() + ". No valid healed locator returned.");
                        // If Gemini returned null, no point in further attempts with Gemini for this specific failure
                        throw new NoSuchElementException("Failed to find element after " + maxHealingAttempts + " Gemini healing attempts for: " + target.getName());
                    }
                }
                return; // Element clicked in initial attempt, task completed
            } catch (Exception e) { // Catch a general Exception to check for specific causes
                boolean isElementNotFoundError = false;
                Throwable cause = e;
                while (cause != null) {
                    if (cause instanceof NoSuchElementException || cause instanceof TimeoutException) {
                        isElementNotFoundError = true;
                        break;
                    }
                    cause = cause.getCause();
                }

                if (isElementNotFoundError) {
                    if (attempt < maxHealingAttempts) {
                        System.err.println("Element not found (attempt " + (attempt + 1) + "): " + target.getName() + " - " + e.getMessage());
                        // Will proceed to next iteration (healing attempt)
                    } else {
                        // All attempts exhausted, re-throw the original or a new exception
                        System.err.println("All " + (attempt + 1) + " attempts failed for: " + target.getName() + ". Re-throwing original exception.");
                        throw new RuntimeException("Failed to find element after " + (attempt + 1) + " attempts including Gemini healing for: " + target.getName(), e);
                    }
                } else {
                    // This was an unexpected error, not related to element not found
                    System.err.println("An unexpected error occurred during click/healing: " + e.getMessage());
                    throw new RuntimeException("Unexpected error during click or healing for: " + target.getName(), e);
                }
            }
        }
    }
}
