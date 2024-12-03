/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.GlobalDeclState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.RootIncludedSchemaState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class RedefineState
extends GlobalDeclState {
    private SimpleTypeExp oldSimpleTypeExp;
    private boolean prevDuplicateCheck;

    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("simpleType")) {
            XMLSchemaReader reader = (XMLSchemaReader)this.reader;
            String name = tag.getAttribute("name");
            SimpleTypeExp sexp = reader.currentSchema.simpleTypes.get(name);
            if (sexp == null) {
                reader.reportError("XMLSchemaReader.RedefineUndefined", (Object)name);
                sexp = reader.currentSchema.simpleTypes.getOrCreate(name);
            }
            reader.currentSchema.simpleTypes.redefine(name, sexp.getClone());
            this.oldSimpleTypeExp = sexp;
        }
        return super.createChildState(tag);
    }

    protected void startSelf() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        super.startSelf();
        try {
            reader.switchSource(this, (State)new RootIncludedSchemaState(reader.sfactory.schemaIncluded(this, reader.currentSchema.targetNamespace)));
        }
        catch (AbortException abortException) {
            // empty catch block
        }
        this.prevDuplicateCheck = reader.doDuplicateDefinitionCheck;
    }

    protected void endSelf() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        reader.doDuplicateDefinitionCheck = this.prevDuplicateCheck;
        super.endSelf();
    }

    public void onEndChild(XSDatatypeExp type) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        String typeName = type.name;
        if (typeName == null) {
            reader.reportError("GrammarReader.MissingAttribute", (Object)"simpleType", (Object)"name");
            return;
        }
        this.oldSimpleTypeExp.set(type);
        reader.setDeclaredLocationOf(this.oldSimpleTypeExp);
        reader.currentSchema.simpleTypes.redefine(this.oldSimpleTypeExp.name, this.oldSimpleTypeExp);
        this.oldSimpleTypeExp = null;
    }
}

