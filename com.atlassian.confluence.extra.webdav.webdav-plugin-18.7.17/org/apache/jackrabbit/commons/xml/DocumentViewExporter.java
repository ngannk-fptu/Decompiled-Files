/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.xml;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import org.apache.jackrabbit.commons.xml.Exporter;
import org.apache.jackrabbit.util.ISO9075;
import org.apache.jackrabbit.value.ValueHelper;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DocumentViewExporter
extends Exporter {
    public DocumentViewExporter(Session session, ContentHandler handler, boolean recurse, boolean binary) {
        super(session, handler, recurse, binary);
    }

    @Override
    protected void exportNode(String uri, String local, Node node) throws RepositoryException, SAXException {
        if ("http://www.jcp.org/jcr/1.0".equals(uri) && "xmltext".equals(local)) {
            try {
                Property property = node.getProperty(this.helper.getJcrName("jcr:xmlcharacters"));
                char[] ch = property.getString().toCharArray();
                this.characters(ch, 0, ch.length);
            }
            catch (PathNotFoundException property) {}
        } else {
            this.exportProperties(node);
            String encoded = ISO9075.encode(local);
            this.startElement(uri, encoded);
            this.exportNodes(node);
            this.endElement(uri, encoded);
        }
    }

    @Override
    protected void exportProperty(String uri, String local, Value value) throws RepositoryException {
        String attribute = ValueHelper.serialize(value, false);
        this.addAttribute(uri, ISO9075.encode(local), attribute);
    }

    @Override
    protected void exportProperty(String uri, String local, int type, Value[] values) {
    }
}

