/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.ds.custombuilder;

import java.io.ByteArrayOutputStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.ByteArrayDataSource;
import org.apache.axiom.om.impl.builder.CustomBuilder;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;

public class ByteArrayCustomBuilder
implements CustomBuilder {
    private String encoding = null;

    public ByteArrayCustomBuilder(String encoding) {
        this.encoding = encoding == null ? "utf-8" : encoding;
    }

    public OMElement create(String namespace, String localPart, OMContainer parent, XMLStreamReader reader, OMFactory factory) throws OMException {
        try {
            String prefix = reader.getPrefix();
            if (prefix == null) {
                prefix = "";
            }
            StreamingOMSerializer ser = new StreamingOMSerializer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(baos, this.encoding);
            ser.serialize(reader, writer, false);
            writer.flush();
            byte[] bytes = baos.toByteArray();
            String text = new String(bytes, "utf-8");
            ByteArrayDataSource ds = new ByteArrayDataSource(bytes, this.encoding);
            OMNamespace ns = factory.createOMNamespace(namespace, prefix);
            OMSourcedElement om = null;
            om = parent instanceof SOAPHeader && factory instanceof SOAPFactory ? ((SOAPFactory)factory).createSOAPHeaderBlock(localPart, ns, ds) : factory.createOMElement(ds, localPart, ns);
            parent.addChild(om);
            return om;
        }
        catch (XMLStreamException e) {
            throw new OMException(e);
        }
        catch (OMException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new OMException(t);
        }
    }
}

