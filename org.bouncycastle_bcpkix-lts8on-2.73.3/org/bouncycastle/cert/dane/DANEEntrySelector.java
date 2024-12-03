/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Selector
 */
package org.bouncycastle.cert.dane;

import org.bouncycastle.cert.dane.DANEEntry;
import org.bouncycastle.util.Selector;

public class DANEEntrySelector
implements Selector {
    private final String domainName;

    DANEEntrySelector(String domainName) {
        this.domainName = domainName;
    }

    public boolean match(Object obj) {
        DANEEntry dEntry = (DANEEntry)obj;
        return dEntry.getDomainName().equals(this.domainName);
    }

    public Object clone() {
        return this;
    }

    public String getDomainName() {
        return this.domainName;
    }
}

