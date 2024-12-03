/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import org.apache.tools.ant.Location;

public class BuildException
extends RuntimeException {
    private static final long serialVersionUID = -5419014565354664240L;
    private Location location = Location.UNKNOWN_LOCATION;

    public static BuildException of(Throwable t) {
        if (t instanceof BuildException) {
            return (BuildException)t;
        }
        return new BuildException(t);
    }

    public BuildException() {
    }

    public BuildException(String message) {
        super(message);
    }

    public BuildException(String pattern, Object ... formatArguments) {
        super(String.format(pattern, formatArguments));
    }

    public BuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public BuildException(String message, Throwable cause, Location location) {
        this(message, cause);
        this.location = location;
    }

    public BuildException(Throwable cause) {
        super(cause);
    }

    public BuildException(String message, Location location) {
        super(message);
        this.location = location;
    }

    public BuildException(Throwable cause, Location location) {
        this(cause);
        this.location = location;
    }

    @Deprecated
    public Throwable getException() {
        return this.getCause();
    }

    @Override
    public String toString() {
        return this.location.toString() + this.getMessage();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }
}

