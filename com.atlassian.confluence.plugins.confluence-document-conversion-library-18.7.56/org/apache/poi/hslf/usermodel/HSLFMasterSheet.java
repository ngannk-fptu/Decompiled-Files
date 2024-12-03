/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.hslf.record.SheetContainer;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.MasterSheet;

public abstract class HSLFMasterSheet
extends HSLFSheet
implements MasterSheet<HSLFShape, HSLFTextParagraph> {
    public HSLFMasterSheet(SheetContainer container, int sheetNo) {
        super(container, sheetNo);
    }

    public abstract TextPropCollection getPropCollection(int var1, int var2, String var3, boolean var4);
}

