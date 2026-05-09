package com.sismics.util.totp;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test of {@link GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder}.
 */
public class TestGoogleAuthenticatorConfig {

    @Test
    public void testDefaultValues() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().build();

        Assert.assertEquals(6, config.getCodeDigits());
        Assert.assertEquals(1_000_000, config.getKeyModulus());
        Assert.assertEquals(30_000L, config.getTimeStepSizeInMillis());
        Assert.assertEquals(3, config.getWindowSize());
        Assert.assertEquals(KeyRepresentation.BASE32, config.getKeyRepresentation());
    }

    @Test
    public void testSettersWithValidValues() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setCodeDigits(8)
                .setTimeStepSizeInMillis(45_000L)
                .setWindowSize(5)
                .setKeyRepresentation(KeyRepresentation.BASE64)
                .build();

        Assert.assertEquals(8, config.getCodeDigits());
        Assert.assertEquals(100_000_000, config.getKeyModulus());
        Assert.assertEquals(45_000L, config.getTimeStepSizeInMillis());
        Assert.assertEquals(5, config.getWindowSize());
        Assert.assertEquals(KeyRepresentation.BASE64, config.getKeyRepresentation());
    }

    @Test
    public void testSetCodeDigitsRejectsNonPositive() {
        try {
            new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setCodeDigits(0);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Code digits must be positive.", e.getMessage());
        }
    }

    @Test
    public void testSetCodeDigitsRejectsTooSmall() {
        try {
            new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setCodeDigits(5);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The minimum number of digits is 6.", e.getMessage());
        }
    }

    @Test
    public void testSetCodeDigitsRejectsTooLarge() {
        try {
            new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setCodeDigits(9);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The maximum number of digits is 8.", e.getMessage());
        }
    }

    @Test
    public void testSetTimeStepSizeRejectsNonPositive() {
        try {
            new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setTimeStepSizeInMillis(0);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Time step size must be positive.", e.getMessage());
        }
    }

    @Test
    public void testSetWindowSizeRejectsNonPositive() {
        try {
            new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setWindowSize(0);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Window number must be positive.", e.getMessage());
        }
    }

    @Test
    public void testSetKeyRepresentationRejectsNull() {
        try {
            new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setKeyRepresentation(null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Key representation cannot be null.", e.getMessage());
        }
    }
}