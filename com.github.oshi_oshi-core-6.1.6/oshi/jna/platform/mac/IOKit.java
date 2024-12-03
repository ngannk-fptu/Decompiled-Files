/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.NativeLong
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$FieldOrder
 *  com.sun.jna.platform.mac.IOKit
 *  com.sun.jna.platform.mac.IOKit$IOConnect
 *  com.sun.jna.ptr.NativeLongByReference
 */
package oshi.jna.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.ptr.NativeLongByReference;

public interface IOKit
extends com.sun.jna.platform.mac.IOKit {
    public static final IOKit INSTANCE = (IOKit)Native.load((String)"IOKit", IOKit.class);

    public int IOConnectCallStructMethod(IOKit.IOConnect var1, int var2, Structure var3, NativeLong var4, Structure var5, NativeLongByReference var6);

    @Structure.FieldOrder(value={"key", "dataSize", "dataType", "bytes"})
    public static class SMCVal
    extends Structure {
        public byte[] key = new byte[5];
        public int dataSize;
        public byte[] dataType = new byte[5];
        public byte[] bytes = new byte[32];
    }

    @Structure.FieldOrder(value={"key", "vers", "pLimitData", "keyInfo", "result", "status", "data8", "data32", "bytes"})
    public static class SMCKeyData
    extends Structure {
        public int key;
        public SMCKeyDataVers vers;
        public SMCKeyDataPLimitData pLimitData;
        public SMCKeyDataKeyInfo keyInfo;
        public byte result;
        public byte status;
        public byte data8;
        public int data32;
        public byte[] bytes = new byte[32];
    }

    @Structure.FieldOrder(value={"dataSize", "dataType", "dataAttributes"})
    public static class SMCKeyDataKeyInfo
    extends Structure {
        public int dataSize;
        public int dataType;
        public byte dataAttributes;
    }

    @Structure.FieldOrder(value={"version", "length", "cpuPLimit", "gpuPLimit", "memPLimit"})
    public static class SMCKeyDataPLimitData
    extends Structure {
        public short version;
        public short length;
        public int cpuPLimit;
        public int gpuPLimit;
        public int memPLimit;
    }

    @Structure.FieldOrder(value={"major", "minor", "build", "reserved", "release"})
    public static class SMCKeyDataVers
    extends Structure {
        public byte major;
        public byte minor;
        public byte build;
        public byte reserved;
        public short release;
    }
}

