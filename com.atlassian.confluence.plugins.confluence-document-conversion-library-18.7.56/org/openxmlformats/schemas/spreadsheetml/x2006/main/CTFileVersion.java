/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STGuid;

public interface CTFileVersion
extends XmlObject {
    public static final DocumentFactory<CTFileVersion> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfileversion559btype");
    public static final SchemaType type = Factory.getType();

    public String getAppName();

    public XmlString xgetAppName();

    public boolean isSetAppName();

    public void setAppName(String var1);

    public void xsetAppName(XmlString var1);

    public void unsetAppName();

    public String getLastEdited();

    public XmlString xgetLastEdited();

    public boolean isSetLastEdited();

    public void setLastEdited(String var1);

    public void xsetLastEdited(XmlString var1);

    public void unsetLastEdited();

    public String getLowestEdited();

    public XmlString xgetLowestEdited();

    public boolean isSetLowestEdited();

    public void setLowestEdited(String var1);

    public void xsetLowestEdited(XmlString var1);

    public void unsetLowestEdited();

    public String getRupBuild();

    public XmlString xgetRupBuild();

    public boolean isSetRupBuild();

    public void setRupBuild(String var1);

    public void xsetRupBuild(XmlString var1);

    public void unsetRupBuild();

    public String getCodeName();

    public STGuid xgetCodeName();

    public boolean isSetCodeName();

    public void setCodeName(String var1);

    public void xsetCodeName(STGuid var1);

    public void unsetCodeName();
}

