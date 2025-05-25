package SeleniumTest;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class SeleniumTest {
    public static void main(String[] args) {
        // Set the path to the Edge WebDriver executable
        System.setProperty("webdriver.chrome.driver", "C:\\Selenium Webdriver\\chromedriver-win64\\msedgedriver.exe");

        // Launch Microsoft Edge with options
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        WebDriver driver = new EdgeDriver(options);

        // WebDriver wait for dynamic elements
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Navigate to HP Canada laptops page
            driver.get("https://www.hp.com/ca-en/shop/list.aspx?sel=ntb");

            // Handle cookie consent and any popups
            try {
                // Accept cookies
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#onetrust-accept-btn-handler"))).click();

                // Close any modal popup if it appears
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".dy-modal-wrapper")));
                WebElement closeButton = driver.findElement(By.cssSelector(".dy-modal-wrapper .dy-modal-contents .dy-lb-close"));
                closeButton.click();

                // Wait for modal to disappear
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".dy-modal-wrapper")));
            } catch (NoSuchElementException | TimeoutException e) {
                System.out.println("Modal not present or already closed.");
            }

            // Prepare CSV writer
            FileWriter csvWriter;
            try {
                csvWriter = new FileWriter("C:\\Users\\Faseeh\\eclipse-workspace\\SeleniumTest\\hp_laptops.csv");
                csvWriter.append("Brand Name,Product Name,Price,Operating System,Processor,Memory,Storage,Display,Graphics\n");
            } catch (IOException e) {
                System.out.println("Error: Unable to create or write to the file. Please close it in Excel or other programs.");
                driver.quit();
                return;
            }

            // Brand name to be used in all records
            String brandName = "HP";
            boolean hasNextPage = true;

            // Loop through pages while "Next" button is available
            while (hasNextPage) {
                try {
                    // Wait until product tiles are visible
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".HorizontalProductTile_container__3F4IN")));

                    // Collect all product listings
                    List<WebElement> products = driver.findElements(By.cssSelector(".HorizontalProductTile_container__3F4IN"));

                    for (WebElement product : products) {
                        try {
                            // Get product name
                            String name = product.findElement(By.cssSelector(".HorizontalProductTile_infoContainer__1BjHM .HorizontalProductTile_main__1nR2d .Typography-module_titleS__sF7UO"))
                                .getText().replace(",", " ");

                            // Get product price
                            String price = product.findElement(By.cssSelector("[data-test-hook='@hpstellar/core/price-block__sale-price']"))
                                .getText().replace(",", "");

                            // Get the first 6 specs (OS, Processor, Memory, Storage, Display, Graphics)
                            List<WebElement> specs = product.findElements(By.cssSelector(".HorizontalProductTile_specFirstGlance__3vnhf span")).subList(0, 6);

                            // Prepare specs string
                            StringBuilder tempSpecs = new StringBuilder();
                            for (WebElement spec : specs) {
                                tempSpecs.append(spec.getText().replace(",", " ")).append(",");
                            }

                            // Write product info to CSV
                            csvWriter.append(String.format("%s,%s,%s,%s\n", brandName, name, price, tempSpecs.toString()));
                        } catch (IndexOutOfBoundsException e) {
                            // Skip product if specifications are missing
                            System.out.println("Product Name: '" + product.findElement(By.cssSelector(".HorizontalProductTile_infoContainer__1BjHM .HorizontalProductTile_main__1nR2d .Typography-module_titleS__sF7UO"))
                            .getText() + "' details are missing. Skipping this product.");
                            continue;
                        }
                    }

                    // Check and click the 'Next' button if available and enabled
                    WebElement nextButton = driver.findElement(By.cssSelector(".PaginationContainer_container__ngfOS .Pagination-module_container__JHnDH .Pagination-module_list__1fx5U .Pagination-module_next__T1EtZ"));
                    if (nextButton.isDisplayed() && !"true".equals(nextButton.getDomAttribute("disabled"))) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", nextButton);
                        nextButton.click();
                        wait.until(ExpectedConditions.stalenessOf(products.get(0))); // Wait for page refresh
                    } else {
                        hasNextPage = false; // No more pages
                    }
                } catch (Exception e) {
                    System.out.println("Error during product extraction or pagination: " + e.getMessage());
                    break;
                }
            }

            // Finalize CSV
            csvWriter.flush();
            csvWriter.close();
            System.out.println("Data extraction completed. Check 'hp_laptops.csv' for results.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Quit the browser session
            driver.quit();
        }
    }
}
