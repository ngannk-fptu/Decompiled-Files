/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.radeox.macros.junit.report;

import com.atlassian.confluence.renderer.radeox.macros.junit.report.AbstractTestReport;
import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestReport;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestSuiteReport
extends AbstractTestReport {
    private String name;
    private String systemOutput;
    private String systemError;
    private List testCases = new LinkedList();
    private Map properties = new HashMap();

    @Override
    public void addTest(TestReport test) {
        if (test.getName() == null) {
            test.setName(String.valueOf(this.testCases.size() + 1));
        }
        this.testCases.add(test);
    }

    @Override
    public List getTestCases() {
        return this.testCases;
    }

    public void putProperty(String key, String value) {
        this.properties.put(key, value);
    }

    public String getProperty(String key) {
        return (String)this.properties.get(key);
    }

    public Map getProperties() {
        return this.properties;
    }

    public String getSystemError() {
        return this.systemError;
    }

    public void setSystemError(String systemError) {
        this.systemError = systemError;
    }

    public String getSystemOutput() {
        return this.systemOutput;
    }

    public void setSystemOutput(String systemOutput) {
        this.systemOutput = systemOutput;
    }

    @Override
    public int getErrorsCount() {
        int count = 0;
        for (int i = 0; i < this.testCases.size(); ++i) {
            TestReport test = (TestReport)this.testCases.get(i);
            if (test.getErrorsCount() <= 0) continue;
            ++count;
        }
        return count;
    }

    @Override
    public int getFailuresCount() {
        int count = 0;
        for (int i = 0; i < this.testCases.size(); ++i) {
            TestReport test = (TestReport)this.testCases.get(i);
            if (test.getFailuresCount() <= 0) continue;
            ++count;
        }
        return count;
    }

    public int getTotalErrorsCount() {
        int count = 0;
        for (int i = 0; i < this.testCases.size(); ++i) {
            TestReport test = (TestReport)this.testCases.get(i);
            if (test.getErrorsCount() <= 0) continue;
            ++count;
        }
        return count;
    }

    public int getTotalFailuresCount() {
        int count = 0;
        for (int i = 0; i < this.testCases.size(); ++i) {
            TestReport test = (TestReport)this.testCases.get(i);
            if (test.getFailuresCount() <= 0) continue;
            ++count;
        }
        return count;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public int getTotalTestsCount() {
        int count = 0;
        for (int i = 0; i < this.testCases.size(); ++i) {
            TestReport test = (TestReport)this.testCases.get(i);
            count += test.getTestsCount();
        }
        if (count == 0) {
            count = this.testCases.size();
        }
        return count;
    }

    @Override
    public int getTestsCount() {
        return this.testCases.size();
    }

    @Override
    public long getTime() {
        long time = 0L;
        for (int i = 0; i < this.testCases.size(); ++i) {
            time += ((TestReport)this.testCases.get(i)).getTime();
        }
        return time;
    }

    @Override
    public TestReport getCurrentTest() {
        if (this.testCases.size() == 0) {
            return null;
        }
        return ((TestReport)this.testCases.get(this.testCases.size() - 1)).getCurrentTest();
    }
}

