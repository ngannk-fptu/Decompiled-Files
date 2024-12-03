/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.util;

import java.security.PrivateKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnnotatedPrivateKey
implements PrivateKey {
    public static final String LABEL = "label";
    private final PrivateKey key;
    private final Map<String, Object> annotations;

    AnnotatedPrivateKey(PrivateKey key, String label) {
        this.key = key;
        this.annotations = Collections.singletonMap(LABEL, label);
    }

    AnnotatedPrivateKey(PrivateKey key, Map<String, Object> annotations) {
        this.key = key;
        this.annotations = annotations;
    }

    public PrivateKey getKey() {
        return this.key;
    }

    public Map<String, Object> getAnnotations() {
        return this.annotations;
    }

    @Override
    public String getAlgorithm() {
        return this.key.getAlgorithm();
    }

    public Object getAnnotation(String key) {
        return this.annotations.get(key);
    }

    public AnnotatedPrivateKey addAnnotation(String name, Object annotation) {
        HashMap<String, Object> newAnnotations = new HashMap<String, Object>(this.annotations);
        newAnnotations.put(name, annotation);
        return new AnnotatedPrivateKey(this.key, Collections.unmodifiableMap(newAnnotations));
    }

    public AnnotatedPrivateKey removeAnnotation(String name) {
        HashMap<String, Object> newAnnotations = new HashMap<String, Object>(this.annotations);
        newAnnotations.remove(name);
        return new AnnotatedPrivateKey(this.key, Collections.unmodifiableMap(newAnnotations));
    }

    @Override
    public String getFormat() {
        return this.key.getFormat();
    }

    @Override
    public byte[] getEncoded() {
        return this.key.getEncoded();
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof AnnotatedPrivateKey) {
            return this.key.equals(((AnnotatedPrivateKey)o).key);
        }
        return this.key.equals(o);
    }

    public String toString() {
        if (this.annotations.containsKey(LABEL)) {
            return this.annotations.get(LABEL).toString();
        }
        return this.key.toString();
    }
}

