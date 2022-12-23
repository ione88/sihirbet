package com.sihirbet.sihirbet.controller;

import com.sihirbet.sihirbet.entity.Liga;
import com.sihirbet.sihirbet.entity.Ptt;
import com.sihirbet.sihirbet.entity.Url;
import com.sihirbet.sihirbet.service.LigaService;
import com.sihirbet.sihirbet.service.PttService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverLogLevel;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Level;

@Controller
public class PTTController {

    WebDriver driver;
    public PttService pttService;

    public PTTController(PttService pttService) {
        this.pttService = pttService;
    }

    @GetMapping("/ptt/search")
    public String search() {
        pttService.getAllId()
                .forEach(this::browser);
        return "/ptt/search";
    }

    private void browser(Long id) {
        driverSetting();
        openPage("https://gonderitakip.ptt.gov.tr/Track/Index");
        search(id);
        try {
            Thread.sleep(2*1000);
        } catch (Exception ignored) {};
        closePage();
    }

    private void closePage() {
        driver.quit();
    }

    private void search(Long id) {
        driver.findElement(By.id("search-area")).sendKeys(id.toString());
        driver.findElement(By.id("searchButton")).click();
        try {
            save(id);
        } catch (Exception ignored) {
            Ptt ptt = Ptt.builder()
                    .id(id)
                    .alici("no data")
                    .adres("no data")
                    .build();
            pttService.update(ptt);
        }
    }

    private void save(Long id) {
        String alici = driver.findElement(By.cssSelector("body > main > div > div:nth-child(4) > div:nth-child(2) > div > ul > li:nth-child(1)")).getText().split(": ")[1];
        String adres = driver.findElement(By.cssSelector("body > main > div > div:nth-child(4) > div:nth-child(2) > div > ul > li:nth-child(2)")).getText().split(": ")[1];
        Ptt ptt = Ptt.builder()
                .id(id)
                .alici(alici)
                .adres(adres)
                .build();
        pttService.update(ptt);
    }

    private void openPage(String regionUrl) {
        Url url = Url.builder()
                .baseUrl(regionUrl)
                .build();
        driver.get(url.toString());
    }

    private void driverSetting() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setLogLevel(ChromeDriverLogLevel.fromLevel(Level.OFF));
        driver = new ChromeDriver(chromeOptions);
    }


}
