/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdownload;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.metadata.system.sXMLTOOLS.TypeSystemHolder;

public interface DownloadedSchemaEntry
extends XmlObject {
    public static final DocumentFactory<DownloadedSchemaEntry> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "downloadedschemaentry1c75type");
    public static final SchemaType type = Factory.getType();

    public String getFilename();

    public XmlToken xgetFilename();

    public void setFilename(String var1);

    public void xsetFilename(XmlToken var1);

    public String getSha1();

    public XmlToken xgetSha1();

    public void setSha1(String var1);

    public void xsetSha1(XmlToken var1);

    public List<String> getSchemaLocationList();

    public String[] getSchemaLocationArray();

    public String getSchemaLocationArray(int var1);

    public List<XmlAnyURI> xgetSchemaLocationList();

    public XmlAnyURI[] xgetSchemaLocationArray();

    public XmlAnyURI xgetSchemaLocationArray(int var1);

    public int sizeOfSchemaLocationArray();

    public void setSchemaLocationArray(String[] var1);

    public void setSchemaLocationArray(int var1, String var2);

    public void xsetSchemaLocationArray(XmlAnyURI[] var1);

    public void xsetSchemaLocationArray(int var1, XmlAnyURI var2);

    public void insertSchemaLocation(int var1, String var2);

    public void addSchemaLocation(String var1);

    public XmlAnyURI insertNewSchemaLocation(int var1);

    public XmlAnyURI addNewSchemaLocation();

    public void removeSchemaLocation(int var1);

    public String getNamespace();

    public XmlAnyURI xgetNamespace();

    public boolean isSetNamespace();

    public void setNamespace(String var1);

    public void xsetNamespace(XmlAnyURI var1);

    public void unsetNamespace();
}

