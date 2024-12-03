/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.Utilities;
import com.lowagie.text.error_messages.MessageLocalization;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Jpeg
extends Image {
    public static final int NOT_A_MARKER = -1;
    public static final int VALID_MARKER = 0;
    public static final int[] VALID_MARKERS = new int[]{192, 193, 194};
    public static final int UNSUPPORTED_MARKER = 1;
    public static final int[] UNSUPPORTED_MARKERS = new int[]{195, 197, 198, 199, 200, 201, 202, 203, 205, 206, 207};
    public static final int NOPARAM_MARKER = 2;
    public static final int[] NOPARAM_MARKERS = new int[]{208, 209, 210, 211, 212, 213, 214, 215, 216, 1};
    public static final int M_APP0 = 224;
    public static final int M_APP2 = 226;
    public static final int M_APPE = 238;
    public static final byte[] JFIF_ID = new byte[]{74, 70, 73, 70, 0};
    private byte[][] icc;

    Jpeg(Image image) {
        super(image);
    }

    public Jpeg(URL url) throws BadElementException, IOException {
        super(url);
        this.processParameters();
    }

    public Jpeg(byte[] img) throws BadElementException, IOException {
        super((URL)null);
        this.rawData = img;
        this.originalData = img;
        this.processParameters();
    }

    public Jpeg(byte[] img, float width, float height) throws BadElementException, IOException {
        this(img);
        this.scaledWidth = width;
        this.scaledHeight = height;
    }

    private static int getShort(InputStream is) throws IOException {
        return (is.read() << 8) + is.read();
    }

    private static int marker(int marker) {
        for (int validMarker : VALID_MARKERS) {
            if (marker != validMarker) continue;
            return 0;
        }
        for (int noparamMarker : NOPARAM_MARKERS) {
            if (marker != noparamMarker) continue;
            return 2;
        }
        for (int unsupportedMarker : UNSUPPORTED_MARKERS) {
            if (marker != unsupportedMarker) continue;
            return 1;
        }
        return -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processParameters() throws BadElementException, IOException {
        this.type = 32;
        this.originalType = 1;
        try (InputStream is = null;){
            String errorID;
            if (this.rawData == null) {
                is = this.url.openStream();
                errorID = this.url.toString();
            } else {
                is = new ByteArrayInputStream(this.rawData);
                errorID = "Byte array";
            }
            if (is.read() != 255 || is.read() != 216) {
                throw new BadElementException(MessageLocalization.getComposedMessage("1.is.not.a.valid.jpeg.file", errorID));
            }
            boolean firstPass = true;
            while (true) {
                int len;
                int v;
                if ((v = is.read()) < 0) {
                    throw new IOException(MessageLocalization.getComposedMessage("premature.eof.while.reading.jpg"));
                }
                if (v != 255) continue;
                int marker = is.read();
                if (firstPass && marker == 224) {
                    firstPass = false;
                    len = Jpeg.getShort(is);
                    if (len < 16) {
                        Utilities.skip(is, len - 2);
                        continue;
                    }
                    byte[] bcomp = new byte[JFIF_ID.length];
                    int r = is.read(bcomp);
                    if (r != bcomp.length) {
                        throw new BadElementException(MessageLocalization.getComposedMessage("1.corrupted.jfif.marker", errorID));
                    }
                    boolean found = true;
                    for (int k = 0; k < bcomp.length; ++k) {
                        if (bcomp[k] == JFIF_ID[k]) continue;
                        found = false;
                        break;
                    }
                    if (!found) {
                        Utilities.skip(is, len - 2 - bcomp.length);
                        continue;
                    }
                    Utilities.skip(is, 2);
                    int units = is.read();
                    int dx = Jpeg.getShort(is);
                    int dy = Jpeg.getShort(is);
                    if (units == 1) {
                        this.dpiX = dx;
                        this.dpiY = dy;
                    } else if (units == 2) {
                        this.dpiX = (int)((float)dx * 2.54f + 0.5f);
                        this.dpiY = (int)((float)dy * 2.54f + 0.5f);
                    }
                    Utilities.skip(is, len - 2 - bcomp.length - 7);
                    continue;
                }
                if (marker == 238) {
                    String appe;
                    len = Jpeg.getShort(is) - 2;
                    byte[] byteappe = new byte[len];
                    for (int k = 0; k < len; ++k) {
                        byteappe[k] = (byte)is.read();
                    }
                    if (byteappe.length < 12 || !(appe = new String(byteappe, 0, 5, StandardCharsets.ISO_8859_1)).equals("Adobe")) continue;
                    this.invert = true;
                    continue;
                }
                if (marker == 226) {
                    String app2;
                    len = Jpeg.getShort(is) - 2;
                    byte[] byteapp2 = new byte[len];
                    for (int k = 0; k < len; ++k) {
                        byteapp2[k] = (byte)is.read();
                    }
                    if (byteapp2.length < 14 || !(app2 = new String(byteapp2, 0, 11, StandardCharsets.ISO_8859_1)).equals("ICC_PROFILE")) continue;
                    int order = byteapp2[12] & 0xFF;
                    int count = byteapp2[13] & 0xFF;
                    if (order < 1) {
                        order = 1;
                    }
                    if (count < 1) {
                        count = 1;
                    }
                    if (this.icc == null) {
                        this.icc = new byte[count][];
                    }
                    this.icc[order - 1] = byteapp2;
                    continue;
                }
                firstPass = false;
                int markertype = Jpeg.marker(marker);
                if (markertype == 0) {
                    Utilities.skip(is, 2);
                    if (is.read() != 8) {
                        throw new BadElementException(MessageLocalization.getComposedMessage("1.must.have.8.bits.per.component", errorID));
                    }
                    this.scaledHeight = Jpeg.getShort(is);
                    this.setTop(this.scaledHeight);
                    this.scaledWidth = Jpeg.getShort(is);
                    this.setRight(this.scaledWidth);
                    this.colorspace = is.read();
                    this.bpc = 8;
                    break;
                }
                if (markertype == 1) {
                    throw new BadElementException(MessageLocalization.getComposedMessage("1.unsupported.jpeg.marker.2", errorID, String.valueOf(marker)));
                }
                if (markertype == 2) continue;
                Utilities.skip(is, Jpeg.getShort(is) - 2);
            }
        }
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
        if (this.icc != null) {
            int total = 0;
            for (int k = 0; k < this.icc.length; ++k) {
                if (this.icc[k] == null) {
                    this.icc = null;
                    return;
                }
                total += this.icc[k].length - 14;
            }
            byte[] ficc = new byte[total];
            total = 0;
            for (byte[] bytes : this.icc) {
                System.arraycopy(bytes, 14, ficc, total, bytes.length - 14);
                total += bytes.length - 14;
            }
            try {
                ICC_Profile icc_prof = ICC_Profile.getInstance(ficc);
                this.tagICC(icc_prof);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            this.icc = null;
        }
    }
}

