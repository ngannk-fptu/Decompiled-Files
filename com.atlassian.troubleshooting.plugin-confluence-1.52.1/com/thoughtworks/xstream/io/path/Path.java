/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.core.util.FastStack;
import java.util.ArrayList;

public class Path {
    private final String[] chunks;
    private transient String pathAsString;
    private transient String pathExplicit;
    private static final Path DOT = new Path(new String[]{"."});

    public Path(String pathAsString) {
        int nextSeparator;
        ArrayList<String> result = new ArrayList<String>();
        int currentIndex = 0;
        this.pathAsString = pathAsString;
        while ((nextSeparator = pathAsString.indexOf(47, currentIndex)) != -1) {
            result.add(this.normalize(pathAsString, currentIndex, nextSeparator));
            currentIndex = nextSeparator + 1;
        }
        result.add(this.normalize(pathAsString, currentIndex, pathAsString.length()));
        String[] arr = new String[result.size()];
        result.toArray(arr);
        this.chunks = arr;
    }

    private String normalize(String s, int start, int end) {
        if (end - start > 3 && s.charAt(end - 3) == '[' && s.charAt(end - 2) == '1' && s.charAt(end - 1) == ']') {
            this.pathAsString = null;
            return s.substring(start, end - 3);
        }
        return s.substring(start, end);
    }

    public Path(String[] chunks) {
        this.chunks = chunks;
    }

    public String toString() {
        if (this.pathAsString == null) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < this.chunks.length; ++i) {
                if (i > 0) {
                    buffer.append('/');
                }
                buffer.append(this.chunks[i]);
            }
            this.pathAsString = buffer.toString();
        }
        return this.pathAsString;
    }

    public String explicit() {
        if (this.pathExplicit == null) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < this.chunks.length; ++i) {
                char c;
                if (i > 0) {
                    buffer.append('/');
                }
                String chunk = this.chunks[i];
                buffer.append(chunk);
                int length = chunk.length();
                if (length <= 0 || (c = chunk.charAt(length - 1)) == ']' || c == '.') continue;
                buffer.append("[1]");
            }
            this.pathExplicit = buffer.toString();
        }
        return this.pathExplicit;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Path)) {
            return false;
        }
        Path other = (Path)o;
        if (this.chunks.length != other.chunks.length) {
            return false;
        }
        for (int i = 0; i < this.chunks.length; ++i) {
            if (this.chunks[i].equals(other.chunks[i])) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = 543645643;
        for (int i = 0; i < this.chunks.length; ++i) {
            result = 29 * result + this.chunks[i].hashCode();
        }
        return result;
    }

    public Path relativeTo(Path that) {
        int depthOfPathDivergence = this.depthOfPathDivergence(this.chunks, that.chunks);
        String[] result = new String[this.chunks.length + that.chunks.length - 2 * depthOfPathDivergence];
        int count = 0;
        for (int i = depthOfPathDivergence; i < this.chunks.length; ++i) {
            result[count++] = "..";
        }
        for (int j = depthOfPathDivergence; j < that.chunks.length; ++j) {
            result[count++] = that.chunks[j];
        }
        if (count == 0) {
            return DOT;
        }
        return new Path(result);
    }

    private int depthOfPathDivergence(String[] path1, String[] path2) {
        int minLength = Math.min(path1.length, path2.length);
        for (int i = 0; i < minLength; ++i) {
            if (path1[i].equals(path2[i])) continue;
            return i;
        }
        return minLength;
    }

    public Path apply(Path relativePath) {
        int i;
        FastStack absoluteStack = new FastStack(16);
        for (i = 0; i < this.chunks.length; ++i) {
            absoluteStack.push(this.chunks[i]);
        }
        for (i = 0; i < relativePath.chunks.length; ++i) {
            String relativeChunk = relativePath.chunks[i];
            if (relativeChunk.equals("..")) {
                absoluteStack.pop();
                continue;
            }
            if (relativeChunk.equals(".")) continue;
            absoluteStack.push(relativeChunk);
        }
        String[] result = new String[absoluteStack.size()];
        for (int i2 = 0; i2 < result.length; ++i2) {
            result[i2] = (String)absoluteStack.get(i2);
        }
        return new Path(result);
    }

    public boolean isAncestor(Path child) {
        if (child == null || child.chunks.length < this.chunks.length) {
            return false;
        }
        for (int i = 0; i < this.chunks.length; ++i) {
            if (this.chunks[i].equals(child.chunks[i])) continue;
            return false;
        }
        return true;
    }
}

