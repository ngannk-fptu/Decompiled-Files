/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.IdentityHashMap;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class VectorSerializer
implements Serializer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$VectorSerializer == null ? (class$org$apache$axis$encoding$ser$VectorSerializer = VectorSerializer.class$("org.apache.axis.encoding.ser.VectorSerializer")) : class$org$apache$axis$encoding$ser$VectorSerializer).getName());
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$VectorSerializer;

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        if (!(value instanceof Vector)) {
            throw new IOException(Messages.getMessage("noVector00", "VectorSerializer", value.getClass().getName()));
        }
        Vector vector = (Vector)value;
        if (this.isRecursive(new IdentityHashMap(), vector)) {
            throw new IOException(Messages.getMessage("badVector00"));
        }
        context.startElement(name, attributes);
        Iterator i = vector.iterator();
        while (i.hasNext()) {
            Object item = i.next();
            context.serialize(Constants.QNAME_LITERAL_ITEM, null, item);
        }
        context.endElement();
    }

    public boolean isRecursive(IdentityHashMap map, Vector vector) {
        map.add(vector);
        boolean recursive = false;
        for (int i = 0; i < vector.size() && !recursive; ++i) {
            Object o = vector.get(i);
            if (!(o instanceof Vector)) continue;
            if (map.containsKey(o)) {
                return true;
            }
            recursive = this.isRecursive(map, (Vector)o);
        }
        return recursive;
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        Element complexType = types.createElement("complexType");
        complexType.setAttribute("name", "Vector");
        types.writeSchemaTypeDecl(Constants.SOAP_VECTOR, complexType);
        Element seq = types.createElement("sequence");
        complexType.appendChild(seq);
        Element element = types.createElement("element");
        element.setAttribute("name", "item");
        element.setAttribute("minOccurs", "0");
        element.setAttribute("maxOccurs", "unbounded");
        element.setAttribute("type", "xsd:anyType");
        seq.appendChild(element);
        return complexType;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

