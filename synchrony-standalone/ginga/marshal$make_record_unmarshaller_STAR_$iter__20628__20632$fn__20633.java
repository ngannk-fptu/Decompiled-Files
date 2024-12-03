/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Util;
import clojure.lang.Var;
import ginga.marshal$make_record_unmarshaller_STAR_$iter__20628__20632$fn__20633$fn__20634;

public final class marshal$make_record_unmarshaller_STAR_$iter__20628__20632$fn__20633
extends AFunction {
    Object default_syms;
    Object data_sym;
    Object defaults;
    Object kws;
    Object unmarshaller_sym;
    Object iter__20628;
    Object s__20629;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "chunked-seq?");
    public static final Var const__2 = RT.var("clojure.core", "chunk-first");
    public static final Var const__5 = RT.var("clojure.core", "chunk-buffer");
    public static final Var const__6 = RT.var("clojure.core", "chunk-cons");
    public static final Var const__7 = RT.var("clojure.core", "chunk");
    public static final Var const__8 = RT.var("clojure.core", "chunk-rest");
    public static final Var const__9 = RT.var("clojure.core", "first");
    public static final Var const__10 = RT.var("clojure.core", "cons");
    public static final Var const__11 = RT.var("clojure.core", "concat");
    public static final Var const__12 = RT.var("clojure.core", "list");
    public static final AFn const__13 = Symbol.intern("ginga.marshal", "unmarshal");
    public static final AFn const__14 = Symbol.intern("clojure.core", "nth");
    public static final Var const__17 = RT.var("clojure.core", "rest");

    public marshal$make_record_unmarshaller_STAR_$iter__20628__20632$fn__20633(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) {
        this.default_syms = object;
        this.data_sym = object2;
        this.defaults = object3;
        this.kws = object4;
        this.unmarshaller_sym = object5;
        this.iter__20628 = object6;
        this.s__20629 = object7;
    }

    @Override
    public Object invoke() {
        Object object;
        Object temp__5804__auto__20641;
        Object s__20629;
        Object object2 = s__20629 = (this_.s__20629 = null);
        s__20629 = null;
        Object object3 = temp__5804__auto__20641 = ((IFn)const__0.getRawRoot()).invoke(object2);
        if (object3 != null && object3 != Boolean.FALSE) {
            marshal$make_record_unmarshaller_STAR_$iter__20628__20632$fn__20633 this_;
            Object object4 = temp__5804__auto__20641;
            temp__5804__auto__20641 = null;
            Object s__206292 = object4;
            Object object5 = ((IFn)const__1.getRawRoot()).invoke(s__206292);
            if (object5 != null && object5 != Boolean.FALSE) {
                Object c__6371__auto__20638 = ((IFn)const__2.getRawRoot()).invoke(s__206292);
                int size__6372__auto__20639 = RT.intCast(RT.count(c__6371__auto__20638));
                Object b__20631 = ((IFn)const__5.getRawRoot()).invoke(size__6372__auto__20639);
                Object object6 = c__6371__auto__20638;
                c__6371__auto__20638 = null;
                Object object7 = ((IFn)new marshal$make_record_unmarshaller_STAR_$iter__20628__20632$fn__20633$fn__20634(this_.default_syms, this_.data_sym, this_.defaults, b__20631, object6, this_.kws, this_.unmarshaller_sym, size__6372__auto__20639)).invoke();
                if (object7 != null && object7 != Boolean.FALSE) {
                    Object object8 = b__20631;
                    b__20631 = null;
                    Object object9 = s__206292;
                    s__206292 = null;
                    this_ = null;
                    object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object8), ((IFn)this_.iter__20628).invoke(((IFn)const__8.getRawRoot()).invoke(object9)));
                } else {
                    Object object10 = b__20631;
                    b__20631 = null;
                    this_ = null;
                    object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object10), null);
                }
            } else {
                Object object11;
                Object i = ((IFn)const__9.getRawRoot()).invoke(s__206292);
                IFn iFn = (IFn)const__10.getRawRoot();
                IFn iFn2 = (IFn)const__0.getRawRoot();
                IFn iFn3 = (IFn)const__11.getRawRoot();
                Object object12 = ((IFn)const__12.getRawRoot()).invoke(const__13);
                IFn iFn4 = (IFn)const__12.getRawRoot();
                IFn iFn5 = (IFn)const__0.getRawRoot();
                IFn iFn6 = (IFn)const__11.getRawRoot();
                Object object13 = ((IFn)const__12.getRawRoot()).invoke(const__14);
                Object object14 = ((IFn)const__12.getRawRoot()).invoke(this_.data_sym);
                Object object15 = ((IFn)const__12.getRawRoot()).invoke(Numbers.inc(i));
                IFn iFn7 = (IFn)const__12.getRawRoot();
                Object temp__5808__auto__20640 = ((IFn)((IFn)this_.kws).invoke(i)).invoke(this_.defaults);
                if (Util.identical(temp__5808__auto__20640, null)) {
                    object11 = null;
                } else {
                    temp__5808__auto__20640 = null;
                    Object object16 = i;
                    i = null;
                    object11 = ((IFn)((IFn)this_.kws).invoke(object16)).invoke(this_.default_syms);
                }
                Object object17 = s__206292;
                s__206292 = null;
                this_ = null;
                object = iFn.invoke(iFn2.invoke(iFn3.invoke(object12, iFn4.invoke(iFn5.invoke(iFn6.invoke(object13, object14, object15, iFn7.invoke(object11)))), ((IFn)const__12.getRawRoot()).invoke(this_.unmarshaller_sym))), ((IFn)this_.iter__20628).invoke(((IFn)const__17.getRawRoot()).invoke(object17)));
            }
        } else {
            object = null;
        }
        return object;
    }
}

