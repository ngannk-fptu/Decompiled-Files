/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Holder
 */
package com.sun.xml.ws.server.sei;

import com.sun.xml.ws.model.ParameterImpl;
import javax.xml.ws.Holder;

public abstract class EndpointValueSetter {
    private static final EndpointValueSetter[] POOL = new EndpointValueSetter[16];

    private EndpointValueSetter() {
    }

    abstract void put(Object var1, Object[] var2);

    public static EndpointValueSetter get(ParameterImpl p) {
        int idx = p.getIndex();
        if (p.isIN()) {
            if (idx < POOL.length) {
                return POOL[idx];
            }
            return new Param(idx);
        }
        return new HolderParam(idx);
    }

    static {
        for (int i = 0; i < POOL.length; ++i) {
            EndpointValueSetter.POOL[i] = new Param(i);
        }
    }

    static final class HolderParam
    extends Param {
        public HolderParam(int idx) {
            super(idx);
        }

        @Override
        void put(Object obj, Object[] args) {
            Holder holder = new Holder();
            if (obj != null) {
                holder.value = obj;
            }
            args[this.idx] = holder;
        }
    }

    static class Param
    extends EndpointValueSetter {
        protected final int idx;

        public Param(int idx) {
            this.idx = idx;
        }

        @Override
        void put(Object obj, Object[] args) {
            if (obj != null) {
                args[this.idx] = obj;
            }
        }
    }
}

