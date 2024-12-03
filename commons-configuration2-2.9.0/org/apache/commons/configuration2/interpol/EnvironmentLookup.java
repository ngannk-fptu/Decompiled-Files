/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.interpol;

import org.apache.commons.configuration2.interpol.Lookup;

@Deprecated
public class EnvironmentLookup
implements Lookup {
    @Override
    public String lookup(String key) {
        return System.getenv(key);
    }
}

