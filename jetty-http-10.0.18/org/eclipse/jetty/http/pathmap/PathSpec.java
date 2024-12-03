/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.http.pathmap;

import java.util.Objects;
import org.eclipse.jetty.http.pathmap.MatchedPath;
import org.eclipse.jetty.http.pathmap.PathSpecGroup;
import org.eclipse.jetty.http.pathmap.RegexPathSpec;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;

public interface PathSpec
extends Comparable<PathSpec> {
    public static PathSpec from(String pathSpecString) {
        Objects.requireNonNull(pathSpecString, "null PathSpec not supported");
        if (pathSpecString.length() == 0) {
            return new ServletPathSpec("");
        }
        return pathSpecString.charAt(0) == '^' ? new RegexPathSpec(pathSpecString) : new ServletPathSpec(pathSpecString);
    }

    public int getSpecLength();

    public PathSpecGroup getGroup();

    public int getPathDepth();

    @Deprecated
    public String getPathInfo(String var1);

    @Deprecated
    public String getPathMatch(String var1);

    public String getDeclaration();

    public String getPrefix();

    public String getSuffix();

    @Deprecated
    public boolean matches(String var1);

    public MatchedPath matched(String var1);
}

