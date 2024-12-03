/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.PointerType
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$FieldOrder
 *  com.sun.jna.Union
 *  com.sun.jna.ptr.PointerByReference
 */
package com.sun.jna.platform.unix.solaris;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.unix.solaris.Kstat2StatusException;
import com.sun.jna.ptr.PointerByReference;

public interface Kstat2
extends Library {
    public static final Kstat2 INSTANCE = (Kstat2)Native.load((String)"kstat2", Kstat2.class);
    public static final int KSTAT2_S_OK = 0;
    public static final int KSTAT2_S_NO_PERM = 1;
    public static final int KSTAT2_S_NO_MEM = 2;
    public static final int KSTAT2_S_NO_SPACE = 3;
    public static final int KSTAT2_S_INVAL_ARG = 4;
    public static final int KSTAT2_S_INVAL_STATE = 5;
    public static final int KSTAT2_S_INVAL_TYPE = 6;
    public static final int KSTAT2_S_NOT_FOUND = 7;
    public static final int KSTAT2_S_CONC_MOD = 8;
    public static final int KSTAT2_S_DEL_MAP = 9;
    public static final int KSTAT2_S_SYS_FAIL = 10;
    public static final int KSTAT2_M_STRING = 0;
    public static final int KSTAT2_M_GLOB = 1;
    public static final int KSTAT2_M_RE = 2;
    public static final byte KSTAT2_NVVT_MAP = 0;
    public static final byte KSTAT2_NVVT_INT = 1;
    public static final byte KSTAT2_NVVT_INTS = 2;
    public static final byte KSTAT2_NVVT_STR = 3;
    public static final byte KSTAT2_NVVT_STRS = 4;
    public static final byte KSTAT2_NVK_SYS = 1;
    public static final byte KSTAT2_NVK_USR = 2;
    public static final byte KSTAT2_NVK_MAP = 4;
    public static final byte KSTAT2_NVK_ALL = 7;
    public static final short KSTAT2_NVF_NONE = 0;
    public static final short KSTAT2_NVF_INVAL = 1;

    public int kstat2_open(PointerByReference var1, Kstat2MatcherList var2);

    public int kstat2_update(Kstat2Handle var1);

    public int kstat2_close(PointerByReference var1);

    public int kstat2_alloc_matcher_list(PointerByReference var1);

    public int kstat2_free_matcher_list(PointerByReference var1);

    public int kstat2_add_matcher(int var1, String var2, Kstat2MatcherList var3);

    public int kstat2_lookup_map(Kstat2Handle var1, String var2, PointerByReference var3);

    public int kstat2_map_get(Kstat2Map var1, String var2, PointerByReference var3);

    public String kstat2_status_string(int var1);

    @Structure.FieldOrder(value={"name", "type", "kind", "flags", "data"})
    public static class Kstat2NV
    extends Structure {
        public String name;
        public byte type;
        public byte kind;
        public short flags;
        public UNION data;

        public Kstat2NV() {
        }

        public Kstat2NV(Pointer p) {
            super(p);
            this.read();
        }

        public void read() {
            super.read();
            switch (this.type) {
                case 0: {
                    this.data.setType(Kstat2Map.class);
                    break;
                }
                case 1: {
                    this.data.setType(Long.TYPE);
                    break;
                }
                case 2: {
                    this.data.setType(UNION.IntegersArr.class);
                    break;
                }
                case 3: 
                case 4: {
                    this.data.setType(UNION.StringsArr.class);
                    break;
                }
            }
            this.data.read();
        }

        public static class UNION
        extends Union {
            public Kstat2Map map;
            public long integerVal;
            public IntegersArr integers;
            public StringsArr strings;

            @Structure.FieldOrder(value={"addr", "len"})
            public static class StringsArr
            extends Structure {
                public Pointer addr;
                public int len;
            }

            @Structure.FieldOrder(value={"addr", "len"})
            public static class IntegersArr
            extends Structure {
                public Pointer addr;
                public int len;
            }
        }
    }

    public static class Kstat2Map
    extends PointerType {
        public Kstat2Map() {
        }

        public Kstat2Map(Pointer p) {
            super(p);
        }

        public Kstat2NV mapGet(String name) {
            PointerByReference pbr = new PointerByReference();
            int ks = INSTANCE.kstat2_map_get(this, name, pbr);
            if (ks != 0) {
                throw new Kstat2StatusException(ks);
            }
            return new Kstat2NV(pbr.getValue());
        }

        public Object getValue(String name) {
            try {
                Kstat2NV nv = this.mapGet(name);
                if (nv.flags == 1) {
                    return null;
                }
                switch (nv.type) {
                    case 0: {
                        return nv.data.map;
                    }
                    case 1: {
                        return nv.data.integerVal;
                    }
                    case 2: {
                        return nv.data.integers.addr.getLongArray(0L, nv.data.integers.len);
                    }
                    case 3: {
                        return nv.data.strings.addr.getString(0L);
                    }
                    case 4: {
                        return nv.data.strings.addr.getStringArray(0L, nv.data.strings.len);
                    }
                }
                return null;
            }
            catch (Kstat2StatusException e) {
                return null;
            }
        }
    }

    public static class Kstat2MatcherList
    extends PointerType {
        private PointerByReference ref = new PointerByReference();

        public Kstat2MatcherList() {
            int ks = INSTANCE.kstat2_alloc_matcher_list(this.ref);
            if (ks != 0) {
                throw new Kstat2StatusException(ks);
            }
            this.setPointer(this.ref.getValue());
        }

        public int addMatcher(int type, String match) {
            return INSTANCE.kstat2_add_matcher(type, match, this);
        }

        public int free() {
            return INSTANCE.kstat2_free_matcher_list(this.ref);
        }
    }

    public static class Kstat2Handle
    extends PointerType {
        private PointerByReference ref = new PointerByReference();

        public Kstat2Handle() {
            this(null);
        }

        public Kstat2Handle(Kstat2MatcherList matchers) {
            int ks = INSTANCE.kstat2_open(this.ref, matchers);
            if (ks != 0) {
                throw new Kstat2StatusException(ks);
            }
            this.setPointer(this.ref.getValue());
        }

        public int update() {
            return INSTANCE.kstat2_update(this);
        }

        public Kstat2Map lookupMap(String uri) {
            PointerByReference pMap = new PointerByReference();
            int ks = INSTANCE.kstat2_lookup_map(this, uri, pMap);
            if (ks != 0) {
                throw new Kstat2StatusException(ks);
            }
            return new Kstat2Map(pMap.getValue());
        }

        public int close() {
            return INSTANCE.kstat2_close(this.ref);
        }
    }
}

