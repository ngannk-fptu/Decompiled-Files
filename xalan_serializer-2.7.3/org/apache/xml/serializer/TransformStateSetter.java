/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import javax.xml.transform.Transformer;
import org.w3c.dom.Node;

public interface TransformStateSetter {
    public void setCurrentNode(Node var1);

    public void resetState(Transformer var1);
}

