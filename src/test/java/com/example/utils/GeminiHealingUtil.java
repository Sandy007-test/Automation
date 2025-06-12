package com.example.utils; // Adjust package as needed

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictRequest;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.protobuf.Value;
import com.google.protobuf.Struct;
import com.google.protobuf.ListValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By; // Still needed for constructing the returned By object
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeminiHealingUtil {

    // IMPORTANT: Replace with your actual Gemini API Key.
    // For a quick demo, hardcoding is okay, but for production, use environment variables!
    private static final String GEMINI_API_KEY = "AIzaSyCE8DBI0QxCYCnPw6x94VQUfZ3J3AOjMSU"; // <-- PASTE YOUR KEY HERE

 // Corrected: Model to use for text generation. 'gemini-pro' is not directly accessible
    // via this V1beta endpoint for generateContent. Using 'gemini-2.0-flash' instead.
private static final String MODEL_NAME = "gemini-1.5-flash"; 
    
    private static final int NUMBER_OF_XPATHS_TO_GENERATE = 10; // Number of XPaths to request from LLM

    // Method now accepts a String for brokenLocatorString
    public static By healLocatorWithGemini(WebDriver driver, String brokenLocatorString) {
        String pageSource = driver.getPageSource();
        System.out.println("DEBUG: Attempting to heal locator with Gemini: " + brokenLocatorString);

        // --- Construct your prompt for Gemini ---
        // Enhanced prompt for robustness
        String prompt = "Given the following HTML page structure:\n\n" + pageSource +
                        "\n\nAn interactive element, intended to represent 'Amazon', could not be located using the selector '" + brokenLocatorString + "'. " +
                        "Note that 'Amazonn' in the original selector is a likely typo for 'Amazon'.\n" +
                        "Please provide " + NUMBER_OF_XPATHS_TO_GENERATE + " **new, valid, and highly robust XPaths** to locate the *correct* 'Amazon' link or element on this page. " +
                        "Prioritize the following qualities for XPaths:\n" +
                        "1.  **Stability:** Focus on user-facing text (e.g., `text()`, `contains(text(), ...)`) or stable, non-dynamic attributes (e.g., `id`, `name`, `data-*` attributes, `href`).\n" +
                        "2.  **Readability:** Prefer simpler and shorter XPaths when possible.\n" +
                        "3.  **Specificity (without being brittle):** Ensure the XPath uniquely identifies the intended 'Amazon' element.\n" +
                        "4.  **Avoidance of Dynamic Content:** Absolutely *do not* use automatically generated, random, or excessively long class names/IDs that change frequently.\n" +
                        "5.  **Directness:** Aim for XPaths that are as direct as possible to the target element.\n" +
                        "Provide only the XPath strings, nothing else. Separate each XPath strictly with `|||` (three pipe characters) and ensure there are no leading/trailing spaces or newlines for each XPath. " +
                        "Example format: `//a[text()='Amazon']|||//div[@id='nav-link']/a[contains(@href,'amazon.com')]`";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + MODEL_NAME + ":generateContent?key=" + GEMINI_API_KEY))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\"contents\":[{\"parts\":[{\"text\":\"" + escapeJson(prompt) + "\"}]}]}"
                    ))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String geminiRawResponse = response.body();
                System.out.println("DEBUG: Gemini Raw Response (Full): " + geminiRawResponse);
                System.out.println("DEBUG: Gemini Raw Response Length: " + geminiRawResponse.length());
                if (geminiRawResponse.length() > 50) {
                    System.out.println("DEBUG: Gemini Raw Response First 50: '" + geminiRawResponse.substring(0, Math.min(geminiRawResponse.length(), 50)) + "'");
                    System.out.println("DEBUG: Gemini Raw Response Last 50: '" + geminiRawResponse.substring(Math.max(0, geminiRawResponse.length() - 50)) + "'");
                }
                
                String extractedGeminiText = extractTextFromGeminiResponse(geminiRawResponse);
                System.out.println("DEBUG: Extracted text from Gemini JSON for parsing: '" + extractedGeminiText + "'");

                List<String> suggestedLocators = parseGeminiResponseForXPaths(extractedGeminiText);

                System.out.println("DEBUG: Number of suggested locators after parsing: " + (suggestedLocators != null ? suggestedLocators.size() : "null"));
                System.out.println("DEBUG: Suggested locators list: " + suggestedLocators);

                if (suggestedLocators != null && !suggestedLocators.isEmpty()) {
                    for (int i = 0; i < suggestedLocators.size(); i++) {
                        String currentSuggestedLocator = suggestedLocators.get(i);
                        System.out.println("DEBUG: Attempting to validate/use suggested locator #" + (i + 1) + ": '" + currentSuggestedLocator + "'");
                        
                        if (currentSuggestedLocator == null || currentSuggestedLocator.isEmpty() || !currentSuggestedLocator.startsWith("/")) {
                            System.err.println("WARN: Suggested locator #" + (i + 1) + " is not a valid XPath format or empty: '" + currentSuggestedLocator + "'");
                            continue;
                        }

                        try {
                            driver.findElement(By.xpath(currentSuggestedLocator));
                            System.out.println("DEBUG: Successfully validated and found element with locator: " + currentSuggestedLocator);
                            return By.xpath(currentSuggestedLocator);
                        } catch (Exception validationEx) {
                            System.err.println("WARN: Suggested locator #" + (i + 1) + " could not find element using Selenium: '" + currentSuggestedLocator + "' - " + validationEx.getMessage());
                        }
                    }
                    System.err.println("WARN: All " + suggestedLocators.size() + " Gemini suggested locators failed to find the element.");
                    return null;
                } else {
                    System.err.println("WARN: Gemini did not return any valid locator strings. (suggestedLocators list was null or empty)");
                }
            } else {
                System.err.println("ERROR: Gemini API call failed with status code: " + response.statusCode() + " and body: " + response.body());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("ERROR: Problem communicating with Gemini API: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error during Gemini healing: " + e.getMessage());
        }
        return null;
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    private static String extractTextFromGeminiResponse(String jsonResponse) {
        System.out.println("DEBUG: extractTextFromGeminiResponse: Starting JSON parsing with org.json.");
        System.out.println("DEBUG: extractTextFromGeminiResponse: First 100 chars of JSON: '" + jsonResponse.substring(0, Math.min(jsonResponse.length(), 100)) + "...'"); 

        try {
            JSONObject root = new JSONObject(jsonResponse);
            System.out.println("DEBUG: Root JSONObject created successfully.");

            if (!root.has("candidates")) {
                System.err.println("WARN: JSON response missing 'candidates' key.");
                return null;
            }
            JSONArray candidates = root.getJSONArray("candidates");
            System.out.println("DEBUG: 'candidates' JSONArray found. Size: " + candidates.length());

            if (candidates.length() == 0) {
                System.err.println("WARN: 'candidates' JSONArray is empty.");
                return null;
            }

            JSONObject firstCandidate = candidates.getJSONObject(0);
            System.out.println("DEBUG: First candidate JSONObject found.");

            if (!firstCandidate.has("content")) {
                System.err.println("WARN: First candidate missing 'content' key.");
                return null;
            }
            JSONObject content = firstCandidate.getJSONObject("content");
            System.out.println("DEBUG: 'content' JSONObject found.");

            if (!content.has("parts")) {
                System.err.println("WARN: 'content' missing 'parts' key.");
                return null;
            }
            JSONArray parts = content.getJSONArray("parts");
            System.out.println("DEBUG: 'parts' JSONArray found. Size: " + parts.length());

            if (parts.length() == 0) {
                System.err.println("WARN: 'parts' JSONArray is empty.");
                return null;
            }

            JSONObject firstPart = parts.getJSONObject(0);
            System.out.println("DEBUG: First part JSONObject found.");

            if (!firstPart.has("text")) {
                System.err.println("WARN: First part missing 'text' key.");
                return null;
            }
            String extractedText = firstPart.getString("text");
            System.out.println("DEBUG: Raw text extracted by extractTextFromGeminiResponse (via org.json): '" + extractedText + "'");
            return extractedText;

        } catch (JSONException e) {
            System.err.println("ERROR: JSONException during parsing in extractTextFromGeminiResponse: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for detailed debugging
            return null;
        } catch (Exception e) { // Catch any other unexpected runtime exceptions
            System.err.println("ERROR: Unexpected exception during JSON parsing in extractTextFromGeminiResponse: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for detailed debugging
            return null;
        } finally {
            System.out.println("DEBUG: extractTextFromGeminiResponse: Exiting parsing block.");
        }
    }


    // parseGeminiResponseForXPaths now accepts the extracted text directly
    private static List<String> parseGeminiResponseForXPaths(String extractedText) {
        List<String> xpaths = new ArrayList<>();
        
        if (extractedText == null || extractedText.isEmpty()) {
            System.out.println("DEBUG: No text provided for XPath parsing. Returning empty list.");
            return xpaths;
        }

        // Unescape any special characters that might still be present in the text
        // (org.json handles standard JSON escapes, but LLM might add extra ones)
        String unescapedAndCleanedText = extractedText.replace("\\\"", "\"") // Replace escaped double quotes
                                                  .replace("\\t", "\t")     // Replace escaped tabs
                                                  .replace("\\\\", "\\");   // Replace escaped backslashes
        
        // Remove newlines and carriage returns from the text content itself (LLM might include them in text field)
        unescapedAndCleanedText = unescapedAndCleanedText.replace("\n", "") 
                                                          .replace("\r", "");

        // NEW: Aggressively remove any trailing JSON-like characters or commas that might be left
        // This targets remnants from the LLM's raw output if it doesn't strictly adhere to the format.
        unescapedAndCleanedText = unescapedAndCleanedText.replaceAll("[,\\s\\]\\}]*$", "").trim();


        System.out.println("DEBUG: Unescaped and cleaned text before splitting: '" + unescapedAndCleanedText + "'");

        // Split the unescaped text by the custom delimiter "|||"
        String[] splitXpaths = unescapedAndCleanedText.split("\\|\\|\\|"); // \\|\\|\\| escapes the pipe characters
        System.out.println("DEBUG: Split XPaths array: " + Arrays.toString(splitXpaths));


        for (String xpath : splitXpaths) {
            // Trim whitespace from each individual XPath
            String cleanedLocator = xpath.trim();

            if (!cleanedLocator.isEmpty()) { // Add only non-empty xpaths
                xpaths.add(cleanedLocator);
            }
        }
        return xpaths;
    }
}