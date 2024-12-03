/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import java.util.Set;

public interface PackageHelper {
    public static final String IMPORT_COMPLETION_PREFERENCE_KEY = "disable-import-completion";

    public Set<String> getContents(String var1);
}

