/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.SimpleTypeState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeWithOneChildState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeResolver;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class ListState
extends TypeWithOneChildState {
    protected final String newTypeUri;
    protected final String newTypeName;

    protected ListState(String newTypeUri, String newTypeName) {
        this.newTypeUri = newTypeUri;
        this.newTypeName = newTypeName;
    }

    protected XSDatatypeExp annealType(XSDatatypeExp itemType) throws DatatypeException {
        return XSDatatypeExp.makeList(this.newTypeUri, this.newTypeName, itemType, this.reader);
    }

    protected void startSelf() {
        super.startSelf();
        String itemType = this.startTag.getAttribute("itemType");
        if (itemType != null) {
            this.onEndChild(((XSDatatypeResolver)((Object)this.reader)).resolveXSDatatype(itemType));
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
        return null;
    }
}

