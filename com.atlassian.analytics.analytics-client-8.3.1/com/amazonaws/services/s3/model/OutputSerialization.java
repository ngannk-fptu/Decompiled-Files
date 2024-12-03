/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.CSVOutput;
import com.amazonaws.services.s3.model.JSONOutput;
import java.io.Serializable;

public class OutputSerialization
implements Serializable,
Cloneable {
    private CSVOutput csv;
    private JSONOutput json;

    public CSVOutput getCsv() {
        return this.csv;
    }

    public void setCsv(CSVOutput csv) {
        this.csv = csv;
    }

    public OutputSerialization withCsv(CSVOutput csvOutput) {
        this.setCsv(csvOutput);
        return this;
    }

    public JSONOutput getJson() {
        return this.json;
    }

    public void setJson(JSONOutput json) {
        this.json = json;
    }

    public OutputSerialization withJson(JSONOutput json) {
        this.setJson(json);
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof OutputSerialization)) {
            return false;
        }
        OutputSerialization other = (OutputSerialization)obj;
        if (other.getCsv() == null ^ this.getCsv() == null) {
            return false;
        }
        if (other.getCsv() != null && !other.getCsv().equals(this.getCsv())) {
            return false;
        }
        if (other.getJson() == null ^ this.getJson() == null) {
            return false;
        }
        return other.getJson() == null || other.getJson().equals(this.getJson());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getCsv() == null ? 0 : this.getCsv().hashCode());
        hashCode = 31 * hashCode + (this.getJson() == null ? 0 : this.getJson().hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCsv() != null) {
            sb.append("CSV: ").append(this.getCsv());
        }
        if (this.getJson() != null) {
            sb.append("JSON: ").append(this.getJson());
        }
        sb.append("}");
        return sb.toString();
    }

    public OutputSerialization clone() {
        try {
            return (OutputSerialization)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

