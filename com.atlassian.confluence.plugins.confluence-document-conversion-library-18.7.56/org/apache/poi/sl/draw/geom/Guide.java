/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.util.Objects;
import org.apache.poi.sl.draw.geom.GuideIf;

public class Guide
implements GuideIf {
    private String name;
    private String fmla;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFmla() {
        return this.fmla;
    }

    @Override
    public void setFmla(String fmla) {
        this.fmla = fmla;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Guide guide = (Guide)o;
        return Objects.equals(this.name, guide.name) && Objects.equals(this.fmla, guide.fmla);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.fmla);
    }
}

