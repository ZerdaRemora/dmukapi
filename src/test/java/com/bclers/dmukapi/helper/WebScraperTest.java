package com.bclers.dmukapi.helper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebScraperTest
{

    @Test
    void testCanGetBBCScraperInstance()
    {
        String url = "https://www.bbc.co.uk/news";
        WebScraper scraper = WebScraper.getInstance(url);

        assertNotNull(scraper, "Scraper object was null.");
        assertTrue(scraper instanceof BBCScraper, "Return scraper not a BBCScraper.");
    }

    @Test
    void testCanGetDMScraperInstance()
    {
        String url = "https://www.dailymail.co.uk/home/index.html";
        WebScraper scraper = WebScraper.getInstance(url);

        assertNotNull(scraper, "Scraper object was null.");
        assertTrue(scraper instanceof DMScraper, "Return scraper not a DMScraper.");
    }

    @Test
    void testCanGetRedditInstance()
    {
        String url = "https://www.reddit.com/r/ukpolitics/";
        WebScraper scraper = WebScraper.getInstance(url);

        assertNotNull(scraper, "Scraper object was null.");
        assertTrue(scraper instanceof UKPScraper, "Return scraper not a UKPScraper.");
    }

    @Test
    void testGetInstanceReturnsNullWithUnknownURL()
    {
        String url = "https://www.stackoverflow.com";
        WebScraper scraper = WebScraper.getInstance(url);

        assertNull(scraper, "getInstance did not return null.");
    }

    @Test
    void getPageTitle()
    {
        String url = "https://www.bbc.co.uk/news/uk-england-leeds-50313620";
        WebScraper scraper = WebScraper.getInstance(url);

        String title = scraper.getPageTitle();

        assertNotNull(title, "Page Title was null.");
        assertEquals("Bonfire Night: Riot police injured during Leeds disorder - BBC News", title,
                "Page Title was incorrect.");
    }

    @Test
    void getUrl()
    {
        String url = "https://www.bbc.co.uk/news/uk-england-leeds-50313620";
        WebScraper scraper = WebScraper.getInstance(url);

        String returnedUrl = scraper.getUrl();

        assertNotNull(returnedUrl, "getUrl() returned null.");
        assertEquals(url, returnedUrl, "Input URL and returned URL did not match.");
    }
}