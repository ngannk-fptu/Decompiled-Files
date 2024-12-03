/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.google.template.soy.msgs;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.msgs.restricted.SoyMsg;
import java.util.Iterator;

public interface SoyMsgBundle
extends Iterable<SoyMsg> {
    public static final SoyMsgBundle EMPTY = new SoyMsgBundle(){

        @Override
        public String getLocaleString() {
            return "en";
        }

        @Override
        public SoyMsg getMsg(long msgId) {
            return null;
        }

        @Override
        public int getNumMsgs() {
            return 0;
        }

        @Override
        public Iterator<SoyMsg> iterator() {
            return ImmutableList.of().iterator();
        }
    };

    public String getLocaleString();

    public SoyMsg getMsg(long var1);

    public int getNumMsgs();

    @Override
    public Iterator<SoyMsg> iterator();
}

