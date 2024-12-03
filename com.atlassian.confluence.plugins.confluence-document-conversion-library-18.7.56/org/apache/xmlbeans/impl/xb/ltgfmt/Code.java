/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.ltgfmt;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.metadata.system.sXMLTOOLS.TypeSystemHolder;

public interface Code
extends XmlObject {
    public static final DocumentFactory<Code> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "codef72ftype");
    public static final SchemaType type = Factory.getType();

    public String getID();

    public XmlToken xgetID();

    public boolean isSetID();

    public void setID(String var1);

    public void xsetID(XmlToken var1);

    public void unsetID();
}

