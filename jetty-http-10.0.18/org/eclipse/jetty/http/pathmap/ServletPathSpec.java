/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.StringUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.http.pathmap;

import org.eclipse.jetty.http.pathmap.AbstractPathSpec;
import org.eclipse.jetty.http.pathmap.MatchedPath;
import org.eclipse.jetty.http.pathmap.PathSpecGroup;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletPathSpec
extends AbstractPathSpec {
    private static final Logger LOG = LoggerFactory.getLogger(ServletPathSpec.class);
    private final String _declaration;
    private final PathSpecGroup _group;
    private final int _pathDepth;
    private final int _specLength;
    private final int _matchLength;
    private final String _prefix;
    private final String _suffix;
    private final MatchedPath _preMatchedPath;

    public static String normalize(String pathSpec) {
        if (StringUtil.isNotBlank((String)pathSpec) && !pathSpec.startsWith("/") && !pathSpec.startsWith("*")) {
            return "/" + pathSpec;
        }
        return pathSpec;
    }

    public static boolean match(String pathSpec, String path) {
        return ServletPathSpec.match(pathSpec, path, false);
    }

    public static boolean match(String pathSpec, String path, boolean noDefault) {
        if (pathSpec.length() == 0) {
            return "/".equals(path);
        }
        char c = pathSpec.charAt(0);
        if (c == '/') {
            if (!noDefault && pathSpec.length() == 1 || pathSpec.equals(path)) {
                return true;
            }
            if (ServletPathSpec.isPathWildcardMatch(pathSpec, path)) {
                return true;
            }
        } else if (c == '*') {
            return path.regionMatches(path.length() - pathSpec.length() + 1, pathSpec, 1, pathSpec.length() - 1);
        }
        return false;
    }

    private static boolean isPathWildcardMatch(String pathSpec, String path) {
        int cpl = pathSpec.length() - 2;
        return pathSpec.endsWith("/*") && path.regionMatches(0, pathSpec, 0, cpl) && (path.length() == cpl || '/' == path.charAt(cpl));
    }

    public static String pathMatch(String pathSpec, String path) {
        char c = pathSpec.charAt(0);
        if (c == '/') {
            if (pathSpec.length() == 1) {
                return path;
            }
            if (pathSpec.equals(path)) {
                return path;
            }
            if (ServletPathSpec.isPathWildcardMatch(pathSpec, path)) {
                return path.substring(0, pathSpec.length() - 2);
            }
        } else if (c == '*' && path.regionMatches(path.length() - (pathSpec.length() - 1), pathSpec, 1, pathSpec.length() - 1)) {
            return path;
        }
        return null;
    }

    public static String pathInfo(String pathSpec, String path) {
        if ("".equals(pathSpec)) {
            return path;
        }
        char c = pathSpec.charAt(0);
        if (c == '/') {
            if (pathSpec.length() == 1) {
                return null;
            }
            boolean wildcard = ServletPathSpec.isPathWildcardMatch(pathSpec, path);
            if (pathSpec.equals(path) && !wildcard) {
                return null;
            }
            if (wildcard) {
                if (path.length() == pathSpec.length() - 2) {
                    return null;
                }
                return path.substring(pathSpec.length() - 2);
            }
        }
        return null;
    }

    public static String relativePath(String base, String pathSpec, String path) {
        String info = ServletPathSpec.pathInfo(pathSpec, (String)path);
        if (info == null) {
            info = path;
        }
        if (info.startsWith("./")) {
            info = info.substring(2);
        }
        path = base.endsWith("/") ? (info.startsWith("/") ? base + info.substring(1) : base + info) : (info.startsWith("/") ? base + info : base + "/" + info);
        return path;
    }

    public ServletPathSpec(String servletPathSpec) {
        MatchedPath preMatchedPath;
        String suffix;
        String prefix;
        PathSpecGroup group;
        if (servletPathSpec == null) {
            servletPathSpec = "";
        }
        if (servletPathSpec.startsWith("servlet|")) {
            servletPathSpec = servletPathSpec.substring("servlet|".length());
        }
        ServletPathSpec.assertValidServletPathSpec(servletPathSpec);
        if (servletPathSpec.isEmpty()) {
            this._declaration = "";
            this._group = PathSpecGroup.ROOT;
            this._pathDepth = -1;
            this._specLength = 1;
            this._matchLength = 0;
            this._prefix = null;
            this._suffix = null;
            this._preMatchedPath = MatchedPath.from("", "/");
            return;
        }
        if ("/".equals(servletPathSpec)) {
            this._declaration = "/";
            this._group = PathSpecGroup.DEFAULT;
            this._pathDepth = -1;
            this._specLength = 1;
            this._matchLength = 0;
            this._prefix = null;
            this._suffix = null;
            this._preMatchedPath = null;
            return;
        }
        int specLength = servletPathSpec.length();
        if (servletPathSpec.charAt(0) == '/' && servletPathSpec.endsWith("/*")) {
            group = PathSpecGroup.PREFIX_GLOB;
            prefix = servletPathSpec.substring(0, specLength - 2);
            suffix = null;
            preMatchedPath = MatchedPath.from(prefix, null);
        } else if (servletPathSpec.charAt(0) == '*' && servletPathSpec.length() > 1) {
            group = PathSpecGroup.SUFFIX_GLOB;
            prefix = null;
            suffix = servletPathSpec.substring(2, specLength);
            preMatchedPath = null;
        } else {
            group = PathSpecGroup.EXACT;
            prefix = servletPathSpec;
            suffix = null;
            if (servletPathSpec.endsWith("*")) {
                LOG.warn("Suspicious URL pattern: '{}'; see sections 12.1 and 12.2 of the Servlet specification", (Object)servletPathSpec);
            }
            preMatchedPath = MatchedPath.from(servletPathSpec, null);
        }
        int pathDepth = 0;
        for (int i = 0; i < specLength; ++i) {
            char c = servletPathSpec.charAt(i);
            if (c >= '\u0080' || c != '/') continue;
            ++pathDepth;
        }
        this._declaration = servletPathSpec;
        this._group = group;
        this._pathDepth = pathDepth;
        this._specLength = specLength;
        this._matchLength = prefix == null ? 0 : prefix.length();
        this._prefix = prefix;
        this._suffix = suffix;
        this._preMatchedPath = preMatchedPath;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating {}[{}] (group: {}, prefix: \"{}\", suffix: \"{}\")", new Object[]{this.getClass().getSimpleName(), this._declaration, this._group, this._prefix, this._suffix});
        }
    }

    private static void assertValidServletPathSpec(String servletPathSpec) {
        if (servletPathSpec == null || servletPathSpec.equals("")) {
            return;
        }
        int len = servletPathSpec.length();
        if (servletPathSpec.charAt(0) == '/') {
            if (len == 1) {
                return;
            }
            int idx = servletPathSpec.indexOf(42);
            if (idx < 0) {
                return;
            }
            if (idx != len - 1) {
                throw new IllegalArgumentException("Servlet Spec 12.2 violation: glob '*' can only exist at end of prefix based matches: bad spec \"" + servletPathSpec + "\"");
            }
        } else if (servletPathSpec.startsWith("*.")) {
            int idx = servletPathSpec.indexOf(47);
            if (idx >= 0) {
                throw new IllegalArgumentException("Servlet Spec 12.2 violation: suffix based path spec cannot have path separators: bad spec \"" + servletPathSpec + "\"");
            }
            idx = servletPathSpec.indexOf(42, 2);
            if (idx >= 1) {
                throw new IllegalArgumentException("Servlet Spec 12.2 violation: suffix based path spec cannot have multiple glob '*': bad spec \"" + servletPathSpec + "\"");
            }
        } else {
            throw new IllegalArgumentException("Servlet Spec 12.2 violation: path spec must start with \"/\" or \"*.\": bad spec \"" + servletPathSpec + "\"");
        }
    }

    @Override
    public int getSpecLength() {
        return this._specLength;
    }

    @Override
    public PathSpecGroup getGroup() {
        return this._group;
    }

    @Override
    public int getPathDepth() {
        return this._pathDepth;
    }

    @Override
    @Deprecated
    public String getPathInfo(String path) {
        switch (this._group) {
            case ROOT: {
                return path;
            }
            case PREFIX_GLOB: {
                if (path.length() == this._matchLength) {
                    return null;
                }
                return path.substring(this._matchLength);
            }
        }
        return null;
    }

    @Override
    @Deprecated
    public String getPathMatch(String path) {
        switch (this._group) {
            case ROOT: {
                return "";
            }
            case EXACT: {
                if (this._declaration.equals(path)) {
                    return path;
                }
                return null;
            }
            case PREFIX_GLOB: {
                if (this.isWildcardMatch(path)) {
                    return path.substring(0, this._matchLength);
                }
                return null;
            }
            case SUFFIX_GLOB: {
                if (path.regionMatches(path.length() - (this._specLength - 1), this._declaration, 1, this._specLength - 1)) {
                    return path;
                }
                return null;
            }
            case DEFAULT: {
                return path;
            }
        }
        return null;
    }

    @Override
    public String getDeclaration() {
        return this._declaration;
    }

    @Override
    public String getPrefix() {
        return this._prefix;
    }

    @Override
    public String getSuffix() {
        return this._suffix;
    }

    private boolean isWildcardMatch(String path) {
        if (this._group == PathSpecGroup.PREFIX_GLOB && path.length() >= this._matchLength && path.regionMatches(0, this._declaration, 0, this._matchLength)) {
            return path.length() == this._matchLength || path.charAt(this._matchLength) == '/';
        }
        return false;
    }

    @Override
    public MatchedPath matched(String path) {
        switch (this._group) {
            case EXACT: {
                if (!this._declaration.equals(path)) break;
                return this._preMatchedPath;
            }
            case PREFIX_GLOB: {
                if (!this.isWildcardMatch(path)) break;
                if (path.length() == this._matchLength) {
                    return this._preMatchedPath;
                }
                return MatchedPath.from(path.substring(0, this._matchLength), path.substring(this._matchLength));
            }
            case SUFFIX_GLOB: {
                if (!path.regionMatches(path.length() - this._specLength + 1, this._declaration, 1, this._specLength - 1)) break;
                return MatchedPath.from(path, null);
            }
            case ROOT: {
                if (!"/".equals(path)) break;
                return this._preMatchedPath;
            }
            case DEFAULT: {
                return MatchedPath.from(path, null);
            }
        }
        return null;
    }

    @Override
    public boolean matches(String path) {
        switch (this._group) {
            case EXACT: {
                return this._declaration.equals(path);
            }
            case PREFIX_GLOB: {
                return this.isWildcardMatch(path);
            }
            case SUFFIX_GLOB: {
                return path.regionMatches(path.length() - this._specLength + 1, this._declaration, 1, this._specLength - 1);
            }
            case ROOT: {
                return "/".equals(path);
            }
            case DEFAULT: {
                return true;
            }
        }
        return false;
    }
}

