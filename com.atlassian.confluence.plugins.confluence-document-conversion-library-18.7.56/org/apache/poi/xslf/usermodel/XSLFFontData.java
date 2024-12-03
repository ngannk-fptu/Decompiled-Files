/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;

public class XSLFFontData
extends POIXMLDocumentPart {
    protected XSLFFontData() {
    }

    public XSLFFontData(PackagePart part) {
        super(part);
    }

    public InputStream getInputStream() throws IOException {
        return this.getPackagePart().getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        PackagePart pp = this.getPackagePart();
        pp.clear();
        return pp.getOutputStream();
    }

    @Override
    protected void prepareForCommit() {
    }

    public void setData(byte[] data) throws IOException {
        try (OutputStream os = this.getPackagePart().getOutputStream();){
            os.write(data);
        }
    }
}

