/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc.dataclassification;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.dataclassification.ColumnSensitivity;
import com.microsoft.sqlserver.jdbc.dataclassification.InformationType;
import com.microsoft.sqlserver.jdbc.dataclassification.Label;
import java.util.ArrayList;
import java.util.List;

public class SensitivityClassification {
    private List<Label> labels;
    private List<InformationType> informationTypes;
    private List<ColumnSensitivity> columnSensitivities;
    private int sensitivityRank;

    public SensitivityClassification(List<Label> labels, List<InformationType> informationTypes, List<ColumnSensitivity> columnSensitivity) {
        this.labels = new ArrayList<Label>(labels);
        this.informationTypes = new ArrayList<InformationType>(informationTypes);
        this.columnSensitivities = new ArrayList<ColumnSensitivity>(columnSensitivity);
    }

    public SensitivityClassification(List<Label> labels, List<InformationType> informationTypes, List<ColumnSensitivity> columnSensitivity, int sensitivityRank) {
        this.labels = new ArrayList<Label>(labels);
        this.informationTypes = new ArrayList<InformationType>(informationTypes);
        this.columnSensitivities = new ArrayList<ColumnSensitivity>(columnSensitivity);
        this.sensitivityRank = sensitivityRank;
    }

    public List<Label> getLabels() {
        return this.labels;
    }

    public List<InformationType> getInformationTypes() {
        return this.informationTypes;
    }

    public List<ColumnSensitivity> getColumnSensitivities() {
        return this.columnSensitivities;
    }

    public int getSensitivityRank() {
        return this.sensitivityRank;
    }

    public static enum SensitivityRank {
        NOT_DEFINED(-1),
        NONE(0),
        LOW(10),
        MEDIUM(20),
        HIGH(30),
        CRITICAL(40);

        private static final SensitivityRank[] VALUES;
        private int rank;

        private SensitivityRank(int rank) {
            this.rank = rank;
        }

        public int getValue() {
            return this.rank;
        }

        public static boolean isValid(int rank) throws SQLServerException {
            for (SensitivityRank r : VALUES) {
                if (r.getValue() != rank) continue;
                return true;
            }
            return false;
        }

        static {
            VALUES = SensitivityRank.values();
        }
    }
}

