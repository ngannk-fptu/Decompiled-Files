/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import org.xml.sax.ContentHandler;

public interface IslandSchemaReader
extends ContentHandler {
    public IslandSchema getSchema();
}

