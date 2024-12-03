/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.LazySeq;
import clout.core$re_match_groups$iter__34647__34651$fn__34652;

public final class core$re_match_groups$iter__34647__34651
extends AFunction {
    Object matcher;

    public core$re_match_groups$iter__34647__34651(Object object) {
        this.matcher = object;
    }

    @Override
    public Object invoke(Object s__34648) {
        Object object = s__34648;
        s__34648 = null;
        return new LazySeq(new core$re_match_groups$iter__34647__34651$fn__34652(object, this.matcher, this));
    }
}

