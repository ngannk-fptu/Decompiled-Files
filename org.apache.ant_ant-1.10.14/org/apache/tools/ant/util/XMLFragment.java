/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import org.apache.tools.ant.DynamicConfiguratorNS;
import org.apache.tools.ant.DynamicElementNS;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.JAXPUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class XMLFragment
extends ProjectComponent
implements DynamicElementNS {
    private Document doc = JAXPUtils.getDocumentBuilder().newDocument();
    private DocumentFragment fragment = this.doc.createDocumentFragment();

    public DocumentFragment getFragment() {
        return this.fragment;
    }

    public void addText(String s) {
        this.addText(this.fragment, s);
    }

    @Override
    public Object createDynamicElement(String uri, String name, String qName) {
        Element e = uri.isEmpty() ? this.doc.createElement(name) : this.doc.createElementNS(uri, qName);
        this.fragment.appendChild(e);
        return new Child(e);
    }

    private void addText(Node n, String s) {
        s = this.getProject().replaceProperties(s);
        if (s != null && !s.trim().isEmpty()) {
            Text t = this.doc.createTextNode(s.trim());
            n.appendChild(t);
        }
    }

    public class Child
    implements DynamicConfiguratorNS {
        private Element e;

        Child(Element e) {
            this.e = e;
        }

        public void addText(String s) {
            XMLFragment.this.addText(this.e, s);
        }

        @Override
        public void setDynamicAttribute(String uri, String name, String qName, String value) {
            if (uri.isEmpty()) {
                this.e.setAttribute(name, value);
            } else {
                this.e.setAttributeNS(uri, qName, value);
            }
        }

        @Override
        public Object createDynamicElement(String uri, String name, String qName) {
            Element e2 = null;
            e2 = uri.isEmpty() ? XMLFragment.this.doc.createElement(name) : XMLFragment.this.doc.createElementNS(uri, qName);
            this.e.appendChild(e2);
            return new Child(e2);
        }
    }
}

