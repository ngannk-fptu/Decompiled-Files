/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$FieldOrder
 *  com.sun.jna.win32.W32APITypeMapper
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.W32APITypeMapper;

public interface LMShare {
    public static final int STYPE_DISKTREE = 0;
    public static final int STYPE_PRINTQ = 1;
    public static final int STYPE_DEVICE = 2;
    public static final int STYPE_IPC = 3;
    public static final int STYPE_TEMPORARY = 0x40000000;
    public static final int STYPE_SPECIAL = Integer.MIN_VALUE;

    @Structure.FieldOrder(value={"shi502_netname", "shi502_type", "shi502_remark", "shi502_permissions", "shi502_max_uses", "shi502_current_uses", "shi502_path", "shi502_passwd", "shi502_reserved", "shi502_security_descriptor"})
    public static class SHARE_INFO_502
    extends Structure {
        public String shi502_netname;
        public int shi502_type;
        public String shi502_remark;
        public int shi502_permissions;
        public int shi502_max_uses;
        public int shi502_current_uses;
        public String shi502_path;
        public String shi502_passwd;
        public int shi502_reserved;
        public Pointer shi502_security_descriptor;

        public SHARE_INFO_502() {
            super(W32APITypeMapper.UNICODE);
        }

        public SHARE_INFO_502(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            this.read();
        }
    }

    @Structure.FieldOrder(value={"shi2_netname", "shi2_type", "shi2_remark", "shi2_permissions", "shi2_max_uses", "shi2_current_uses", "shi2_path", "shi2_passwd"})
    public static class SHARE_INFO_2
    extends Structure {
        public String shi2_netname;
        public int shi2_type;
        public String shi2_remark;
        public int shi2_permissions;
        public int shi2_max_uses;
        public int shi2_current_uses;
        public String shi2_path;
        public String shi2_passwd;

        public SHARE_INFO_2() {
            super(W32APITypeMapper.UNICODE);
        }

        public SHARE_INFO_2(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            this.read();
        }
    }
}

