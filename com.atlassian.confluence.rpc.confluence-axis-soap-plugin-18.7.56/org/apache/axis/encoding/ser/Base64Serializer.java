/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.Base64;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class Base64Serializer
implements SimpleValueSerializer {
    public QName xmlType;
    public Class javaType;
    static /* synthetic */ Class array$B;

    public Base64Serializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        context.startElement(name, attributes);
        context.writeString(this.getValueAsString(value, context));
        context.endElement();
    }

    public String getValueAsString(Object value, SerializationContext context) {
        byte[] data = null;
        if (this.javaType == (array$B == null ? (array$B = Base64Serializer.class$("[B")) : array$B)) {
            data = (byte[])value;
        } else {
            data = new byte[((Byte[])value).length];
            for (int i = 0; i < data.length; ++i) {
                Byte b = ((Byte[])value)[i];
                if (b == null) continue;
                data[i] = b;
            }
        }
        return Base64.encode(data, 0, data.length);
    }

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
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

