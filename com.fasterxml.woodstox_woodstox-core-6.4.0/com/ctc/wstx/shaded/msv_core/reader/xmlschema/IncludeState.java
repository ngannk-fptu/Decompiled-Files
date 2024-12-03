/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.RootIncludedSchemaState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;

public class IncludeState
extends ChildlessState {
    protected void startSelf() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        super.startSelf();
        try {
            reader.switchSource(this, (State)new RootIncludedSchemaState(reader.sfactory.schemaIncluded(this, reader.currentSchema.targetNamespace)));
        }
        catch (AbortException abortException) {
            // empty catch block
        }
    }
}

