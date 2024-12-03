/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.webdav;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.jackrabbit.commons.webdav.NodeTypeConstants;
import org.apache.jackrabbit.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class NodeTypeUtil
implements NodeTypeConstants {
    public static Element ntNameToXml(String nodeTypeName, Document document) {
        Element ntElem = document.createElementNS("http://www.day.com/jcr/webdav/1.0", "dcr:nodetype");
        Element nameElem = document.createElementNS("http://www.day.com/jcr/webdav/1.0", "dcr:nodetypename");
        Text txt = document.createTextNode(nodeTypeName);
        nameElem.appendChild(txt);
        ntElem.appendChild(nameElem);
        return ntElem;
    }

    public static Collection<String> ntNamesFromXml(Object propValue) {
        if (propValue instanceof List) {
            return NodeTypeUtil.retrieveNodeTypeNames((List)propValue);
        }
        if (propValue instanceof Element) {
            List<Element> l = Collections.singletonList((Element)propValue);
            return NodeTypeUtil.retrieveNodeTypeNames(l);
        }
        return Collections.emptySet();
    }

    private static Set<String> retrieveNodeTypeNames(List<?> elementList) {
        HashSet<String> nodetypeNames = new HashSet<String>();
        for (Object content : elementList) {
            String nodetypeName;
            Element el;
            if (!(content instanceof Element) || !"nodetype".equals((el = (Element)content).getLocalName()) || !"http://www.day.com/jcr/webdav/1.0".equals(el.getNamespaceURI()) || (nodetypeName = XMLUtil.getChildText(el, "nodetypename", "http://www.day.com/jcr/webdav/1.0")) == null || "".equals(nodetypeName)) continue;
            nodetypeNames.add(nodetypeName);
        }
        return nodetypeNames;
    }
}

