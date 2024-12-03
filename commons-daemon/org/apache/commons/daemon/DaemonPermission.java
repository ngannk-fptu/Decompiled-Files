/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.daemon;

import java.security.Permission;
import java.util.StringTokenizer;

public final class DaemonPermission
extends Permission {
    private static final long serialVersionUID = -8682149075879731987L;
    protected static final String CONTROL = "control";
    protected static final int TYPE_CONTROL = 1;
    protected static final String CONTROL_START = "start";
    protected static final String CONTROL_STOP = "stop";
    protected static final String CONTROL_SHUTDOWN = "shutdown";
    protected static final String CONTROL_RELOAD = "reload";
    protected static final int MASK_CONTROL_START = 1;
    protected static final int MASK_CONTROL_STOP = 2;
    protected static final int MASK_CONTROL_SHUTDOWN = 4;
    protected static final int MASK_CONTROL_RELOAD = 8;
    protected static final String WILDCARD = "*";
    private transient int type;
    private transient int mask;
    private transient String desc;

    public DaemonPermission(String target) throws IllegalArgumentException {
        super(target);
        if (target == null) {
            throw new IllegalArgumentException("Null permission name");
        }
        if (CONTROL.equalsIgnoreCase(target)) {
            this.type = 1;
            return;
        }
        throw new IllegalArgumentException("Invalid permission name \"" + target + "\" specified");
    }

    public DaemonPermission(String target, String actions) throws IllegalArgumentException {
        this(target);
        if (this.type == 1) {
            this.mask = this.createControlMask(actions);
        }
    }

    @Override
    public String getActions() {
        if (this.type == 1) {
            return this.createControlActions(this.mask);
        }
        return "";
    }

    @Override
    public int hashCode() {
        this.setupDescription();
        return this.desc.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof DaemonPermission)) {
            return false;
        }
        DaemonPermission that = (DaemonPermission)object;
        if (this.type != that.type) {
            return false;
        }
        return this.mask == that.mask;
    }

    @Override
    public boolean implies(Permission permission) {
        if (permission == this) {
            return true;
        }
        if (!(permission instanceof DaemonPermission)) {
            return false;
        }
        DaemonPermission that = (DaemonPermission)permission;
        if (this.type != that.type) {
            return false;
        }
        return (this.mask & that.mask) == that.mask;
    }

    @Override
    public String toString() {
        this.setupDescription();
        return this.desc;
    }

    private void setupDescription() {
        if (this.desc != null) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getName());
        buf.append('[');
        switch (this.type) {
            case 1: {
                buf.append(CONTROL);
                break;
            }
            default: {
                buf.append("UNKNOWN");
            }
        }
        buf.append(':');
        buf.append(this.getActions());
        buf.append(']');
        this.desc = buf.toString();
    }

    private int createControlMask(String actions) throws IllegalArgumentException {
        if (actions == null) {
            return 0;
        }
        int mask = 0;
        StringTokenizer tok = new StringTokenizer(actions, ",", false);
        while (tok.hasMoreTokens()) {
            String val = tok.nextToken().trim();
            if (WILDCARD.equals(val)) {
                return 15;
            }
            if (CONTROL_START.equalsIgnoreCase(val)) {
                mask |= 1;
                continue;
            }
            if (CONTROL_STOP.equalsIgnoreCase(val)) {
                mask |= 2;
                continue;
            }
            if (CONTROL_SHUTDOWN.equalsIgnoreCase(val)) {
                mask |= 4;
                continue;
            }
            if (CONTROL_RELOAD.equalsIgnoreCase(val)) {
                mask |= 8;
                continue;
            }
            throw new IllegalArgumentException("Invalid action name \"" + val + "\" specified");
        }
        return mask;
    }

    private String createControlActions(int mask) {
        StringBuilder buf = new StringBuilder();
        boolean sep = false;
        if ((mask & 1) == 1) {
            sep = true;
            buf.append(CONTROL_START);
        }
        if ((mask & 2) == 2) {
            if (sep) {
                buf.append(",");
            } else {
                sep = true;
            }
            buf.append(CONTROL_STOP);
        }
        if ((mask & 4) == 4) {
            if (sep) {
                buf.append(",");
            } else {
                sep = true;
            }
            buf.append(CONTROL_SHUTDOWN);
        }
        if ((mask & 8) == 8) {
            if (sep) {
                buf.append(",");
            } else {
                sep = true;
            }
            buf.append(CONTROL_RELOAD);
        }
        return buf.toString();
    }
}

