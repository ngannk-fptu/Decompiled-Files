/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.interpol;

import org.apache.commons.configuration2.interpol.Lookup;

public enum DummyLookup implements Lookup
{
    INSTANCE;


    @Override
    public Object lookup(String variable) {
        return null;
    }
}

