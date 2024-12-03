/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfparser.FDFParser;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.fdf.FDFCatalog;
import org.apache.pdfbox.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FDFDocument
implements Closeable {
    private COSDocument document;

    public FDFDocument() {
        this.document = new COSDocument();
        this.document.setVersion(1.2f);
        this.document.setTrailer(new COSDictionary());
        FDFCatalog catalog = new FDFCatalog();
        this.setCatalog(catalog);
    }

    public FDFDocument(COSDocument doc) {
        this.document = doc;
    }

    public FDFDocument(Document doc) throws IOException {
        this();
        Element xfdf = doc.getDocumentElement();
        if (!xfdf.getNodeName().equals("xfdf")) {
            throw new IOException("Error while importing xfdf document, root should be 'xfdf' and not '" + xfdf.getNodeName() + "'");
        }
        FDFCatalog cat = new FDFCatalog(xfdf);
        this.setCatalog(cat);
    }

    public void writeXML(Writer output) throws IOException {
        output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        output.write("<xfdf xmlns=\"http://ns.adobe.com/xfdf/\" xml:space=\"preserve\">\n");
        this.getCatalog().writeXML(output);
        output.write("</xfdf>\n");
    }

    public COSDocument getDocument() {
        return this.document;
    }

    public FDFCatalog getCatalog() {
        FDFCatalog retval = null;
        COSDictionary trailer = this.document.getTrailer();
        COSDictionary root = trailer.getCOSDictionary(COSName.ROOT);
        if (root == null) {
            retval = new FDFCatalog();
            this.setCatalog(retval);
        } else {
            retval = new FDFCatalog(root);
        }
        return retval;
    }

    public void setCatalog(FDFCatalog cat) {
        COSDictionary trailer = this.document.getTrailer();
        trailer.setItem(COSName.ROOT, (COSObjectable)cat);
    }

    public static FDFDocument load(String filename) throws IOException {
        FDFParser parser = new FDFParser(filename);
        parser.parse();
        return new FDFDocument(parser.getDocument());
    }

    public static FDFDocument load(File file) throws IOException {
        FDFParser parser = new FDFParser(file);
        parser.parse();
        return new FDFDocument(parser.getDocument());
    }

    public static FDFDocument load(InputStream input) throws IOException {
        FDFParser parser = new FDFParser(input);
        parser.parse();
        return new FDFDocument(parser.getDocument());
    }

    public static FDFDocument loadXFDF(String filename) throws IOException {
        return FDFDocument.loadXFDF(new BufferedInputStream(new FileInputStream(filename)));
    }

    public static FDFDocument loadXFDF(File file) throws IOException {
        return FDFDocument.loadXFDF(new BufferedInputStream(new FileInputStream(file)));
    }

    public static FDFDocument loadXFDF(InputStream input) throws IOException {
        return new FDFDocument(XMLUtil.parse(input));
    }

    public void save(File fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        this.save(fos);
        fos.close();
    }

    public void save(String fileName) throws IOException {
        this.save(new File(fileName));
    }

    public void save(OutputStream output) throws IOException {
        COSWriter writer = null;
        try {
            writer = new COSWriter(output);
            writer.write(this);
            writer.close();
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void saveXFDF(File fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(fileName), "UTF-8"));
        this.saveXFDF(writer);
        writer.close();
    }

    public void saveXFDF(String fileName) throws IOException {
        this.saveXFDF(new File(fileName));
    }

    public void saveXFDF(Writer output) throws IOException {
        try {
            this.writeXML(output);
        }
        finally {
            if (output != null) {
                output.close();
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.document.close();
    }
}

