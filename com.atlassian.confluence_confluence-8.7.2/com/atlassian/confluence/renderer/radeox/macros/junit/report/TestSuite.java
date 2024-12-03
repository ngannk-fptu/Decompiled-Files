/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.renderer.radeox.macros.junit.report;

import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestCaseFailureReport;
import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestReport;
import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestSuiteReport;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;

public class TestSuite {
    public static final String REPORT_DETAIL_ALL = "all";
    public static final String REPORT_DETAIL_PER_FIXTURE = "fixture";
    public static final String REPORT_DETAIL_SUMMARY = "summary";
    public static final String REPORT_DETAIL_FAILURES_ONLY = "failuresonly";
    private Writer writer;
    private TestReport report = null;
    private boolean debug = false;
    private String reportDetail = "fixture";
    private HttpServletRequest currentRequest = null;
    private boolean stopRunning = false;
    private boolean stopOnException = false;

    public TestSuite(TestReport report) {
        this.report = report;
    }

    public Writer getWriter() {
        return this.writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public TestReport getReport() {
        return this.report;
    }

    public void setReport(TestSuiteReport report) {
        this.report = report;
    }

    public boolean isRunningSuite() {
        return this.report instanceof TestSuiteReport;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void onException(String nodeText, String message, Throwable exception) {
        while (exception.getClass().equals(InvocationTargetException.class)) {
            exception = ((InvocationTargetException)exception).getTargetException();
        }
        StringWriter buf = new StringWriter();
        exception.printStackTrace(new PrintWriter(buf));
        TestCaseFailureReport failure = new TestCaseFailureReport();
        StringBuilder msg = new StringBuilder();
        msg.append("Exception executing '<i>");
        msg.append(nodeText);
        msg.append("</i>': ");
        msg.append(exception.getMessage());
        msg.append(" ( ").append(exception.getClass().getName()).append(")");
        if (message != null) {
            msg.append("<br>").append(message);
        }
        failure.setMessage(msg.toString());
        failure.setContent(buf.toString());
        failure.setType("exception");
        this.getReport().getCurrentTest().getCurrentTest().setFailure(failure);
    }

    public void onFailure(String nodeText, String actualValue, String message) {
        TestCaseFailureReport failure = new TestCaseFailureReport();
        StringBuilder msg = new StringBuilder();
        if (actualValue != null) {
            msg.append("'<i>").append(nodeText).append("</i>'");
            msg.append(" was expected, but actual value was ");
            msg.append("'<i>").append(actualValue).append("</i>'");
        }
        if (message != null) {
            msg.append("<br>").append(message);
        }
        failure.setMessage(msg.toString());
        failure.setType("failure");
        TestReport report = this.getReport().getCurrentTest().getCurrentTest();
        report.setFailure(failure);
    }

    public void onPass(String nodeText, String message) {
        this.getReport().getCurrentTest().getCurrentTest().setPassMessage(message);
    }

    public void onDebug(String nodeText, String message) {
        this.getReport().getCurrentTest().getCurrentTest().setPassMessage("[" + nodeText + "] " + message);
    }

    public String getReportDetail() {
        return this.reportDetail;
    }

    public void setReportDetail(String reportDetail) {
        this.reportDetail = reportDetail;
    }

    public HttpServletRequest getCurrentRequest() {
        return this.currentRequest;
    }

    public void setCurrentRequest(HttpServletRequest currentRequest) {
        this.currentRequest = currentRequest;
    }

    public String getCurrentRequestUrl() {
        StringBuffer buf = this.getCurrentRequest().getRequestURL();
        buf.append("?");
        buf.append(this.getCurrentRequest().getQueryString());
        return buf.toString();
    }

    public boolean isStopRunning() {
        return this.stopRunning;
    }

    public void setStopRunning(boolean stopRunning) {
        this.stopRunning = stopRunning;
    }

    public boolean isStopOnException() {
        return this.stopOnException;
    }

    public void setStopOnException(boolean stopOnException) {
        this.stopOnException = stopOnException;
    }
}

