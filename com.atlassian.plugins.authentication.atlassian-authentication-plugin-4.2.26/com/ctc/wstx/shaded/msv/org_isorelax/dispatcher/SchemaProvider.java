/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import java.util.Iterator;

public interface SchemaProvider {
    public IslandVerifier createTopLevelVerifier();

    public IslandSchema getSchemaByNamespace(String var1);

    public Iterator iterateNamespace();

    public IslandSchema[] getSchemata();
}

