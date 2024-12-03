/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.SimpleTypeState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeResolver;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class UnionState
extends TypeState
implements XSTypeOwner {
    protected final String newTypeUri;
    protected final String newTypeName;
    private final ArrayList memberTypes = new ArrayList();

    protected UnionState(String newTypeUri, String newTypeName) {
        this.newTypeUri = newTypeUri;
        this.newTypeName = newTypeName;
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

    protected void startSelf() {
        super.startSelf();
        String memberTypes = this.startTag.getAttribute("memberTypes");
        if (memberTypes != null) {
            StringTokenizer tokens = new StringTokenizer(memberTypes);
            while (tokens.hasMoreTokens()) {
                this.onEndChild(((XSDatatypeResolver)((Object)this.reader)).resolveXSDatatype(tokens.nextToken()));
            }
        }
    }

    public void onEndChild(XSDatatypeExp type) {
        this.memberTypes.add(type);
    }

    protected final XSDatatypeExp makeType() throws DatatypeException {
        return XSDatatypeExp.makeUnion(this.newTypeUri, this.newTypeName, this.memberTypes, this.reader);
    }
}

