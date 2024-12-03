/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model.textproperties;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.poi.hslf.model.textproperties.HSLFTabStop;
import org.apache.poi.hslf.model.textproperties.TextProp;
import org.apache.poi.sl.usermodel.TabStop;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianOutputStream;

@Internal
public class HSLFTabStopPropCollection
extends TextProp {
    public static final String NAME = "tabStops";
    private final List<HSLFTabStop> tabStops = new ArrayList<HSLFTabStop>();

    public HSLFTabStopPropCollection() {
        super(0, 0x100000, NAME);
    }

    public HSLFTabStopPropCollection(HSLFTabStopPropCollection other) {
        super(other);
        other.tabStops.stream().map(HSLFTabStop::copy).forEach(this.tabStops::add);
    }

    public void parseProperty(byte[] data, int offset) {
        this.tabStops.addAll(HSLFTabStopPropCollection.readTabStops(new LittleEndianByteArrayInputStream(data, offset)));
    }

    public static List<HSLFTabStop> readTabStops(LittleEndianInput lei) {
        int count = lei.readUShort();
        ArrayList<HSLFTabStop> tabs = new ArrayList<HSLFTabStop>(count);
        for (int i = 0; i < count; ++i) {
            short position = lei.readShort();
            TabStop.TabStopType type = TabStop.TabStopType.fromNativeId(lei.readShort());
            tabs.add(new HSLFTabStop(position, type));
        }
        return tabs;
    }

    public void writeProperty(OutputStream out) {
        HSLFTabStopPropCollection.writeTabStops(new LittleEndianOutputStream(out), this.tabStops);
    }

    public static void writeTabStops(LittleEndianOutput leo, List<HSLFTabStop> tabStops) {
        int count = tabStops.size();
        leo.writeShort(count);
        for (HSLFTabStop ts : tabStops) {
            leo.writeShort(ts.getPosition());
            leo.writeShort(ts.getType().nativeId);
        }
    }

    @Override
    public int getValue() {
        return this.tabStops.size();
    }

    @Override
    public int getSize() {
        return 2 + this.tabStops.size() * 4;
    }

    public List<HSLFTabStop> getTabStops() {
        return this.tabStops;
    }

    public void clearTabs() {
        this.tabStops.clear();
    }

    public void addTabStop(HSLFTabStop ts) {
        this.tabStops.add(ts);
    }

    @Override
    public HSLFTabStopPropCollection copy() {
        return new HSLFTabStopPropCollection(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.tabStops);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HSLFTabStopPropCollection)) {
            return false;
        }
        HSLFTabStopPropCollection other = (HSLFTabStopPropCollection)obj;
        if (!super.equals(other)) {
            return false;
        }
        return this.tabStops.equals(other.tabStops);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(" [ ");
        boolean isFirst = true;
        for (HSLFTabStop tabStop : this.tabStops) {
            if (!isFirst) {
                sb.append(", ");
            }
            sb.append((Object)tabStop.getType());
            sb.append(" @ ");
            sb.append(tabStop.getPosition());
            isFirst = false;
        }
        sb.append(" ]");
        return sb.toString();
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), NAME, this::getTabStops);
    }
}

