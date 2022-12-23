package com.sihirbet.sihirbet.scheduled;

import com.sihirbet.sihirbet.entity.Match;
import com.sihirbet.sihirbet.entity.Team;
import com.sihirbet.sihirbet.entity.Url;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverLogLevel;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

@Component
@Log4j2
public class Task {

    WebDriver driver;
    JavascriptExecutor jse;

    private static final String liveUrl = "https://www.flashscore.com.ua/";


    //@Scheduled(fixedDelay = 600000)
    public void scheduleFixedDelayTask() {
        driverSetting();
        openPage(liveUrl);
        hideBanner();
        expandList();
        process();
        closePage();
    }


//driver.get("https://www.flashscore.com.ua/match/G0YxSq3T/#/match-summary/match-summary");
//
    private void process() {
        //String format = String.format("#%s > div > .event__stage--block", id);
        driver.findElements(By.xpath("//div[contains(@id,\"g_1_\")]"))
                .stream()
                .map(e -> e.getAttribute("id"))
                .filter(id -> driver.findElements(By.cssSelector(String.format("#%s > div > .event__stage--block", id)))
                        .size() > 0)
                .filter(id -> !driver.findElement(By.cssSelector(String.format("#%s > div > .event__stage--block", id)))
                        .getText()
                        .equals("Завершен"))
                .filter(id -> !driver.findElement(By.cssSelector(String.format("#%s > div > .event__stage--block", id)))
                        .getText()
                        .equals("Перенесен"))
                .filter(id -> !driver.findElement(By.cssSelector(String.format("#%s > div > .event__stage--block", id)))
                        .getText()
                        .equals("Прерван"))
                .filter(id -> !driver.findElement(By.cssSelector(String.format("#%s > div > .event__stage--block", id)))
                        .getText()
                        .equals("Задержка"))
                .map(this::collectData)
                .filter(Objects::nonNull)
                .forEach(this::sendToTelegram);
    }

    private void sendToTelegram(Match match) {
    }

    private Match collectData(String id) {
        Match match = Match.builder()
                .home(Team.builder()
                        .name(driver.findElement(By.cssSelector("#" + id + " > .event__participant--home")).getText() )
                        .score(driver.findElement(By.cssSelector("#" + id + " > .event__score--home")).getText() )
                        .build())
                .away(Team.builder()
                        .name(driver.findElement(By.cssSelector("#" + id + " > .event__participant--away")).getText() )
                        .score(driver.findElement(By.cssSelector("#" + id + " > .event__score--away")).getText() )
                        .build())
                .build();
        String stage = StringUtils.trim(
                        driver.findElement(By.cssSelector("#" + id + " > div > .event__stage--block")).getText())
                .split("\\+")[0];
        int min = 90;
        if (StringUtils.isNumeric(stage)) {
            min = Integer.parseInt(stage);
        }
        match.setTime(min);
        if (min > 20) {
            log.debug("time more 20 minutes");
            log.info(match);
            return null;
        }
        WebElement e = driver.findElement(By.cssSelector("#" + id + " > .event__participant--home"));
        jse.executeScript(String.format("window.scrollTo(0, %s)", e.getLocation().getY()-100));

        driver.findElement(By.cssSelector("#" + id + " > div > .event__stage--block")).click();
        Set<String> windows = driver.getWindowHandles();
        String main = driver.getWindowHandle();
        windows.remove(main);
        Optional<String> popup = windows.stream().findFirst();

        if (popup.isPresent()) {
            driver.switchTo().window(popup.get());
            Optional<WebElement> webEl = driver.findElements(By.cssSelector("#detail > div.tabs.tabs__detail--nav > div > a")).stream().filter(p -> p.getText().equals("СТАТИСТИКА")).findFirst();
            if (webEl.isPresent()) {
                webEl.get().click();
            } else {
                driver.close();
                driver.switchTo().window(main);
                log.debug("Statistic excepts");
                log.info(match);
                return null;
            }
            driver.close();
            driver.switchTo().window(main);
        } else {
            driver.close();
            driver.switchTo().window(main);
            log.debug("Detail excepts");
            log.debug(match);
            return null;
        }
        log.info(match);
        return match;
    }

    private void driverSetting() {
        log.info("Start driver setup");
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setLogLevel(ChromeDriverLogLevel.fromLevel(Level.OFF));
        driver = new ChromeDriver(chromeOptions);
        jse = (JavascriptExecutor) driver;
        log.info("Driver setup well");
    }


    private void openPage(String liveUrl) {
        log.info("Start open: " + liveUrl);
        Url url = Url.builder()
                .baseUrl(liveUrl)
                .build();
        driver.get(url.toString());
        driver.findElements(By.cssSelector("#live-table > div.filters > div.filters__group > div > div"))
                .stream()
                .filter(e -> e.getText().equals("LIVE"))
                .findFirst().ifPresent(WebElement::click);
        log.info("Opened: " + liveUrl);
    }

    private void hideBanner() {
        log.info("Start banner to hide: " + liveUrl);
        try {
            driver.findElement(By.cssSelector("#onetrust-accept-btn-handler")).click();
            log.info("Banner hided: " + liveUrl);
        } catch (Exception e) {
            log.debug("Banner miss: " + liveUrl);
        }
    }

    private void expandList() {
        log.info("Start list to expand: " + liveUrl);
        try {
            driver.findElements(By.cssSelector("#live-table > section > div > div > div > div.event__info"))
                    .stream()
                    .filter(e -> e.getText().contains("показать игры"))
                    .forEach(e -> {
                        jse.executeScript(String.format("window.scrollTo(0, %s)", e.getLocation().getY()-100));
                        e.click();

                    });
            log.info("List expanded");
        } catch (Exception e) {
            log.warn("List except: " + e.getMessage());
        }
    }

    private void closePage() {
        log.info("Start page to close: " + liveUrl);
        driver.quit();
        log.info("Page closed: " + liveUrl);
    }

}
