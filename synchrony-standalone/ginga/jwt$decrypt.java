/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Reflector;

public final class jwt$decrypt
extends AFunction {
    public static Object invokeStatic(Object jwe_object, Object rsa_private_key) {
        Object[] objectArray = new Object[1];
        Object[] objectArray2 = new Object[1];
        Object object = rsa_private_key;
        rsa_private_key = null;
        objectArray2[0] = object;
        objectArray[0] = Reflector.invokeConstructor(RT.classForName("com.nimbusds.jose.crypto.RSADecrypter"), objectArray2);
        Reflector.invokeInstanceMethod(jwe_object, "decrypt", objectArray);
        Object object2 = jwe_object;
        jwe_object = null;
        return Reflector.invokeNoArgInstanceMember(Reflector.invokeNoArgInstanceMember(object2, "getPayload", false), "toSignedJWT", false);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return jwt$decrypt.invokeStatic(object3, object4);
    }
}

