/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.nio.ByteBuffer;

class ZigZagEncoding {
    ZigZagEncoding() {
    }

    static void putLong(ByteBuffer buffer, long value) {
        if ((value = value << 1 ^ value >> 63) >>> 7 == 0L) {
            buffer.put((byte)value);
        } else {
            buffer.put((byte)(value & 0x7FL | 0x80L));
            if (value >>> 14 == 0L) {
                buffer.put((byte)(value >>> 7));
            } else {
                buffer.put((byte)(value >>> 7 | 0x80L));
                if (value >>> 21 == 0L) {
                    buffer.put((byte)(value >>> 14));
                } else {
                    buffer.put((byte)(value >>> 14 | 0x80L));
                    if (value >>> 28 == 0L) {
                        buffer.put((byte)(value >>> 21));
                    } else {
                        buffer.put((byte)(value >>> 21 | 0x80L));
                        if (value >>> 35 == 0L) {
                            buffer.put((byte)(value >>> 28));
                        } else {
                            buffer.put((byte)(value >>> 28 | 0x80L));
                            if (value >>> 42 == 0L) {
                                buffer.put((byte)(value >>> 35));
                            } else {
                                buffer.put((byte)(value >>> 35 | 0x80L));
                                if (value >>> 49 == 0L) {
                                    buffer.put((byte)(value >>> 42));
                                } else {
                                    buffer.put((byte)(value >>> 42 | 0x80L));
                                    if (value >>> 56 == 0L) {
                                        buffer.put((byte)(value >>> 49));
                                    } else {
                                        buffer.put((byte)(value >>> 49 | 0x80L));
                                        buffer.put((byte)(value >>> 56));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static void putInt(ByteBuffer buffer, int value) {
        if ((value = value << 1 ^ value >> 31) >>> 7 == 0) {
            buffer.put((byte)value);
        } else {
            buffer.put((byte)(value & 0x7F | 0x80));
            if (value >>> 14 == 0) {
                buffer.put((byte)(value >>> 7));
            } else {
                buffer.put((byte)(value >>> 7 | 0x80));
                if (value >>> 21 == 0) {
                    buffer.put((byte)(value >>> 14));
                } else {
                    buffer.put((byte)(value >>> 14 | 0x80));
                    if (value >>> 28 == 0) {
                        buffer.put((byte)(value >>> 21));
                    } else {
                        buffer.put((byte)(value >>> 21 | 0x80));
                        buffer.put((byte)(value >>> 28));
                    }
                }
            }
        }
    }

    static long getLong(ByteBuffer buffer) {
        long v = buffer.get();
        long value = v & 0x7FL;
        if ((v & 0x80L) != 0L) {
            v = buffer.get();
            value |= (v & 0x7FL) << 7;
            if ((v & 0x80L) != 0L) {
                v = buffer.get();
                value |= (v & 0x7FL) << 14;
                if ((v & 0x80L) != 0L) {
                    v = buffer.get();
                    value |= (v & 0x7FL) << 21;
                    if ((v & 0x80L) != 0L) {
                        v = buffer.get();
                        value |= (v & 0x7FL) << 28;
                        if ((v & 0x80L) != 0L) {
                            v = buffer.get();
                            value |= (v & 0x7FL) << 35;
                            if ((v & 0x80L) != 0L) {
                                v = buffer.get();
                                value |= (v & 0x7FL) << 42;
                                if ((v & 0x80L) != 0L) {
                                    v = buffer.get();
                                    value |= (v & 0x7FL) << 49;
                                    if ((v & 0x80L) != 0L) {
                                        v = buffer.get();
                                        value |= v << 56;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        value = value >>> 1 ^ -(value & 1L);
        return value;
    }

    static int getInt(ByteBuffer buffer) {
        byte v = buffer.get();
        int value = v & 0x7F;
        if ((v & 0x80) != 0) {
            v = buffer.get();
            value |= (v & 0x7F) << 7;
            if ((v & 0x80) != 0) {
                v = buffer.get();
                value |= (v & 0x7F) << 14;
                if ((v & 0x80) != 0) {
                    v = buffer.get();
                    value |= (v & 0x7F) << 21;
                    if ((v & 0x80) != 0) {
                        v = buffer.get();
                        value |= (v & 0x7F) << 28;
                    }
                }
            }
        }
        value = value >>> 1 ^ -(value & 1);
        return value;
    }
}

