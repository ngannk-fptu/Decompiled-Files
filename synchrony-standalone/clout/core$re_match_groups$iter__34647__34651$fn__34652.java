/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Var;
import clout.core$re_match_groups$iter__34647__34651$fn__34652$fn__34653;
import java.util.regex.Matcher;

public final class core$re_match_groups$iter__34647__34651$fn__34652
extends AFunction {
    Object s__34648;
    Object matcher;
    Object iter__34647;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "chunked-seq?");
    public static final Var const__2 = RT.var("clojure.core", "chunk-first");
    public static final Var const__5 = RT.var("clojure.core", "chunk-buffer");
    public static final Var const__6 = RT.var("clojure.core", "chunk-cons");
    public static final Var const__7 = RT.var("clojure.core", "chunk");
    public static final Var const__8 = RT.var("clojure.core", "chunk-rest");
    public static final Var const__9 = RT.var("clojure.core", "first");
    public static final Var const__10 = RT.var("clojure.core", "cons");
    public static final Var const__12 = RT.var("clojure.core", "rest");

    public core$re_match_groups$iter__34647__34651$fn__34652(Object object, Object object2, Object object3) {
        this.s__34648 = object;
        this.matcher = object2;
        this.iter__34647 = object3;
    }

    @Override
    public Object invoke() {
        Object object;
        Object temp__5804__auto__34658;
        Object s__34648;
        Object object2 = s__34648 = (this_.s__34648 = null);
        s__34648 = null;
        Object object3 = temp__5804__auto__34658 = ((IFn)const__0.getRawRoot()).invoke(object2);
        if (object3 != null && object3 != Boolean.FALSE) {
            core$re_match_groups$iter__34647__34651$fn__34652 this_;
            Object object4 = temp__5804__auto__34658;
            temp__5804__auto__34658 = null;
            Object s__346482 = object4;
            Object object5 = ((IFn)const__1.getRawRoot()).invoke(s__346482);
            if (object5 != null && object5 != Boolean.FALSE) {
                Object c__6371__auto__34656 = ((IFn)const__2.getRawRoot()).invoke(s__346482);
                int size__6372__auto__34657 = RT.intCast(RT.count(c__6371__auto__34656));
                Object b__34650 = ((IFn)const__5.getRawRoot()).invoke(size__6372__auto__34657);
                Object object6 = c__6371__auto__34656;
                c__6371__auto__34656 = null;
                Object object7 = ((IFn)new core$re_match_groups$iter__34647__34651$fn__34652$fn__34653(size__6372__auto__34657, this_.matcher, b__34650, object6)).invoke();
                if (object7 != null && object7 != Boolean.FALSE) {
                    Object object8 = b__34650;
                    b__34650 = null;
                    Object object9 = s__346482;
                    s__346482 = null;
                    this_ = null;
                    object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object8), ((IFn)this_.iter__34647).invoke(((IFn)const__8.getRawRoot()).invoke(object9)));
                } else {
                    Object object10 = b__34650;
                    b__34650 = null;
                    this_ = null;
                    object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object10), null);
                }
            } else {
                Object i;
                Object object11 = i = ((IFn)const__9.getRawRoot()).invoke(s__346482);
                i = null;
                Object object12 = s__346482;
                s__346482 = null;
                this_ = null;
                object = ((IFn)const__10.getRawRoot()).invoke(((Matcher)this_.matcher).group(RT.intCast(Numbers.inc(object11))), ((IFn)this_.iter__34647).invoke(((IFn)const__12.getRawRoot()).invoke(object12)));
            }
        } else {
            object = null;
        }
        return object;
    }
}

