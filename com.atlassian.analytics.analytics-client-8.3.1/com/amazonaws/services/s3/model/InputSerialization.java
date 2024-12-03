/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.CSVInput;
import com.amazonaws.services.s3.model.CompressionType;
import com.amazonaws.services.s3.model.JSONInput;
import com.amazonaws.services.s3.model.ParquetInput;
import java.io.Serializable;

public class InputSerialization
implements Serializable,
Cloneable {
    private CSVInput csv;
    private JSONInput json;
    private ParquetInput parquet;
    private String compressionType;

    public CSVInput getCsv() {
        return this.csv;
    }

    public void setCsv(CSVInput csv) {
        this.csv = csv;
    }

    public InputSerialization withCsv(CSVInput csvInput) {
        this.setCsv(csvInput);
        return this;
    }

    public JSONInput getJson() {
        return this.json;
    }

    public void setJson(JSONInput json) {
        this.json = json;
    }

    public InputSerialization withJson(JSONInput json) {
        this.setJson(json);
        return this;
    }

    public ParquetInput getParquet() {
        return this.parquet;
    }

    public void setParquet(ParquetInput parquet) {
        this.parquet = parquet;
    }

    public InputSerialization withParquet(ParquetInput parquet) {
        this.setParquet(parquet);
        return this;
    }

    public String getCompressionType() {
        return this.compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public void setCompressionType(CompressionType compressionType) {
        this.setCompressionType(compressionType == null ? null : compressionType.toString());
    }

    public InputSerialization withCompressionType(String compressionType) {
        this.setCompressionType(compressionType);
        return this;
    }

    public InputSerialization withCompressionType(CompressionType compressionType) {
        this.setCompressionType(compressionType);
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof InputSerialization)) {
            return false;
        }
        InputSerialization other = (InputSerialization)obj;
        if (other.getCsv() == null ^ this.getCsv() == null) {
            return false;
        }
        if (other.getCsv() != null && !other.getCsv().equals(this.getCsv())) {
            return false;
        }
        if (other.getJson() == null ^ this.getJson() == null) {
            return false;
        }
        if (other.getJson() != null && !other.getJson().equals(this.getJson())) {
            return false;
        }
        if (other.getCompressionType() == null ^ this.getCompressionType() == null) {
            return false;
        }
        return other.getCompressionType() == null || other.getCompressionType().equals(this.getCompressionType());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getCsv() == null ? 0 : this.getCsv().hashCode());
        hashCode = 31 * hashCode + (this.getJson() == null ? 0 : this.getJson().hashCode());
        hashCode = 31 * hashCode + (this.getCompressionType() == null ? 0 : this.getCompressionType().hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCsv() != null) {
            sb.append("Csv: ").append(this.getCsv());
        }
        if (this.getJson() != null) {
            sb.append("Json: ").append(this.getJson());
        }
        if (this.getCompressionType() != null) {
            sb.append("CompressionType: ").append(this.getCompressionType());
        }
        sb.append("}");
        return sb.toString();
    }

    public InputSerialization clone() {
        try {
            return (InputSerialization)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

