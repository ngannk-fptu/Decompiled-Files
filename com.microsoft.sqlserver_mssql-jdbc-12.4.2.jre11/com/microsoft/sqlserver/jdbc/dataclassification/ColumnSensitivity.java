/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc.dataclassification;

import com.microsoft.sqlserver.jdbc.dataclassification.SensitivityProperty;
import java.util.ArrayList;
import java.util.List;

public class ColumnSensitivity {
    private List<SensitivityProperty> sensitivityProperties;

    public ColumnSensitivity(List<SensitivityProperty> sensitivityProperties) {
        this.sensitivityProperties = new ArrayList<SensitivityProperty>(sensitivityProperties);
    }

    public List<SensitivityProperty> getSensitivityProperties() {
        return this.sensitivityProperties;
    }
}

