/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabAlignment
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabLeader
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabRelativeTo
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabAlignment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabLeader;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabRelativeTo;

public interface CTPTab
extends XmlObject {
    public static final DocumentFactory<CTPTab> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctptaba283type");
    public static final SchemaType type = Factory.getType();

    public STPTabAlignment.Enum getAlignment();

    public STPTabAlignment xgetAlignment();

    public void setAlignment(STPTabAlignment.Enum var1);

    public void xsetAlignment(STPTabAlignment var1);

    public STPTabRelativeTo.Enum getRelativeTo();

    public STPTabRelativeTo xgetRelativeTo();

    public void setRelativeTo(STPTabRelativeTo.Enum var1);

    public void xsetRelativeTo(STPTabRelativeTo var1);

    public STPTabLeader.Enum getLeader();

    public STPTabLeader xgetLeader();

    public void setLeader(STPTabLeader.Enum var1);

    public void xsetLeader(STPTabLeader var1);
}

