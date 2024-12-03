/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.RootIncludedSchemaState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.Util;
import javax.xml.transform.Source;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.LocatorImpl;

public class MultiSchemaReader {
    private final XMLSchemaReader reader;
    private boolean finalized = false;

    public MultiSchemaReader(XMLSchemaReader _reader) {
        this.reader = _reader;
        this.reader.setDocumentLocator(new LocatorImpl());
    }

    public final XMLSchemaReader getReader() {
        return this.reader;
    }

    public final XMLSchemaGrammar getResult() {
        this.finish();
        return this.reader.getResult();
    }

    public void parse(Source source) {
        this.reader.switchSource(source, (State)new RootIncludedSchemaState(this.reader.sfactory.schemaHead(null)));
    }

    public final void parse(String source) {
        this.parse(Util.getInputSource(source));
    }

    public void parse(InputSource is) {
        this.reader.switchSource(is, (State)new RootIncludedSchemaState(this.reader.sfactory.schemaHead(null)));
    }

    public void finish() {
        if (!this.finalized) {
            this.finalized = true;
            this.reader.wrapUp();
        }
    }
}

