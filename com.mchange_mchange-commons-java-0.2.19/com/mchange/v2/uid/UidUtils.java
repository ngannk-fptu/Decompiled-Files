/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.uid;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.SecureRandom;

public final class UidUtils {
    static final MLogger logger = MLog.getLogger(UidUtils.class);
    public static final String VM_ID = UidUtils.generateVmId();
    private static long within_vm_seq_counter = 0L;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String generateVmId() {
        FilterOutputStream filterOutputStream = null;
        FilterInputStream filterInputStream = null;
        try {
            byte[] byArray;
            SecureRandom secureRandom = new SecureRandom();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            filterOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                filterOutputStream.write(InetAddress.getLocalHost().getAddress());
            }
            catch (Exception exception) {
                if (logger.isLoggable(MLevel.INFO)) {
                    logger.log(MLevel.INFO, "Failed to get local InetAddress for VMID. This is unlikely to matter. At all. We'll add some extra randomness", exception);
                }
                ((DataOutputStream)filterOutputStream).write(secureRandom.nextInt());
            }
            ((DataOutputStream)filterOutputStream).writeLong(System.currentTimeMillis());
            ((DataOutputStream)filterOutputStream).write(secureRandom.nextInt());
            int n = byteArrayOutputStream.size() % 4;
            if (n > 0) {
                int n2 = 4 - n;
                byArray = new byte[n2];
                secureRandom.nextBytes(byArray);
                filterOutputStream.write(byArray);
            }
            StringBuffer stringBuffer = new StringBuffer(32);
            byArray = byteArrayOutputStream.toByteArray();
            filterInputStream = new DataInputStream(new ByteArrayInputStream(byArray));
            int n3 = byArray.length / 4;
            for (int i = 0; i < n3; ++i) {
                int n4 = ((DataInputStream)filterInputStream).readInt();
                long l = (long)n4 & 0xFFFFFFFFL;
                stringBuffer.append(Long.toString(l, 36));
            }
            String string = stringBuffer.toString();
            return string;
        }
        catch (IOException iOException) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "Bizarro! IOException while reading/writing from ByteArray-based streams? We're skipping the VMID thing. It almost certainly doesn't matter, but please report the error.", iOException);
            }
            String string = "";
            return string;
        }
        finally {
            try {
                if (filterOutputStream != null) {
                    filterOutputStream.close();
                }
            }
            catch (IOException iOException) {
                logger.log(MLevel.WARNING, "Huh? Exception close()ing a byte-array bound OutputStream.", iOException);
            }
            try {
                if (filterInputStream != null) {
                    filterInputStream.close();
                }
            }
            catch (IOException iOException) {
                logger.log(MLevel.WARNING, "Huh? Exception close()ing a byte-array bound IntputStream.", iOException);
            }
        }
    }

    private static synchronized long nextWithinVmSeq() {
        return ++within_vm_seq_counter;
    }

    public static String allocateWithinVmSequential() {
        return VM_ID + "#" + UidUtils.nextWithinVmSeq();
    }

    private UidUtils() {
    }
}

