/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLCommonMediaNodeData;

public interface CTTLMediaNodeVideo
extends XmlObject {
    public static final DocumentFactory<CTTLMediaNodeVideo> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttlmedianodevideoe3f8type");
    public static final SchemaType type = Factory.getType();

    public CTTLCommonMediaNodeData getCMediaNode();

    public void setCMediaNode(CTTLCommonMediaNodeData var1);

    public CTTLCommonMediaNodeData addNewCMediaNode();

    public boolean getFullScrn();

    public XmlBoolean xgetFullScrn();

    public boolean isSetFullScrn();

    public void setFullScrn(boolean var1);

    public void xsetFullScrn(XmlBoolean var1);

    public void unsetFullScrn();
}

