/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import org.xml.sax.Attributes;

public abstract class Substitutor {
    public abstract Attributes substitute(Attributes var1);

    public abstract String substitute(String var1);
}

