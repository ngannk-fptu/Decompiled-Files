/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2;

import com.opensymphony.xwork2.util.location.Locatable;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;

public class StrutsException
extends RuntimeException
implements Locatable {
    private Location location;

    public StrutsException() {
    }

    public StrutsException(String s) {
        this(s, null, null);
    }

    public StrutsException(String s, Object target) {
        this(s, null, target);
    }

    public StrutsException(Throwable cause) {
        this(null, cause, null);
    }

    public StrutsException(Throwable cause, Object target) {
        this(null, cause, target);
    }

    public StrutsException(String s, Throwable cause) {
        this(s, cause, null);
    }

    public StrutsException(String s, Throwable cause, Object target) {
        super(s, cause);
        this.location = LocationUtils.getLocation(target);
        if (this.location == Location.UNKNOWN) {
            this.location = LocationUtils.getLocation(cause);
        }
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        String msg = this.getMessage();
        if (msg == null && this.getCause() != null) {
            msg = this.getCause().getMessage();
        }
        if (this.location != null) {
            if (msg != null) {
                return msg + " - " + this.location.toString();
            }
            return this.location.toString();
        }
        return msg;
    }
}

