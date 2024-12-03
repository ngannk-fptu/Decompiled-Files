/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLTriggerRuntimeNode
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLTriggerTimeNodeID
 *  org.openxmlformats.schemas.presentationml.x2006.main.STTLTriggerEvent
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeTargetElement;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTriggerRuntimeNode;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTriggerTimeNodeID;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTime;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTriggerEvent;

public interface CTTLTimeCondition
extends XmlObject {
    public static final DocumentFactory<CTTLTimeCondition> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttltimecondition1eb9type");
    public static final SchemaType type = Factory.getType();

    public CTTLTimeTargetElement getTgtEl();

    public boolean isSetTgtEl();

    public void setTgtEl(CTTLTimeTargetElement var1);

    public CTTLTimeTargetElement addNewTgtEl();

    public void unsetTgtEl();

    public CTTLTriggerTimeNodeID getTn();

    public boolean isSetTn();

    public void setTn(CTTLTriggerTimeNodeID var1);

    public CTTLTriggerTimeNodeID addNewTn();

    public void unsetTn();

    public CTTLTriggerRuntimeNode getRtn();

    public boolean isSetRtn();

    public void setRtn(CTTLTriggerRuntimeNode var1);

    public CTTLTriggerRuntimeNode addNewRtn();

    public void unsetRtn();

    public STTLTriggerEvent.Enum getEvt();

    public STTLTriggerEvent xgetEvt();

    public boolean isSetEvt();

    public void setEvt(STTLTriggerEvent.Enum var1);

    public void xsetEvt(STTLTriggerEvent var1);

    public void unsetEvt();

    public Object getDelay();

    public STTLTime xgetDelay();

    public boolean isSetDelay();

    public void setDelay(Object var1);

    public void xsetDelay(STTLTime var1);

    public void unsetDelay();
}

