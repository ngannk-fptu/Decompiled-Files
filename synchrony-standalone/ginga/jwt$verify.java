/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.Numbers;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Reflector;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class jwt$verify
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Keyword const__6 = RT.keyword(null, "expires");
    public static final Keyword const__7 = RT.keyword(null, "clock-skew-secs");
    public static final Keyword const__8 = RT.keyword(null, "claims");
    public static final Var const__9 = RT.var("clojure.core", "ex-info");
    public static final Keyword const__10 = RT.keyword(null, "type");
    public static final Keyword const__11 = RT.keyword("jwt", "unknown-iss");
    public static final Keyword const__12 = RT.keyword(null, "entity");
    public static final Keyword const__13 = RT.keyword(null, "jws-object");
    public static final Keyword const__14 = RT.keyword("jwt", "invalid-signature");
    public static final Keyword const__15 = RT.keyword(null, "level");
    public static final Keyword const__16 = RT.keyword(null, "error");
    public static final Keyword const__20 = RT.keyword("jwt", "passphrase-length");
    public static final Keyword const__23 = RT.keyword("jwt", "invalid-exp");
    public static final Keyword const__24 = RT.keyword("jwt", "invalid-iat");
    public static final Var const__25 = RT.var("ginga.jwt", "expired?");
    public static final Keyword const__26 = RT.keyword("jwt", "expired");
    public static final Keyword const__27 = RT.keyword(null, "warning");
    public static final Var const__28 = RT.var("ginga.jwt", "before?");
    public static final Keyword const__29 = RT.keyword("jwt", "before");

    public static Object invokeStatic(Object jwt2, Object key2, ISeq p__20382) {
        Object v92;
        Object object;
        Object and__5579__auto__20393;
        Object map__20389;
        Object object2;
        Object map__20388;
        Object object3;
        Object map__20387;
        Object object4;
        Object object5;
        Object map__20385;
        Object object6;
        Object map__20384;
        Object object7;
        Object object8;
        ISeq iSeq = p__20382;
        p__20382 = null;
        ISeq map__20383 = iSeq;
        Object object9 = ((IFn)const__0.getRawRoot()).invoke(map__20383);
        if (object9 != null && object9 != Boolean.FALSE) {
            Object object10 = ((IFn)const__1.getRawRoot()).invoke(map__20383);
            if (object10 != null && object10 != Boolean.FALSE) {
                ISeq iSeq2 = map__20383;
                map__20383 = null;
                object8 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(iSeq2));
            } else {
                Object object11 = ((IFn)const__3.getRawRoot()).invoke(map__20383);
                if (object11 != null && object11 != Boolean.FALSE) {
                    ISeq iSeq3 = map__20383;
                    map__20383 = null;
                    object8 = ((IFn)const__4.getRawRoot()).invoke(iSeq3);
                } else {
                    object8 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object8 = map__20383;
            map__20383 = null;
        }
        ISeq map__203832 = object8;
        Object expires = RT.get(map__203832, const__6, Boolean.TRUE);
        ISeq iSeq4 = map__203832;
        map__203832 = null;
        Object clock_skew_secs = RT.get(iSeq4, const__7);
        Object map__203842 = jwt2;
        Object object12 = ((IFn)const__0.getRawRoot()).invoke(map__203842);
        if (object12 != null && object12 != Boolean.FALSE) {
            Object object13 = ((IFn)const__1.getRawRoot()).invoke(map__203842);
            if (object13 != null && object13 != Boolean.FALSE) {
                Object object14 = map__203842;
                map__203842 = null;
                object7 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object14));
            } else {
                Object object15 = ((IFn)const__3.getRawRoot()).invoke(map__203842);
                if (object15 != null && object15 != Boolean.FALSE) {
                    Object object16 = map__203842;
                    map__203842 = null;
                    object7 = ((IFn)const__4.getRawRoot()).invoke(object16);
                } else {
                    object7 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object7 = map__203842;
            map__203842 = null;
        }
        Object object17 = map__20384 = object7;
        map__20384 = null;
        Object map__203852 = RT.get(object17, const__8);
        Object object18 = ((IFn)const__0.getRawRoot()).invoke(map__203852);
        if (object18 != null && object18 != Boolean.FALSE) {
            Object object19 = ((IFn)const__1.getRawRoot()).invoke(map__203852);
            if (object19 != null && object19 != Boolean.FALSE) {
                Object object20 = map__203852;
                map__203852 = null;
                object6 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object20));
            } else {
                Object object21 = ((IFn)const__3.getRawRoot()).invoke(map__203852);
                if (object21 != null && object21 != Boolean.FALSE) {
                    Object object22 = map__203852;
                    map__203852 = null;
                    object6 = ((IFn)const__4.getRawRoot()).invoke(object22);
                } else {
                    object6 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object6 = map__203852;
            map__203852 = null;
        }
        Object object23 = map__20385 = object6;
        map__20385 = null;
        Object iss = RT.get(object23, "iss");
        Object object24 = key2;
        if (object24 == null || object24 == Boolean.FALSE) {
            Object[] objectArray = new Object[4];
            objectArray[0] = const__10;
            objectArray[1] = const__11;
            objectArray[2] = const__12;
            Object object25 = iss;
            iss = null;
            objectArray[3] = object25;
            throw (Throwable)((IFn)const__9.getRawRoot()).invoke("no jwt key provided", RT.mapUniqueKeys(objectArray));
        }
        Object map__20386 = jwt2;
        Object object26 = ((IFn)const__0.getRawRoot()).invoke(map__20386);
        if (object26 != null && object26 != Boolean.FALSE) {
            Object object27 = ((IFn)const__1.getRawRoot()).invoke(map__20386);
            if (object27 != null && object27 != Boolean.FALSE) {
                Object object28 = map__20386;
                map__20386 = null;
                object5 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object28));
            } else {
                Object object29 = ((IFn)const__3.getRawRoot()).invoke(map__20386);
                if (object29 != null && object29 != Boolean.FALSE) {
                    Object object30 = map__20386;
                    map__20386 = null;
                    object5 = ((IFn)const__4.getRawRoot()).invoke(object30);
                } else {
                    object5 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object5 = map__20386;
            map__20386 = null;
        }
        Object map__203862 = object5;
        Object map__203872 = RT.get(map__203862, const__8);
        Object object31 = ((IFn)const__0.getRawRoot()).invoke(map__203872);
        if (object31 != null && object31 != Boolean.FALSE) {
            Object object32 = ((IFn)const__1.getRawRoot()).invoke(map__203872);
            if (object32 != null && object32 != Boolean.FALSE) {
                Object object33 = map__203872;
                map__203872 = null;
                object4 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object33));
            } else {
                Object object34 = ((IFn)const__3.getRawRoot()).invoke(map__203872);
                if (object34 != null && object34 != Boolean.FALSE) {
                    Object object35 = map__203872;
                    map__203872 = null;
                    object4 = ((IFn)const__4.getRawRoot()).invoke(object35);
                } else {
                    object4 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object4 = map__203872;
            map__203872 = null;
        }
        Object object36 = map__20387 = object4;
        map__20387 = null;
        iss = RT.get(object36, "iss");
        Object object37 = map__203862;
        map__203862 = null;
        Object jws_object = RT.get(object37, const__13);
        Object map__203882 = jwt2;
        Object object38 = ((IFn)const__0.getRawRoot()).invoke(map__203882);
        if (object38 != null && object38 != Boolean.FALSE) {
            Object object39 = ((IFn)const__1.getRawRoot()).invoke(map__203882);
            if (object39 != null && object39 != Boolean.FALSE) {
                Object object40 = map__203882;
                map__203882 = null;
                object3 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object40));
            } else {
                Object object41 = ((IFn)const__3.getRawRoot()).invoke(map__203882);
                if (object41 != null && object41 != Boolean.FALSE) {
                    Object object42 = map__203882;
                    map__203882 = null;
                    object3 = ((IFn)const__4.getRawRoot()).invoke(object42);
                } else {
                    object3 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object3 = map__203882;
            map__203882 = null;
        }
        Object object43 = map__20388 = object3;
        map__20388 = null;
        Object map__203892 = RT.get(object43, const__8);
        Object object44 = ((IFn)const__0.getRawRoot()).invoke(map__203892);
        if (object44 != null && object44 != Boolean.FALSE) {
            Object object45 = ((IFn)const__1.getRawRoot()).invoke(map__203892);
            if (object45 != null && object45 != Boolean.FALSE) {
                Object object46 = map__203892;
                map__203892 = null;
                object2 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object46));
            } else {
                Object object47 = ((IFn)const__3.getRawRoot()).invoke(map__203892);
                if (object47 != null && object47 != Boolean.FALSE) {
                    Object object48 = map__203892;
                    map__203892 = null;
                    object2 = ((IFn)const__4.getRawRoot()).invoke(object48);
                } else {
                    object2 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object2 = map__203892;
            map__203892 = null;
        }
        Object object49 = map__20389 = object2;
        map__20389 = null;
        Object passphrase = RT.get(object49, "passphrase");
        Object[] objectArray = new Object[1];
        Object object50 = key2;
        key2 = null;
        objectArray[0] = Reflector.invokeNoArgInstanceMember(object50, "getBytes", false);
        Object verifier = Reflector.invokeConstructor(RT.classForName("com.nimbusds.jose.crypto.MACVerifier"), objectArray);
        Object object51 = jws_object;
        jws_object = null;
        Object[] objectArray2 = new Object[1];
        Object object52 = verifier;
        verifier = null;
        objectArray2[0] = object52;
        Object object53 = Reflector.invokeInstanceMethod(object51, "verify", objectArray2);
        if (object53 == null || object53 == Boolean.FALSE) {
            throw (Throwable)((IFn)const__9.getRawRoot()).invoke("invalid token signature", RT.mapUniqueKeys(const__10, const__14, const__15, const__16, const__12, iss));
        }
        Object object54 = and__5579__auto__20393 = passphrase;
        if (object54 != null && object54 != Boolean.FALSE) {
            Object object55 = passphrase;
            passphrase = null;
            object = Numbers.lt((long)RT.count(object55), 32L) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            object = and__5579__auto__20393;
            and__5579__auto__20393 = null;
        }
        if (object != null && object != Boolean.FALSE) {
            Object[] objectArray3 = new Object[6];
            objectArray3[0] = const__10;
            objectArray3[1] = const__20;
            objectArray3[2] = const__15;
            objectArray3[3] = const__16;
            objectArray3[4] = const__12;
            Object object56 = iss;
            iss = null;
            objectArray3[5] = object56;
            throw (Throwable)((IFn)const__9.getRawRoot()).invoke("Passphrase must be 32 characters or greater", RT.mapUniqueKeys(objectArray3));
        }
        Object object57 = expires;
        expires = null;
        if (object57 != null && object57 != Boolean.FALSE) {
            Object object58;
            Object and__5579__auto__20395;
            Object object59;
            Object and__5579__auto__20394;
            Object object60;
            Object map__20390;
            Object object61;
            Object object62 = jwt2;
            jwt2 = null;
            Object map__203902 = object62;
            Object object63 = ((IFn)const__0.getRawRoot()).invoke(map__203902);
            if (object63 != null && object63 != Boolean.FALSE) {
                Object object64 = ((IFn)const__1.getRawRoot()).invoke(map__203902);
                if (object64 != null && object64 != Boolean.FALSE) {
                    Object object65 = map__203902;
                    map__203902 = null;
                    object61 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object65));
                } else {
                    Object object66 = ((IFn)const__3.getRawRoot()).invoke(map__203902);
                    if (object66 != null && object66 != Boolean.FALSE) {
                        Object object67 = map__203902;
                        map__203902 = null;
                        object61 = ((IFn)const__4.getRawRoot()).invoke(object67);
                    } else {
                        object61 = PersistentArrayMap.EMPTY;
                    }
                }
            } else {
                object61 = map__203902;
                map__203902 = null;
            }
            Object object68 = map__20390 = object61;
            map__20390 = null;
            Object map__20391 = RT.get(object68, const__8);
            Object object69 = ((IFn)const__0.getRawRoot()).invoke(map__20391);
            if (object69 != null && object69 != Boolean.FALSE) {
                Object object70 = ((IFn)const__1.getRawRoot()).invoke(map__20391);
                if (object70 != null && object70 != Boolean.FALSE) {
                    Object object71 = map__20391;
                    map__20391 = null;
                    object60 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object71));
                } else {
                    Object object72 = ((IFn)const__3.getRawRoot()).invoke(map__20391);
                    if (object72 != null && object72 != Boolean.FALSE) {
                        Object object73 = map__20391;
                        map__20391 = null;
                        object60 = ((IFn)const__4.getRawRoot()).invoke(object73);
                    } else {
                        object60 = PersistentArrayMap.EMPTY;
                    }
                }
            } else {
                object60 = map__20391;
                map__20391 = null;
            }
            Object map__203912 = object60;
            Object exp2 = RT.get(map__203912, "exp");
            Object nbf = RT.get(map__203912, "nbf");
            Object iat = RT.get(map__203912, "iat");
            Object object74 = map__203912;
            map__203912 = null;
            Object iss2 = RT.get(object74, "iss");
            long curr_secs = System.currentTimeMillis() / 1000L;
            Object object75 = exp2;
            if (object75 == null || object75 == Boolean.FALSE) {
                throw (Throwable)((IFn)const__9.getRawRoot()).invoke("token must have an exp property", RT.mapUniqueKeys(const__10, const__23, const__15, const__16, const__12, iss2));
            }
            Object object76 = and__5579__auto__20394 = iat;
            if (object76 != null && object76 != Boolean.FALSE) {
                Object object77 = iat;
                iat = null;
                object59 = Numbers.lt(exp2, object77) ? Boolean.TRUE : Boolean.FALSE;
            } else {
                object59 = and__5579__auto__20394;
                and__5579__auto__20394 = null;
            }
            if (object59 != null && object59 != Boolean.FALSE) {
                throw (Throwable)((IFn)const__9.getRawRoot()).invoke("exp must be greater than or equal iat", RT.mapUniqueKeys(const__10, const__24, const__15, const__16, const__12, iss2));
            }
            Object object78 = exp2;
            exp2 = null;
            Object object79 = ((IFn)const__25.getRawRoot()).invoke(object78, Numbers.num(curr_secs), const__7, clock_skew_secs);
            if (object79 != null && object79 != Boolean.FALSE) {
                throw (Throwable)((IFn)const__9.getRawRoot()).invoke("token expired", RT.mapUniqueKeys(const__10, const__26, const__15, const__27, const__12, iss2));
            }
            Object object80 = and__5579__auto__20395 = nbf;
            if (object80 != null && object80 != Boolean.FALSE) {
                Object object81 = nbf;
                nbf = null;
                Object object82 = clock_skew_secs;
                clock_skew_secs = null;
                object58 = ((IFn)const__28.getRawRoot()).invoke(object81, Numbers.num(curr_secs), const__7, object82);
            } else {
                object58 = and__5579__auto__20395;
                and__5579__auto__20395 = null;
            }
            if (object58 != null && object58 != Boolean.FALSE) {
                Object[] objectArray4 = new Object[6];
                objectArray4[0] = const__10;
                objectArray4[1] = const__29;
                objectArray4[2] = const__15;
                objectArray4[3] = const__27;
                objectArray4[4] = const__12;
                Object object83 = iss2;
                iss2 = null;
                objectArray4[5] = object83;
                throw (Throwable)((IFn)const__9.getRawRoot()).invoke("token expired", RT.mapUniqueKeys(objectArray4));
            }
            v92 = null;
        } else {
            v92 = null;
        }
        return v92;
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return jwt$verify.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

