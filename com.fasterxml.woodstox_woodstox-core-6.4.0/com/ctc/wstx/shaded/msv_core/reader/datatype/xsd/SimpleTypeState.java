/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.ListState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.RestrictionState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeWithOneChildState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.UnionState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.util.StringTokenizer;

public class SimpleTypeState
extends TypeWithOneChildState {
    protected State createChildState(StartTagInfo tag) {
        if (!this.startTag.namespaceURI.equals(tag.namespaceURI)) {
            return null;
        }
        String name = this.startTag.getAttribute("name");
        String uri = this.getTargetNamespaceUri();
        if (tag.localName.equals("annotation")) {
            return new IgnoreState();
        }
        if (tag.localName.equals("restriction")) {
            return new RestrictionState(uri, name);
        }
        if (tag.localName.equals("list")) {
            return new ListState(uri, name);
        }
        if (tag.localName.equals("union")) {
            return new UnionState(uri, name);
        }
        return null;
    }

    protected XSDatatypeExp annealType(XSDatatypeExp dt) {
        String finalValueStr = this.startTag.getAttribute("final");
        if (finalValueStr != null) {
            int finalValue = this.getFinalValue(finalValueStr);
            return dt.createFinalizedType(finalValue, this.reader);
        }
        return dt;
    }

    public int getFinalValue(String list) {
        int finalValue = 0;
        StringTokenizer tokens = new StringTokenizer(list);
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (token.equals("#all")) {
                finalValue |= 7;
                continue;
            }
            if (token.equals("restriction")) {
                finalValue |= 1;
                continue;
            }
            if (token.equals("list")) {
                finalValue |= 2;
                continue;
            }
            if (token.equals("union")) {
                finalValue |= 4;
                continue;
            }
            this.reader.reportError("GrammarReader.IllegalFinalValue", (Object)token);
            return 0;
        }
        return finalValue;
    }
}

