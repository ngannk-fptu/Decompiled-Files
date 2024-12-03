/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$FieldOrder
 *  com.sun.jna.Union
 */
package com.sun.jna.platform.unix.solaris;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;

public interface LibKstat
extends Library {
    public static final LibKstat INSTANCE = (LibKstat)Native.load((String)"kstat", LibKstat.class);
    public static final byte KSTAT_TYPE_RAW = 0;
    public static final byte KSTAT_TYPE_NAMED = 1;
    public static final byte KSTAT_TYPE_INTR = 2;
    public static final byte KSTAT_TYPE_IO = 3;
    public static final byte KSTAT_TYPE_TIMER = 4;
    public static final byte KSTAT_DATA_CHAR = 0;
    public static final byte KSTAT_DATA_INT32 = 1;
    public static final byte KSTAT_DATA_UINT32 = 2;
    public static final byte KSTAT_DATA_INT64 = 3;
    public static final byte KSTAT_DATA_UINT64 = 4;
    public static final byte KSTAT_DATA_STRING = 9;
    public static final int KSTAT_INTR_HARD = 0;
    public static final int KSTAT_INTR_SOFT = 1;
    public static final int KSTAT_INTR_WATCHDOG = 2;
    public static final int KSTAT_INTR_SPURIOUS = 3;
    public static final int KSTAT_INTR_MULTSVC = 4;
    public static final int KSTAT_NUM_INTRS = 5;
    public static final int KSTAT_STRLEN = 31;
    public static final int EAGAIN = 11;

    public KstatCtl kstat_open();

    public int kstat_close(KstatCtl var1);

    public int kstat_chain_update(KstatCtl var1);

    public int kstat_read(KstatCtl var1, Kstat var2, Pointer var3);

    public int kstat_write(KstatCtl var1, Kstat var2, Pointer var3);

    public Kstat kstat_lookup(KstatCtl var1, String var2, int var3, String var4);

    public Pointer kstat_data_lookup(Kstat var1, String var2);

    @Structure.FieldOrder(value={"kc_chain_id", "kc_chain", "kc_kd"})
    public static class KstatCtl
    extends Structure {
        public int kc_chain_id;
        public Kstat kc_chain;
        public int kc_kd;
    }

    @Structure.FieldOrder(value={"nread", "nwritten", "reads", "writes", "wtime", "wlentime", "wlastupdate", "rtime", "rlentime", "rlastupdate", "wcnt", "rcnt"})
    public static class KstatIO
    extends Structure {
        public long nread;
        public long nwritten;
        public int reads;
        public int writes;
        public long wtime;
        public long wlentime;
        public long wlastupdate;
        public long rtime;
        public long rlentime;
        public long rlastupdate;
        public int wcnt;
        public int rcnt;

        public KstatIO() {
        }

        public KstatIO(Pointer p) {
            super(p);
            this.read();
        }
    }

    @Structure.FieldOrder(value={"name", "resv", "num_events", "elapsed_time", "min_time", "max_time", "start_time", "stop_time"})
    public static class KstatTimer
    extends Structure {
        public byte[] name = new byte[31];
        public byte resv;
        public long num_events;
        public long elapsed_time;
        public long min_time;
        public long max_time;
        public long start_time;
        public long stop_time;
    }

    @Structure.FieldOrder(value={"intrs"})
    public static class KstatIntr
    extends Structure {
        public int[] intrs = new int[5];
    }

    @Structure.FieldOrder(value={"name", "data_type", "value"})
    public static class KstatNamed
    extends Structure {
        public byte[] name = new byte[31];
        public byte data_type;
        public UNION value;

        public KstatNamed() {
        }

        public KstatNamed(Pointer p) {
            super(p);
            this.read();
        }

        public void read() {
            super.read();
            switch (this.data_type) {
                case 0: {
                    this.value.setType(byte[].class);
                    break;
                }
                case 9: {
                    this.value.setType(UNION.STR.class);
                    break;
                }
                case 1: 
                case 2: {
                    this.value.setType(Integer.TYPE);
                    break;
                }
                case 3: 
                case 4: {
                    this.value.setType(Long.TYPE);
                    break;
                }
            }
            this.value.read();
        }

        public static class UNION
        extends Union {
            public byte[] charc = new byte[16];
            public int i32;
            public int ui32;
            public long i64;
            public long ui64;
            public STR str;

            @Structure.FieldOrder(value={"addr", "len"})
            public static class STR
            extends Structure {
                public Pointer addr;
                public int len;
            }
        }
    }

    @Structure.FieldOrder(value={"ks_crtime", "ks_next", "ks_kid", "ks_module", "ks_resv", "ks_instance", "ks_name", "ks_type", "ks_class", "ks_flags", "ks_data", "ks_ndata", "ks_data_size", "ks_snaptime", "ks_update", "ks_private", "ks_snapshot", "ks_lock"})
    public static class Kstat
    extends Structure {
        public long ks_crtime;
        public Pointer ks_next;
        public int ks_kid;
        public byte[] ks_module = new byte[31];
        public byte ks_resv;
        public int ks_instance;
        public byte[] ks_name = new byte[31];
        public byte ks_type;
        public byte[] ks_class = new byte[31];
        public byte ks_flags;
        public Pointer ks_data;
        public int ks_ndata;
        public long ks_data_size;
        public long ks_snaptime;
        public int ks_update;
        public Pointer ks_private;
        public int ks_snapshot;
        public Pointer ks_lock;

        public Kstat next() {
            if (this.ks_next == null) {
                return null;
            }
            Kstat n = new Kstat();
            n.useMemory(this.ks_next);
            n.read();
            return n;
        }
    }
}

