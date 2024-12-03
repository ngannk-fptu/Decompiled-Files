/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdfparser;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.ScratchFile;
import org.apache.pdfbox.pdfparser.COSParser;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFParser
extends COSParser {
    private static final Log LOG = LogFactory.getLog(PDFParser.class);

    public PDFParser(RandomAccessRead source) throws IOException {
        this(source, "", ScratchFile.getMainMemoryOnlyInstance());
    }

    public PDFParser(RandomAccessRead source, ScratchFile scratchFile) throws IOException {
        this(source, "", scratchFile);
    }

    public PDFParser(RandomAccessRead source, String decryptionPassword) throws IOException {
        this(source, decryptionPassword, ScratchFile.getMainMemoryOnlyInstance());
    }

    public PDFParser(RandomAccessRead source, String decryptionPassword, ScratchFile scratchFile) throws IOException {
        this(source, decryptionPassword, null, null, scratchFile);
    }

    public PDFParser(RandomAccessRead source, String decryptionPassword, InputStream keyStore, String alias) throws IOException {
        this(source, decryptionPassword, keyStore, alias, ScratchFile.getMainMemoryOnlyInstance());
    }

    public PDFParser(RandomAccessRead source, String decryptionPassword, InputStream keyStore, String alias, ScratchFile scratchFile) throws IOException {
        super(source, decryptionPassword, keyStore, alias);
        this.fileLen = source.length();
        this.init(scratchFile);
    }

    private void init(ScratchFile scratchFile) {
        String eofLookupRangeStr = System.getProperty("org.apache.pdfbox.pdfparser.nonSequentialPDFParser.eofLookupRange");
        if (eofLookupRangeStr != null) {
            try {
                this.setEOFLookupRange(Integer.parseInt(eofLookupRangeStr));
            }
            catch (NumberFormatException nfe) {
                LOG.warn((Object)("System property org.apache.pdfbox.pdfparser.nonSequentialPDFParser.eofLookupRange does not contain an integer value, but: '" + eofLookupRangeStr + "'"));
            }
        }
        this.document = new COSDocument(scratchFile);
    }

    public PDDocument getPDDocument() throws IOException {
        PDDocument doc = new PDDocument(this.getDocument(), this.source, this.getAccessPermission());
        doc.setEncryptionDictionary(this.getEncryption());
        return doc;
    }

    protected void initialParse() throws IOException {
        COSDictionary trailer = this.retrieveTrailer();
        COSBase base = this.parseTrailerValuesDynamically(trailer);
        if (!(base instanceof COSDictionary)) {
            throw new IOException("Expected root dictionary, but got this: " + base);
        }
        COSDictionary root = (COSDictionary)base;
        if (this.isLenient() && !root.containsKey(COSName.TYPE)) {
            root.setItem(COSName.TYPE, (COSBase)COSName.CATALOG);
        }
        this.parseDictObjects(root, null);
        COSBase infoBase = trailer.getDictionaryObject(COSName.INFO);
        if (infoBase instanceof COSDictionary) {
            this.parseDictObjects((COSDictionary)infoBase, null);
        }
        this.checkPages(root);
        if (!(root.getDictionaryObject(COSName.PAGES) instanceof COSDictionary)) {
            throw new IOException("Page tree root must be a dictionary");
        }
        this.document.setDecrypted();
        this.initialParseDone = true;
    }

    public void parse() throws IOException {
        boolean exceptionOccurred = true;
        try {
            if (!this.parsePDFHeader() && !this.parseFDFHeader()) {
                if (this.isLenient()) {
                    LOG.warn((Object)"Error: Header doesn't contain versioninfo");
                } else {
                    throw new IOException("Error: Header doesn't contain versioninfo");
                }
            }
            if (!this.initialParseDone) {
                this.initialParse();
            }
            exceptionOccurred = false;
        }
        finally {
            if (exceptionOccurred && this.document != null) {
                IOUtils.closeQuietly(this.document);
                this.document = null;
            }
        }
    }
}

