/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.file;

public class DataFileConstants {
    public static final byte VERSION = 1;
    public static final byte[] MAGIC = new byte[]{79, 98, 106, 1};
    public static final long FOOTER_BLOCK = -1L;
    public static final int SYNC_SIZE = 16;
    public static final int DEFAULT_SYNC_INTERVAL = 64000;
    public static final String SCHEMA = "avro.schema";
    public static final String CODEC = "avro.codec";
    public static final String NULL_CODEC = "null";
    public static final String DEFLATE_CODEC = "deflate";
    public static final String SNAPPY_CODEC = "snappy";
    public static final String BZIP2_CODEC = "bzip2";
    public static final String XZ_CODEC = "xz";
    public static final String ZSTANDARD_CODEC = "zstandard";

    private DataFileConstants() {
    }
}

