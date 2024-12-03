/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfReaderInstance;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

public class PdfLister {
    PrintStream out;

    public PdfLister(PrintStream out) {
        this.out = out;
    }

    public void listAnyObject(PdfObject object) {
        switch (object.type()) {
            case 5: {
                this.listArray((PdfArray)object);
                break;
            }
            case 6: {
                this.listDict((PdfDictionary)object);
                break;
            }
            case 3: {
                this.out.println("(" + object.toString() + ")");
                break;
            }
            default: {
                this.out.println(object.toString());
            }
        }
    }

    public void listDict(PdfDictionary dictionary) {
        this.out.println("<<");
        Iterator<PdfName> iterator = dictionary.getKeys().iterator();
        while (iterator.hasNext()) {
            PdfName pdfName;
            PdfName key = pdfName = iterator.next();
            PdfObject value = dictionary.get(key);
            this.out.print(key.toString());
            this.out.print(' ');
            this.listAnyObject(value);
        }
        this.out.println(">>");
    }

    public void listArray(PdfArray array) {
        this.out.println('[');
        array.getElements().forEach(this::listAnyObject);
        this.out.println(']');
    }

    public void listStream(PRStream stream, PdfReaderInstance reader) {
        try {
            this.listDict(stream);
            this.out.println("startstream");
            byte[] b = PdfReader.getStreamBytes(stream);
            int len = b.length - 1;
            for (int k = 0; k < len; ++k) {
                if (b[k] != 13 || b[k + 1] == 10) continue;
                b[k] = 10;
            }
            this.out.println(new String(b));
            this.out.println("endstream");
        }
        catch (IOException e) {
            System.err.println("I/O exception: " + e);
        }
    }

    public void listPage(PdfImportedPage iPage) {
        int pageNum = iPage.getPageNumber();
        PdfReaderInstance readerInst = iPage.getPdfReaderInstance();
        PdfReader reader = readerInst.getReader();
        PdfDictionary page = reader.getPageN(pageNum);
        this.listDict(page);
        PdfObject obj = PdfReader.getPdfObject(page.get(PdfName.CONTENTS));
        if (obj == null) {
            return;
        }
        switch (obj.type) {
            case 7: {
                this.listStream((PRStream)obj, readerInst);
                break;
            }
            case 5: {
                for (PdfObject pdfObject : ((PdfArray)obj).getElements()) {
                    PdfObject o = PdfReader.getPdfObject(pdfObject);
                    this.listStream((PRStream)o, readerInst);
                    this.out.println("-----------");
                }
                break;
            }
        }
    }
}

