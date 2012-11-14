import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

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
 *
 * @author Kazuki Nishiura
 */
public class QuizzyTest {
	private static WebDriver driver;
	private static long WAIT_LIMIT_SEC = 2;
	private WebDriverWait wait;

	@BeforeClass
	static public void setUpBrowser() {
        driver = new FirefoxDriver();
	}

	@AfterClass
	static public void terminateBrowser() {
		driver.close();
	}

	@Before
	public void setup() {
        driver.get(LocalSettings.TARGET_WEB_PAGE_URL);
        wait = new WebDriverWait(driver, WAIT_LIMIT_SEC);
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
		assertEquals("✓", best.getText());
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
		wait.until(visibilityOfElementLocated(By.id("quizzy_start_b")));
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

	private void click(By by) {
		WebElement target = findElement(by);
		target.click();
	}

	private WebElement findElement(By by) {
		return driver.findElement(by);
	}
}
