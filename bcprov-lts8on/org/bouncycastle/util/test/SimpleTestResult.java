/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.test;

import org.bouncycastle.util.Strings;
import org.bouncycastle.util.test.Test;
import org.bouncycastle.util.test.TestResult;

public class SimpleTestResult
implements TestResult {
    private static final String SEPARATOR = Strings.lineSeparator();
    private boolean success;
    private String message;
    private Throwable exception;

    public SimpleTestResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public SimpleTestResult(boolean success, String message, Throwable exception) {
        this.success = success;
        this.message = message;
        this.exception = exception;
    }

    public static TestResult successful(Test test, String message) {
        return new SimpleTestResult(true, test.getName() + ": " + message);
    }

    public static TestResult failed(Test test, String message) {
        return new SimpleTestResult(false, test.getName() + ": " + message);
    }

    public static TestResult failed(Test test, String message, Throwable t) {
        return new SimpleTestResult(false, test.getName() + ": " + message, t);
    }

    public static TestResult failed(Test test, String message, Object expected, Object found) {
        return SimpleTestResult.failed(test, message + SEPARATOR + "Expected: " + expected + SEPARATOR + "Found   : " + found);
    }

    public static String failedMessage(String algorithm, String testName, String expected, String actual) {
        StringBuffer sb = new StringBuffer(algorithm);
        sb.append(" failing ").append(testName);
        sb.append(SEPARATOR).append("    expected: ").append(expected);
        sb.append(SEPARATOR).append("    got     : ").append(actual);
        return sb.toString();
    }

    @Override
    public boolean isSuccessful() {
        return this.success;
    }

    @Override
    public String toString() {
        return this.message;
    }

    @Override
    public Throwable getException() {
        return this.exception;
    }
}

