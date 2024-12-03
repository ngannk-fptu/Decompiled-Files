/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.types.selectors.TokenizedPattern;
import org.apache.tools.ant.util.FileUtils;

public class TokenizedPath {
    public static final TokenizedPath EMPTY_PATH = new TokenizedPath("", new String[0]);
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final boolean[] CS_SCAN_ONLY = new boolean[]{true};
    private static final boolean[] CS_THEN_NON_CS = new boolean[]{true, false};
    private final String path;
    private final String[] tokenizedPath;

    public TokenizedPath(String path) {
        this(path, SelectorUtils.tokenizePathAsArray(path));
    }

    public TokenizedPath(TokenizedPath parent, String child) {
        this.path = !parent.path.isEmpty() && parent.path.charAt(parent.path.length() - 1) != File.separatorChar ? parent.path + File.separatorChar + child : parent.path + child;
        this.tokenizedPath = new String[parent.tokenizedPath.length + 1];
        System.arraycopy(parent.tokenizedPath, 0, this.tokenizedPath, 0, parent.tokenizedPath.length);
        this.tokenizedPath[parent.tokenizedPath.length] = child;
    }

    TokenizedPath(String path, String[] tokens) {
        this.path = path;
        this.tokenizedPath = tokens;
    }

    public String toString() {
        return this.path;
    }

    public int depth() {
        return this.tokenizedPath.length;
    }

    String[] getTokens() {
        return this.tokenizedPath;
    }

    public File findFile(File base, boolean cs) {
        String[] tokens = this.tokenizedPath;
        if (FileUtils.isAbsolutePath(this.path)) {
            if (base == null) {
                String[] s = FILE_UTILS.dissect(this.path);
                base = new File(s[0]);
                tokens = SelectorUtils.tokenizePathAsArray(s[1]);
            } else {
                File f = FILE_UTILS.normalize(this.path);
                String s = FILE_UTILS.removeLeadingPath(base, f);
                if (s.equals(f.getAbsolutePath())) {
                    return null;
                }
                tokens = SelectorUtils.tokenizePathAsArray(s);
            }
        }
        return TokenizedPath.findFile(base, tokens, cs);
    }

    public boolean isSymlink(File base) {
        for (String token : this.tokenizedPath) {
            Path pathToTraverse = base == null ? Paths.get(token, new String[0]) : Paths.get(base.toPath().toString(), token);
            if (Files.isSymbolicLink(pathToTraverse)) {
                return true;
            }
            base = new File(base, token);
        }
        return false;
    }

    public boolean equals(Object o) {
        return o instanceof TokenizedPath && this.path.equals(((TokenizedPath)o).path);
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    private static File findFile(File base, String[] pathElements, boolean cs) {
        for (String pathElement : pathElements) {
            if (!base.isDirectory()) {
                return null;
            }
            String[] files = base.list();
            if (files == null) {
                throw new BuildException("IO error scanning directory %s", base.getAbsolutePath());
            }
            boolean found = false;
            boolean[] matchCase = cs ? CS_SCAN_ONLY : CS_THEN_NON_CS;
            for (int i = 0; !found && i < matchCase.length; ++i) {
                for (int j = 0; !found && j < files.length; ++j) {
                    if (!(matchCase[i] ? files[j].equals(pathElement) : files[j].equalsIgnoreCase(pathElement))) continue;
                    base = new File(base, files[j]);
                    found = true;
                }
            }
            if (found) continue;
            return null;
        }
        return pathElements.length == 0 && !base.isDirectory() ? null : base;
    }

    public TokenizedPattern toPattern() {
        return new TokenizedPattern(this.path, this.tokenizedPath);
    }
}

