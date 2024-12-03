/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.types.HexBinary;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class HexSerializer
implements SimpleValueSerializer {
    public QName xmlType;
    public Class javaType;
    static /* synthetic */ Class class$org$apache$axis$types$HexBinary;

    public HexSerializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {
        context.startElement(name, attributes);
        context.writeString(this.getValueAsString(value, context));
        context.endElement();
    }

    public String getValueAsString(Object value, SerializationContext context) {
        value = JavaUtils.convert(value, this.javaType);
        if (this.javaType == (class$org$apache$axis$types$HexBinary == null ? (class$org$apache$axis$types$HexBinary = HexSerializer.class$("org.apache.axis.types.HexBinary")) : class$org$apache$axis$types$HexBinary)) {
            return value.toString();
        }
        return HexBinary.encode((byte[])value);
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

