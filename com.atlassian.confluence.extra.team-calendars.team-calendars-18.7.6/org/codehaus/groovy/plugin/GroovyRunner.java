/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.plugin;

import groovy.lang.GroovyClassLoader;

public interface GroovyRunner {
    public boolean canRun(Class var1, GroovyClassLoader var2);

    public Object run(Class var1, GroovyClassLoader var2);
}

