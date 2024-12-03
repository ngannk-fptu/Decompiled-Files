/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.chunks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.png.chunks.PngChunk;

public class PngChunkIccp
extends PngChunk {
    private static final Logger LOGGER = Logger.getLogger(PngChunkIccp.class.getName());
    public final String profileName;
    public final int compressionMethod;
    private final byte[] compressedProfile;
    private final byte[] uncompressedProfile;

    public PngChunkIccp(int length, int chunkType, int crc, byte[] bytes) throws ImageReadException, IOException {
        super(length, chunkType, crc, bytes);
        int index = BinaryFunctions.findNull(bytes);
        if (index < 0) {
            throw new ImageReadException("PngChunkIccp: No Profile Name");
        }
        byte[] nameBytes = new byte[index];
        System.arraycopy(bytes, 0, nameBytes, 0, index);
        this.profileName = new String(nameBytes, StandardCharsets.ISO_8859_1);
        this.compressionMethod = bytes[index + 1];
        int compressedProfileLength = bytes.length - (index + 1 + 1);
        this.compressedProfile = new byte[compressedProfileLength];
        System.arraycopy(bytes, index + 1 + 1, this.compressedProfile, 0, compressedProfileLength);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("ProfileName: " + this.profileName);
            LOGGER.finest("ProfileName.length(): " + this.profileName.length());
            LOGGER.finest("CompressionMethod: " + this.compressionMethod);
            LOGGER.finest("CompressedProfileLength: " + compressedProfileLength);
            LOGGER.finest("bytes.length: " + bytes.length);
        }
        this.uncompressedProfile = BinaryFunctions.getStreamBytes(new InflaterInputStream(new ByteArrayInputStream(this.compressedProfile)));
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("UncompressedProfile: " + bytes.length);
        }
    }

    public byte[] getUncompressedProfile() {
        return (byte[])this.uncompressedProfile.clone();
    }
}

