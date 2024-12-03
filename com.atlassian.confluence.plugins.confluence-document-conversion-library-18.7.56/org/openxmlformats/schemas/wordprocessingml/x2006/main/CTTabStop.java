/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabTlc;

public interface CTTabStop
extends XmlObject {
    public static final DocumentFactory<CTTabStop> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttabstop5ebbtype");
    public static final SchemaType type = Factory.getType();

    public STTabJc.Enum getVal();

    public STTabJc xgetVal();

    public void setVal(STTabJc.Enum var1);

    public void xsetVal(STTabJc var1);

    public STTabTlc.Enum getLeader();

    public STTabTlc xgetLeader();

    public boolean isSetLeader();

    public void setLeader(STTabTlc.Enum var1);

    public void xsetLeader(STTabTlc var1);

    public void unsetLeader();

    public Object getPos();

    public STSignedTwipsMeasure xgetPos();

    public void setPos(Object var1);

    public void xsetPos(STSignedTwipsMeasure var1);
}

