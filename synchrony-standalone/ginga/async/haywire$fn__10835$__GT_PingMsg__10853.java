/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import ginga.async.haywire.PingMsg;

public final class haywire$fn__10835$__GT_PingMsg__10853
extends AFunction {
    @Override
    public Object invoke(Object type2, Object seq2) {
        Object object = type2;
        type2 = null;
        Object object2 = seq2;
        seq2 = null;
        return new PingMsg(object, object2);
    }
}

