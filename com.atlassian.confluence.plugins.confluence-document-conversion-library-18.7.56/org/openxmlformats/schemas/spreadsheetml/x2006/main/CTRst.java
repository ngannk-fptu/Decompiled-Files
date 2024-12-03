/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPhoneticPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPhoneticRun;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRElt;

public interface CTRst
extends XmlObject {
    public static final DocumentFactory<CTRst> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrsta472type");
    public static final SchemaType type = Factory.getType();

    public String getT();

    public STXstring xgetT();

    public boolean isSetT();

    public void setT(String var1);

    public void xsetT(STXstring var1);

    public void unsetT();

    public List<CTRElt> getRList();

    public CTRElt[] getRArray();

    public CTRElt getRArray(int var1);

    public int sizeOfRArray();

    public void setRArray(CTRElt[] var1);

    public void setRArray(int var1, CTRElt var2);

    public CTRElt insertNewR(int var1);

    public CTRElt addNewR();

    public void removeR(int var1);

    public List<CTPhoneticRun> getRPhList();

    public CTPhoneticRun[] getRPhArray();

    public CTPhoneticRun getRPhArray(int var1);

    public int sizeOfRPhArray();

    public void setRPhArray(CTPhoneticRun[] var1);

    public void setRPhArray(int var1, CTPhoneticRun var2);

    public CTPhoneticRun insertNewRPh(int var1);

    public CTPhoneticRun addNewRPh();

    public void removeRPh(int var1);

    public CTPhoneticPr getPhoneticPr();

    public boolean isSetPhoneticPr();

    public void setPhoneticPr(CTPhoneticPr var1);

    public CTPhoneticPr addNewPhoneticPr();

    public void unsetPhoneticPr();
}

