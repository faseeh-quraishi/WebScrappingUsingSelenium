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
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SeleniumTest {
	public static void main(String[] args) {
	    // Set the path to your ChromeDriver executable
	    System.setProperty("webdriver.chrome.driver", "C:\\Selenium Webdriver\\chromedriver-win64\\msedgedriver.exe");
	
		EdgeOptions options = new EdgeOptions();
		options.addArguments("--start-maximized");
		WebDriver driver = new EdgeDriver(options);
	    
		// Initialize WebDriverWait
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	    
	
	    try {
	        // Navigate to the HP Canada laptops page
	        driver.get("https://www.hp.com/ca-en/shop/list.aspx?sel=ntb");
	
	        try {
	        	new WebDriverWait(driver, Duration.ofSeconds(10))
	        		.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#onetrust-accept-btn-handler"))).click();
	        	
	        	new WebDriverWait(driver, Duration.ofSeconds(10))
	            	.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".dy-modal-wrapper")));
	            WebElement closeButton = driver.findElement(By.cssSelector(".dy-modal-wrapper .dy-modal-contents .dy-lb-close")); // Use correct selector
	            closeButton.click();
	            // Wait for modal to disappear
	            new WebDriverWait(driver, Duration.ofSeconds(10))
	                .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".dy-modal-wrapper")));
	        } catch (NoSuchElementException | TimeoutException e) {
	            System.out.println("Modal not present or already closed.");
	        }
	        
	     // Prepare to write data to CSV file
            FileWriter csvWriter;
            try {
                csvWriter = new FileWriter("C:\\Users\\Faseeh\\eclipse-workspace\\SeleniumTest\\hp_laptops.csv");
                csvWriter.append("Brand Name,Product Name,Price,Operating System,Processor,Memory,Storage,Display,Graphics\n");
            } catch (IOException e) {
                System.out.println("Error: Unable to create or write to the file. Please make sure the file 'hp_laptops.csv' is closed in Excel or any other program.");
                driver.quit();
                return;
            }
            
	        String brandName = "HP";
	        boolean hasNextPage = true;
	
	        while (hasNextPage) {
	            // Wait for product listings to load
	            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".HorizontalProductTile_container__3F4IN")));
	            try {
	            	List<WebElement> products = driver.findElements(By.cssSelector(".HorizontalProductTile_container__3F4IN"));
		            // Find all product tiles
		
		            for (WebElement product : products) {
		                // Extract product name
		                String name = product.findElement(By.cssSelector(".HorizontalProductTile_infoContainer__1BjHM .HorizontalProductTile_main__1nR2d .Typography-module_titleS__sF7UO")).getText().replace(",", " ");
		                
		                String price = product.findElement(By.cssSelector("[data-test-hook='@hpstellar/core/price-block__sale-price']")).getText().replace(",", "");
		                try {
			                // Extract specifications
		                	List<WebElement> specs = product.findElements(By.cssSelector(".HorizontalProductTile_specFirstGlance__3vnhf span")).subList(0, 6);
			                String tempSpecs = "";
			                for (WebElement spec: specs) {
			                	tempSpecs = tempSpecs + spec.getText().replace(",", " ") + ",";
	//			                System.out.println( price+" : "+ spec.getText().replace(",", " ").replace("\n", " "));
			                	//product.findElement(By.cssSelector(".product-specs")).getText().replace(",", " ").replace("\n", " ");
			                }
			                csvWriter.append(String.format("%s,%s,%s,%s\n", brandName,name, price,tempSpecs));
		                } catch (IndexOutOfBoundsException e) {
		                	System.out.println("Product Name: \'"+name+"\' details are missing. So, skipping this product.");
		                	continue;
		                }
		
		                // Write to CSV
		            }
		
		            // Check for the presence of a 'Next' button
		            WebElement nextButtons = driver.findElement(By.cssSelector(".PaginationContainer_container__ngfOS .Pagination-module_container__JHnDH .Pagination-module_list__1fx5U .Pagination-module_next__T1EtZ"));
		            
		            if (nextButtons.isDisplayed() && !"true".equals(nextButtons.getDomAttribute("disabled"))) {
		                // Click the 'Next' button
		                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", nextButtons);
	
		                nextButtons.click();
		
		                // Wait for the next page to load
		                wait.until(ExpectedConditions.stalenessOf(products.get(0)));
		            } else {
		                hasNextPage = false;
		            }
		        }
	            catch(Exception e) {
	    	        System.out.println(e);
	    	        break;
	            }
	            
	        }
	
	        csvWriter.flush();
	        csvWriter.close();
	        System.out.println("Data extraction completed. Check 'hp_laptops.csv' for results.");
	
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        // Close the browser
	        driver.quit();
	    }
	}
	
	
}