package com.gfk.senbot.framework.context;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.LocaleUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/cucumber.xml"})
public class TestEnvironmentTest {
	
	@After
	public void cleanup() {
		SenBotContext.cleanupSenBot();
	}
	
	@Test
	public void testContructor_noLocale() {
		TestEnvironment env = new TestEnvironment(null, null, null);
		
		assertNull(env.getLocale());
	}

	@Test
	public void testContructor_locale() {
		TestEnvironment env = new TestEnvironment(null, null, null, "en_US");
		
		assertEquals(LocaleUtils.toLocale("en_US"), env.getLocale());
	}

    @Test
    public void testEquals_allNull() {
        TestEnvironment left = new TestEnvironment(null, null, null);
        TestEnvironment right = new TestEnvironment(null, null, null);

        assertTrue(left.matches(right));
    }

    @Test
    public void testEquals_browser() {
        TestEnvironment left = new TestEnvironment(TestEnvironment.CH, null, null);
        TestEnvironment right = new TestEnvironment(TestEnvironment.CH, null, null);

        assertTrue(left.matches(right));

        right = new TestEnvironment(TestEnvironment.FF, null, null);
        assertFalse(left.matches(right));
    }

    @Test
    public void testEquals_browserVersion() {
        TestEnvironment left = new TestEnvironment(null, "LATEST", null);
        TestEnvironment right = new TestEnvironment(null, "LATEST", null);

        assertTrue(left.matches(right));

        right = new TestEnvironment(null, "IE8", null);
        assertFalse(left.matches(right));
    }

    @Test
    public void testEquals_OS() {
        TestEnvironment left = new TestEnvironment(null, null, Platform.WINDOWS);
        TestEnvironment right = new TestEnvironment(null, null, Platform.WINDOWS);

        assertTrue(left.matches(right));

        right = new TestEnvironment(null, null, Platform.LINUX);
        assertFalse(left.matches(right));

        right = new TestEnvironment(null, null, Platform.XP);
        assertTrue(left.matches(right));

        right = new TestEnvironment(null, null, Platform.VISTA);
        assertTrue(left.matches(right));

        right = new TestEnvironment(null, null, Platform.ANY);
        assertTrue(left.matches(right));
    }
    
    @Test
    public void testEquals_locale() {
        TestEnvironment left = new TestEnvironment(null, null, null, "nl_NL");
        TestEnvironment right = new TestEnvironment(null, null, null, "nl_NL");

        assertTrue(left.matches(right));

        right = new TestEnvironment(null, null, null, "en_NZ");
        assertFalse(left.matches(right));
    }

    @Test
    public void testCleanupDriver() throws Throwable {
    	final WebDriver webDriver = mock(WebDriver.class);
    	TestEnvironment env = new TestEnvironment(null, null, Platform.WINDOWS){
    		@Override
    		public WebDriver getWebDriver(){
    			return webDriver;
    		}
    	};
    	
    	assertEquals(webDriver, env.getWebDriver());
    	
    	env.cleanupDriver();
    	
    	verify(webDriver, times(1)).quit();
    	env.cleanupAllDrivers();
    }        

    @Test
    public void testIsWebDriverAccessedSince() {
        TestEnvironment env = new TestEnvironment(TestEnvironment.FF, null, Platform.WINDOWS);
      
        long beforeAccess = System.currentTimeMillis() - 1;
        assertFalse(env.isWebDriverAccessedSince(0));
        assertFalse(env.isWebDriverAccessedSince(beforeAccess));

        env.getWebDriver();
        assertTrue(env.isWebDriverAccessedSince(0));
        assertTrue(env.isWebDriverAccessedSince(beforeAccess));
        assertFalse(env.isWebDriverAccessedSince(System.currentTimeMillis()));
        
        env.cleanupAllDrivers();
        
    }

}
