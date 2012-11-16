import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Test cases for quizzy application.
 * You need to implement some static members on LocalSettings Class according to
 * your conditions.
 *
 * @author Kazuki Nishiura
 */
public class QuizzyTest {
	// If this flag is set true, calculate coverage by using jsCoverage.
	private static final boolean calcCoverage = false;
	private static WebDriver driver;
	private static long WAIT_LIMIT_SEC = 3;
	private WebDriverWait wait;

	@BeforeClass
	static public void setUpBrowser() {
        if (LocalSettings.driver == null)
        	driver = new FirefoxDriver();
        else
        	driver = LocalSettings.driver;
	}

	@AfterClass
	static public void terminateBrowser() {
		if (LocalSettings.driver == null)
			driver.close();
		else
			;
	}

	@Before
	public void setup() {
		loadUrl();
        wait = new WebDriverWait(driver, WAIT_LIMIT_SEC);
	}

	private void loadUrl() {
		if (calcCoverage) {
			driver.switchTo().defaultContent();
			if (!LocalSettings.JS_COVERAGE_FRONT_PAGE_URL.equals(driver.getCurrentUrl())) {
				driver.get(LocalSettings.JS_COVERAGE_FRONT_PAGE_URL);
				WebElement location = driver.findElement(By.id("location"));
				location.clear();
				location.sendKeys(LocalSettings.JS_COVERAGE_INSTRUMENTED_WEB_PAGE_URL);
			}
			for (WebElement elm: driver.findElements(By.tagName("button"))) {
				if (elm.getText().equals("Open in frame")) {
					elm.click();
					break;
				}
			}
			driver.switchTo().frame("browserIframe");
		} else {
			driver.get(LocalSettings.TARGET_WEB_PAGE_URL);
		}
	}

	// Test what should happen is actually happens
	@Test
	public void followUseCase() {
		WebElement element = wait.until(visibilityOfElementLocated(By.tagName("input")));
		element.click();
		WebElement desc = wait.until(visibilityOfElementLocated(By.className("quizzy_quiz_desc")));
		assertEquals("Introduction of myself", desc.getText());

		// start quiz
		click(By.id("quizzy_start_b"));
		WebElement checkButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));

		// choice first option and answer
		click(By.id("quizzy_q0_opt0"));
		checkButton.click();

		WebElement nextButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_nxt")));
		WebElement best = findElement(By.className("quizzy_opt_best"));
		assertTrue(best != null);
		assertEquals("-", best.getText());
		nextButton.click();

		// choice first option and answer
		checkButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_chk")));
		click(By.id("quizzy_q1_opt0"));
		checkButton.click();
		WebElement mid = wait.until(visibilityOfElementLocated(By.className("quizzy_opt_mid")));
		assertEquals("10", mid.getText());
		desc = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_exp")));
		desc = desc.findElement(By.tagName("p"));
		assertEquals("Not bad", desc.getText());

		nextButton = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_nxt")));
		nextButton.click();

		// get score
		WebElement score = wait.until(visibilityOfElementLocated(By.className("quizzy_result_score")));
		assertEquals("25", score.getText());

		WebElement againButton = findElement(By.className("quizzy_result_foot")).findElement(By.tagName("input"));
		againButton.click();

		// user can start quiz again
		wait.until(elementToBeClickable(By.id("quizzy_start_b")));
	}

	// test what shouldn't happen acutally do not happen
	@Test
	public void tryInvalidOperation() {
		WebElement startButton = wait.until(visibilityOfElementLocated(By.id("quizzy_start_b")));
		// we cannot start quiz before selecting option
		startButton.click();
		try {
			wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));
			fail("test should not start");
		} catch (TimeoutException e) {
			// expected exception
		}

		// start quiz
		click(By.tagName("input"));
		click(By.id("quizzy_start_b"));
		WebElement checkButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));

		// choice first option and answer
		click(By.id("quizzy_q0_opt0"));
		checkButton.click();
		WebElement nextButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_nxt")));
		nextButton.click();

		checkButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_chk")));

		// we cannot check answer unless we choice option
		checkButton.click();
		try {
			wait.until(visibilityOfElementLocated(By.className("quizzy_opt_mid")));
			fail("we cannot check answer unless choosing option");
		} catch (TimeoutException e) {
			// expected exception
		}
	}

	// test for sliding up. when user choose option, the one user choose and the
	// best one is left, other options must be hidden.
	@Test
	public void testSlideUp() throws InterruptedException {
		WebElement element = wait.until(visibilityOfElementLocated(By.tagName("input")));
		element.click();

		// start quiz
		click(By.id("quizzy_start_b"));
		WebElement checkButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));

		// choice first option and answer
		click(By.id("quizzy_q0_opt0"));
		checkButton.click();

		WebElement nextButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_nxt")));
		assertTrue(findElement(By.id("quizzy_q0_opt0")).isDisplayed());
		assertFalse(findElement(By.id("quizzy_q0_opt1")).isDisplayed());
		assertFalse(findElement(By.id("quizzy_q0_opt2")).isDisplayed());
		nextButton.click();

		// choice first option and answer
		checkButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_chk")));
		click(By.id("quizzy_q1_opt2"));
		checkButton.click();
		Thread.sleep(1000);
		nextButton = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_nxt")));

		assertFalse(findElement(By.id("quizzy_q1_opt0")).isDisplayed());
		assertTrue(findElement(By.id("quizzy_q1_opt1")).isDisplayed());
		assertTrue(findElement(By.id("quizzy_q1_opt2")).isDisplayed());
	}

	@Test
	public void testDescription() {
		WebElement element1 = wait.until(visibilityOfElementLocated(By.tagName("input")));
		assertFalse(findElement(By.className("quizzy_quiz_desc")).isDisplayed());
		element1.click();
		WebElement desc = wait.until(visibilityOfElementLocated(By.className("quizzy_quiz_desc")));
		assertEquals("Introduction of myself", desc.getText());
		assertFalse(driver.findElements(By.className("quizzy_quiz_desc")).get(1).isDisplayed());
		WebElement element2 = driver.findElements(By.tagName("input")).get(1);
		element2.click();
		desc = wait.until(visibilityOfElementLocated(By.id("quizzy_quiz_desc1")));
		assertFalse(driver.findElement(By.id("quizzy_quiz_desc0")).isDisplayed());
	}

	// test when playing test two times
	@Test
	public void doQuizTwice() throws InterruptedException {
		followAnotherUseCaseClickingLabel();
		Thread.sleep(200);
		WebElement againButton = findElement(By.className("quizzy_result_foot")).findElement(By.tagName("input"));
		againButton.click();
		Thread.sleep(3000);
		assertFalse(driver.findElement(By.id("quizzy_quiz_desc0")).isDisplayed());
		assertFalse(driver.findElement(By.id("quizzy_quiz_desc1")).isDisplayed());
		assertFalse(driver.findElements(By.tagName("input")).get(0).isSelected());
		assertFalse(driver.findElements(By.tagName("input")).get(1).isSelected());
		followUseCase();
	}

	// test when clicking label, and when clicking second option
	@Test
	public void followAnotherUseCaseClickingLabel() {
		WebElement label = wait.until(
				visibilityOfElementLocated(By.className("quizzy_quiz_lbl")));
		label.click();
		WebElement desc = wait.until(visibilityOfElementLocated(By.className("quizzy_quiz_desc")));
		assertEquals("Introduction of myself", desc.getText());

		// start quiz
		click(By.id("quizzy_start_b"));
		WebElement checkButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));

		WebElement nextButton = findElement(By.id("quizzy_q0_foot_nxt"));
		assertFalse("Next button must be hidden before choosing option",
				nextButton.isDisplayed());

		// choice second option and answer
		click(By.id("quizzy_q0_opt1"));
		checkButton.click();

		nextButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_nxt")));
		WebElement worst = findElement(By.className("quizzy_opt_worst"));
		assertTrue(worst != null);
		nextButton.click();

		// choice second option and answer
		checkButton
			= wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_chk")));
		assertFalse("Previous next button must be disappear",
				driver.findElement(By.id("quizzy_q0_foot_nxt")).isDisplayed());
		click(By.id("quizzy_q1_opt1"));
		checkButton.click();
		WebElement best = wait.until(visibilityOfElementLocated(
				By.xpath("//*[@id='quizzy_q1_opt1_val']/*[@class='quizzy_opt_best']")));
		assertEquals("30", best.getText());
		desc = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_exp")));
		desc = desc.findElement(By.tagName("p"));
		assertEquals("I love.", desc.getText());

		nextButton = wait.until(visibilityOfElementLocated(By.id("quizzy_q1_foot_nxt")));
		nextButton.click();

		// get score
		WebElement score = wait.until(visibilityOfElementLocated(By.className("quizzy_result_score")));
		assertEquals("30", score.getText());
	}

	// test 'Loading..' message is correctly shown. This test insert sleep
	// into the PHP file so that WebDriver certainly capture the message.
	@Test
	public void testLoadingByInsertingDelay() throws IOException, InterruptedException {
		rewriteConfig(true);
		loadUrl();
		try {
			WebElement loading = driver.findElement(By.id("quizzy"));
			if (!loading.getText().startsWith("Loading")) {
				throw new IllegalStateException("Loading message must be shown");
			}
			WebElement element = wait.until(visibilityOfElementLocated(By.tagName("input")));
			element.click();
			// start quiz
			click(By.id("quizzy_start_b"));
			wait.until(presenceOfElementLocated(By.xpath("//*[@id='quizzy']/*[@class='loading bottom left']")));
			WebElement checkButton
				= wait.until(visibilityOfElementLocated(By.id("quizzy_q0_foot_chk")));

			// choice first option and answer
			click(By.id("quizzy_q0_opt0"));
			checkButton.click();
			wait.until(visibilityOfElementLocated(By.xpath("//*[@id='quizzy']/*[@class='loading bottom left']")));

			rewriteConfig(false);
		} catch (Exception e) {
			fail(e.toString());
			rewriteConfig(false);
		}
	}

	private void rewriteConfig(boolean insertSleep) throws IOException {
		File file = new File(LocalSettings.CONFIG_PHP_FILE_PATH);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<String> contents = new ArrayList<String>();
		String content;
		while ((content = reader.readLine()) != null)
			contents.add(content);
		reader.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		int index = 0;
		for (String line: contents) {
			if (index == 1) {
				if (insertSleep) {
					writer.write("sleep(1);\n");
				} else {
					index++;
					continue;
				}
			}
			writer.write(line + "\n");
			index++;
		}
		writer.flush();
		writer.close();
	}

	private void click(By by) {
		WebElement target = findElement(by);
		target.click();
	}

	private WebElement findElement(By by) {
		return driver.findElement(by);
	}
}
