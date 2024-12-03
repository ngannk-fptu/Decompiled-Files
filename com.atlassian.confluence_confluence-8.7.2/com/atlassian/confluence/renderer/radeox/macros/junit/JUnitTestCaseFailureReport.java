/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.radeox.macros.junit;

import com.atlassian.confluence.renderer.radeox.macros.junit.report.TestCaseFailureReport;

public class JUnitTestCaseFailureReport
extends TestCaseFailureReport {
    @Override
    public void setType(String type) {
        if (type != null && type.startsWith("junit.framework.")) {
            if (this.getMessage() == null) {
                this.setMessage(type);
            }
            type = "failure";
        }
        super.setType(type);
    }
}

