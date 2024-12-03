/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing;

import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.sharing.RemoveType;
import org.bedework.caldav.util.sharing.SetType;
import org.bedework.util.misc.ToString;

public class ShareType {
    private List<SetType> set;
    private List<RemoveType> remove;

    public List<SetType> getSet() {
        if (this.set == null) {
            this.set = new ArrayList<SetType>();
        }
        return this.set;
    }

    public List<RemoveType> getRemove() {
        if (this.remove == null) {
            this.remove = new ArrayList<RemoveType>();
        }
        return this.remove;
    }

    protected void toStringSegment(ToString ts) {
        ts.append("set", this.getSet());
        ts.append("remove", this.getRemove());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

