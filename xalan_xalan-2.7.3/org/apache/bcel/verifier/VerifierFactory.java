/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.bcel.verifier.Verifier;
import org.apache.bcel.verifier.VerifierFactoryObserver;

public class VerifierFactory {
    private static final Map<String, Verifier> MAP = new HashMap<String, Verifier>();
    private static final List<VerifierFactoryObserver> OBSVERVERS = new Vector<VerifierFactoryObserver>();

    public static void attach(VerifierFactoryObserver o) {
        OBSVERVERS.add(o);
    }

    public static void clear() {
        MAP.clear();
        OBSVERVERS.clear();
    }

    public static void detach(VerifierFactoryObserver o) {
        OBSVERVERS.remove(o);
    }

    public static Verifier getVerifier(String fullyQualifiedClassName) {
        return MAP.computeIfAbsent(fullyQualifiedClassName, k -> {
            Verifier v = new Verifier((String)k);
            VerifierFactory.notify(k);
            return v;
        });
    }

    public static Verifier[] getVerifiers() {
        return MAP.values().toArray(Verifier.EMPTY_ARRAY);
    }

    private static void notify(String fullyQualifiedClassName) {
        OBSVERVERS.forEach(vfo -> vfo.update(fullyQualifiedClassName));
    }

    private VerifierFactory() {
    }
}

