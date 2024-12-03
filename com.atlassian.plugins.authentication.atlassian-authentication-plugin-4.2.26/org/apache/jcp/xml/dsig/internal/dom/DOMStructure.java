/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.w3c.dom.Node;

public abstract class DOMStructure
implements XMLStructure {
    @Override
    public final boolean isFeatureSupported(String feature) {
        if (feature == null) {
            throw new NullPointerException();
        }
        return false;
    }

    public abstract void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException;

    protected boolean equalsContent(List<XMLStructure> content, List<XMLStructure> otherContent) {
        int size = content.size();
        if (size != otherContent.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            XMLStructure oxs = otherContent.get(i);
            XMLStructure xs = content.get(i);
            if (oxs instanceof javax.xml.crypto.dom.DOMStructure) {
                if (!(xs instanceof javax.xml.crypto.dom.DOMStructure)) {
                    return false;
                }
                Node otherNode = ((javax.xml.crypto.dom.DOMStructure)oxs).getNode();
                Node node = ((javax.xml.crypto.dom.DOMStructure)xs).getNode();
                if (DOMUtils.nodesEqual(node, otherNode)) continue;
                return false;
            }
            if (xs.equals(oxs)) continue;
            return false;
        }
        return true;
    }
}

