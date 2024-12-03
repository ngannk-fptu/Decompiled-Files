/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.rgbe;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.common.GenericImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.rgbe.InfoHeaderReader;

class RgbeInfo
implements Closeable {
    private static final byte[] HEADER = new byte[]{35, 63, 82, 65, 68, 73, 65, 78, 67, 69};
    private static final Pattern RESOLUTION_STRING = Pattern.compile("-Y (\\d+) \\+X (\\d+)");
    private final InputStream in;
    private GenericImageMetadata metadata;
    private int width = -1;
    private int height = -1;
    private static final byte[] TWO_TWO = new byte[]{2, 2};

    RgbeInfo(ByteSource byteSource) throws IOException {
        this.in = byteSource.getInputStream();
    }

    ImageMetadata getMetadata() throws IOException, ImageReadException {
        if (null == this.metadata) {
            this.readMetadata();
        }
        return this.metadata;
    }

    int getWidth() throws IOException, ImageReadException {
        if (-1 == this.width) {
            this.readDimensions();
        }
        return this.width;
    }

    int getHeight() throws IOException, ImageReadException {
        if (-1 == this.height) {
            this.readDimensions();
        }
        return this.height;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    private void readDimensions() throws IOException, ImageReadException {
        this.getMetadata();
        InfoHeaderReader reader = new InfoHeaderReader(this.in);
        String resolution = reader.readNextLine();
        Matcher matcher = RESOLUTION_STRING.matcher(resolution);
        if (!matcher.matches()) {
            throw new ImageReadException("Invalid HDR resolution string. Only \"-Y N +X M\" is supported. Found \"" + resolution + "\"");
        }
        this.height = Integer.parseInt(matcher.group(1));
        this.width = Integer.parseInt(matcher.group(2));
    }

    private void readMetadata() throws IOException, ImageReadException {
        BinaryFunctions.readAndVerifyBytes(this.in, HEADER, "Not a valid HDR: Incorrect Header");
        InfoHeaderReader reader = new InfoHeaderReader(this.in);
        if (reader.readNextLine().length() != 0) {
            throw new ImageReadException("Not a valid HDR: Incorrect Header");
        }
        this.metadata = new GenericImageMetadata();
        String info = reader.readNextLine();
        while (info.length() != 0) {
            int equals = info.indexOf(61);
            if (equals > 0) {
                String variable = info.substring(0, equals);
                String value = info.substring(equals + 1);
                if ("FORMAT".equals(value) && !"32-bit_rle_rgbe".equals(value)) {
                    throw new ImageReadException("Only 32-bit_rle_rgbe images are supported, trying to read " + value);
                }
                this.metadata.add(variable, value);
            } else {
                this.metadata.add("<command>", info);
            }
            info = reader.readNextLine();
        }
    }

    public float[][] getPixelData() throws IOException, ImageReadException {
        int ht = this.getHeight();
        int wd = this.getWidth();
        if (wd >= 32768) {
            throw new ImageReadException("Scan lines must be less than 32768 bytes long");
        }
        byte[] scanLineBytes = ByteConversions.toBytes((short)wd, ByteOrder.BIG_ENDIAN);
        byte[] rgbe = new byte[wd * 4];
        float[][] out = new float[3][wd * ht];
        for (int i = 0; i < ht; ++i) {
            BinaryFunctions.readAndVerifyBytes(this.in, TWO_TWO, "Scan line " + i + " expected to start with 0x2 0x2");
            BinaryFunctions.readAndVerifyBytes(this.in, scanLineBytes, "Scan line " + i + " length expected");
            RgbeInfo.decompress(this.in, rgbe);
            for (int channel = 0; channel < 3; ++channel) {
                int channelOffset = channel * wd;
                int eOffset = 3 * wd;
                for (int p = 0; p < wd; ++p) {
                    int mantissa = rgbe[p + eOffset] & 0xFF;
                    int pos = p + i * wd;
                    if (0 == mantissa) {
                        out[channel][pos] = 0.0f;
                        continue;
                    }
                    float mult = (float)Math.pow(2.0, mantissa - 136);
                    out[channel][pos] = ((float)(rgbe[p + channelOffset] & 0xFF) + 0.5f) * mult;
                }
            }
        }
        return out;
    }

    private static void decompress(InputStream in, byte[] out) throws IOException, ImageReadException {
        int position = 0;
        int total = out.length;
        while (position < total) {
            int n = in.read();
            if (n < 0) {
                throw new ImageReadException("Error decompressing RGBE file");
            }
            if (n > 128) {
                int value = in.read();
                for (int i = 0; i < (n & 0x7F); ++i) {
                    out[position++] = (byte)value;
                }
                continue;
            }
            for (int i = 0; i < n; ++i) {
                out[position++] = (byte)in.read();
            }
        }
    }
}

