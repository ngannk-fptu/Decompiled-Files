/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.util.Objects;
import org.apache.poi.sl.draw.geom.AdjustPoint;
import org.apache.poi.sl.draw.geom.AdjustPointIf;
import org.apache.poi.sl.draw.geom.ConnectionSiteIf;

public final class ConnectionSite
implements ConnectionSiteIf {
    private final AdjustPoint pos = new AdjustPoint();
    private String ang;

    @Override
    public AdjustPoint getPos() {
        return this.pos;
    }

    @Override
    public void setPos(AdjustPointIf pos) {
        if (pos != null) {
            this.pos.setX(pos.getX());
            this.pos.setY(pos.getY());
        }
    }

    @Override
    public String getAng() {
        return this.ang;
    }

    @Override
    public void setAng(String value) {
        this.ang = value;
    }

    @Override
    public boolean isSetAng() {
        return this.ang != null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConnectionSite)) {
            return false;
        }
        ConnectionSite that = (ConnectionSite)o;
        return Objects.equals(this.pos, that.pos) && Objects.equals(this.ang, that.ang);
    }

    public int hashCode() {
        return Objects.hash(this.pos, this.ang);
    }
}

