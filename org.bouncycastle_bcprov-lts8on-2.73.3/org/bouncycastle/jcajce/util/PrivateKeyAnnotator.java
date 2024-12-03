/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.util;

import java.security.PrivateKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;

public class PrivateKeyAnnotator {
    public static AnnotatedPrivateKey annotate(PrivateKey privKey, String label) {
        return new AnnotatedPrivateKey(privKey, label);
    }

    public static AnnotatedPrivateKey annotate(PrivateKey privKey, Map<String, Object> annotations) {
        HashMap<String, Object> savedAnnotations = new HashMap<String, Object>(annotations);
        return new AnnotatedPrivateKey(privKey, Collections.unmodifiableMap(savedAnnotations));
    }
}

