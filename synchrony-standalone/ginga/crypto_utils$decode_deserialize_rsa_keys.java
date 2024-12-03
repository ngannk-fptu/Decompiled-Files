/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.Keyword;
import clojure.lang.KeywordLookupSite;
import clojure.lang.RT;
import clojure.lang.Var;

public final class crypto_utils$decode_deserialize_rsa_keys
extends AFunction {
    public static final Var const__0 = RT.var("ginga.crypto-utils", "decode-base64-keys");
    public static final Keyword const__1 = RT.keyword(null, "private");
    public static final Var const__2 = RT.var("ginga.crypto-utils", "pkcs8->rsa-priv-key");
    public static final Keyword const__3 = RT.keyword(null, "public");
    public static final Var const__4 = RT.var("ginga.crypto-utils", "x509->rsa-pub-key");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "private"));
    static ILookupThunk __thunk__0__ = __site__0__;
    static final KeywordLookupSite __site__1__ = new KeywordLookupSite(RT.keyword(null, "public"));
    static ILookupThunk __thunk__1__ = __site__1__;

    public static Object invokeStatic(Object base64_keys) {
        Object object = base64_keys;
        base64_keys = null;
        Object decoded_keys = ((IFn)const__0.getRawRoot()).invoke(object);
        Object[] objectArray = new Object[4];
        objectArray[0] = const__1;
        IFn iFn = (IFn)const__2.getRawRoot();
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object2 = decoded_keys;
        Object object3 = iLookupThunk.get(object2);
        if (iLookupThunk == object3) {
            __thunk__0__ = __site__0__.fault(object2);
            object3 = __thunk__0__.get(object2);
        }
        objectArray[1] = iFn.invoke(object3);
        objectArray[2] = const__3;
        IFn iFn2 = (IFn)const__4.getRawRoot();
        ILookupThunk iLookupThunk2 = __thunk__1__;
        Object object4 = decoded_keys;
        decoded_keys = null;
        Object object5 = iLookupThunk2.get(object4);
        if (iLookupThunk2 == object5) {
            __thunk__1__ = __site__1__.fault(object4);
            object5 = __thunk__1__.get(object4);
        }
        objectArray[3] = iFn2.invoke(object5);
        return RT.mapUniqueKeys(objectArray);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return crypto_utils$decode_deserialize_rsa_keys.invokeStatic(object2);
    }
}

