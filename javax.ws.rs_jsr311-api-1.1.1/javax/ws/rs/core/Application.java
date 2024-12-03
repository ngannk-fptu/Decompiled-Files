/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import java.util.Collections;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Application {
    private static final Set<Object> emptyObjectSet = Collections.emptySet();
    private static final Set<Class<?>> emptyClassSet = Collections.emptySet();

    public Set<Class<?>> getClasses() {
        return emptyClassSet;
    }

    public Set<Object> getSingletons() {
        return emptyObjectSet;
    }
}

