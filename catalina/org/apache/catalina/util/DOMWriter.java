/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.util;

import java.io.PrintWriter;
import java.io.Writer;
import org.apache.tomcat.util.security.Escape;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMWriter {
    private final PrintWriter out;

    public DOMWriter(Writer writer) {
        this.out = new PrintWriter(writer);
    }

    public void print(Node node) {
        if (node == null) {
            return;
        }
        short type = node.getNodeType();
        switch (type) {
            case 9: {
                this.print(((Document)node).getDocumentElement());
                this.out.flush();
                break;
            }
            case 1: {
                Attr[] attrs;
                this.out.print('<');
                this.out.print(node.getLocalName());
                for (Attr attr : attrs = this.sortAttributes(node.getAttributes())) {
                    this.out.print(' ');
                    this.out.print(attr.getLocalName());
                    this.out.print("=\"");
                    this.out.print(Escape.xml((String)"", (boolean)true, (String)attr.getNodeValue()));
                    this.out.print('\"');
                }
                this.out.print('>');
                this.printChildren(node);
                break;
            }
            case 5: {
                this.printChildren(node);
                break;
            }
            case 4: {
                this.out.print(Escape.xml((String)"", (boolean)true, (String)node.getNodeValue()));
                break;
            }
            case 3: {
                this.out.print(Escape.xml((String)"", (boolean)true, (String)node.getNodeValue()));
                break;
            }
            case 7: {
                this.out.print("<?");
                this.out.print(node.getLocalName());
                String data = node.getNodeValue();
                if (data != null && data.length() > 0) {
                    this.out.print(' ');
                    this.out.print(data);
                }
                this.out.print("?>");
            }
        }
        if (type == 1) {
            this.out.print("</");
            this.out.print(node.getLocalName());
            this.out.print('>');
        }
        this.out.flush();
    }

    private void printChildren(Node node) {
        NodeList children = node.getChildNodes();
        if (children != null) {
            int len = children.getLength();
            for (int i = 0; i < len; ++i) {
                this.print(children.item(i));
            }
        }
    }

    private Attr[] sortAttributes(NamedNodeMap attrs) {
        int i;
        if (attrs == null) {
            return new Attr[0];
        }
        int len = attrs.getLength();
        Attr[] array = new Attr[len];
        for (i = 0; i < len; ++i) {
            array[i] = (Attr)attrs.item(i);
        }
        for (i = 0; i < len - 1; ++i) {
            String name = null;
            name = array[i].getLocalName();
            int index = i;
            for (int j = i + 1; j < len; ++j) {
                String curName = null;
                curName = array[j].getLocalName();
                if (curName.compareTo(name) >= 0) continue;
                name = curName;
                index = j;
            }
            if (index == i) continue;
            Attr temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
        return array;
    }
}

