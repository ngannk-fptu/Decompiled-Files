/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

public class IrritantSet {
    public static final int GROUP_MASK = -536870912;
    public static final int GROUP_SHIFT = 29;
    public static final int GROUP_MAX = 3;
    public static final int GROUP0 = 0;
    public static final int GROUP1 = 0x20000000;
    public static final int GROUP2 = 0x40000000;
    public static final IrritantSet ALL = new IrritantSet(0x1FFFFFFF);
    public static final IrritantSet BOXING = new IrritantSet(0x20000100);
    public static final IrritantSet CAST = new IrritantSet(0x4000000);
    public static final IrritantSet DEPRECATION = new IrritantSet(4);
    public static final IrritantSet TERMINAL_DEPRECATION = new IrritantSet(0x40800000);
    public static final IrritantSet DEP_ANN = new IrritantSet(0x20002000);
    public static final IrritantSet FALLTHROUGH = new IrritantSet(0x20080000);
    public static final IrritantSet FINALLY = new IrritantSet(0x1000000);
    public static final IrritantSet HIDING = new IrritantSet(8);
    public static final IrritantSet INCOMPLETE_SWITCH = new IrritantSet(0x20001000);
    public static final IrritantSet NLS = new IrritantSet(256);
    public static final IrritantSet NULL = new IrritantSet(0x20000080);
    public static final IrritantSet RAW = new IrritantSet(0x20010000);
    public static final IrritantSet RESTRICTION = new IrritantSet(0x20000020);
    public static final IrritantSet SERIAL = new IrritantSet(0x20000008);
    public static final IrritantSet STATIC_ACCESS = new IrritantSet(0x10000000);
    public static final IrritantSet STATIC_METHOD = new IrritantSet(0x40000010);
    public static final IrritantSet SYNTHETIC_ACCESS = new IrritantSet(128);
    public static final IrritantSet SYNCHRONIZED = new IrritantSet(0x30000000);
    public static final IrritantSet SUPER = new IrritantSet(0x20100000);
    public static final IrritantSet UNUSED = new IrritantSet(16);
    public static final IrritantSet UNCHECKED = new IrritantSet(0x20000002);
    public static final IrritantSet UNQUALIFIED_FIELD_ACCESS = new IrritantSet(0x400000);
    public static final IrritantSet RESOURCE = new IrritantSet(0x40000080);
    public static final IrritantSet UNLIKELY_ARGUMENT_TYPE = new IrritantSet(0x40200000);
    public static final IrritantSet API_LEAK = new IrritantSet(0x41000000);
    public static final IrritantSet MODULE = new IrritantSet(0x42000000);
    public static final IrritantSet JAVADOC = new IrritantSet(0x2000000);
    public static final IrritantSet PREVIEW = new IrritantSet(0x44000000);
    public static final IrritantSet COMPILER_DEFAULT_ERRORS = new IrritantSet(0);
    public static final IrritantSet COMPILER_DEFAULT_WARNINGS = new IrritantSet(0);
    public static final IrritantSet COMPILER_DEFAULT_INFOS = new IrritantSet(0);
    private int[] bits = new int[3];

    static {
        COMPILER_DEFAULT_INFOS.set(1480589312);
        COMPILER_DEFAULT_WARNINGS.set(16838239).set(721671934).set(1203384454);
        COMPILER_DEFAULT_ERRORS.set(0x40000C00);
        ALL.setAll();
        HIDING.set(131072).set(65536).set(0x20000400);
        NULL.set(0x20200000).set(0x20400000).set(0x40000400).set(0x40000800).set(0x40001000).set(0x40002000).set(0x40020000).set(0x40004000).set(0x40080000).set(0x40100000).set(0x50000000);
        RESTRICTION.set(0x20004000);
        STATIC_ACCESS.set(2048);
        UNUSED.set(32).set(0x40040000).set(32768).set(0x800000).set(0x20020000).set(1024).set(0x21000000).set(0x24000000).set(0x40000002).set(0x40000008).set(0x40010000).set(0x40000040);
        STATIC_METHOD.set(0x40000020);
        RESOURCE.set(0x40000100).set(0x40000200);
        INCOMPLETE_SWITCH.set(0x40008000);
        String suppressRawWhenUnchecked = System.getProperty("suppressRawWhenUnchecked");
        if (suppressRawWhenUnchecked != null && "true".equalsIgnoreCase(suppressRawWhenUnchecked)) {
            UNCHECKED.set(0x20010000);
        }
        JAVADOC.set(0x100000).set(0x200000);
        UNLIKELY_ARGUMENT_TYPE.set(0x40400000);
    }

    public IrritantSet(int singleGroupIrritants) {
        this.initialize(singleGroupIrritants);
    }

    public IrritantSet(IrritantSet other) {
        this.initialize(other);
    }

    public boolean areAllSet() {
        int i = 0;
        while (i < 3) {
            if (this.bits[i] != 0x1FFFFFFF) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public IrritantSet clear(int singleGroupIrritants) {
        int group;
        int n = group = (singleGroupIrritants & 0xE0000000) >> 29;
        this.bits[n] = this.bits[n] & ~singleGroupIrritants;
        return this;
    }

    public IrritantSet clearAll() {
        int i = 0;
        while (i < 3) {
            this.bits[i] = 0;
            ++i;
        }
        return this;
    }

    public void initialize(int singleGroupIrritants) {
        if (singleGroupIrritants == 0) {
            return;
        }
        int group = (singleGroupIrritants & 0xE0000000) >> 29;
        this.bits[group] = singleGroupIrritants & 0x1FFFFFFF;
    }

    public void initialize(IrritantSet other) {
        if (other == null) {
            return;
        }
        this.bits = new int[3];
        System.arraycopy(other.bits, 0, this.bits, 0, 3);
    }

    public boolean isAnySet(IrritantSet other) {
        if (other == null) {
            return false;
        }
        int i = 0;
        while (i < 3) {
            if ((this.bits[i] & other.bits[i]) != 0) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public boolean hasSameIrritants(IrritantSet irritantSet) {
        if (irritantSet == null) {
            return false;
        }
        int i = 0;
        while (i < 3) {
            if (this.bits[i] != irritantSet.bits[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public boolean isSet(int singleGroupIrritants) {
        int group = (singleGroupIrritants & 0xE0000000) >> 29;
        return (this.bits[group] & singleGroupIrritants) != 0;
    }

    public int[] getBits() {
        return this.bits;
    }

    public IrritantSet set(int singleGroupIrritants) {
        int group;
        int n = group = (singleGroupIrritants & 0xE0000000) >> 29;
        this.bits[n] = this.bits[n] | singleGroupIrritants & 0x1FFFFFFF;
        return this;
    }

    public IrritantSet set(IrritantSet other) {
        if (other == null) {
            return this;
        }
        boolean wasNoOp = true;
        int i = 0;
        while (i < 3) {
            int otherIrritant = other.bits[i] & 0x1FFFFFFF;
            if ((this.bits[i] & otherIrritant) != otherIrritant) {
                wasNoOp = false;
                int n = i;
                this.bits[n] = this.bits[n] | otherIrritant;
            }
            ++i;
        }
        return wasNoOp ? null : this;
    }

    public IrritantSet setAll() {
        int i = 0;
        while (i < 3) {
            int n = i++;
            this.bits[n] = this.bits[n] | 0x1FFFFFFF;
        }
        return this;
    }
}

