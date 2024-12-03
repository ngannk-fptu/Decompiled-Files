/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public interface NamedNodeMap {
    public Node getNamedItem(String var1);

    public Node setNamedItem(Node var1) throws DOMException;

    public Node removeNamedItem(String var1) throws DOMException;

    public Node item(int var1);

    public int getLength();

    public Node getNamedItemNS(String var1, String var2) throws DOMException;

    public Node setNamedItemNS(Node var1) throws DOMException;

    public Node removeNamedItemNS(String var1, String var2) throws DOMException;
}

