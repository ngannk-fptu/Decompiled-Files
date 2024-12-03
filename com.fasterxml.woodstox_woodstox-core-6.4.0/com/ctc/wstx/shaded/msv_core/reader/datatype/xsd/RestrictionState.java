/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.SimpleTypeState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeWithOneChildState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeResolver;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class RestrictionState
extends TypeWithOneChildState
implements FacetStateParent {
    protected final String newTypeUri;
    protected final String newTypeName;
    protected XSTypeIncubator incubator;

    protected RestrictionState(String newTypeUri, String newTypeName) {
        this.newTypeUri = newTypeUri;
        this.newTypeName = newTypeName;
    }

    public final XSTypeIncubator getIncubator() {
        return this.incubator;
    }

    protected XSDatatypeExp annealType(XSDatatypeExp baseType) throws DatatypeException {
        return this.incubator.derive(this.newTypeUri, this.newTypeName);
    }

    public void onEndChild(XSDatatypeExp child) {
        super.onEndChild(child);
        this.createTypeIncubator();
    }

    private void createTypeIncubator() {
        this.incubator = this.type.createIncubator();
    }

    protected void startSelf() {
        super.startSelf();
        String base = this.startTag.getAttribute("base");
        if (base != null) {
            this.onEndChild(((XSDatatypeResolver)((Object)this.reader)).resolveXSDatatype(base));
        }
    }

    protected State createChildState(StartTagInfo tag) {
        if (!this.startTag.namespaceURI.equals(tag.namespaceURI)) {
            return null;
        }
        if (tag.localName.equals("annotation")) {
            return new IgnoreState();
        }
        if (tag.localName.equals("simpleType")) {
            return new SimpleTypeState();
        }
        if (FacetState.facetNames.contains(tag.localName)) {
            if (this.incubator == null) {
                this.reader.reportError("GrammarReader.MissingAttribute", (Object)"restriction", (Object)"base");
                this.onEndChild(new XSDatatypeExp(StringType.theInstance, this.reader.pool));
            }
            return new FacetState();
        }
        return null;
    }
}

