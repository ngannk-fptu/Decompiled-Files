/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.xml.XmlUtil;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlGroovyMethods {
    public static Iterator<Node> iterator(final NodeList nodeList) {
        return new Iterator<Node>(){
            private int current;

            @Override
            public boolean hasNext() {
                return this.current < nodeList.getLength();
            }

            @Override
            public Node next() {
                return nodeList.item(this.current++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove() from a NodeList iterator");
            }
        };
    }

    public static String serialize(Element element) {
        return XmlUtil.serialize(element).replaceFirst("<\\?xml version=\"1.0\".*\\?>", "");
    }
}

