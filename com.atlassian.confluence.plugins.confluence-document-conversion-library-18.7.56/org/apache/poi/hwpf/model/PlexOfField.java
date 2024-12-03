/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Locale;
import org.apache.poi.hwpf.model.FieldDescriptor;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.util.Internal;

@Internal
public class PlexOfField {
    private final GenericPropertyNode propertyNode;
    private final FieldDescriptor fld;

    @Deprecated
    public PlexOfField(int fcStart, int fcEnd, byte[] data) {
        this.propertyNode = new GenericPropertyNode(fcStart, fcEnd, data);
        this.fld = new FieldDescriptor(data);
    }

    public PlexOfField(GenericPropertyNode propertyNode) {
        this.propertyNode = propertyNode;
        this.fld = new FieldDescriptor(propertyNode.getBytes());
    }

    public int getFcStart() {
        return this.propertyNode.getStart();
    }

    public int getFcEnd() {
        return this.propertyNode.getEnd();
    }

    public FieldDescriptor getFld() {
        return this.fld;
    }

    public String toString() {
        return String.format(Locale.ROOT, "[%d, %d) - FLD - 0x%x; 0x%x", this.getFcStart(), this.getFcEnd(), this.fld.getBoundaryType(), this.fld.getFlt());
    }
}

