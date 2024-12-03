/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.rest.model;

import java.io.Serializable;

public class CollectMetadata
implements Serializable {
    private String problemDescription;
    private boolean checkbox1;
    private boolean checkbox2;

    public String getProblemDescription() {
        return this.problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public boolean isCheckbox1() {
        return this.checkbox1;
    }

    public void setCheckbox1(boolean isChecked) {
        this.checkbox1 = isChecked;
    }

    public boolean isCheckbox2() {
        return this.checkbox2;
    }

    public void setCheckbox2(boolean isChecked) {
        this.checkbox2 = isChecked;
    }
}

