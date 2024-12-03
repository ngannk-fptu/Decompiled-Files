/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.icc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.icc.IccTagDataType;

public enum IccTagDataTypes implements IccTagDataType
{
    DESC_TYPE("descType", 1684370275){

        @Override
        public void dump(String prefix, byte[] bytes) throws ImageReadException, IOException {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);){
                BinaryFunctions.read4Bytes("type_signature", bis, "ICC: corrupt tag data", ByteOrder.BIG_ENDIAN);
                BinaryFunctions.read4Bytes("ignore", bis, "ICC: corrupt tag data", ByteOrder.BIG_ENDIAN);
                int stringLength = BinaryFunctions.read4Bytes("stringLength", bis, "ICC: corrupt tag data", ByteOrder.BIG_ENDIAN);
                String s = new String(bytes, 12, stringLength - 1, StandardCharsets.US_ASCII);
                LOGGER.fine(prefix + "s: '" + s + "'");
            }
        }
    }
    ,
    DATA_TYPE("dataType", 1684108385){

        @Override
        public void dump(String prefix, byte[] bytes) throws ImageReadException, IOException {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);){
                BinaryFunctions.read4Bytes("type_signature", bis, "ICC: corrupt tag data", ByteOrder.BIG_ENDIAN);
            }
        }
    }
    ,
    MULTI_LOCALIZED_UNICODE_TYPE("multiLocalizedUnicodeType", 1835824483){

        @Override
        public void dump(String prefix, byte[] bytes) throws ImageReadException, IOException {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);){
                BinaryFunctions.read4Bytes("type_signature", bis, "ICC: corrupt tag data", ByteOrder.BIG_ENDIAN);
            }
        }
    }
    ,
    SIGNATURE_TYPE("signatureType", 1936287520){

        @Override
        public void dump(String prefix, byte[] bytes) throws ImageReadException, IOException {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);){
                BinaryFunctions.read4Bytes("type_signature", bis, "ICC: corrupt tag data", ByteOrder.BIG_ENDIAN);
                BinaryFunctions.read4Bytes("ignore", bis, "ICC: corrupt tag data", ByteOrder.BIG_ENDIAN);
                int thesignature = BinaryFunctions.read4Bytes("thesignature ", bis, "ICC: corrupt tag data", ByteOrder.BIG_ENDIAN);
                LOGGER.fine(prefix + "thesignature: " + Integer.toHexString(thesignature) + " (" + new String(new byte[]{(byte)(0xFF & thesignature >> 24), (byte)(0xFF & thesignature >> 16), (byte)(0xFF & thesignature >> 8), (byte)(0xFF & thesignature >> 0)}, StandardCharsets.US_ASCII) + ")");
            }
        }
    }
    ,
    TEXT_TYPE("textType", 1952807028){

        @Override
        public void dump(String prefix, byte[] bytes) throws ImageReadException, IOException {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);){
                BinaryFunctions.read4Bytes("type_signature", bis, "ICC: corrupt tag data", ByteOrder.BIG_ENDIAN);
                BinaryFunctions.read4Bytes("ignore", bis, "ICC: corrupt tag data", ByteOrder.BIG_ENDIAN);
                String s = new String(bytes, 8, bytes.length - 8, StandardCharsets.US_ASCII);
                LOGGER.fine(prefix + "s: '" + s + "'");
            }
        }
    };

    private static final Logger LOGGER;
    public final String name;
    public final int signature;

    private IccTagDataTypes(String name, int signature) {
        this.name = name;
        this.signature = signature;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSignature() {
        return this.signature;
    }

    static {
        LOGGER = Logger.getLogger(IccTagDataTypes.class.getName());
    }
}

