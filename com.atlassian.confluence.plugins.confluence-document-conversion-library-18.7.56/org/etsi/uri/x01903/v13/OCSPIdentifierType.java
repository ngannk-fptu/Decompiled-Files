/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import java.util.Calendar;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.ResponderIDType;

public interface OCSPIdentifierType
extends XmlObject {
    public static final DocumentFactory<OCSPIdentifierType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ocspidentifiertype3968type");
    public static final SchemaType type = Factory.getType();

    public ResponderIDType getResponderID();

    public void setResponderID(ResponderIDType var1);

    public ResponderIDType addNewResponderID();

    public Calendar getProducedAt();

    public XmlDateTime xgetProducedAt();

    public void setProducedAt(Calendar var1);

    public void xsetProducedAt(XmlDateTime var1);

    public String getURI();

    public XmlAnyURI xgetURI();

    public boolean isSetURI();

    public void setURI(String var1);

    public void xsetURI(XmlAnyURI var1);

    public void unsetURI();
}

