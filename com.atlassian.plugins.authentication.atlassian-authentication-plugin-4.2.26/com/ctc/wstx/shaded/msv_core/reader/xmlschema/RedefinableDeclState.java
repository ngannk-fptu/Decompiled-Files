/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.GlobalDeclState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.RedefineState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;

public abstract class RedefinableDeclState
extends ExpressionWithChildState {
    protected RedefinableExp oldDecl;

    protected boolean isGlobal() {
        return this.parentState instanceof GlobalDeclState;
    }

    protected boolean isRedefine() {
        return this.parentState instanceof RedefineState;
    }

    protected abstract ReferenceContainer getContainer();

    protected void startSelf() {
        super.startSelf();
        if (this.isRedefine()) {
            XMLSchemaReader reader = (XMLSchemaReader)this.reader;
            String name = this.startTag.getAttribute("name");
            if (name == null) {
                return;
            }
            this.oldDecl = (RedefinableExp)this.getContainer()._get(name);
            if (this.oldDecl == null) {
                reader.reportError("XMLSchemaReader.RedefineUndefined", (Object)name);
                this.oldDecl = (RedefinableExp)this.getContainer()._getOrCreate(name);
                return;
            }
            this.getContainer().redefine(name, this.oldDecl.getClone());
        }
    }

    protected void endSelf() {
        if (this.oldDecl != null) {
            this.getContainer().redefine(this.oldDecl.name, this.oldDecl);
        }
        super.endSelf();
    }
}

