/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

class FunctorUtils {
    static final String UNSAFE_SERIALIZABLE_PROPERTY = "org.apache.commons.collections.enableUnsafeSerialization";

    private FunctorUtils() {
    }

    static Predicate[] copy(Predicate[] predicates) {
        if (predicates == null) {
            return null;
        }
        return (Predicate[])predicates.clone();
    }

    static void validate(Predicate[] predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException("The predicate array must not be null");
        }
        for (int i = 0; i < predicates.length; ++i) {
            if (predicates[i] != null) continue;
            throw new IllegalArgumentException("The predicate array must not contain a null predicate, index " + i + " was null");
        }
    }

    static Predicate[] validate(Collection predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException("The predicate collection must not be null");
        }
        Predicate[] preds = new Predicate[predicates.size()];
        int i = 0;
        Iterator it = predicates.iterator();
        while (it.hasNext()) {
            preds[i] = (Predicate)it.next();
            if (preds[i] == null) {
                throw new IllegalArgumentException("The predicate collection must not contain a null predicate, index " + i + " was null");
            }
            ++i;
        }
        return preds;
    }

    static Closure[] copy(Closure[] closures) {
        if (closures == null) {
            return null;
        }
        return (Closure[])closures.clone();
    }

    static void validate(Closure[] closures) {
        if (closures == null) {
            throw new IllegalArgumentException("The closure array must not be null");
        }
        for (int i = 0; i < closures.length; ++i) {
            if (closures[i] != null) continue;
            throw new IllegalArgumentException("The closure array must not contain a null closure, index " + i + " was null");
        }
    }

    static Transformer[] copy(Transformer[] transformers) {
        if (transformers == null) {
            return null;
        }
        return (Transformer[])transformers.clone();
    }

    static void validate(Transformer[] transformers) {
        if (transformers == null) {
            throw new IllegalArgumentException("The transformer array must not be null");
        }
        for (int i = 0; i < transformers.length; ++i) {
            if (transformers[i] != null) continue;
            throw new IllegalArgumentException("The transformer array must not contain a null transformer, index " + i + " was null");
        }
    }

    static void checkUnsafeSerialization(Class clazz) {
        String unsafeSerializableProperty;
        try {
            unsafeSerializableProperty = (String)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return System.getProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY);
                }
            });
        }
        catch (SecurityException ex) {
            unsafeSerializableProperty = null;
        }
        if (!"true".equalsIgnoreCase(unsafeSerializableProperty)) {
            throw new UnsupportedOperationException("Serialization support for " + clazz.getName() + " is disabled for security reasons. " + "To enable it set system property '" + UNSAFE_SERIALIZABLE_PROPERTY + "' to 'true', " + "but you must ensure that your application does not de-serialize objects from untrusted sources.");
        }
    }
}

