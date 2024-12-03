/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.ognl.accessor;

import java.util.ArrayList;
import java.util.Collection;

class SurrugateList
extends ArrayList {
    private Collection surrugate;

    public SurrugateList(Collection surrugate) {
        this.surrugate = surrugate;
    }

    @Override
    public void add(int arg0, Object arg1) {
        if (arg1 != null) {
            this.surrugate.add(arg1);
        }
        super.add(arg0, arg1);
    }

    @Override
    public boolean add(Object arg0) {
        if (arg0 != null) {
            this.surrugate.add(arg0);
        }
        return super.add(arg0);
    }

    @Override
    public boolean addAll(Collection arg0) {
        this.surrugate.addAll(arg0);
        return super.addAll(arg0);
    }

    @Override
    public boolean addAll(int arg0, Collection arg1) {
        this.surrugate.addAll(arg1);
        return super.addAll(arg0, arg1);
    }

    @Override
    public Object set(int arg0, Object arg1) {
        if (arg1 != null) {
            this.surrugate.add(arg1);
        }
        return super.set(arg0, arg1);
    }
}

