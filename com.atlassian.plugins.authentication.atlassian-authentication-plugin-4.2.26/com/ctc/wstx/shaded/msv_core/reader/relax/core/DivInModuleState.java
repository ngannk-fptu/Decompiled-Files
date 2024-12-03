/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class DivInModuleState
extends SimpleState
implements ExpressionOwner,
XSTypeOwner {
    protected RELAXCoreReader getReader() {
        return (RELAXCoreReader)this.reader;
    }

    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("div")) {
            return this.getReader().getStateFactory().divInModule(this, tag);
        }
        if (tag.localName.equals("hedgeRule")) {
            return this.getReader().getStateFactory().hedgeRule(this, tag);
        }
        if (tag.localName.equals("tag")) {
            return this.getReader().getStateFactory().tag(this, tag);
        }
        if (tag.localName.equals("attPool")) {
            return this.getReader().getStateFactory().attPool(this, tag);
        }
        if (tag.localName.equals("include")) {
            return this.getReader().getStateFactory().include(this, tag);
        }
        if (tag.localName.equals("interface")) {
            return this.getReader().getStateFactory().interface_(this, tag);
        }
        if (tag.localName.equals("elementRule")) {
            return this.getReader().getStateFactory().elementRule(this, tag);
        }
        if (tag.localName.equals("simpleType")) {
            return this.getReader().getStateFactory().simpleType(this, tag);
        }
        return null;
    }

    public void onEndChild(Expression exp) {
    }

    public String getTargetNamespaceUri() {
        return "";
    }

    public void onEndChild(XSDatatypeExp type) {
        String typeName = type.name;
        if (typeName == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)"simpleType", (Object)"name");
            return;
        }
        this.getReader().addUserDefinedType(type);
    }
}

