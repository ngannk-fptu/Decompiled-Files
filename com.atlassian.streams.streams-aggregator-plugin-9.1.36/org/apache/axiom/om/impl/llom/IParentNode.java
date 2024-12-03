/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;

public interface IParentNode {
    public static final int INCOMPLETE = 0;
    public static final int COMPLETE = 1;
    public static final int DISCARDED = 2;

    public OMXMLParserWrapper getBuilder();

    public int getState();

    public boolean isComplete();

    public OMNode getFirstOMChildIfAvailable();
}

