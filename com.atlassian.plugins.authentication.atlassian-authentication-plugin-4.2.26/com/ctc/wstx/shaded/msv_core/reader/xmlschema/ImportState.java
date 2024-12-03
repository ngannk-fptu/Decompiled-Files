/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.RootIncludedSchemaState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;

public class ImportState
extends ChildlessState {
    protected void startSelf() {
        super.startSelf();
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        String namespace = this.startTag.getAttribute("namespace");
        if (namespace == null) {
            namespace = "";
        }
        if (namespace.equals(reader.currentSchema.targetNamespace)) {
            reader.reportError("XMLSchemaReader.ImportingSameNamespace");
            return;
        }
        if (reader.isSchemaDefined(reader.getOrCreateSchema(namespace))) {
            return;
        }
        try {
            reader.switchSource(this, (State)new RootIncludedSchemaState(reader.sfactory.schemaHead(namespace)));
        }
        catch (AbortException abortException) {
            // empty catch block
        }
    }
}

