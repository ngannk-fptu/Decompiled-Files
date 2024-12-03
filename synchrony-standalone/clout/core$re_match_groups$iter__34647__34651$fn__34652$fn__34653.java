/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Var;
import java.util.regex.Matcher;

public final class core$re_match_groups$iter__34647__34651$fn__34652$fn__34653
extends AFunction {
    int size__6372__auto__;
    Object matcher;
    Object b__34650;
    Object c__6371__auto__;
    public static final Var const__3 = RT.var("clojure.core", "chunk-append");

    public core$re_match_groups$iter__34647__34651$fn__34652$fn__34653(int n, Object object, Object object2, Object object3) {
        this.size__6372__auto__ = n;
        this.matcher = object;
        this.b__34650 = object2;
        this.c__6371__auto__ = object3;
    }

    @Override
    public Object invoke() {
        for (long i__34649 = (long)RT.intCast(0L); i__34649 < (long)this.size__6372__auto__; ++i__34649) {
            Object i;
            Object object = i = ((Indexed)this.c__6371__auto__).nth(RT.intCast(i__34649));
            i = null;
            ((IFn)const__3.getRawRoot()).invoke(this.b__34650, ((Matcher)this.matcher).group(RT.intCast(Numbers.inc(object))));
        }
        return Boolean.TRUE;
    }
}

