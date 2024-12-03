/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.entity;

import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.EffectiveCoverageLevel;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AuditCoverageConfig {
    @Nonnull
    private final Map<CoverageArea, EffectiveCoverageLevel> levelByArea;

    public AuditCoverageConfig(@Nonnull Map<CoverageArea, EffectiveCoverageLevel> levelByArea) {
        this.levelByArea = levelByArea;
    }

    @Nonnull
    public Map<CoverageArea, EffectiveCoverageLevel> getLevelByArea() {
        return this.levelByArea;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditCoverageConfig that = (AuditCoverageConfig)o;
        return this.levelByArea.equals(that.levelByArea);
    }

    public int hashCode() {
        return Objects.hash(this.levelByArea);
    }
}

