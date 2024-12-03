/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import java.math.BigInteger;
import java.util.Calendar;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CRLIdentifierType
extends XmlObject {
    public static final DocumentFactory<CRLIdentifierType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "crlidentifiertypeb702type");
    public static final SchemaType type = Factory.getType();

    public String getIssuer();

    public XmlString xgetIssuer();

    public void setIssuer(String var1);

    public void xsetIssuer(XmlString var1);

    public Calendar getIssueTime();

    public XmlDateTime xgetIssueTime();

    public void setIssueTime(Calendar var1);

    public void xsetIssueTime(XmlDateTime var1);

    public BigInteger getNumber();

    public XmlInteger xgetNumber();

    public boolean isSetNumber();

    public void setNumber(BigInteger var1);

    public void xsetNumber(XmlInteger var1);

    public void unsetNumber();

    public String getURI();

    public XmlAnyURI xgetURI();

    public boolean isSetURI();

    public void setURI(String var1);

    public void xsetURI(XmlAnyURI var1);

    public void unsetURI();
}

