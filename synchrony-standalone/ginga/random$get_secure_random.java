/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class random$get_secure_random
extends AFunction {
    public static Object invokeStatic() {
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance("NativePRNG");
        }
        catch (NoSuchAlgorithmException ignored) {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        }
        return secureRandom;
    }

    @Override
    public Object invoke() {
        return random$get_secure_random.invokeStatic();
    }
}

