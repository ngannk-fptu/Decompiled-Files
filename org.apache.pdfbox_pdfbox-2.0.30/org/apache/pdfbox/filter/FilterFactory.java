/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.ASCII85Filter;
import org.apache.pdfbox.filter.ASCIIHexFilter;
import org.apache.pdfbox.filter.CCITTFaxFilter;
import org.apache.pdfbox.filter.CryptFilter;
import org.apache.pdfbox.filter.DCTFilter;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.FlateFilter;
import org.apache.pdfbox.filter.JBIG2Filter;
import org.apache.pdfbox.filter.JPXFilter;
import org.apache.pdfbox.filter.LZWFilter;
import org.apache.pdfbox.filter.RunLengthDecodeFilter;

public final class FilterFactory {
    public static final FilterFactory INSTANCE = new FilterFactory();
    private final Map<COSName, Filter> filters = new HashMap<COSName, Filter>();

    private FilterFactory() {
        FlateFilter flate = new FlateFilter();
        DCTFilter dct = new DCTFilter();
        CCITTFaxFilter ccittFax = new CCITTFaxFilter();
        LZWFilter lzw = new LZWFilter();
        ASCIIHexFilter asciiHex = new ASCIIHexFilter();
        ASCII85Filter ascii85 = new ASCII85Filter();
        RunLengthDecodeFilter runLength = new RunLengthDecodeFilter();
        CryptFilter crypt = new CryptFilter();
        JPXFilter jpx = new JPXFilter();
        JBIG2Filter jbig2 = new JBIG2Filter();
        this.filters.put(COSName.FLATE_DECODE, flate);
        this.filters.put(COSName.FLATE_DECODE_ABBREVIATION, flate);
        this.filters.put(COSName.DCT_DECODE, dct);
        this.filters.put(COSName.DCT_DECODE_ABBREVIATION, dct);
        this.filters.put(COSName.CCITTFAX_DECODE, ccittFax);
        this.filters.put(COSName.CCITTFAX_DECODE_ABBREVIATION, ccittFax);
        this.filters.put(COSName.LZW_DECODE, lzw);
        this.filters.put(COSName.LZW_DECODE_ABBREVIATION, lzw);
        this.filters.put(COSName.ASCII_HEX_DECODE, asciiHex);
        this.filters.put(COSName.ASCII_HEX_DECODE_ABBREVIATION, asciiHex);
        this.filters.put(COSName.ASCII85_DECODE, ascii85);
        this.filters.put(COSName.ASCII85_DECODE_ABBREVIATION, ascii85);
        this.filters.put(COSName.RUN_LENGTH_DECODE, runLength);
        this.filters.put(COSName.RUN_LENGTH_DECODE_ABBREVIATION, runLength);
        this.filters.put(COSName.CRYPT, crypt);
        this.filters.put(COSName.JPX_DECODE, jpx);
        this.filters.put(COSName.JBIG2_DECODE, jbig2);
    }

    public Filter getFilter(String filterName) throws IOException {
        return this.getFilter(COSName.getPDFName(filterName));
    }

    public Filter getFilter(COSName filterName) throws IOException {
        Filter filter = this.filters.get(filterName);
        if (filter == null) {
            throw new IOException("Invalid filter: " + filterName);
        }
        return filter;
    }

    Collection<Filter> getAllFilters() {
        return this.filters.values();
    }
}

