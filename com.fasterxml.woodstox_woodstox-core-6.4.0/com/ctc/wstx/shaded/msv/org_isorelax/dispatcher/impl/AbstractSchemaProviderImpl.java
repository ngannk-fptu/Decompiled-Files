/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractSchemaProviderImpl
implements SchemaProvider {
    protected final Map schemata = new HashMap();

    public void addSchema(String s, IslandSchema islandschema) {
        if (this.schemata.containsKey(s)) {
            throw new IllegalArgumentException();
        }
        this.schemata.put(s, islandschema);
    }

    public IslandSchema getSchemaByNamespace(String s) {
        return (IslandSchema)this.schemata.get(s);
    }

    public Iterator iterateNamespace() {
        return this.schemata.keySet().iterator();
    }

    public IslandSchema[] getSchemata() {
        IslandSchema[] aislandschema = new IslandSchema[this.schemata.size()];
        this.schemata.values().toArray(aislandschema);
        return aislandschema;
    }
}

