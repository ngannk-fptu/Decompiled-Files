/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.filespecification;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDSimpleFileSpecification;

public abstract class PDFileSpecification
implements COSObjectable {
    public static PDFileSpecification createFS(COSBase base) throws IOException {
        PDFileSpecification retval = null;
        if (base != null) {
            if (base instanceof COSString) {
                retval = new PDSimpleFileSpecification((COSString)base);
            } else if (base instanceof COSDictionary) {
                retval = new PDComplexFileSpecification((COSDictionary)base);
            } else {
                throw new IOException("Error: Unknown file specification " + base);
            }
        }
        return retval;
    }

    public abstract String getFile();

    public abstract void setFile(String var1);
}

