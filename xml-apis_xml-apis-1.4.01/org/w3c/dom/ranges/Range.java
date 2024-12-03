/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.ranges;

import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.RangeException;

public interface Range {
    public static final short START_TO_START = 0;
    public static final short START_TO_END = 1;
    public static final short END_TO_END = 2;
    public static final short END_TO_START = 3;

    public Node getStartContainer() throws DOMException;

    public int getStartOffset() throws DOMException;

    public Node getEndContainer() throws DOMException;

    public int getEndOffset() throws DOMException;

    public boolean getCollapsed() throws DOMException;

    public Node getCommonAncestorContainer() throws DOMException;

    public void setStart(Node var1, int var2) throws RangeException, DOMException;

    public void setEnd(Node var1, int var2) throws RangeException, DOMException;

    public void setStartBefore(Node var1) throws RangeException, DOMException;

    public void setStartAfter(Node var1) throws RangeException, DOMException;

    public void setEndBefore(Node var1) throws RangeException, DOMException;

    public void setEndAfter(Node var1) throws RangeException, DOMException;

    public void collapse(boolean var1) throws DOMException;

    public void selectNode(Node var1) throws RangeException, DOMException;

    public void selectNodeContents(Node var1) throws RangeException, DOMException;

    public short compareBoundaryPoints(short var1, Range var2) throws DOMException;

    public void deleteContents() throws DOMException;

    public DocumentFragment extractContents() throws DOMException;

    public DocumentFragment cloneContents() throws DOMException;

    public void insertNode(Node var1) throws DOMException, RangeException;

    public void surroundContents(Node var1) throws DOMException, RangeException;

    public Range cloneRange() throws DOMException;

    public String toString() throws DOMException;

    public void detach() throws DOMException;
}

