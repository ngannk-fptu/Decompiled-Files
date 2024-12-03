/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.holders;

import javax.xml.rpc.holders.Holder;
import org.apache.axis.types.Schema;

public final class SchemaHolder
implements Holder {
    public Schema value;

    public SchemaHolder() {
    }

    public SchemaHolder(Schema value) {
        this.value = value;
    }
}

