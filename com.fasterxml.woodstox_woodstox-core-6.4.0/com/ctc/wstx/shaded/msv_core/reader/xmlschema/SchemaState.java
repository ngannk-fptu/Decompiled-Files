/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.SchemaIncludedState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;

public class SchemaState
extends SchemaIncludedState {
    private XMLSchemaSchema old;

    protected SchemaState(String expectedTargetNamespace) {
        super(expectedTargetNamespace);
    }

    protected void onTargetNamespaceResolved(String targetNs, boolean ignoreContents) {
        super.onTargetNamespaceResolved(targetNs, ignoreContents);
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        this.old = reader.currentSchema;
        reader.currentSchema = reader.getOrCreateSchema(targetNs);
        if (ignoreContents) {
            return;
        }
        if (reader.isSchemaDefined(reader.currentSchema)) {
            reader.reportError("XMLSchemaReader.DuplicateSchemaDefinition", (Object)targetNs);
            reader.currentSchema = new XMLSchemaSchema(targetNs, reader.grammar);
        }
        reader.markSchemaAsDefined(reader.currentSchema);
    }

    protected void endSelf() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        reader.currentSchema = this.old;
        super.endSelf();
    }
}

