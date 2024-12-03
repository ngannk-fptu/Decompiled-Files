/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.annotations;

import com.thoughtworks.xstream.XStream;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class Annotations {
    private Annotations() {
    }

    @Deprecated
    public static synchronized void configureAliases(XStream xstream, Class<?> ... topLevelClasses) {
        xstream.processAnnotations(topLevelClasses);
    }
}

