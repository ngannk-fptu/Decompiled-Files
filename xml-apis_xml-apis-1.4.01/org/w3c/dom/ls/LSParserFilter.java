/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.ls;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface LSParserFilter {
    public static final short FILTER_ACCEPT = 1;
    public static final short FILTER_REJECT = 2;
    public static final short FILTER_SKIP = 3;
    public static final short FILTER_INTERRUPT = 4;

    public short startElement(Element var1);

    public short acceptNode(Node var1);

    public int getWhatToShow();
}

