/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.color;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingColor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

public class SetStrokingDeviceRGBColor
extends SetStrokingColor {
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        PDColorSpace cs = this.context.getResources().getColorSpace(COSName.DEVICERGB);
        this.context.getGraphicsState().setStrokingColorSpace(cs);
        super.process(operator, arguments);
    }

    @Override
    public String getName() {
        return "RG";
    }
}

