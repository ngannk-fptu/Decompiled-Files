/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.radeox.macros.junit.report;

import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestCaseFailureReport;
import java.util.List;

public interface TestReport {
    public TestReport getCurrentTest();

    public void addTest(TestReport var1);

    public List getTestCases();

    public int getErrorsCount();

    public int getFailuresCount();

    public String getName();

    public void setName(String var1);

    public int getTestsCount();

    public long getTime();

    public int getSuccessRate();

    public int getErrorRate();

    public int getFailureRate();

    public TestCaseFailureReport getFailure();

    public void setFailure(TestCaseFailureReport var1);

    public String getPassMessage();

    public void setPassMessage(String var1);

    public String getDebugMessage();

    public void setDebugMessage(String var1);
}

