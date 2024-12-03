/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.badgerfish;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.Convention;
import org.codehaus.jettison.Node;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class BadgerFishConvention
implements Convention {
    @Override
    public void processAttributesAndNamespaces(Node n, JSONObject object) throws JSONException, XMLStreamException {
        Iterator itr = object.keys();
        while (itr.hasNext()) {
            String k = (String)itr.next();
            if (!k.startsWith("@")) continue;
            Object o = object.opt(k);
            if ((k = k.substring(1)).equals("xmlns")) {
                if (o instanceof JSONObject) {
                    JSONObject jo = (JSONObject)o;
                    Iterator pitr = jo.keys();
                    while (pitr.hasNext()) {
                        String prefix = (String)pitr.next();
                        String uri = jo.getString(prefix);
                        if (prefix.equals("$")) {
                            prefix = "";
                        }
                        n.setNamespace(prefix, uri);
                    }
                }
            } else {
                String strValue = (String)o;
                QName name = null;
                name = k.contains(":") ? this.createQName(k, n) : new QName("", k);
                n.setAttribute(name, strValue);
            }
            itr.remove();
        }
    }

    @Override
    public QName createQName(String rootName, Node node) throws XMLStreamException {
        int idx = rootName.indexOf(58);
        if (idx != -1) {
            String prefix = rootName.substring(0, idx);
            String local = rootName.substring(idx + 1);
            String uri = node.getNamespaceURI(prefix);
            if (uri == null) {
                throw new XMLStreamException("Invalid prefix " + prefix + " on element " + rootName);
            }
            return new QName(uri, local, prefix);
        }
        String uri = node.getNamespaceURI("");
        if (uri != null) {
            return new QName(uri, rootName);
        }
        return new QName(rootName);
    }
}

