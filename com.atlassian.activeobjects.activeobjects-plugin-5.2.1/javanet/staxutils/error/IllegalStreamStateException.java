/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.error;

import javax.xml.stream.Location;

public class IllegalStreamStateException
extends IllegalStateException {
    private Location location;

    public IllegalStreamStateException() {
    }

    public IllegalStreamStateException(Location location) {
        this.location = location;
    }

    public IllegalStreamStateException(String s) {
        super(s);
    }

    public IllegalStreamStateException(String s, Location location) {
        super(s);
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

