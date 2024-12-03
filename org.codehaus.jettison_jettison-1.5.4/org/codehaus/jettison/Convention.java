/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.Node;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public interface Convention {
    public void processAttributesAndNamespaces(Node var1, JSONObject var2) throws JSONException, XMLStreamException;

    public QName createQName(String var1, Node var2) throws XMLStreamException;
}

