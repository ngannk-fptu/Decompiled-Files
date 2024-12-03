/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.util.HashMap;
import java.util.Map;

public abstract class CFFCharset {
    private final boolean isCIDFont;
    private final Map<Integer, Integer> sidOrCidToGid = new HashMap<Integer, Integer>(250);
    private final Map<Integer, Integer> gidToSid = new HashMap<Integer, Integer>(250);
    private final Map<String, Integer> nameToSid = new HashMap<String, Integer>(250);
    private final Map<Integer, Integer> gidToCid = new HashMap<Integer, Integer>();
    private final Map<Integer, String> gidToName = new HashMap<Integer, String>(250);

    CFFCharset(boolean isCIDFont) {
        this.isCIDFont = isCIDFont;
    }

    public boolean isCIDFont() {
        return this.isCIDFont;
    }

    public void addSID(int gid, int sid, String name) {
        if (this.isCIDFont) {
            throw new IllegalStateException("Not a Type 1-equivalent font");
        }
        this.sidOrCidToGid.put(sid, gid);
        this.gidToSid.put(gid, sid);
        this.nameToSid.put(name, sid);
        this.gidToName.put(gid, name);
    }

    public void addCID(int gid, int cid) {
        if (!this.isCIDFont) {
            throw new IllegalStateException("Not a CIDFont");
        }
        this.sidOrCidToGid.put(cid, gid);
        this.gidToCid.put(gid, cid);
    }

    int getSIDForGID(int gid) {
        if (this.isCIDFont) {
            throw new IllegalStateException("Not a Type 1-equivalent font");
        }
        Integer sid = this.gidToSid.get(gid);
        if (sid == null) {
            return 0;
        }
        return sid;
    }

    int getGIDForSID(int sid) {
        if (this.isCIDFont) {
            throw new IllegalStateException("Not a Type 1-equivalent font");
        }
        Integer gid = this.sidOrCidToGid.get(sid);
        if (gid == null) {
            return 0;
        }
        return gid;
    }

    public int getGIDForCID(int cid) {
        if (!this.isCIDFont) {
            throw new IllegalStateException("Not a CIDFont");
        }
        Integer gid = this.sidOrCidToGid.get(cid);
        if (gid == null) {
            return 0;
        }
        return gid;
    }

    int getSID(String name) {
        if (this.isCIDFont) {
            throw new IllegalStateException("Not a Type 1-equivalent font");
        }
        Integer sid = this.nameToSid.get(name);
        if (sid == null) {
            return 0;
        }
        return sid;
    }

    public String getNameForGID(int gid) {
        if (this.isCIDFont) {
            throw new IllegalStateException("Not a Type 1-equivalent font");
        }
        return this.gidToName.get(gid);
    }

    public int getCIDForGID(int gid) {
        if (!this.isCIDFont) {
            throw new IllegalStateException("Not a CIDFont");
        }
        Integer cid = this.gidToCid.get(gid);
        if (cid != null) {
            return cid;
        }
        return 0;
    }
}

