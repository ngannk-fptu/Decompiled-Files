/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public interface Serializer
extends javax.xml.rpc.encoding.Serializer {
    public void serialize(QName var1, Attributes var2, Object var3, SerializationContext var4) throws IOException;

    public Element writeSchema(Class var1, Types var2) throws Exception;
}

