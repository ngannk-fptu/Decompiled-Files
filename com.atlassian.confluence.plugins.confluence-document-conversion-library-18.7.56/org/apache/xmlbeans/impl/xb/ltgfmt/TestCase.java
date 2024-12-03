/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.ltgfmt;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.ltgfmt.FileDesc;
import org.apache.xmlbeans.metadata.system.sXMLTOOLS.TypeSystemHolder;

public interface TestCase
extends XmlObject {
    public static final DocumentFactory<TestCase> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "testcase939btype");
    public static final SchemaType type = Factory.getType();

    public String getDescription();

    public XmlString xgetDescription();

    public boolean isSetDescription();

    public void setDescription(String var1);

    public void xsetDescription(XmlString var1);

    public void unsetDescription();

    public Files getFiles();

    public void setFiles(Files var1);

    public Files addNewFiles();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();

    public String getOrigin();

    public XmlToken xgetOrigin();

    public boolean isSetOrigin();

    public void setOrigin(String var1);

    public void xsetOrigin(XmlToken var1);

    public void unsetOrigin();

    public boolean getModified();

    public XmlBoolean xgetModified();

    public boolean isSetModified();

    public void setModified(boolean var1);

    public void xsetModified(XmlBoolean var1);

    public void unsetModified();

    public static interface Files
    extends XmlObject {
        public static final ElementFactory<Files> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "files7c3eelemtype");
        public static final SchemaType type = Factory.getType();

        public List<FileDesc> getFileList();

        public FileDesc[] getFileArray();

        public FileDesc getFileArray(int var1);

        public int sizeOfFileArray();

        public void setFileArray(FileDesc[] var1);

        public void setFileArray(int var1, FileDesc var2);

        public FileDesc insertNewFile(int var1);

        public FileDesc addNewFile();

        public void removeFile(int var1);
    }
}

