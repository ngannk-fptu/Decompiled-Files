/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.radeox.macros.junit.report;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestCaseFailureReport;
import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestReport;

public abstract class AbstractTestReport
implements TestReport {
    protected String passMessage = null;
    protected String debugMessage = null;
    protected TestCaseFailureReport failure;

    @Override
    public int getSuccessRate() {
        float rate = (float)(this.getTestsCount() - (this.getFailuresCount() + this.getErrorsCount())) / (float)this.getTestsCount();
        return Math.round(rate * 100.0f);
    }

    @Override
    public int getErrorRate() {
        return Math.round((float)this.getErrorsCount() / (float)this.getTestsCount() * 100.0f);
    }

    @Override
    public int getFailureRate() {
        return Math.round((float)this.getFailuresCount() / (float)this.getTestsCount() * 100.0f);
    }

    @Override
    public String getPassMessage() {
        return this.passMessage;
    }

    @Override
    public void setPassMessage(String passMessage) {
        this.passMessage = passMessage;
    }

    @Override
    public String getDebugMessage() {
        return this.debugMessage;
    }

    @Override
    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    @Override
    public TestCaseFailureReport getFailure() {
        return this.failure;
    }

    @Override
    public void setFailure(TestCaseFailureReport failure) {
        this.failure = failure;
    }

    public String getTimeAsString() {
        return DateFormatter.formatMillis(this.getTime());
    }
}

