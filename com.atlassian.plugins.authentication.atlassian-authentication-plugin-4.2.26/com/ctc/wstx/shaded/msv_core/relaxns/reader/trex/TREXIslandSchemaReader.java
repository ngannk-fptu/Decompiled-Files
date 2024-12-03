/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.reader.trex;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchemaReader;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.trex.TREXIslandSchema;
import org.xml.sax.helpers.XMLFilterImpl;

public class TREXIslandSchemaReader
extends XMLFilterImpl
implements IslandSchemaReader {
    private final TREXGrammarReader baseReader;

    public TREXIslandSchemaReader(TREXGrammarReader baseReader) {
        this.baseReader = baseReader;
        this.setContentHandler(baseReader);
    }

    public final IslandSchema getSchema() {
        TREXGrammar g = this.baseReader.getResult();
        if (g == null) {
            return null;
        }
        return new TREXIslandSchema(g);
    }
}

