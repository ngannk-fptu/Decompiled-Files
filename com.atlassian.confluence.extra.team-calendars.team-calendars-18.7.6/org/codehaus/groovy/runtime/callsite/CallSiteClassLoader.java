/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.groovy.reflection.ClassLoaderForClassArtifacts;

public class CallSiteClassLoader
extends ClassLoaderForClassArtifacts {
    private static final Set<String> KNOWN_CLASSES = new HashSet<String>();

    public CallSiteClassLoader(Class klazz) {
        super(klazz);
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (KNOWN_CLASSES.contains(name)) {
            return this.getClass().getClassLoader().loadClass(name);
        }
        try {
            return super.loadClass(name, resolve);
        }
        catch (ClassNotFoundException e) {
            return this.getClass().getClassLoader().loadClass(name);
        }
    }

    static {
        Collections.addAll(KNOWN_CLASSES, "org.codehaus.groovy.runtime.callsite.PogoMetaMethodSite", "org.codehaus.groovy.runtime.callsite.PojoMetaMethodSite", "org.codehaus.groovy.runtime.callsite.StaticMetaMethodSite", "org.codehaus.groovy.runtime.callsite.CallSite", "org.codehaus.groovy.runtime.callsite.CallSiteArray", "groovy.lang.MetaMethod", "groovy.lang.MetaClassImpl");
    }
}

