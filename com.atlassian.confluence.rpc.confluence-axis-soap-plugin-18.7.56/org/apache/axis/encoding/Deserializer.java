/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.Callback;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Target;
import org.apache.axis.message.SOAPHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface Deserializer
extends javax.xml.rpc.encoding.Deserializer,
Callback {
    public Object getValue();

    public void setValue(Object var1);

    public Object getValue(Object var1);

    public void setChildValue(Object var1, Object var2) throws SAXException;

    public void setDefaultType(QName var1);

    public QName getDefaultType();

    public void registerValueTarget(Target var1);

    public Vector getValueTargets();

    public void removeValueTargets();

    public void moveValueTargets(Deserializer var1);

    public boolean componentsReady();

    public void valueComplete() throws SAXException;

    public void startElement(String var1, String var2, String var3, Attributes var4, DeserializationContext var5) throws SAXException;

    public void onStartElement(String var1, String var2, String var3, Attributes var4, DeserializationContext var5) throws SAXException;

    public SOAPHandler onStartChild(String var1, String var2, String var3, Attributes var4, DeserializationContext var5) throws SAXException;

    public void endElement(String var1, String var2, DeserializationContext var3) throws SAXException;

    public void onEndElement(String var1, String var2, DeserializationContext var3) throws SAXException;
}

