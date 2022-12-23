package com.sihirbet.sihirbet.controller;

import com.sihirbet.sihirbet.entity.Liga;
import com.sihirbet.sihirbet.entity.Region;
import com.sihirbet.sihirbet.entity.Url;
import com.sihirbet.sihirbet.service.LigaService;
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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;

@Controller
public class LigaController {

    WebDriver driver;
    public LigaService ligaService;
    public RegionService regionService;

    public LigaController(LigaService ligaService,
                          RegionService regionService) {
        this.ligaService = ligaService;
        this.regionService = regionService;
    }

    @GetMapping("/liga/scrubbing")
    public String scrubbing() {
        regionService.getAllUrl()
                .forEach(this::processRegionUrl);
        return "/region/scrubbing";
    }

    private void processRegionUrl(String regionUrl) {
        driverSetting();
        openPage(regionUrl);
        hideBanner();
        expandList();
        processLiga();
        closePage();
    }

    private void closePage() {
        driver.quit();
    }

    private void processLiga() {
        driver.findElements(By.xpath("//*[@id=\"mt\"]/div/div/a"))
                .forEach(this::process);
    }

    private void expandList() {
        try {
            driver.findElement(By.xpath("//*[@id=\"mt\"]/div/div[12]")).click();
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

    private void process(WebElement ligaElement) {
        Liga liga = Liga.builder()
                .name(ligaElement.getText())
                .url(ligaElement.getAttribute("href"))
                .build();
        ligaService.update(liga);
    }

}
