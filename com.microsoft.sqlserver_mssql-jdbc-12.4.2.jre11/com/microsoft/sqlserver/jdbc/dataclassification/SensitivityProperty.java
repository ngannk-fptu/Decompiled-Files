/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc.dataclassification;

import com.microsoft.sqlserver.jdbc.dataclassification.InformationType;
import com.microsoft.sqlserver.jdbc.dataclassification.Label;

public class SensitivityProperty {
    private Label label;
    private InformationType informationType;
    private int sensitivityRank;

    public SensitivityProperty(Label label, InformationType informationType) {
        this.label = label;
        this.informationType = informationType;
    }

    public SensitivityProperty(Label label, InformationType informationType, int sensitivityRank) {
        this.label = label;
        this.informationType = informationType;
        this.sensitivityRank = sensitivityRank;
    }

    public Label getLabel() {
        return this.label;
    }

    public InformationType getInformationType() {
        return this.informationType;
    }

    public int getSensitivityRank() {
        return this.sensitivityRank;
    }
}

