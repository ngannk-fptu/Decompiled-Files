/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.contentstream.operator.state;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

public class SetLineDashPattern
extends OperatorProcessor {
    private static final Log LOG = LogFactory.getLog(SetLineDashPattern.class);

    @Override
    public void process(Operator operator, List<COSBase> arguments) throws MissingOperandException {
        if (arguments.size() < 2) {
            throw new MissingOperandException(operator, arguments);
        }
        COSBase base0 = arguments.get(0);
        if (!(base0 instanceof COSArray)) {
            return;
        }
        COSBase base1 = arguments.get(1);
        if (!(base1 instanceof COSNumber)) {
            return;
        }
        COSArray dashArray = (COSArray)base0;
        int dashPhase = ((COSNumber)base1).intValue();
        for (COSBase base : dashArray) {
            if (base instanceof COSNumber) {
                COSNumber num = (COSNumber)base;
                if (num.floatValue() == 0.0f) continue;
                break;
            }
            LOG.warn((Object)("dash array has non number element " + base + ", ignored"));
            dashArray = new COSArray();
            break;
        }
        this.context.setLineDashPattern(dashArray, dashPhase);
    }

    @Override
    public String getName() {
        return "d";
    }
}

