package com.sihirbet.sihirbet.controller;

import com.sihirbet.sihirbet.entity.Region;
import com.sihirbet.sihirbet.entity.Url;
import com.sihirbet.sihirbet.service.RegionService;
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
public class RegionController {

    WebDriver driver;
    public RegionService ligaService;

    public RegionController(RegionService ligaService) {
        this.ligaService = ligaService;
    }

    @GetMapping("/region/scrubbing")
    public String scrubbing() {
        driverSetting();
        openPage();
        hideBanner();
        expandList();
        processRegion();
        closePage();
        return "/region/scrubbing";
    }

    private void closePage() {
        driver.quit();
    }

    private void processRegion() {
        driver.findElements(By.xpath("//*[@id=\"category-left-menu\"]/div/div/a"))
                .parallelStream().forEach(this::process);
    }

    private void expandList() {
        try {
            driver.findElement(By.xpath("//*[@id=\"category-left-menu\"]/div/span")).click();
        } catch (Exception e) {
            System.out.println("no more span");
        }
    }

    private void hideBanner() {
        try {
            driver.findElement(By.xpath("//*[@id=\"onetrust-accept-btn-handler\"]")).click();
        } catch (Exception e) {
            System.out.println("no banner");
        }
    }

    private void openPage() {
        Url url = Url.builder()
                .baseUrl("https://www.flashscore.com.ua/football/")
                .build();
        driver.get(url.toString());
    }

    private void driverSetting() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setLogLevel(ChromeDriverLogLevel.fromLevel(Level.OFF));
        driver = new ChromeDriver(chromeOptions);
    }

    private void process(WebElement regionElement) {
        Region region = Region.builder()
                .name(regionElement.getText())
                .url(regionElement.getAttribute("href"))
                .build();
        ligaService.update(region);
    }

}
