/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdownload;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdownload.DownloadedSchemaEntry;
import org.apache.xmlbeans.metadata.system.sXMLTOOLS.TypeSystemHolder;

public interface DownloadedSchemasDocument
extends XmlObject {
    public static final DocumentFactory<DownloadedSchemasDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "downloadedschemas2dd7doctype");
    public static final SchemaType type = Factory.getType();

    public DownloadedSchemas getDownloadedSchemas();

    public void setDownloadedSchemas(DownloadedSchemas var1);

    public DownloadedSchemas addNewDownloadedSchemas();

    public static interface DownloadedSchemas
    extends XmlObject {
        public static final ElementFactory<DownloadedSchemas> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "downloadedschemasb3efelemtype");
        public static final SchemaType type = Factory.getType();

        public List<DownloadedSchemaEntry> getEntryList();

        public DownloadedSchemaEntry[] getEntryArray();

        public DownloadedSchemaEntry getEntryArray(int var1);

        public int sizeOfEntryArray();

        public void setEntryArray(DownloadedSchemaEntry[] var1);

        public void setEntryArray(int var1, DownloadedSchemaEntry var2);

        public DownloadedSchemaEntry insertNewEntry(int var1);

        public DownloadedSchemaEntry addNewEntry();

        public void removeEntry(int var1);

        public String getDefaultDirectory();

        public XmlToken xgetDefaultDirectory();

        public boolean isSetDefaultDirectory();

        public void setDefaultDirectory(String var1);

        public void xsetDefaultDirectory(XmlToken var1);

        public void unsetDefaultDirectory();
    }
}

