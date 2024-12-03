/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.radeox.macros.junit.report;

import com.atlassian.confluence.renderer.radeox.macros.junit.report.AbstractTestReport;
import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestCaseFailureReport;
import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestReport;
import java.util.ArrayList;
import java.util.List;

public class TestCaseReport
extends AbstractTestReport {
    private String name;
    private List testCases = new ArrayList();
    private Timer timer = new Timer();

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getTime() {
        return this.timer.getTime();
    }

    @Override
    public TestCaseFailureReport getFailure() {
        if (this.failure == null) {
            for (int i = 0; i < this.testCases.size(); ++i) {
                TestCaseFailureReport temp = ((TestCaseReport)this.testCases.get(i)).getFailure();
                if (temp == null) continue;
                return temp;
            }
        }
        return this.failure;
    }

    @Override
    public void setFailure(TestCaseFailureReport failure) {
        if (this.failure == null) {
            this.failure = failure;
        }
    }

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

    public int getTopErrorsCount() {
        int errors = 0;
        if (this.getFailure() != null && !this.getFailure().getType().equalsIgnoreCase("failure")) {
            ++errors;
        }
        return errors;
    }

    @Override
    public int getErrorsCount() {
        int errors = 0;
        if (this.failure != null && !this.failure.getType().equalsIgnoreCase("failure")) {
            ++errors;
        }
        for (int i = 0; i < this.testCases.size(); ++i) {
            errors += ((TestCaseReport)this.testCases.get(i)).getTopErrorsCount();
        }
        return errors;
    }

    public int getTopFailuresCount() {
        int failures = 0;
        if (this.getFailure() != null && this.getFailure().getType().equalsIgnoreCase("failure")) {
            ++failures;
        }
        return failures;
    }

    @Override
    public int getFailuresCount() {
        int failures = 0;
        if (this.failure != null && this.failure.getType().equalsIgnoreCase("failure")) {
            ++failures;
        }
        for (int i = 0; i < this.testCases.size(); ++i) {
            failures += ((TestCaseReport)this.testCases.get(i)).getTopFailuresCount();
        }
        return failures;
    }

    public int getTotalTestsCount() {
        int tests = 0;
        for (int i = 0; i < this.testCases.size(); ++i) {
            tests += ((TestCaseReport)this.testCases.get(i)).getTotalTestsCount();
        }
        if (tests == 0) {
            tests = this.getTestsCount();
        }
        return tests;
    }

    @Override
    public int getTestsCount() {
        return this.testCases.size();
    }

    @Override
    public TestReport getCurrentTest() {
        if (this.testCases.size() == 0) {
            return this;
        }
        TestReport test = (TestCaseReport)this.testCases.get(this.testCases.size() - 1);
        if ((test = test.getCurrentTest()).getTime() == 0L) {
            return test;
        }
        return this;
    }

    public void stopTimer() {
        this.timer.stop();
    }

    public boolean isTimerStopped() {
        return this.timer.getEnd() > 0L;
    }

    public static class Timer {
        private long start = System.currentTimeMillis();
        private long end = 0L;
        private long time = 0L;

        public long stop() {
            this.end = System.currentTimeMillis();
            this.time = this.end - this.start;
            if (this.time < 10L) {
                this.time = 10L;
            }
            return this.time;
        }

        public long getStart() {
            return this.start;
        }

        public long getTime() {
            return this.time;
        }

        public long getEnd() {
            return this.end;
        }
    }
}

