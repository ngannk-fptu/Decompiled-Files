/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrChange
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

public interface CTPPr
extends CTPPrBase {
    public static final DocumentFactory<CTPPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctppr01c0type");
    public static final SchemaType type = Factory.getType();

    public CTParaRPr getRPr();

    public boolean isSetRPr();

    public void setRPr(CTParaRPr var1);

    public CTParaRPr addNewRPr();

    public void unsetRPr();

    public CTSectPr getSectPr();

    public boolean isSetSectPr();

    public void setSectPr(CTSectPr var1);

    public CTSectPr addNewSectPr();

    public void unsetSectPr();

    public CTPPrChange getPPrChange();

    public boolean isSetPPrChange();

    public void setPPrChange(CTPPrChange var1);

    public CTPPrChange addNewPPrChange();

    public void unsetPPrChange();
}

