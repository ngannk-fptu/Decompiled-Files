/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.DOMASException;
import org.w3c.dom.Node;

public interface NodeEditAS {
    public static final short WF_CHECK = 1;
    public static final short NS_WF_CHECK = 2;
    public static final short PARTIAL_VALIDITY_CHECK = 3;
    public static final short STRICT_VALIDITY_CHECK = 4;

    public boolean canInsertBefore(Node var1, Node var2);

    public boolean canRemoveChild(Node var1);

    public boolean canReplaceChild(Node var1, Node var2);

    public boolean canAppendChild(Node var1);

    public boolean isNodeValid(boolean var1, short var2) throws DOMASException;
}

