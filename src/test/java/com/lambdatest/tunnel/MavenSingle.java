package com.lambdatest.tunnel;

import java.net.URL;
import java.util.HashMap;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IExecutionListener;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class MavenSingle implements IExecutionListener {
  Tunnel t;

  WebDriver driver = null;
  public static String status = "passed";

  String username = System.getenv("LT_USERNAME");
  String access_key = System.getenv("LT_ACCESS_KEY");

    @BeforeTest
    @org.testng.annotations.Parameters(value = { "browser", "version", "platform", "resolution" })
    public void setUp(String browser, String version, String platform, String resolution) throws Exception {
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("build", "Single Maven Tunnel");
        ltOptions.put("name", "Maven Tunnel");
        ltOptions.put("platform", platform);
        ltOptions.put("resolution", resolution);
        ltOptions.put("tunnel", true);
        ltOptions.put("network", true);
        ltOptions.put("console", true);
        ltOptions.put("visual", true);
        ltOptions.put("tunnelName", "MavenSingle");
        ltOptions.put("selenium_version", "4.0.0");

        options.setCapability("browserName", browser);
        options.setCapability("browserVersion", version);
        options.setCapability("LT:Options", ltOptions);

        // create tunnel instance
        t = new Tunnel();
        HashMap<String, String> tunnelOpts = new HashMap<String, String>();
        tunnelOpts.put("user", username);
        tunnelOpts.put("key", access_key);
        tunnelOpts.put("tunnelName", "MavenSingle");

        // start tunnel
        t.start(tunnelOpts);
        driver = new RemoteWebDriver(new URL("http://" + username + ":" + access_key + "@hub.lambdatest.com/wd/hub"),
                options);
        System.out.println("Started session");
    }

  @Test()
  public void testTunnel() throws Exception {
    // Check LocalHost on XAMPP
    driver.get("http://localhost.lambdatest.com");
    // Let's check that the item we added is added in the list.
    driver.get("https://google.com");
  }

  @AfterTest
  public void tearDown() throws Exception {
    ((JavascriptExecutor) driver).executeScript("lambda-status=" + status);
    driver.quit();
    // close tunnel
    t.stop();
  }
}
