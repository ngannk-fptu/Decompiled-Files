/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class crypto_utils$loading__6789__auto____14666
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "refer");
    public static final AFn const__1 = Symbol.intern(null, "clojure.core");
    public static final Var const__2 = RT.var("clojure.core", "require");
    public static final AFn const__3 = (AFn)((Object)Tuple.create(Symbol.intern(null, "ginga.log"), RT.keyword(null, "refer"), Tuple.create(Symbol.intern(null, "log"))));
    public static final AFn const__4 = (AFn)((Object)Tuple.create(Symbol.intern(null, "byte-transforms"), RT.keyword(null, "refer"), Tuple.create(Symbol.intern(null, "encode"), Symbol.intern(null, "decode"))));

    @Override
    public Object invoke() {
        Class clazz;
        Var.pushThreadBindings(RT.mapUniqueKeys(Compiler.LOADER, this.getClass().getClassLoader()));
        try {
            ((IFn)const__0.getRawRoot()).invoke(const__1);
            ((IFn)const__2.getRawRoot()).invoke(const__3, const__4);
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("java.security.KeyPairGenerator"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("java.security.SecureRandom"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("java.security.interfaces.RSAKey"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("java.security.interfaces.RSAPrivateKey"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("java.security.interfaces.RSAPublicKey"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("java.security.spec.X509EncodedKeySpec"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("java.security.spec.PKCS8EncodedKeySpec"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("java.security.KeyFactory"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("javax.crypto.spec.SecretKeySpec"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("javax.crypto.spec.PBEKeySpec"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("javax.crypto.spec.GCMParameterSpec"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("javax.crypto.SecretKey"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("javax.crypto.SecretKeyFactory"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("javax.crypto.Cipher"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("javax.crypto.KeyGenerator"));
            clazz = ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("java.util.Arrays"));
        }
        finally {
            Var.popThreadBindings();
        }
        return clazz;
    }
}

