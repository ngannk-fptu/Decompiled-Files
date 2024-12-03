/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;

public class ZlibDeflate {
    public static byte[] decompress(byte[] bytes, int expectedSize) throws ImageReadException {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(bytes);
            byte[] result = new byte[expectedSize];
            inflater.inflate(result);
            return result;
        }
        catch (DataFormatException e) {
            throw new ImageReadException("Unable to decompress image", e);
        }
    }

    public static byte[] compress(byte[] bytes) throws ImageWriteException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length / 2);
        try (DeflaterOutputStream compressOut = new DeflaterOutputStream(out);){
            compressOut.write(bytes);
        }
        catch (IOException e) {
            throw new ImageWriteException("Unable to compress image", e);
        }
        return out.toByteArray();
    }
}

