/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.Location;

public final class ElementId {
    private boolean mDefined;
    private final String mIdValue;
    private Location mLocation;
    private PrefixedName mElemName;
    private PrefixedName mAttrName;
    private ElementId mNextUndefined;
    private ElementId mNextColl;

    ElementId(String id, Location loc, boolean defined, PrefixedName elemName, PrefixedName attrName) {
        this.mIdValue = id;
        this.mLocation = loc;
        this.mDefined = defined;
        this.mElemName = elemName;
        this.mAttrName = attrName;
    }

    protected void linkUndefined(ElementId undefined) {
        if (this.mNextUndefined != null) {
            throw new IllegalStateException("ElementId '" + this + "' already had net undefined set ('" + this.mNextUndefined + "')");
        }
        this.mNextUndefined = undefined;
    }

    protected void setNextColliding(ElementId nextColl) {
        this.mNextColl = nextColl;
    }

    public String getId() {
        return this.mIdValue;
    }

    public Location getLocation() {
        return this.mLocation;
    }

    public PrefixedName getElemName() {
        return this.mElemName;
    }

    public PrefixedName getAttrName() {
        return this.mAttrName;
    }

    public boolean isDefined() {
        return this.mDefined;
    }

    public boolean idMatches(char[] buf, int start, int len) {
        if (this.mIdValue.length() != len) {
            return false;
        }
        if (buf[start] != this.mIdValue.charAt(0)) {
            return false;
        }
        int i = 1;
        len += start;
        while (++start < len) {
            if (buf[start] != this.mIdValue.charAt(i)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public boolean idMatches(String idStr) {
        return this.mIdValue.equals(idStr);
    }

    public ElementId nextUndefined() {
        return this.mNextUndefined;
    }

    public ElementId nextColliding() {
        return this.mNextColl;
    }

    public void markDefined(Location defLoc) {
        if (this.mDefined) {
            throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
        }
        this.mDefined = true;
        this.mLocation = defLoc;
    }

    public String toString() {
        return this.mIdValue;
    }
}

