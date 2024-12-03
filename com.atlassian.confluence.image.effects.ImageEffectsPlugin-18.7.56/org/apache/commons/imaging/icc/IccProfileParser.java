/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.icc;

import java.awt.color.ICC_Profile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.common.BinaryFileParser;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceArray;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.icc.CachingInputStream;
import org.apache.commons.imaging.icc.IccProfileInfo;
import org.apache.commons.imaging.icc.IccTag;
import org.apache.commons.imaging.icc.IccTagType;
import org.apache.commons.imaging.icc.IccTagTypes;

public class IccProfileParser
extends BinaryFileParser {
    private static final Logger LOGGER = Logger.getLogger(IccProfileParser.class.getName());

    public IccProfileParser() {
        this.setByteOrder(ByteOrder.BIG_ENDIAN);
    }

    public IccProfileInfo getICCProfileInfo(ICC_Profile iccProfile) {
        if (iccProfile == null) {
            return null;
        }
        return this.getICCProfileInfo(new ByteSourceArray(iccProfile.getData()));
    }

    public IccProfileInfo getICCProfileInfo(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return this.getICCProfileInfo(new ByteSourceArray(bytes));
    }

    public IccProfileInfo getICCProfileInfo(File file) {
        if (file == null) {
            return null;
        }
        return this.getICCProfileInfo(new ByteSourceFile(file));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public IccProfileInfo getICCProfileInfo(ByteSource byteSource) {
        InputStream is = null;
        try {
            is = byteSource.getInputStream();
            IccProfileInfo result = this.readICCProfileInfo(is);
            if (result == null) {
                IccProfileInfo iccProfileInfo = null;
                return iccProfileInfo;
            }
            is.close();
            is = null;
            for (IccTag tag : result.getTags()) {
                byte[] bytes = byteSource.getBlock(tag.offset, tag.length);
                tag.setData(bytes);
            }
            IccProfileInfo iccProfileInfo = result;
            return iccProfileInfo;
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return null;
    }

    private IccProfileInfo readICCProfileInfo(InputStream is) {
        CachingInputStream cis = new CachingInputStream(is);
        is = cis;
        try {
            int profileSize = BinaryFunctions.read4Bytes("ProfileSize", is, "Not a Valid ICC Profile", this.getByteOrder());
            int cmmTypeSignature = BinaryFunctions.read4Bytes("Signature", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("CMMTypeSignature", cmmTypeSignature);
            }
            int profileVersion = BinaryFunctions.read4Bytes("ProfileVersion", is, "Not a Valid ICC Profile", this.getByteOrder());
            int profileDeviceClassSignature = BinaryFunctions.read4Bytes("ProfileDeviceClassSignature", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("ProfileDeviceClassSignature", profileDeviceClassSignature);
            }
            int colorSpace = BinaryFunctions.read4Bytes("ColorSpace", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("ColorSpace", colorSpace);
            }
            int profileConnectionSpace = BinaryFunctions.read4Bytes("ProfileConnectionSpace", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("ProfileConnectionSpace", profileConnectionSpace);
            }
            BinaryFunctions.skipBytes(is, 12L, "Not a Valid ICC Profile");
            int profileFileSignature = BinaryFunctions.read4Bytes("ProfileFileSignature", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("ProfileFileSignature", profileFileSignature);
            }
            int primaryPlatformSignature = BinaryFunctions.read4Bytes("PrimaryPlatformSignature", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("PrimaryPlatformSignature", primaryPlatformSignature);
            }
            int variousFlags = BinaryFunctions.read4Bytes("VariousFlags", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("VariousFlags", profileFileSignature);
            }
            int deviceManufacturer = BinaryFunctions.read4Bytes("DeviceManufacturer", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("DeviceManufacturer", deviceManufacturer);
            }
            int deviceModel = BinaryFunctions.read4Bytes("DeviceModel", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("DeviceModel", deviceModel);
            }
            BinaryFunctions.skipBytes(is, 8L, "Not a Valid ICC Profile");
            int renderingIntent = BinaryFunctions.read4Bytes("RenderingIntent", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("RenderingIntent", renderingIntent);
            }
            BinaryFunctions.skipBytes(is, 12L, "Not a Valid ICC Profile");
            int profileCreatorSignature = BinaryFunctions.read4Bytes("ProfileCreatorSignature", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("ProfileCreatorSignature", profileCreatorSignature);
            }
            byte[] profileId = null;
            BinaryFunctions.skipBytes(is, 16L, "Not a Valid ICC Profile");
            BinaryFunctions.skipBytes(is, 28L, "Not a Valid ICC Profile");
            int tagCount = BinaryFunctions.read4Bytes("TagCount", is, "Not a Valid ICC Profile", this.getByteOrder());
            IccTag[] tags = new IccTag[tagCount];
            for (int i = 0; i < tagCount; ++i) {
                IccTag tag;
                int tagSignature = BinaryFunctions.read4Bytes("TagSignature[" + i + "]", is, "Not a Valid ICC Profile", this.getByteOrder());
                int offsetToData = BinaryFunctions.read4Bytes("OffsetToData[" + i + "]", is, "Not a Valid ICC Profile", this.getByteOrder());
                int elementSize = BinaryFunctions.read4Bytes("ElementSize[" + i + "]", is, "Not a Valid ICC Profile", this.getByteOrder());
                IccTagType fIccTagType = this.getIccTagType(tagSignature);
                tags[i] = tag = new IccTag(tagSignature, offsetToData, elementSize, fIccTagType);
            }
            while (is.read() >= 0) {
            }
            byte[] data = cis.getCache();
            if (data.length < profileSize) {
                throw new IOException("Couldn't read ICC Profile.");
            }
            IccProfileInfo result = new IccProfileInfo(data, profileSize, cmmTypeSignature, profileVersion, profileDeviceClassSignature, colorSpace, profileConnectionSpace, profileFileSignature, primaryPlatformSignature, variousFlags, deviceManufacturer, deviceModel, renderingIntent, profileCreatorSignature, profileId, tags);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("issRGB: " + result.issRGB());
            }
            return result;
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    private IccTagType getIccTagType(int quad) {
        for (IccTagTypes iccTagType : IccTagTypes.values()) {
            if (iccTagType.getSignature() != quad) continue;
            return iccTagType;
        }
        return null;
    }

    public boolean issRGB(ICC_Profile iccProfile) throws IOException {
        return this.issRGB(new ByteSourceArray(iccProfile.getData()));
    }

    public boolean issRGB(byte[] bytes) throws IOException {
        return this.issRGB(new ByteSourceArray(bytes));
    }

    public boolean issRGB(File file) throws IOException {
        return this.issRGB(new ByteSourceFile(file));
    }

    public boolean issRGB(ByteSource byteSource) throws IOException {
        try (InputStream is = byteSource.getInputStream();){
            boolean result;
            BinaryFunctions.read4Bytes("ProfileSize", is, "Not a Valid ICC Profile", this.getByteOrder());
            BinaryFunctions.skipBytes(is, 20L);
            BinaryFunctions.skipBytes(is, 12L, "Not a Valid ICC Profile");
            BinaryFunctions.skipBytes(is, 12L);
            int deviceManufacturer = BinaryFunctions.read4Bytes("ProfileFileSignature", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("DeviceManufacturer", deviceManufacturer);
            }
            int deviceModel = BinaryFunctions.read4Bytes("DeviceModel", is, "Not a Valid ICC Profile", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("DeviceModel", deviceModel);
            }
            boolean bl = result = deviceManufacturer == 1229275936 && deviceModel == 1934772034;
            return bl;
        }
    }
}

