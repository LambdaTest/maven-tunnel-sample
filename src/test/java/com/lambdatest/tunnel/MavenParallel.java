package com.lambdatest.tunnel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IExecutionListener;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class MavenParallel implements IExecutionListener {

  public RemoteWebDriver driver = null;
  String status = "passed";
  String username = System.getenv("LT_USERNAME");
  String accessKey = System.getenv("LT_ACCESS_KEY");
  Tunnel t;

  @Override
  public void onExecutionStart() {
    try {
      // start the tunnel
      t = new Tunnel();
      HashMap<String, String> options = new HashMap<String, String>();
      options.put("user", username);
      options.put("key", accessKey);
      options.put("tunnelName", "MavenParallel");
      t.start(options);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

    @BeforeTest
    @org.testng.annotations.Parameters(value = { "browser", "version", "platform", "resolution" })
    public void setUp(String browser, String version, String platform, String resolution) throws Exception {
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> ltOptions = new HashMap<>();

        ltOptions.put("build", "Parallel Maven Tunnel");
        ltOptions.put("name", "Maven Tunnel");
        ltOptions.put("platformName", platform);    // W3C key
        ltOptions.put("resolution", resolution);
        ltOptions.put("tunnel", true);
        ltOptions.put("network", true);
        ltOptions.put("console", true);
        ltOptions.put("visual", true);
        ltOptions.put("tunnelName", "MavenParallel");

        options.setCapability("browserName", browser);
        options.setCapability("browserVersion", version);
        options.setCapability("LT:Options", ltOptions);

        try {
            driver = new RemoteWebDriver(new URL("https://" + username + ":" + accessKey + "@hub.lambdatest.com/wd/hub"),
                    options);
        } catch (MalformedURLException e) {
            System.out.println("Invalid grid URL");
        }
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
    if (driver != null) {
      ((JavascriptExecutor) driver).executeScript("lambda-status=" + status);
      driver.quit();
    }
  }

  @Override
  public void onExecutionFinish() {
    try {
      // stop the Tunnel;
      t.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
