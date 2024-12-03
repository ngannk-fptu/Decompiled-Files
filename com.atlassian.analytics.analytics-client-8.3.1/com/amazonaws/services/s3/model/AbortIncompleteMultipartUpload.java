/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import java.io.Serializable;

public class AbortIncompleteMultipartUpload
implements Serializable {
    private int daysAfterInitiation;

    public int getDaysAfterInitiation() {
        return this.daysAfterInitiation;
    }

    public void setDaysAfterInitiation(int daysAfterInitiation) {
        this.daysAfterInitiation = daysAfterInitiation;
    }

    public AbortIncompleteMultipartUpload withDaysAfterInitiation(int daysAfterInitiation) {
        this.setDaysAfterInitiation(daysAfterInitiation);
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbortIncompleteMultipartUpload that = (AbortIncompleteMultipartUpload)o;
        return this.daysAfterInitiation == that.daysAfterInitiation;
    }

    public int hashCode() {
        return this.daysAfterInitiation;
    }

    protected AbortIncompleteMultipartUpload clone() throws CloneNotSupportedException {
        try {
            return (AbortIncompleteMultipartUpload)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

