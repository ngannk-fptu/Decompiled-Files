/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing;

import java.util.ArrayList;
import java.util.List;
import org.bedework.util.misc.ToString;

public class ShareResultType {
    private List<String> goodSharees = new ArrayList<String>();
    private List<String> badSharees = new ArrayList<String>();

    public List<String> getGoodSharees() {
        if (this.goodSharees == null) {
            this.goodSharees = new ArrayList<String>();
        }
        return this.goodSharees;
    }

    public List<String> getBadSharees() {
        if (this.badSharees == null) {
            this.badSharees = new ArrayList<String>();
        }
        return this.badSharees;
    }

    public void addGood(String val) {
        this.getGoodSharees().add(val);
    }

    public void addBad(String val) {
        this.getBadSharees().add(val);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("good", this.getGoodSharees());
        ts.append("bad", this.getBadSharees());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

