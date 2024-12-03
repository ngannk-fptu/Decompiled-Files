/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.regexp;

import java.util.Vector;
import org.apache.tools.ant.BuildException;

public interface RegexpMatcher {
    public static final int MATCH_DEFAULT = 0;
    public static final int MATCH_CASE_INSENSITIVE = 256;
    public static final int MATCH_MULTILINE = 4096;
    public static final int MATCH_SINGLELINE = 65536;

    public void setPattern(String var1) throws BuildException;

    public String getPattern() throws BuildException;

    public boolean matches(String var1) throws BuildException;

    public Vector<String> getGroups(String var1) throws BuildException;

    public boolean matches(String var1, int var2) throws BuildException;

    public Vector<String> getGroups(String var1, int var2) throws BuildException;
}

