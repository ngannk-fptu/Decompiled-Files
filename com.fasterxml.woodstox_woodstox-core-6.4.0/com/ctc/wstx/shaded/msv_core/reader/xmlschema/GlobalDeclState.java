/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class GlobalDeclState
extends SimpleState
implements ExpressionOwner,
XSTypeOwner {
    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if (tag.localName.equals("include")) {
            return reader.sfactory.include(this, tag);
        }
        if (tag.localName.equals("import")) {
            return reader.sfactory.import_(this, tag);
        }
        if (tag.localName.equals("redefine")) {
            return reader.sfactory.redefine(this, tag);
        }
        if (tag.localName.equals("simpleType")) {
            return reader.sfactory.simpleType(this, tag);
        }
        if (tag.localName.equals("complexType")) {
            return reader.sfactory.complexTypeDecl(this, tag);
        }
        if (tag.localName.equals("group")) {
            return reader.sfactory.group(this, tag);
        }
        if (tag.localName.equals("attributeGroup")) {
            return reader.sfactory.attributeGroup(this, tag);
        }
        if (tag.localName.equals("element")) {
            return reader.sfactory.elementDecl(this, tag);
        }
        if (tag.localName.equals("attribute")) {
            return reader.sfactory.attribute(this, tag);
        }
        if (tag.localName.equals("notation")) {
            return reader.sfactory.notation(this, tag);
        }
        return null;
    }

    public String getTargetNamespaceUri() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.currentSchema.targetNamespace;
    }

    public void onEndChild(Expression exp) {
    }

    public void onEndChild(XSDatatypeExp type) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        String typeName = reader.getCurrentState().getStartTag().getAttribute("name");
        if (typeName == null) {
            reader.reportError("GrammarReader.MissingAttribute", (Object)"simpleType", (Object)"name");
            return;
        }
        SimpleTypeExp exp = reader.currentSchema.simpleTypes.getOrCreate(typeName);
        if (exp.getType() != null) {
            reader.reportError("GrammarReader.DataTypeAlreadyDefined", (Object)typeName);
            return;
        }
        exp.set(type);
        reader.setDeclaredLocationOf(exp);
    }
}

