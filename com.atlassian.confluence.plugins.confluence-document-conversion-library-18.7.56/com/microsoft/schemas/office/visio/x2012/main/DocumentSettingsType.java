/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.visio.x2012.main.AttachedToolbarsType
 *  com.microsoft.schemas.office.visio.x2012.main.CustomMenusFileType
 *  com.microsoft.schemas.office.visio.x2012.main.CustomToolbarsFileType
 *  com.microsoft.schemas.office.visio.x2012.main.DynamicGridEnabledType
 *  com.microsoft.schemas.office.visio.x2012.main.GlueSettingsType
 *  com.microsoft.schemas.office.visio.x2012.main.ProtectBkgndsType
 *  com.microsoft.schemas.office.visio.x2012.main.ProtectMastersType
 *  com.microsoft.schemas.office.visio.x2012.main.ProtectShapesType
 *  com.microsoft.schemas.office.visio.x2012.main.ProtectStylesType
 *  com.microsoft.schemas.office.visio.x2012.main.SnapAnglesType
 *  com.microsoft.schemas.office.visio.x2012.main.SnapExtensionsType
 *  com.microsoft.schemas.office.visio.x2012.main.SnapSettingsType
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.AttachedToolbarsType;
import com.microsoft.schemas.office.visio.x2012.main.CustomMenusFileType;
import com.microsoft.schemas.office.visio.x2012.main.CustomToolbarsFileType;
import com.microsoft.schemas.office.visio.x2012.main.DynamicGridEnabledType;
import com.microsoft.schemas.office.visio.x2012.main.GlueSettingsType;
import com.microsoft.schemas.office.visio.x2012.main.ProtectBkgndsType;
import com.microsoft.schemas.office.visio.x2012.main.ProtectMastersType;
import com.microsoft.schemas.office.visio.x2012.main.ProtectShapesType;
import com.microsoft.schemas.office.visio.x2012.main.ProtectStylesType;
import com.microsoft.schemas.office.visio.x2012.main.SnapAnglesType;
import com.microsoft.schemas.office.visio.x2012.main.SnapExtensionsType;
import com.microsoft.schemas.office.visio.x2012.main.SnapSettingsType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface DocumentSettingsType
extends XmlObject {
    public static final DocumentFactory<DocumentSettingsType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "documentsettingstype945btype");
    public static final SchemaType type = Factory.getType();

    public GlueSettingsType getGlueSettings();

    public boolean isSetGlueSettings();

    public void setGlueSettings(GlueSettingsType var1);

    public GlueSettingsType addNewGlueSettings();

    public void unsetGlueSettings();

    public SnapSettingsType getSnapSettings();

    public boolean isSetSnapSettings();

    public void setSnapSettings(SnapSettingsType var1);

    public SnapSettingsType addNewSnapSettings();

    public void unsetSnapSettings();

    public SnapExtensionsType getSnapExtensions();

    public boolean isSetSnapExtensions();

    public void setSnapExtensions(SnapExtensionsType var1);

    public SnapExtensionsType addNewSnapExtensions();

    public void unsetSnapExtensions();

    public SnapAnglesType getSnapAngles();

    public boolean isSetSnapAngles();

    public void setSnapAngles(SnapAnglesType var1);

    public SnapAnglesType addNewSnapAngles();

    public void unsetSnapAngles();

    public DynamicGridEnabledType getDynamicGridEnabled();

    public boolean isSetDynamicGridEnabled();

    public void setDynamicGridEnabled(DynamicGridEnabledType var1);

    public DynamicGridEnabledType addNewDynamicGridEnabled();

    public void unsetDynamicGridEnabled();

    public ProtectStylesType getProtectStyles();

    public boolean isSetProtectStyles();

    public void setProtectStyles(ProtectStylesType var1);

    public ProtectStylesType addNewProtectStyles();

    public void unsetProtectStyles();

    public ProtectShapesType getProtectShapes();

    public boolean isSetProtectShapes();

    public void setProtectShapes(ProtectShapesType var1);

    public ProtectShapesType addNewProtectShapes();

    public void unsetProtectShapes();

    public ProtectMastersType getProtectMasters();

    public boolean isSetProtectMasters();

    public void setProtectMasters(ProtectMastersType var1);

    public ProtectMastersType addNewProtectMasters();

    public void unsetProtectMasters();

    public ProtectBkgndsType getProtectBkgnds();

    public boolean isSetProtectBkgnds();

    public void setProtectBkgnds(ProtectBkgndsType var1);

    public ProtectBkgndsType addNewProtectBkgnds();

    public void unsetProtectBkgnds();

    public CustomMenusFileType getCustomMenusFile();

    public boolean isSetCustomMenusFile();

    public void setCustomMenusFile(CustomMenusFileType var1);

    public CustomMenusFileType addNewCustomMenusFile();

    public void unsetCustomMenusFile();

    public CustomToolbarsFileType getCustomToolbarsFile();

    public boolean isSetCustomToolbarsFile();

    public void setCustomToolbarsFile(CustomToolbarsFileType var1);

    public CustomToolbarsFileType addNewCustomToolbarsFile();

    public void unsetCustomToolbarsFile();

    public AttachedToolbarsType getAttachedToolbars();

    public boolean isSetAttachedToolbars();

    public void setAttachedToolbars(AttachedToolbarsType var1);

    public AttachedToolbarsType addNewAttachedToolbars();

    public void unsetAttachedToolbars();

    public long getTopPage();

    public XmlUnsignedInt xgetTopPage();

    public boolean isSetTopPage();

    public void setTopPage(long var1);

    public void xsetTopPage(XmlUnsignedInt var1);

    public void unsetTopPage();

    public long getDefaultTextStyle();

    public XmlUnsignedInt xgetDefaultTextStyle();

    public boolean isSetDefaultTextStyle();

    public void setDefaultTextStyle(long var1);

    public void xsetDefaultTextStyle(XmlUnsignedInt var1);

    public void unsetDefaultTextStyle();

    public long getDefaultLineStyle();

    public XmlUnsignedInt xgetDefaultLineStyle();

    public boolean isSetDefaultLineStyle();

    public void setDefaultLineStyle(long var1);

    public void xsetDefaultLineStyle(XmlUnsignedInt var1);

    public void unsetDefaultLineStyle();

    public long getDefaultFillStyle();

    public XmlUnsignedInt xgetDefaultFillStyle();

    public boolean isSetDefaultFillStyle();

    public void setDefaultFillStyle(long var1);

    public void xsetDefaultFillStyle(XmlUnsignedInt var1);

    public void unsetDefaultFillStyle();

    public long getDefaultGuideStyle();

    public XmlUnsignedInt xgetDefaultGuideStyle();

    public boolean isSetDefaultGuideStyle();

    public void setDefaultGuideStyle(long var1);

    public void xsetDefaultGuideStyle(XmlUnsignedInt var1);

    public void unsetDefaultGuideStyle();
}

