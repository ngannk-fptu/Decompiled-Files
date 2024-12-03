/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Util;
import clojure.lang.Var;
import java.util.concurrent.ConcurrentHashMap;

public final class utils$fast_memoize$fn__26288
extends RestFn {
    Object f;
    Object m;
    public static final Var const__0 = RT.var("clj-tuple", "vector");
    public static final Var const__1 = RT.var("clojure.core", "not");
    public static final Keyword const__5 = RT.keyword("potemkin.utils", "nil");
    public static final Var const__7 = RT.var("clojure.core", "list*");
    public static final Var const__8 = RT.var("clojure.core", "apply");

    public utils$fast_memoize$fn__26288(Object object, Object object2) {
        this.f = object;
        this.m = object2;
    }

    @Override
    public Object doInvoke(Object x, Object y, Object z, Object w, Object u, Object v, Object rest) {
        Object object;
        Object object2 = x;
        x = null;
        Object object3 = y;
        y = null;
        Object object4 = z;
        z = null;
        Object object5 = w;
        w = null;
        Object object6 = u;
        u = null;
        Object object7 = v;
        v = null;
        Object object8 = rest;
        rest = null;
        Object k = ((IFn)const__7.getRawRoot()).invoke(object2, object3, object4, object5, object6, object7, object8);
        Object v2 = ((ConcurrentHashMap)this.m).get(k);
        Object object9 = ((IFn)const__1.getRawRoot()).invoke(Util.identical(v2, null) ? Boolean.TRUE : Boolean.FALSE);
        if (object9 != null && object9 != Boolean.FALSE) {
            Object v3 = v2;
            v2 = null;
            Object x__26267__auto__26290 = v3;
            if (Util.identical(const__5, x__26267__auto__26290)) {
                object = null;
            } else {
                object = x__26267__auto__26290;
                x__26267__auto__26290 = null;
            }
        } else {
            Object or__5581__auto__26292;
            Object object10;
            Object x__26276__auto__26291 = ((IFn)const__8.getRawRoot()).invoke(this.f, k);
            if (Util.identical(x__26276__auto__26291, null)) {
                object10 = const__5;
            } else {
                object10 = x__26276__auto__26291;
                x__26276__auto__26291 = null;
            }
            Object v4 = object10;
            Object object11 = k;
            k = null;
            Object object12 = or__5581__auto__26292 = ((ConcurrentHashMap)this.m).putIfAbsent(object11, v4);
            if (object12 != null && object12 != Boolean.FALSE) {
                object = or__5581__auto__26292;
                or__5581__auto__26292 = null;
            } else {
                object = v4;
                v4 = null;
            }
        }
        return object;
    }

    @Override
    public Object invoke(Object x, Object y, Object z, Object w, Object u, Object v) {
        Object object;
        Object k__26285__auto__26299 = ((IFn)const__0.getRawRoot()).invoke(x, y, z, w, u, v);
        Object v__26286__auto__26298 = ((ConcurrentHashMap)this.m).get(k__26285__auto__26299);
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(Util.identical(v__26286__auto__26298, null) ? Boolean.TRUE : Boolean.FALSE);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object v2 = v__26286__auto__26298;
            v__26286__auto__26298 = null;
            Object x__26267__auto__26293 = v2;
            if (Util.identical(const__5, x__26267__auto__26293)) {
                object = null;
            } else {
                object = x__26267__auto__26293;
                x__26267__auto__26293 = null;
            }
        } else {
            Object object3;
            Object or__5581__auto__26295;
            Object object4;
            Object object5 = x;
            x = null;
            Object object6 = y;
            y = null;
            Object object7 = z;
            z = null;
            Object object8 = w;
            w = null;
            Object object9 = u;
            u = null;
            Object object10 = v;
            v = null;
            Object x__26276__auto__26294 = ((IFn)this.f).invoke(object5, object6, object7, object8, object9, object10);
            if (Util.identical(x__26276__auto__26294, null)) {
                object4 = const__5;
            } else {
                object4 = x__26276__auto__26294;
                x__26276__auto__26294 = null;
            }
            Object v__26286__auto__26297 = object4;
            Object object11 = k__26285__auto__26299;
            k__26285__auto__26299 = null;
            Object object12 = or__5581__auto__26295 = ((ConcurrentHashMap)this.m).putIfAbsent(object11, v__26286__auto__26297);
            if (object12 != null && object12 != Boolean.FALSE) {
                object3 = or__5581__auto__26295;
                or__5581__auto__26295 = null;
            } else {
                object3 = v__26286__auto__26297;
                v__26286__auto__26297 = null;
            }
            Object x__26267__auto__26296 = object3;
            if (Util.identical(const__5, x__26267__auto__26296)) {
                object = null;
            } else {
                object = x__26267__auto__26296;
                x__26267__auto__26296 = null;
            }
        }
        return object;
    }

    @Override
    public Object invoke(Object x, Object y, Object z, Object w, Object u) {
        Object object;
        Object k__26285__auto__26306 = ((IFn)const__0.getRawRoot()).invoke(x, y, z, w, u);
        Object v__26286__auto__26305 = ((ConcurrentHashMap)this.m).get(k__26285__auto__26306);
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(Util.identical(v__26286__auto__26305, null) ? Boolean.TRUE : Boolean.FALSE);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object v = v__26286__auto__26305;
            v__26286__auto__26305 = null;
            Object x__26267__auto__26300 = v;
            if (Util.identical(const__5, x__26267__auto__26300)) {
                object = null;
            } else {
                object = x__26267__auto__26300;
                x__26267__auto__26300 = null;
            }
        } else {
            Object object3;
            Object or__5581__auto__26302;
            Object object4;
            Object object5 = x;
            x = null;
            Object object6 = y;
            y = null;
            Object object7 = z;
            z = null;
            Object object8 = w;
            w = null;
            Object object9 = u;
            u = null;
            Object x__26276__auto__26301 = ((IFn)this.f).invoke(object5, object6, object7, object8, object9);
            if (Util.identical(x__26276__auto__26301, null)) {
                object4 = const__5;
            } else {
                object4 = x__26276__auto__26301;
                x__26276__auto__26301 = null;
            }
            Object v__26286__auto__26304 = object4;
            Object object10 = k__26285__auto__26306;
            k__26285__auto__26306 = null;
            Object object11 = or__5581__auto__26302 = ((ConcurrentHashMap)this.m).putIfAbsent(object10, v__26286__auto__26304);
            if (object11 != null && object11 != Boolean.FALSE) {
                object3 = or__5581__auto__26302;
                or__5581__auto__26302 = null;
            } else {
                object3 = v__26286__auto__26304;
                v__26286__auto__26304 = null;
            }
            Object x__26267__auto__26303 = object3;
            if (Util.identical(const__5, x__26267__auto__26303)) {
                object = null;
            } else {
                object = x__26267__auto__26303;
                x__26267__auto__26303 = null;
            }
        }
        return object;
    }

    @Override
    public Object invoke(Object x, Object y, Object z, Object w) {
        Object object;
        Object k__26285__auto__26313 = ((IFn)const__0.getRawRoot()).invoke(x, y, z, w);
        Object v__26286__auto__26312 = ((ConcurrentHashMap)this.m).get(k__26285__auto__26313);
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(Util.identical(v__26286__auto__26312, null) ? Boolean.TRUE : Boolean.FALSE);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object v = v__26286__auto__26312;
            v__26286__auto__26312 = null;
            Object x__26267__auto__26307 = v;
            if (Util.identical(const__5, x__26267__auto__26307)) {
                object = null;
            } else {
                object = x__26267__auto__26307;
                x__26267__auto__26307 = null;
            }
        } else {
            Object object3;
            Object or__5581__auto__26309;
            Object object4;
            Object object5 = x;
            x = null;
            Object object6 = y;
            y = null;
            Object object7 = z;
            z = null;
            Object object8 = w;
            w = null;
            Object x__26276__auto__26308 = ((IFn)this.f).invoke(object5, object6, object7, object8);
            if (Util.identical(x__26276__auto__26308, null)) {
                object4 = const__5;
            } else {
                object4 = x__26276__auto__26308;
                x__26276__auto__26308 = null;
            }
            Object v__26286__auto__26311 = object4;
            Object object9 = k__26285__auto__26313;
            k__26285__auto__26313 = null;
            Object object10 = or__5581__auto__26309 = ((ConcurrentHashMap)this.m).putIfAbsent(object9, v__26286__auto__26311);
            if (object10 != null && object10 != Boolean.FALSE) {
                object3 = or__5581__auto__26309;
                or__5581__auto__26309 = null;
            } else {
                object3 = v__26286__auto__26311;
                v__26286__auto__26311 = null;
            }
            Object x__26267__auto__26310 = object3;
            if (Util.identical(const__5, x__26267__auto__26310)) {
                object = null;
            } else {
                object = x__26267__auto__26310;
                x__26267__auto__26310 = null;
            }
        }
        return object;
    }

    @Override
    public Object invoke(Object x, Object y, Object z) {
        Object object;
        Object k__26285__auto__26320 = ((IFn)const__0.getRawRoot()).invoke(x, y, z);
        Object v__26286__auto__26319 = ((ConcurrentHashMap)this.m).get(k__26285__auto__26320);
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(Util.identical(v__26286__auto__26319, null) ? Boolean.TRUE : Boolean.FALSE);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object v = v__26286__auto__26319;
            v__26286__auto__26319 = null;
            Object x__26267__auto__26314 = v;
            if (Util.identical(const__5, x__26267__auto__26314)) {
                object = null;
            } else {
                object = x__26267__auto__26314;
                x__26267__auto__26314 = null;
            }
        } else {
            Object object3;
            Object or__5581__auto__26316;
            Object object4;
            Object object5 = x;
            x = null;
            Object object6 = y;
            y = null;
            Object object7 = z;
            z = null;
            Object x__26276__auto__26315 = ((IFn)this.f).invoke(object5, object6, object7);
            if (Util.identical(x__26276__auto__26315, null)) {
                object4 = const__5;
            } else {
                object4 = x__26276__auto__26315;
                x__26276__auto__26315 = null;
            }
            Object v__26286__auto__26318 = object4;
            Object object8 = k__26285__auto__26320;
            k__26285__auto__26320 = null;
            Object object9 = or__5581__auto__26316 = ((ConcurrentHashMap)this.m).putIfAbsent(object8, v__26286__auto__26318);
            if (object9 != null && object9 != Boolean.FALSE) {
                object3 = or__5581__auto__26316;
                or__5581__auto__26316 = null;
            } else {
                object3 = v__26286__auto__26318;
                v__26286__auto__26318 = null;
            }
            Object x__26267__auto__26317 = object3;
            if (Util.identical(const__5, x__26267__auto__26317)) {
                object = null;
            } else {
                object = x__26267__auto__26317;
                x__26267__auto__26317 = null;
            }
        }
        return object;
    }

    @Override
    public Object invoke(Object x, Object y) {
        Object object;
        Object k__26285__auto__26327 = ((IFn)const__0.getRawRoot()).invoke(x, y);
        Object v__26286__auto__26326 = ((ConcurrentHashMap)this.m).get(k__26285__auto__26327);
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(Util.identical(v__26286__auto__26326, null) ? Boolean.TRUE : Boolean.FALSE);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object v = v__26286__auto__26326;
            v__26286__auto__26326 = null;
            Object x__26267__auto__26321 = v;
            if (Util.identical(const__5, x__26267__auto__26321)) {
                object = null;
            } else {
                object = x__26267__auto__26321;
                x__26267__auto__26321 = null;
            }
        } else {
            Object object3;
            Object or__5581__auto__26323;
            Object object4;
            Object object5 = x;
            x = null;
            Object object6 = y;
            y = null;
            Object x__26276__auto__26322 = ((IFn)this.f).invoke(object5, object6);
            if (Util.identical(x__26276__auto__26322, null)) {
                object4 = const__5;
            } else {
                object4 = x__26276__auto__26322;
                x__26276__auto__26322 = null;
            }
            Object v__26286__auto__26325 = object4;
            Object object7 = k__26285__auto__26327;
            k__26285__auto__26327 = null;
            Object object8 = or__5581__auto__26323 = ((ConcurrentHashMap)this.m).putIfAbsent(object7, v__26286__auto__26325);
            if (object8 != null && object8 != Boolean.FALSE) {
                object3 = or__5581__auto__26323;
                or__5581__auto__26323 = null;
            } else {
                object3 = v__26286__auto__26325;
                v__26286__auto__26325 = null;
            }
            Object x__26267__auto__26324 = object3;
            if (Util.identical(const__5, x__26267__auto__26324)) {
                object = null;
            } else {
                object = x__26267__auto__26324;
                x__26267__auto__26324 = null;
            }
        }
        return object;
    }

    @Override
    public Object invoke(Object x) {
        Object object;
        Object k__26285__auto__26334 = ((IFn)const__0.getRawRoot()).invoke(x);
        Object v__26286__auto__26333 = ((ConcurrentHashMap)this.m).get(k__26285__auto__26334);
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(Util.identical(v__26286__auto__26333, null) ? Boolean.TRUE : Boolean.FALSE);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object v = v__26286__auto__26333;
            v__26286__auto__26333 = null;
            Object x__26267__auto__26328 = v;
            if (Util.identical(const__5, x__26267__auto__26328)) {
                object = null;
            } else {
                object = x__26267__auto__26328;
                x__26267__auto__26328 = null;
            }
        } else {
            Object object3;
            Object or__5581__auto__26330;
            Object object4;
            Object object5 = x;
            x = null;
            Object x__26276__auto__26329 = ((IFn)this.f).invoke(object5);
            if (Util.identical(x__26276__auto__26329, null)) {
                object4 = const__5;
            } else {
                object4 = x__26276__auto__26329;
                x__26276__auto__26329 = null;
            }
            Object v__26286__auto__26332 = object4;
            Object object6 = k__26285__auto__26334;
            k__26285__auto__26334 = null;
            Object object7 = or__5581__auto__26330 = ((ConcurrentHashMap)this.m).putIfAbsent(object6, v__26286__auto__26332);
            if (object7 != null && object7 != Boolean.FALSE) {
                object3 = or__5581__auto__26330;
                or__5581__auto__26330 = null;
            } else {
                object3 = v__26286__auto__26332;
                v__26286__auto__26332 = null;
            }
            Object x__26267__auto__26331 = object3;
            if (Util.identical(const__5, x__26267__auto__26331)) {
                object = null;
            } else {
                object = x__26267__auto__26331;
                x__26267__auto__26331 = null;
            }
        }
        return object;
    }

    @Override
    public Object invoke() {
        Object object;
        Object k__26285__auto__26341 = ((IFn)const__0.getRawRoot()).invoke();
        Object v__26286__auto__26340 = ((ConcurrentHashMap)this.m).get(k__26285__auto__26341);
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(Util.identical(v__26286__auto__26340, null) ? Boolean.TRUE : Boolean.FALSE);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object v = v__26286__auto__26340;
            v__26286__auto__26340 = null;
            Object x__26267__auto__26335 = v;
            if (Util.identical(const__5, x__26267__auto__26335)) {
                object = null;
            } else {
                object = x__26267__auto__26335;
                x__26267__auto__26335 = null;
            }
        } else {
            Object object3;
            Object or__5581__auto__26337;
            Object v__26286__auto__26339;
            Object object4;
            Object x__26276__auto__26336 = ((IFn)this.f).invoke();
            if (Util.identical(x__26276__auto__26336, null)) {
                object4 = const__5;
            } else {
                object4 = x__26276__auto__26336;
                v__26286__auto__26339 = null;
            }
            v__26286__auto__26339 = object4;
            Object object5 = k__26285__auto__26341;
            k__26285__auto__26341 = null;
            Object object6 = or__5581__auto__26337 = ((ConcurrentHashMap)this.m).putIfAbsent(object5, v__26286__auto__26339);
            if (object6 != null && object6 != Boolean.FALSE) {
                object3 = or__5581__auto__26337;
                or__5581__auto__26337 = null;
            } else {
                object3 = v__26286__auto__26339;
                v__26286__auto__26339 = null;
            }
            Object x__26267__auto__26338 = object3;
            if (Util.identical(const__5, x__26267__auto__26338)) {
                object = null;
            } else {
                object = x__26267__auto__26338;
                x__26267__auto__26338 = null;
            }
        }
        return object;
    }

    @Override
    public int getRequiredArity() {
        return 6;
    }
}

