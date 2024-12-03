/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import clout.core$re_match_groups$iter__34647__34651;
import java.util.regex.Matcher;

public final class core$re_match_groups
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "re-matcher");
    public static final Var const__1 = RT.var("clojure.core", "range");

    public static Object invokeStatic(Object re, Object s2) {
        Object object;
        Object object2 = re;
        re = null;
        Object object3 = s2;
        s2 = null;
        Object matcher = ((IFn)const__0.getRawRoot()).invoke(object2, object3);
        if (((Matcher)matcher).matches()) {
            core$re_match_groups$iter__34647__34651 iter__6373__auto__34661;
            core$re_match_groups$iter__34647__34651 core$re_match_groups$iter__34647__34651 = iter__6373__auto__34661 = new core$re_match_groups$iter__34647__34651(matcher);
            iter__6373__auto__34661 = null;
            Object object4 = matcher;
            matcher = null;
            object = ((IFn)core$re_match_groups$iter__34647__34651).invoke(((IFn)const__1.getRawRoot()).invoke(((Matcher)object4).groupCount()));
        } else {
            object = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$re_match_groups.invokeStatic(object3, object4);
    }
}

