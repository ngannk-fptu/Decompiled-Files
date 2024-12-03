/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.lookup.StringLookup
 */
package org.apache.commons.configuration2.interpol;

import java.util.Objects;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.text.lookup.StringLookup;

class StringLookupAdapter
implements Lookup {
    private final StringLookup stringLookup;

    StringLookupAdapter(StringLookup stringLookup) {
        this.stringLookup = Objects.requireNonNull(stringLookup, "stringLookup");
    }

    @Override
    public Object lookup(String key) {
        return this.stringLookup.lookup(key);
    }
}

