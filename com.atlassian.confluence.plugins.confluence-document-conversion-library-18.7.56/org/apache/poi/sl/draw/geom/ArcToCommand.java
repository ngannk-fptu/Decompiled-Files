/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.util.Objects;
import org.apache.poi.sl.draw.geom.ArcToCommandIf;
import org.apache.poi.util.Internal;

public class ArcToCommand
implements ArcToCommandIf {
    private String wr;
    private String hr;
    private String stAng;
    private String swAng;

    @Override
    public void setHR(String hr) {
        this.hr = hr;
    }

    @Override
    public String getHR() {
        return this.hr;
    }

    @Override
    public String getStAng() {
        return this.stAng;
    }

    @Override
    public String getWR() {
        return this.wr;
    }

    @Override
    public void setWR(String wr) {
        this.wr = wr;
    }

    @Override
    public void setStAng(String stAng) {
        this.stAng = stAng;
    }

    @Override
    public String getSwAng() {
        return this.swAng;
    }

    @Override
    public void setSwAng(String swAng) {
        this.swAng = swAng;
    }

    @Internal
    public static double convertOoxml2AwtAngle(double ooAngle, double width, double height) {
        double aspect = height / width;
        double awtAngle = -ooAngle;
        double awtAngle2 = awtAngle % 360.0;
        double awtAngle3 = awtAngle - awtAngle2;
        switch ((int)(awtAngle2 / 90.0)) {
            case -3: {
                awtAngle3 -= 360.0;
                awtAngle2 += 360.0;
                break;
            }
            case -2: 
            case -1: {
                awtAngle3 -= 180.0;
                awtAngle2 += 180.0;
                break;
            }
            default: {
                break;
            }
            case 1: 
            case 2: {
                awtAngle3 += 180.0;
                awtAngle2 -= 180.0;
                break;
            }
            case 3: {
                awtAngle3 += 360.0;
                awtAngle2 -= 360.0;
            }
        }
        awtAngle = Math.toDegrees(Math.atan2(Math.tan(Math.toRadians(awtAngle2)), aspect)) + awtAngle3;
        return awtAngle;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArcToCommand)) {
            return false;
        }
        ArcToCommand that = (ArcToCommand)o;
        return Objects.equals(this.wr, that.wr) && Objects.equals(this.hr, that.hr) && Objects.equals(this.stAng, that.stAng) && Objects.equals(this.swAng, that.swAng);
    }

    public int hashCode() {
        return Objects.hash(this.wr, this.hr, this.stAng, this.swAng);
    }
}

