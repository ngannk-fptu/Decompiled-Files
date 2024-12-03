/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$FieldOrder
 *  com.sun.jna.platform.linux.LibC
 */
package oshi.jna.platform.linux;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.linux.LibC;
import oshi.jna.platform.unix.CLibrary;

public interface LinuxLibc
extends LibC,
CLibrary {
    public static final LinuxLibc INSTANCE = (LinuxLibc)Native.load((String)"c", LinuxLibc.class);

    public LinuxUtmpx getutxent();

    @Structure.FieldOrder(value={"tv_sec", "tv_usec"})
    public static class Ut_Tv
    extends Structure {
        public int tv_sec;
        public int tv_usec;
    }

    @Structure.FieldOrder(value={"e_termination", "e_exit"})
    public static class Exit_status
    extends Structure {
        public short e_termination;
        public short e_exit;
    }

    @Structure.FieldOrder(value={"ut_type", "ut_pid", "ut_line", "ut_id", "ut_user", "ut_host", "ut_exit", "ut_session", "ut_tv", "ut_addr_v6", "reserved"})
    public static class LinuxUtmpx
    extends Structure {
        public short ut_type;
        public int ut_pid;
        public byte[] ut_line = new byte[32];
        public byte[] ut_id = new byte[4];
        public byte[] ut_user = new byte[32];
        public byte[] ut_host = new byte[256];
        public Exit_status ut_exit;
        public int ut_session;
        public Ut_Tv ut_tv;
        public int[] ut_addr_v6 = new int[4];
        public byte[] reserved = new byte[20];
    }
}

