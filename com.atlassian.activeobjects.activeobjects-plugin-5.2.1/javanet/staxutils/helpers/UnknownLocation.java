/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.helpers;

import javanet.staxutils.StaticLocation;
import javax.xml.stream.Location;

public final class UnknownLocation
implements Location,
StaticLocation {
    public static final UnknownLocation INSTANCE = new UnknownLocation();

    public int getLineNumber() {
        return -1;
    }

    public int getColumnNumber() {
        return -1;
    }

    public int getCharacterOffset() {
        return -1;
    }

    public String getPublicId() {
        return null;
    }

    public String getSystemId() {
        return null;
    }
}

