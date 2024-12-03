/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.PathBuilder;
import com.typesafe.config.impl.PathParser;
import java.util.Iterator;
import java.util.List;

final class Path {
    private final String first;
    private final Path remainder;

    Path(String first, Path remainder) {
        this.first = first;
        this.remainder = remainder;
    }

    Path(String ... elements) {
        if (elements.length == 0) {
            throw new ConfigException.BugOrBroken("empty path");
        }
        this.first = elements[0];
        if (elements.length > 1) {
            PathBuilder pb = new PathBuilder();
            for (int i = 1; i < elements.length; ++i) {
                pb.appendKey(elements[i]);
            }
            this.remainder = pb.result();
        } else {
            this.remainder = null;
        }
    }

    Path(List<Path> pathsToConcat) {
        this(pathsToConcat.iterator());
    }

    Path(Iterator<Path> i) {
        if (!i.hasNext()) {
            throw new ConfigException.BugOrBroken("empty path");
        }
        Path firstPath = i.next();
        this.first = firstPath.first;
        PathBuilder pb = new PathBuilder();
        if (firstPath.remainder != null) {
            pb.appendPath(firstPath.remainder);
        }
        while (i.hasNext()) {
            pb.appendPath(i.next());
        }
        this.remainder = pb.result();
    }

    String first() {
        return this.first;
    }

    Path remainder() {
        return this.remainder;
    }

    Path parent() {
        if (this.remainder == null) {
            return null;
        }
        PathBuilder pb = new PathBuilder();
        Path p = this;
        while (p.remainder != null) {
            pb.appendKey(p.first);
            p = p.remainder;
        }
        return pb.result();
    }

    String last() {
        Path p = this;
        while (p.remainder != null) {
            p = p.remainder;
        }
        return p.first;
    }

    Path prepend(Path toPrepend) {
        PathBuilder pb = new PathBuilder();
        pb.appendPath(toPrepend);
        pb.appendPath(this);
        return pb.result();
    }

    int length() {
        int count = 1;
        Path p = this.remainder;
        while (p != null) {
            ++count;
            p = p.remainder;
        }
        return count;
    }

    Path subPath(int removeFromFront) {
        Path p = this;
        for (int count = removeFromFront; p != null && count > 0; --count) {
            p = p.remainder;
        }
        return p;
    }

    Path subPath(int firstIndex, int lastIndex) {
        if (lastIndex < firstIndex) {
            throw new ConfigException.BugOrBroken("bad call to subPath");
        }
        Path from = this.subPath(firstIndex);
        PathBuilder pb = new PathBuilder();
        for (int count = lastIndex - firstIndex; count > 0; --count) {
            pb.appendKey(from.first());
            if ((from = from.remainder()) != null) continue;
            throw new ConfigException.BugOrBroken("subPath lastIndex out of range " + lastIndex);
        }
        return pb.result();
    }

    boolean startsWith(Path other) {
        Path myRemainder = this;
        Path otherRemainder = other;
        if (otherRemainder.length() <= myRemainder.length()) {
            while (otherRemainder != null) {
                if (!otherRemainder.first().equals(myRemainder.first())) {
                    return false;
                }
                myRemainder = myRemainder.remainder();
                otherRemainder = otherRemainder.remainder();
            }
            return true;
        }
        return false;
    }

    public boolean equals(Object other) {
        if (other instanceof Path) {
            Path that = (Path)other;
            return this.first.equals(that.first) && ConfigImplUtil.equalsHandlingNull(this.remainder, that.remainder);
        }
        return false;
    }

    public int hashCode() {
        return 41 * (41 + this.first.hashCode()) + (this.remainder == null ? 0 : this.remainder.hashCode());
    }

    static boolean hasFunkyChars(String s) {
        int length = s.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_') continue;
            return true;
        }
        return false;
    }

    private void appendToStringBuilder(StringBuilder sb) {
        if (Path.hasFunkyChars(this.first) || this.first.isEmpty()) {
            sb.append(ConfigImplUtil.renderJsonString(this.first));
        } else {
            sb.append(this.first);
        }
        if (this.remainder != null) {
            sb.append(".");
            this.remainder.appendToStringBuilder(sb);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path(");
        this.appendToStringBuilder(sb);
        sb.append(")");
        return sb.toString();
    }

    String render() {
        StringBuilder sb = new StringBuilder();
        this.appendToStringBuilder(sb);
        return sb.toString();
    }

    static Path newKey(String key) {
        return new Path(key, null);
    }

    static Path newPath(String path) {
        return PathParser.parsePath(path);
    }
}

