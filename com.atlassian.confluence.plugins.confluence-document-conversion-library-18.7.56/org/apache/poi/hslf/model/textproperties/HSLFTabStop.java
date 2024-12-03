/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model.textproperties;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.sl.usermodel.TabStop;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;

@Internal
public class HSLFTabStop
implements TabStop,
Duplicatable,
GenericRecord {
    private int position;
    private TabStop.TabStopType type;

    public HSLFTabStop(int position, TabStop.TabStopType type) {
        this.position = position;
        this.type = type;
    }

    public HSLFTabStop(HSLFTabStop other) {
        this.position = other.position;
        this.type = other.type;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public double getPositionInPoints() {
        return Units.masterToPoints(this.getPosition());
    }

    @Override
    public void setPositionInPoints(double points) {
        this.setPosition(Units.pointsToMaster(points));
    }

    @Override
    public TabStop.TabStopType getType() {
        return this.type;
    }

    @Override
    public void setType(TabStop.TabStopType type) {
        this.type = type;
    }

    @Override
    public HSLFTabStop copy() {
        return new HSLFTabStop(this);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.position, this.type});
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HSLFTabStop)) {
            return false;
        }
        HSLFTabStop other = (HSLFTabStop)obj;
        if (this.position != other.position) {
            return false;
        }
        return this.type == other.type;
    }

    public String toString() {
        return (Object)((Object)this.type) + " @ " + this.position;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("type", this::getType, "position", this::getPosition);
    }
}

