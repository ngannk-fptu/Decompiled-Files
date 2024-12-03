/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.util.XMLUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class PDXFAResource
implements COSObjectable {
    private static final int BUFFER_SIZE = 1024;
    private final COSBase xfa;

    public PDXFAResource(COSBase xfaBase) {
        this.xfa = xfaBase;
    }

    @Override
    public COSBase getCOSObject() {
        return this.xfa;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            if (this.getCOSObject() instanceof COSArray) {
                byte[] xfaBytes = new byte[1024];
                COSArray cosArray = (COSArray)this.getCOSObject();
                for (int i = 1; i < cosArray.size(); i += 2) {
                    int nRead;
                    COSBase cosObj = cosArray.getObject(i);
                    if (!(cosObj instanceof COSStream)) continue;
                    is = ((COSStream)cosObj).createInputStream();
                    while ((nRead = is.read(xfaBytes, 0, xfaBytes.length)) != -1) {
                        baos.write(xfaBytes, 0, nRead);
                    }
                    baos.flush();
                }
            } else if (this.xfa.getCOSObject() instanceof COSStream) {
                int nRead;
                byte[] xfaBytes = new byte[1024];
                is = ((COSStream)this.xfa.getCOSObject()).createInputStream();
                while ((nRead = is.read(xfaBytes, 0, xfaBytes.length)) != -1) {
                    baos.write(xfaBytes, 0, nRead);
                }
                baos.flush();
            }
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
        return baos.toByteArray();
    }

    public Document getDocument() throws ParserConfigurationException, SAXException, IOException {
        return XMLUtil.parse(new ByteArrayInputStream(this.getBytes()), true);
    }
}

