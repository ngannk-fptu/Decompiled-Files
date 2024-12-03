/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.decoders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.ArithmeticDecoder;
import org.jpedal.jbig2.decoders.HuffmanDecoder;
import org.jpedal.jbig2.decoders.MMRDecoder;
import org.jpedal.jbig2.image.JBIG2Bitmap;
import org.jpedal.jbig2.io.StreamReader;
import org.jpedal.jbig2.segment.Segment;
import org.jpedal.jbig2.segment.SegmentHeader;
import org.jpedal.jbig2.segment.extensions.ExtensionSegment;
import org.jpedal.jbig2.segment.pageinformation.PageInformationSegment;
import org.jpedal.jbig2.segment.pattern.PatternDictionarySegment;
import org.jpedal.jbig2.segment.region.generic.GenericRegionSegment;
import org.jpedal.jbig2.segment.region.halftone.HalftoneRegionSegment;
import org.jpedal.jbig2.segment.region.refinement.RefinementRegionSegment;
import org.jpedal.jbig2.segment.region.text.TextRegionSegment;
import org.jpedal.jbig2.segment.stripes.EndOfStripeSegment;
import org.jpedal.jbig2.segment.symboldictionary.SymbolDictionarySegment;
import org.jpedal.jbig2.util.BinaryOperation;

public class JBIG2StreamDecoder {
    private StreamReader reader;
    private boolean noOfPagesKnown;
    private boolean randomAccessOrganisation;
    private int noOfPages = -1;
    private List segments = new ArrayList();
    private List bitmaps = new ArrayList();
    private byte[] globalData;
    public static boolean debug = false;

    public void setGlobalData(byte[] byArray) {
        this.globalData = byArray;
    }

    public void decodeJBIG2(byte[] byArray) throws IOException, JBIG2Exception {
        this.reader = new StreamReader(byArray);
        this.resetDecoder();
        boolean bl = this.checkHeader();
        if (debug) {
            System.out.println("validFile = " + bl);
        }
        if (!bl) {
            this.noOfPagesKnown = true;
            this.randomAccessOrganisation = false;
            this.noOfPages = 1;
            if (this.globalData != null) {
                this.reader = new StreamReader(this.globalData);
                HuffmanDecoder.initiate(this.reader);
                MMRDecoder.initiate(this.reader);
                ArithmeticDecoder.initiate(this.reader);
                this.readSegments();
                this.reader = new StreamReader(byArray);
            } else {
                this.reader.movePointer(-8);
            }
        } else {
            if (debug) {
                System.out.println("==== File Header ====");
            }
            this.setFileHeaderFlags();
            if (debug) {
                System.out.println("randomAccessOrganisation = " + this.randomAccessOrganisation);
                System.out.println("noOfPagesKnown = " + this.noOfPagesKnown);
            }
            if (this.noOfPagesKnown) {
                this.noOfPages = this.getNoOfPages();
                if (debug) {
                    System.out.println("noOfPages = " + this.noOfPages);
                }
            }
        }
        HuffmanDecoder.initiate(this.reader);
        MMRDecoder.initiate(this.reader);
        ArithmeticDecoder.initiate(this.reader);
        this.readSegments();
    }

    private void resetDecoder() {
        this.noOfPagesKnown = false;
        this.randomAccessOrganisation = false;
        this.noOfPages = -1;
        this.segments.clear();
        this.bitmaps.clear();
    }

    private void readSegments() throws IOException, JBIG2Exception {
        Segment segment;
        Object object;
        if (debug) {
            System.out.println("==== Segments ====");
        }
        boolean bl = false;
        block23: while (!this.reader.isFinished() && !bl) {
            object = new SegmentHeader();
            if (debug) {
                System.out.println("==== Segment Header ====");
            }
            this.readSegmentHeader((SegmentHeader)object);
            segment = null;
            int n = ((SegmentHeader)object).getSegmentType();
            int[] nArray = ((SegmentHeader)object).getReferredToSegments();
            int n2 = ((SegmentHeader)object).getReferredToSegmentCount();
            switch (n) {
                case 0: {
                    if (debug) {
                        System.out.println("==== Segment Symbol Dictionary ====");
                    }
                    segment = new SymbolDictionarySegment(this);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 4: {
                    if (debug) {
                        System.out.println("==== Intermediate Text Region ====");
                    }
                    segment = new TextRegionSegment(this, false);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 6: {
                    if (debug) {
                        System.out.println("==== Immediate Text Region ====");
                    }
                    segment = new TextRegionSegment(this, true);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 7: {
                    if (debug) {
                        System.out.println("==== Immediate Lossless Text Region ====");
                    }
                    segment = new TextRegionSegment(this, true);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 16: {
                    if (debug) {
                        System.out.println("==== Pattern Dictionary ====");
                    }
                    segment = new PatternDictionarySegment(this);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 20: {
                    if (debug) {
                        System.out.println("==== Intermediate Halftone Region ====");
                    }
                    segment = new HalftoneRegionSegment(this, false);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 22: {
                    if (debug) {
                        System.out.println("==== Immediate Halftone Region ====");
                    }
                    segment = new HalftoneRegionSegment(this, true);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 23: {
                    if (debug) {
                        System.out.println("==== Immediate Lossless Halftone Region ====");
                    }
                    segment = new HalftoneRegionSegment(this, true);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 36: {
                    if (debug) {
                        System.out.println("==== Intermediate Generic Region ====");
                    }
                    segment = new GenericRegionSegment(this, false);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 38: {
                    if (debug) {
                        System.out.println("==== Immediate Generic Region ====");
                    }
                    segment = new GenericRegionSegment(this, true);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 39: {
                    if (debug) {
                        System.out.println("==== Immediate Lossless Generic Region ====");
                    }
                    segment = new GenericRegionSegment(this, true);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 40: {
                    if (debug) {
                        System.out.println("==== Intermediate Generic Refinement Region ====");
                    }
                    segment = new RefinementRegionSegment(this, false, nArray, n2);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 42: {
                    if (debug) {
                        System.out.println("==== Immediate Generic Refinement Region ====");
                    }
                    segment = new RefinementRegionSegment(this, true, nArray, n2);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 43: {
                    if (debug) {
                        System.out.println("==== Immediate lossless Generic Refinement Region ====");
                    }
                    segment = new RefinementRegionSegment(this, true, nArray, n2);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 48: {
                    if (debug) {
                        System.out.println("==== Page Information Dictionary ====");
                    }
                    segment = new PageInformationSegment(this);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 49: {
                    continue block23;
                }
                case 50: {
                    if (debug) {
                        System.out.println("==== End of Stripes ====");
                    }
                    segment = new EndOfStripeSegment(this);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                case 51: {
                    if (debug) {
                        System.out.println("==== End of File ====");
                    }
                    bl = true;
                    continue block23;
                }
                case 52: {
                    if (!debug) break;
                    System.out.println("PROFILES UNIMPLEMENTED");
                    break;
                }
                case 53: {
                    if (!debug) break;
                    System.out.println("TABLES UNIMPLEMENTED");
                    break;
                }
                case 62: {
                    if (debug) {
                        System.out.println("==== Extensions ====");
                    }
                    segment = new ExtensionSegment(this);
                    segment.setSegmentHeader((SegmentHeader)object);
                    break;
                }
                default: {
                    System.out.println("Unknown Segment type in JBIG2 stream");
                }
            }
            if (!this.randomAccessOrganisation) {
                segment.readSegment();
            }
            this.segments.add(segment);
        }
        if (this.randomAccessOrganisation) {
            object = this.segments.iterator();
            while (object.hasNext()) {
                segment = (Segment)object.next();
                segment.readSegment();
            }
        }
    }

    public PageInformationSegment findPageSegement(int n) {
        Iterator iterator = this.segments.iterator();
        while (iterator.hasNext()) {
            Segment segment = (Segment)iterator.next();
            SegmentHeader segmentHeader = segment.getSegmentHeader();
            if (segmentHeader.getSegmentType() != 48 || segmentHeader.getPageAssociation() != n) continue;
            return (PageInformationSegment)segment;
        }
        return null;
    }

    public Segment findSegment(int n) {
        Iterator iterator = this.segments.iterator();
        while (iterator.hasNext()) {
            Segment segment = (Segment)iterator.next();
            if (segment.getSegmentHeader().getSegmentNumber() != n) continue;
            return segment;
        }
        return null;
    }

    private void readSegmentHeader(SegmentHeader segmentHeader) throws IOException, JBIG2Exception {
        this.handleSegmentNumber(segmentHeader);
        this.handleSegmentHeaderFlags(segmentHeader);
        this.handleSegmentReferredToCountAndRententionFlags(segmentHeader);
        this.handleReferedToSegmentNumbers(segmentHeader);
        this.handlePageAssociation(segmentHeader);
        this.handleSegmentDataLength(segmentHeader);
    }

    private void handlePageAssociation(SegmentHeader segmentHeader) throws IOException {
        int n;
        boolean bl = segmentHeader.isPageAssociationSizeSet();
        if (bl) {
            short[] sArray = new short[4];
            this.reader.readByte(sArray);
            n = BinaryOperation.getInt32(sArray);
        } else {
            n = this.reader.readByte();
        }
        segmentHeader.setPageAssociation(n);
        if (debug) {
            System.out.println("pageAssociation = " + n);
        }
    }

    private void handleSegmentNumber(SegmentHeader segmentHeader) throws IOException {
        short[] sArray = new short[4];
        this.reader.readByte(sArray);
        int n = BinaryOperation.getInt32(sArray);
        if (debug) {
            System.out.println("SegmentNumber = " + n);
        }
        segmentHeader.setSegmentNumber(n);
    }

    private void handleSegmentHeaderFlags(SegmentHeader segmentHeader) throws IOException {
        short s = this.reader.readByte();
        segmentHeader.setSegmentHeaderFlags(s);
    }

    private void handleSegmentReferredToCountAndRententionFlags(SegmentHeader segmentHeader) throws IOException, JBIG2Exception {
        short s = this.reader.readByte();
        int n = (s & 0xE0) >> 5;
        short[] sArray = null;
        short s2 = (short)(s & 0x1F);
        if (n <= 4) {
            sArray = new short[]{s2};
        } else if (n == 7) {
            int n2;
            short[] sArray2 = new short[4];
            sArray2[0] = s2;
            for (n2 = 1; n2 < 4; ++n2) {
                sArray2[n2] = this.reader.readByte();
            }
            n = BinaryOperation.getInt32(sArray2);
            n2 = (int)Math.ceil(4.0 + (double)(n + 1) / 8.0);
            int n3 = n2 - 4;
            sArray = new short[n3];
            this.reader.readByte(sArray);
        } else {
            throw new JBIG2Exception("Error, 3 bit Segment count field = " + n);
        }
        segmentHeader.setReferredToSegmentCount(n);
        if (debug) {
            System.out.println("referredToSegmentCount = " + n);
        }
        segmentHeader.setRententionFlags(sArray);
        if (debug) {
            System.out.print("retentionFlags = ");
        }
        if (debug) {
            for (int i = 0; i < sArray.length; ++i) {
                System.out.print(sArray[i] + " ");
            }
            System.out.println("");
        }
    }

    private void handleReferedToSegmentNumbers(SegmentHeader segmentHeader) throws IOException {
        int n;
        int n2;
        int n3 = segmentHeader.getReferredToSegmentCount();
        int[] nArray = new int[n3];
        int n4 = segmentHeader.getSegmentNumber();
        if (n4 <= 256) {
            for (n2 = 0; n2 < n3; ++n2) {
                nArray[n2] = this.reader.readByte();
            }
        } else if (n4 <= 65536) {
            short[] sArray = new short[2];
            for (n = 0; n < n3; ++n) {
                this.reader.readByte(sArray);
                nArray[n] = BinaryOperation.getInt16(sArray);
            }
        } else {
            short[] sArray = new short[4];
            for (n = 0; n < n3; ++n) {
                this.reader.readByte(sArray);
                nArray[n] = BinaryOperation.getInt32(sArray);
            }
        }
        segmentHeader.setReferredToSegments(nArray);
        if (debug) {
            System.out.print("referredToSegments = ");
            for (n2 = 0; n2 < nArray.length; ++n2) {
                System.out.print(nArray[n2] + " ");
            }
            System.out.println("");
        }
    }

    private int getNoOfPages() throws IOException {
        short[] sArray = new short[4];
        this.reader.readByte(sArray);
        return BinaryOperation.getInt32(sArray);
    }

    private void handleSegmentDataLength(SegmentHeader segmentHeader) throws IOException {
        short[] sArray = new short[4];
        this.reader.readByte(sArray);
        int n = BinaryOperation.getInt32(sArray);
        segmentHeader.setDataLength(n);
        if (debug) {
            System.out.println("dateLength = " + n);
        }
    }

    private void setFileHeaderFlags() throws IOException {
        int n;
        short s = this.reader.readByte();
        if ((s & 0xFC) != 0) {
            System.out.println("Warning, reserved bits (2-7) of file header flags are not zero " + s);
        }
        this.randomAccessOrganisation = (n = s & 1) == 0;
        int n2 = s & 2;
        this.noOfPagesKnown = n2 == 0;
    }

    private boolean checkHeader() throws IOException {
        short[] sArray = new short[]{151, 74, 66, 50, 13, 10, 26, 10};
        short[] sArray2 = new short[8];
        this.reader.readByte(sArray2);
        return Arrays.equals(sArray, sArray2);
    }

    public int readBits(int n) throws IOException {
        return this.reader.readBits(n);
    }

    public int readBit() throws IOException {
        return this.reader.readBit();
    }

    public void readByte(short[] sArray) throws IOException {
        this.reader.readByte(sArray);
    }

    public void consumeRemainingBits() throws IOException {
        this.reader.consumeRemainingBits();
    }

    public short readByte() throws IOException {
        return this.reader.readByte();
    }

    public void appendBitmap(JBIG2Bitmap jBIG2Bitmap) {
        this.bitmaps.add(jBIG2Bitmap);
    }

    public JBIG2Bitmap findBitmap(int n) {
        Iterator iterator = this.bitmaps.iterator();
        while (iterator.hasNext()) {
            JBIG2Bitmap jBIG2Bitmap = (JBIG2Bitmap)iterator.next();
            if (jBIG2Bitmap.getBitmapNumber() != n) continue;
            return jBIG2Bitmap;
        }
        return null;
    }

    public JBIG2Bitmap getPageAsJBIG2Bitmap(int n) {
        JBIG2Bitmap jBIG2Bitmap = this.findPageSegement(1).getPageBitmap();
        return jBIG2Bitmap;
    }

    public boolean isNumberOfPagesKnown() {
        return this.noOfPagesKnown;
    }

    public int getNumberOfPages() {
        return this.noOfPages;
    }

    public boolean isRandomAccessOrganisationUsed() {
        return this.randomAccessOrganisation;
    }

    public List getAllSegments() {
        return this.segments;
    }
}

