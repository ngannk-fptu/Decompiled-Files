/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import javax.xml.stream.Location;
import org.apache.xerces.xni.XMLLocator;

public final class StAXLocationWrapper
implements XMLLocator {
    private Location fLocation = null;

    public void setLocation(Location location) {
        this.fLocation = location;
    }

    public Location getLocation() {
        return this.fLocation;
    }

    @Override
    public String getPublicId() {
        if (this.fLocation != null) {
            return this.fLocation.getPublicId();
        }
        return null;
    }

    @Override
    public String getLiteralSystemId() {
        if (this.fLocation != null) {
            return this.fLocation.getSystemId();
        }
        return null;
    }

    @Override
    public String getBaseSystemId() {
        return null;
    }

    @Override
    public String getExpandedSystemId() {
        return this.getLiteralSystemId();
    }

    @Override
    public int getLineNumber() {
        if (this.fLocation != null) {
            return this.fLocation.getLineNumber();
        }
        return -1;
    }

    @Override
    public int getColumnNumber() {
        if (this.fLocation != null) {
            return this.fLocation.getColumnNumber();
        }
        return -1;
    }

    @Override
    public int getCharacterOffset() {
        if (this.fLocation != null) {
            return this.fLocation.getCharacterOffset();
        }
        return -1;
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public String getXMLVersion() {
        return null;
    }
}

