/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport;

import org.apache.catalina.tribes.io.XByteBuffer;

public class Constants {
    public static final String Package = "org.apache.catalina.tribes.transport";
    public static final int DEFAULT_CLUSTER_MSG_BUFFER_SIZE = 65536;
    public static final int DEFAULT_CLUSTER_ACK_BUFFER_SIZE = 25188;
    public static final byte[] ACK_DATA = new byte[]{6, 2, 3};
    public static final byte[] FAIL_ACK_DATA = new byte[]{11, 0, 5};
    public static final byte[] ACK_COMMAND = XByteBuffer.createDataPackage(ACK_DATA);
    public static final byte[] FAIL_ACK_COMMAND = XByteBuffer.createDataPackage(FAIL_ACK_DATA);
}

