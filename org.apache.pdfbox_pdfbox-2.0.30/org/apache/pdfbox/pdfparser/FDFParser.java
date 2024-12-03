/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdfparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.COSParser;

public class FDFParser
extends COSParser {
    private static final Log LOG = LogFactory.getLog(FDFParser.class);

    public FDFParser(String filename) throws IOException {
        this(new File(filename));
    }

    public FDFParser(File file) throws IOException {
        super(new RandomAccessFile(file, "r"));
        this.fileLen = file.length();
        this.init();
    }

    public FDFParser(InputStream input) throws IOException {
        super(new RandomAccessBuffer(input));
        this.fileLen = this.source.length();
        this.init();
    }

    @Override
    protected final boolean isCatalog(COSDictionary dictionary) {
        return dictionary.containsKey(COSName.FDF);
    }

    private void init() {
        String eofLookupRangeStr = System.getProperty("org.apache.pdfbox.pdfparser.nonSequentialPDFParser.eofLookupRange");
        if (eofLookupRangeStr != null) {
            try {
                this.setEOFLookupRange(Integer.parseInt(eofLookupRangeStr));
            }
            catch (NumberFormatException nfe) {
                LOG.warn((Object)("System property org.apache.pdfbox.pdfparser.nonSequentialPDFParser.eofLookupRange does not contain an integer value, but: '" + eofLookupRangeStr + "'"));
            }
        }
        this.document = new COSDocument();
    }

    private void initialParse() throws IOException {
        COSBase rootObject;
        COSDictionary trailer = null;
        boolean rebuildTrailer = false;
        try {
            long startXRefOffset = this.getStartxrefOffset();
            if (startXRefOffset > 0L) {
                trailer = this.parseXref(startXRefOffset);
            } else if (this.isLenient()) {
                rebuildTrailer = true;
            }
        }
        catch (IOException exception) {
            if (this.isLenient()) {
                rebuildTrailer = true;
            }
            throw exception;
        }
        if (rebuildTrailer) {
            trailer = this.rebuildTrailer();
        }
        if ((rootObject = this.parseTrailerValuesDynamically(trailer)) instanceof COSDictionary) {
            this.parseDictObjects((COSDictionary)rootObject, null);
        }
        this.initialParseDone = true;
    }

    public void parse() throws IOException {
        boolean exceptionOccurred = true;
        try {
            if (!this.parseFDFHeader()) {
                throw new IOException("Error: Header doesn't contain versioninfo");
            }
            this.initialParse();
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

