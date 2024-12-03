/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.ByteArrayOutputStream
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoader;
import org.apache.xmlgraphics.image.loader.impl.ImageRawJPEG;
import org.apache.xmlgraphics.image.loader.impl.JPEGConstants;
import org.apache.xmlgraphics.image.loader.impl.JPEGFile;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;
import org.apache.xmlgraphics.io.XmlSourceUtil;
import org.apache.xmlgraphics.java2d.color.ColorSpaces;
import org.apache.xmlgraphics.java2d.color.profile.ColorProfileUtil;

public class ImageLoaderRawJPEG
extends AbstractImageLoader
implements JPEGConstants {
    protected static final Log log = LogFactory.getLog(ImageLoaderRawJPEG.class);

    @Override
    public ImageFlavor getTargetFlavor() {
        return ImageFlavor.RAW_JPEG;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    @Override
    public Image loadImage(ImageInfo info, Map hints, ImageSessionContext session) throws ImageException, IOException {
        block34: {
            if (!"image/jpeg".equals(info.getMimeType())) {
                throw new IllegalArgumentException("ImageInfo must be from a image with MIME type: image/jpeg");
            }
            colorSpace = null;
            appeFound = false;
            sofType = 0;
            iccStream = null;
            src = session.needSource(info.getOriginalURI());
            in = ImageUtil.needImageInputStream(src);
            jpeg = new JPEGFile(in);
            in.mark();
            block20: while (true) {
                block21: while (true) {
                    segID = jpeg.readMarkerSegment();
                    if (ImageLoaderRawJPEG.log.isTraceEnabled()) {
                        ImageLoaderRawJPEG.log.trace((Object)("Seg Marker: " + Integer.toHexString(segID)));
                    }
                    switch (segID) {
                        case 217: {
                            ImageLoaderRawJPEG.log.trace((Object)"EOI found. Stopping.");
                            ** break;
lbl21:
                            // 1 sources

                            break block34;
                        }
                        case 218: {
                            ImageLoaderRawJPEG.log.trace((Object)"SOS found. Stopping early.");
                            ** break;
lbl25:
                            // 1 sources

                            break block34;
                        }
                        case 0: 
                        case 216: {
                            continue block20;
                        }
                        case 192: 
                        case 193: 
                        case 194: 
                        case 202: {
                            sofType = segID;
                            if (ImageLoaderRawJPEG.log.isTraceEnabled()) {
                                ImageLoaderRawJPEG.log.trace((Object)("SOF: " + Integer.toHexString(sofType)));
                            }
                            in.mark();
                            try {
                                reclen = jpeg.readSegmentLength();
                                in.skipBytes(1);
                                in.skipBytes(2);
                                in.skipBytes(2);
                                numComponents = in.readUnsignedByte();
                                if (numComponents != 1) ** GOTO lbl45
                                colorSpace = ColorSpace.getInstance(1003);
                                ** GOTO lbl55
lbl45:
                                // 1 sources

                                if (numComponents != 3) ** GOTO lbl48
                                colorSpace = ColorSpace.getInstance(1004);
                                ** GOTO lbl55
lbl48:
                                // 1 sources

                                if (numComponents != 4) ** GOTO lbl51
                                colorSpace = ColorSpaces.getDeviceCMYKColorSpace();
                                ** GOTO lbl55
lbl51:
                                // 1 sources

                                throw new ImageException("Unsupported ColorSpace for image " + info + ". The number of components supported are 1, 3 and 4.");
                            }
                            finally {
                                in.reset();
                            }
lbl55:
                            // 3 sources

                            in.skipBytes(reclen);
                            continue block20;
                        }
                        case 226: {
                            in.mark();
                            try {
                                reclen = jpeg.readSegmentLength();
                                iccString = new byte[11];
                                in.readFully(iccString);
                                in.skipBytes(1);
                                if ("ICC_PROFILE".equals(new String(iccString, "US-ASCII"))) {
                                    in.skipBytes(2);
                                    payloadSize = reclen - 2 - 12 - 2;
                                    if (this.ignoreColorProfile(hints)) {
                                        ImageLoaderRawJPEG.log.debug((Object)"Ignoring ICC profile data in JPEG");
                                        in.skipBytes(payloadSize);
                                    } else {
                                        buf = new byte[payloadSize];
                                        in.readFully(buf);
                                        if (iccStream == null) {
                                            if (ImageLoaderRawJPEG.log.isDebugEnabled()) {
                                                ImageLoaderRawJPEG.log.debug((Object)"JPEG has an ICC profile");
                                                din = new DataInputStream(new ByteArrayInputStream(buf));
                                                ImageLoaderRawJPEG.log.debug((Object)("Declared ICC profile size: " + din.readInt()));
                                            }
                                            iccStream = new ByteArrayOutputStream();
                                        }
                                        iccStream.write(buf);
                                    }
                                }
                            }
                            finally {
                                in.reset();
                            }
                            in.skipBytes(reclen);
                            continue block20;
                        }
                        case 238: {
                            in.mark();
                            try {
                                reclen = jpeg.readSegmentLength();
                                adobeHeader = new byte[5];
                                in.readFully(adobeHeader);
                                if ("Adobe".equals(new String(adobeHeader, "US-ASCII"))) {
                                    appeFound = true;
                                }
                            }
                            finally {
                                in.reset();
                            }
                            in.skipBytes(reclen);
                            continue block20;
                        }
                        default: {
                            jpeg.skipCurrentMarkerSegment();
                            continue block21;
                        }
                    }
                    break;
                }
                break;
            }
            finally {
                in.reset();
            }
        }
        iccProfile = this.buildICCProfile(info, colorSpace, iccStream);
        if (iccProfile == null && colorSpace == null) {
            throw new ImageException("ColorSpace could not be identified for JPEG image " + info);
        }
        invertImage = false;
        if (appeFound && colorSpace.getType() == 9) {
            if (ImageLoaderRawJPEG.log.isDebugEnabled()) {
                ImageLoaderRawJPEG.log.debug((Object)("JPEG has an Adobe APPE marker. Note: CMYK Image will be inverted. (" + info.getOriginalURI() + ")"));
            }
            invertImage = true;
        }
        rawImage = new ImageRawJPEG(info, XmlSourceUtil.needInputStream(src), sofType, colorSpace, iccProfile, invertImage);
        return rawImage;
    }

    private ICC_Profile buildICCProfile(ImageInfo info, ColorSpace colorSpace, ByteArrayOutputStream iccStream) throws IOException, ImageException {
        if (iccStream != null && iccStream.size() > 0) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Effective ICC profile size: " + iccStream.size()));
            }
            int alignment = 4;
            int padding = (4 - iccStream.size() % 4) % 4;
            if (padding != 0) {
                try {
                    iccStream.write(new byte[padding]);
                }
                catch (IOException ioe) {
                    throw new IOException("Error while aligning ICC stream: " + ioe.getMessage());
                }
            }
            ICC_Profile iccProfile = null;
            try {
                iccProfile = ColorProfileUtil.getICC_Profile(iccStream.toByteArray());
                if (log.isDebugEnabled()) {
                    log.debug((Object)("JPEG has an ICC profile: " + iccProfile.toString()));
                }
            }
            catch (IllegalArgumentException iae) {
                log.warn((Object)("An ICC profile is present in the JPEG file but it is invalid (" + iae.getMessage() + "). The color profile will be ignored. (" + info.getOriginalURI() + ")"));
                return null;
            }
            if (iccProfile.getNumComponents() != colorSpace.getNumComponents()) {
                log.warn((Object)("The number of components of the ICC profile (" + iccProfile.getNumComponents() + ") doesn't match the image (" + colorSpace.getNumComponents() + "). Ignoring the ICC color profile."));
                return null;
            }
            return iccProfile;
        }
        return null;
    }
}

