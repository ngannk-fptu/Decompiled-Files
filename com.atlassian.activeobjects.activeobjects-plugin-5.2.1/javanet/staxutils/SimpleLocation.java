/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import javanet.staxutils.ExtendedLocation;
import javanet.staxutils.StaticLocation;
import javax.xml.stream.Location;

public class SimpleLocation
implements ExtendedLocation,
StaticLocation {
    private Location nestedLocation;
    private int lineNumber = -1;
    private int characterOffset = -1;
    private int columnNumber = -1;
    private String publicId;
    private String systemId;

    public SimpleLocation(String publicId, String systemId, int lineNumber, Location nestedLocation) {
        this.publicId = publicId;
        this.systemId = systemId;
        this.lineNumber = lineNumber;
        this.nestedLocation = nestedLocation;
    }

    public SimpleLocation(String publicId, String systemId, int lineNumber, int columnNumber, Location nestedLocation) {
        this.publicId = publicId;
        this.systemId = systemId;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.nestedLocation = nestedLocation;
    }

    public SimpleLocation(String publicId, String systemId, int lineNumber, int columnNumber, int characterOffset, Location nestedLocation) {
        this.publicId = publicId;
        this.systemId = systemId;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.characterOffset = characterOffset;
        this.nestedLocation = nestedLocation;
    }

    public SimpleLocation(Location loc) {
        this.publicId = loc.getPublicId();
        this.systemId = loc.getSystemId();
        this.lineNumber = loc.getLineNumber();
        this.columnNumber = loc.getColumnNumber();
        this.characterOffset = loc.getCharacterOffset();
        if (loc instanceof ExtendedLocation) {
            this.nestedLocation = ((ExtendedLocation)loc).getNestedLocation();
        }
    }

    public int getCharacterOffset() {
        return this.characterOffset;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public Location getNestedLocation() {
        return this.nestedLocation;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        String publicId = this.getPublicId();
        String systemId = this.getSystemId();
        if (publicId != null) {
            buffer.append(publicId);
            if (systemId != null) {
                buffer.append("#").append(systemId);
            }
        } else if (systemId != null) {
            buffer.append(publicId);
        }
        buffer.append('[');
        buffer.append("line=").append(this.getLineNumber());
        buffer.append("column=").append(this.getColumnNumber());
        buffer.append(']');
        Location nested = this.getNestedLocation();
        if (nested != null) {
            buffer.append("->");
            buffer.append(nested);
        }
        return buffer.toString();
    }
}

