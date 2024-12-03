/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.lookup.StringLookupFactory
 */
package org.apache.commons.configuration2.interpol;

import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.text.lookup.StringLookupFactory;

@Deprecated
public class SystemPropertiesLookup
implements Lookup {
    @Override
    public Object lookup(String variable) {
        return StringLookupFactory.INSTANCE.systemPropertyStringLookup().lookup(variable);
    }
}

