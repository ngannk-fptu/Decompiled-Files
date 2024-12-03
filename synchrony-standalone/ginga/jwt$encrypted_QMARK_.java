/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import com.nimbusds.jwt.EncryptedJWT;

public final class jwt$encrypted_QMARK_
extends AFunction {
    public static Object invokeStatic(Object jwt_object) {
        Object object = jwt_object;
        jwt_object = null;
        return object instanceof EncryptedJWT ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return jwt$encrypted_QMARK_.invokeStatic(object2);
    }
}

