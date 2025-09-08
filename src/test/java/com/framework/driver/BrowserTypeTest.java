package com.framework.driver;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for BrowserType enum
 */
public class BrowserTypeTest {

    @Test
    public void testFromStringValidBrowsers() {
        Assert.assertEquals(BrowserType.fromString("chrome"), BrowserType.CHROME);
        Assert.assertEquals(BrowserType.fromString("CHROME"), BrowserType.CHROME);
        Assert.assertEquals(BrowserType.fromString("Chrome"), BrowserType.CHROME);
        Assert.assertEquals(BrowserType.fromString("firefox"), BrowserType.FIREFOX);
        Assert.assertEquals(BrowserType.fromString("edge"), BrowserType.EDGE);
        Assert.assertEquals(BrowserType.fromString("safari"), BrowserType.SAFARI);
        Assert.assertEquals(BrowserType.fromString("chrome-headless"), BrowserType.CHROME_HEADLESS);
        Assert.assertEquals(BrowserType.fromString("firefox-headless"), BrowserType.FIREFOX_HEADLESS);
    }

    @Test
    public void testFromStringNullOrEmpty() {
        Assert.assertEquals(BrowserType.fromString(null), BrowserType.CHROME);
        Assert.assertEquals(BrowserType.fromString(""), BrowserType.CHROME);
        Assert.assertEquals(BrowserType.fromString("   "), BrowserType.CHROME);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFromStringInvalidBrowser() {
        BrowserType.fromString("invalid-browser");
    }

    @Test
    public void testIsHeadless() {
        Assert.assertTrue(BrowserType.CHROME_HEADLESS.isHeadless());
        Assert.assertTrue(BrowserType.FIREFOX_HEADLESS.isHeadless());
        Assert.assertFalse(BrowserType.CHROME.isHeadless());
        Assert.assertFalse(BrowserType.FIREFOX.isHeadless());
        Assert.assertFalse(BrowserType.EDGE.isHeadless());
        Assert.assertFalse(BrowserType.SAFARI.isHeadless());
    }

    @Test
    public void testGetBaseBrowser() {
        Assert.assertEquals(BrowserType.CHROME_HEADLESS.getBaseBrowser(), BrowserType.CHROME);
        Assert.assertEquals(BrowserType.FIREFOX_HEADLESS.getBaseBrowser(), BrowserType.FIREFOX);
        Assert.assertEquals(BrowserType.CHROME.getBaseBrowser(), BrowserType.CHROME);
        Assert.assertEquals(BrowserType.FIREFOX.getBaseBrowser(), BrowserType.FIREFOX);
        Assert.assertEquals(BrowserType.EDGE.getBaseBrowser(), BrowserType.EDGE);
        Assert.assertEquals(BrowserType.SAFARI.getBaseBrowser(), BrowserType.SAFARI);
    }

    @Test
    public void testGetBrowserName() {
        Assert.assertEquals(BrowserType.CHROME.getBrowserName(), "chrome");
        Assert.assertEquals(BrowserType.FIREFOX.getBrowserName(), "firefox");
        Assert.assertEquals(BrowserType.EDGE.getBrowserName(), "edge");
        Assert.assertEquals(BrowserType.SAFARI.getBrowserName(), "safari");
        Assert.assertEquals(BrowserType.CHROME_HEADLESS.getBrowserName(), "chrome-headless");
        Assert.assertEquals(BrowserType.FIREFOX_HEADLESS.getBrowserName(), "firefox-headless");
    }

    @Test
    public void testGetDisplayName() {
        Assert.assertEquals(BrowserType.CHROME.getDisplayName(), "Chrome");
        Assert.assertEquals(BrowserType.FIREFOX.getDisplayName(), "Firefox");
        Assert.assertEquals(BrowserType.EDGE.getDisplayName(), "Microsoft Edge");
        Assert.assertEquals(BrowserType.SAFARI.getDisplayName(), "Safari");
        Assert.assertEquals(BrowserType.CHROME_HEADLESS.getDisplayName(), "Chrome Headless");
        Assert.assertEquals(BrowserType.FIREFOX_HEADLESS.getDisplayName(), "Firefox Headless");
    }

    @Test
    public void testToString() {
        Assert.assertEquals(BrowserType.CHROME.toString(), "Chrome");
        Assert.assertEquals(BrowserType.FIREFOX.toString(), "Firefox");
        Assert.assertEquals(BrowserType.EDGE.toString(), "Microsoft Edge");
    }
}