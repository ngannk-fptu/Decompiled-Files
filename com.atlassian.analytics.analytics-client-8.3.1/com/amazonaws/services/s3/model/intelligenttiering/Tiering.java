/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.intelligenttiering;

import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringAccessTier;
import java.io.Serializable;
import java.util.Objects;

public class Tiering
implements Serializable {
    private Integer days;
    private IntelligentTieringAccessTier accessTier;

    public Integer getDays() {
        return this.days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Tiering withDays(Integer days) {
        this.setDays(days);
        return this;
    }

    public IntelligentTieringAccessTier getAccessTier() {
        return this.accessTier;
    }

    public void setAccessTier(IntelligentTieringAccessTier accessTier) {
        this.accessTier = accessTier;
    }

    public Tiering withIntelligentTieringAccessTier(IntelligentTieringAccessTier intelligentTieringAccessTier) {
        this.setAccessTier(intelligentTieringAccessTier);
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Tiering tiering = (Tiering)o;
        return Objects.equals(this.days, tiering.days) && this.accessTier == tiering.accessTier;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.days, this.accessTier});
    }
}

