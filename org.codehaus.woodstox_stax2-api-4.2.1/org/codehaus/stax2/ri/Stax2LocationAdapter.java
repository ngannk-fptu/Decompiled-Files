/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import javax.xml.stream.Location;
import org.codehaus.stax2.XMLStreamLocation2;

public class Stax2LocationAdapter
implements XMLStreamLocation2 {
    protected final Location mWrappedLocation;
    protected final Location mParentLocation;

    public Stax2LocationAdapter(Location loc) {
        this(loc, null);
    }

    public Stax2LocationAdapter(Location loc, Location parent) {
        this.mWrappedLocation = loc;
        this.mParentLocation = parent;
    }

    @Override
    public int getCharacterOffset() {
        return this.mWrappedLocation.getCharacterOffset();
    }

    @Override
    public int getColumnNumber() {
        return this.mWrappedLocation.getColumnNumber();
    }

    @Override
    public int getLineNumber() {
        return this.mWrappedLocation.getLineNumber();
    }

    @Override
    public String getPublicId() {
        return this.mWrappedLocation.getPublicId();
    }

    @Override
    public String getSystemId() {
        return this.mWrappedLocation.getSystemId();
    }

    @Override
    public XMLStreamLocation2 getContext() {
        if (this.mParentLocation == null) {
            return null;
        }
        if (this.mParentLocation instanceof XMLStreamLocation2) {
            return (XMLStreamLocation2)this.mParentLocation;
        }
        return new Stax2LocationAdapter(this.mParentLocation);
    }
}

