/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.io.WstxInputLocation;
import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;

public abstract class WstxInputSource {
    protected final WstxInputSource mParent;
    protected final String mFromEntity;
    protected int mScopeId = 0;
    protected int mEntityDepth;

    protected WstxInputSource(WstxInputSource parent, String fromEntity) {
        this.mParent = parent;
        this.mFromEntity = fromEntity;
    }

    public abstract void overrideSource(URL var1);

    public final WstxInputSource getParent() {
        return this.mParent;
    }

    public boolean isOrIsExpandedFrom(String entityId) {
        if (entityId != null) {
            WstxInputSource curr = this;
            while (curr != null) {
                if (entityId == curr.mFromEntity) {
                    return true;
                }
                curr = curr.mParent;
            }
        }
        return false;
    }

    public abstract boolean fromInternalEntity();

    public abstract URL getSource() throws IOException;

    public abstract String getPublicId();

    public abstract String getSystemId();

    protected abstract WstxInputLocation getLocation();

    public abstract WstxInputLocation getLocation(long var1, int var3, int var4);

    public String getEntityId() {
        return this.mFromEntity;
    }

    public int getScopeId() {
        return this.mScopeId;
    }

    public int getEntityDepth() {
        return this.mEntityDepth;
    }

    public final void initInputLocation(WstxInputData reader, int currScopeId, int entityDepth) {
        this.mScopeId = currScopeId;
        this.mEntityDepth = entityDepth;
        this.doInitInputLocation(reader);
    }

    protected abstract void doInitInputLocation(WstxInputData var1);

    public abstract int readInto(WstxInputData var1) throws IOException, XMLStreamException;

    public abstract boolean readMore(WstxInputData var1, int var2) throws IOException, XMLStreamException;

    public abstract void saveContext(WstxInputData var1);

    public abstract void restoreContext(WstxInputData var1);

    public abstract void close() throws IOException;

    public abstract void closeCompletely() throws IOException;

    public String toString() {
        StringBuffer sb = new StringBuffer(80);
        sb.append("<WstxInputSource [class ");
        sb.append(this.getClass().toString());
        sb.append("]; systemId: ");
        sb.append(this.getSystemId());
        sb.append(", source: ");
        try {
            URL url = this.getSource();
            sb.append(url.toString());
        }
        catch (IOException e) {
            sb.append("[ERROR: " + e.getMessage() + "]");
        }
        sb.append('>');
        return sb.toString();
    }
}

