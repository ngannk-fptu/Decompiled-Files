/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.xpath;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathException;

public interface XPathResult {
    public static final short ANY_TYPE = 0;
    public static final short NUMBER_TYPE = 1;
    public static final short STRING_TYPE = 2;
    public static final short BOOLEAN_TYPE = 3;
    public static final short UNORDERED_NODE_ITERATOR_TYPE = 4;
    public static final short ORDERED_NODE_ITERATOR_TYPE = 5;
    public static final short UNORDERED_NODE_SNAPSHOT_TYPE = 6;
    public static final short ORDERED_NODE_SNAPSHOT_TYPE = 7;
    public static final short ANY_UNORDERED_NODE_TYPE = 8;
    public static final short FIRST_ORDERED_NODE_TYPE = 9;

    public short getResultType();

    public double getNumberValue() throws XPathException;

    public String getStringValue() throws XPathException;

    public boolean getBooleanValue() throws XPathException;

    public Node getSingleNodeValue() throws XPathException;

    public boolean getInvalidIteratorState();

    public int getSnapshotLength() throws XPathException;

    public Node iterateNext() throws XPathException, DOMException;

    public Node snapshotItem(int var1) throws XPathException;
}

