/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.RootIncludedSchemaState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;

public class RootState
extends RootIncludedSchemaState {
    protected RootState(State topLevelState) {
        super(topLevelState);
    }

    protected void endSelf() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        reader.wrapUp();
        super.endSelf();
    }
}

