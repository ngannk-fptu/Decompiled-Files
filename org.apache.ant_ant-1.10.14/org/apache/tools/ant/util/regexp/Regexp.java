/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.regexp;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.regexp.RegexpMatcher;

public interface Regexp
extends RegexpMatcher {
    public static final int REPLACE_FIRST = 1;
    public static final int REPLACE_ALL = 16;

    public String substitute(String var1, String var2, int var3) throws BuildException;
}

