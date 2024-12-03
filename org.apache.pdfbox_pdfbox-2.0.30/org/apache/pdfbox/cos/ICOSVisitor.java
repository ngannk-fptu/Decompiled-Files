/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;

public interface ICOSVisitor {
    public Object visitFromArray(COSArray var1) throws IOException;

    public Object visitFromBoolean(COSBoolean var1) throws IOException;

    public Object visitFromDictionary(COSDictionary var1) throws IOException;

    public Object visitFromDocument(COSDocument var1) throws IOException;

    public Object visitFromFloat(COSFloat var1) throws IOException;

    public Object visitFromInt(COSInteger var1) throws IOException;

    public Object visitFromName(COSName var1) throws IOException;

    public Object visitFromNull(COSNull var1) throws IOException;

    public Object visitFromStream(COSStream var1) throws IOException;

    public Object visitFromString(COSString var1) throws IOException;
}

