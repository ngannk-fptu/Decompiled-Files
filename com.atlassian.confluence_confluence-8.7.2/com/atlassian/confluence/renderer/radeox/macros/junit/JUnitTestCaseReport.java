/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.radeox.macros.junit;

import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestCaseReport;
import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestReport;
import java.util.Iterator;

public class JUnitTestCaseReport
extends TestCaseReport {
    private long time = 0L;

    @Override
    public long getTime() {
        if (this.time == 0L) {
            Iterator i = this.getTestCases().iterator();
            while (i.hasNext()) {
                this.time += ((TestReport)i.next()).getTime();
            }
        }
        return this.time;
    }

    public void setTimeAsString(String time) {
        this.time = (long)(1000.0 * Double.parseDouble(time));
    }

    @Override
    public void setName(String name) {
        if (name.lastIndexOf(".") > -1) {
            name = name.substring(name.lastIndexOf(".") + 1);
        }
        super.setName(name);
    }
}

