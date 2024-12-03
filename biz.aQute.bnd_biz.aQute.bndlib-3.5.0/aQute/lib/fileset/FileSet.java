/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.fileset;

import aQute.libg.glob.Glob;
import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FileSet {
    private final File base;
    private DFA dfa;
    private String source;

    public FileSet(File base, String filesetSpec) {
        this.source = filesetSpec;
        this.dfa = FileSet.compile(filesetSpec);
        this.base = base;
    }

    private static DFA compile(String filesetSpec) {
        String[] parts = filesetSpec.trim().split("\\s*,\\s*");
        DFA result = null;
        for (String part : parts) {
            String[] segments;
            String lastSegment;
            if (part.startsWith("/")) {
                throw new IllegalArgumentException("FileSet must not start with a /");
            }
            if (part.endsWith("**")) {
                part = part + "/*";
            }
            DFA prev = (lastSegment = (segments = part.split("/"))[segments.length - 1]).startsWith("**") ? new AnyDir(new FileMatch(lastSegment.substring(1))) : new FileMatch(lastSegment);
            for (int i = segments.length - 2; i >= 0; --i) {
                String segment = segments[i];
                prev = segment.equals("**") ? new AnyDir(prev) : new DirMatch(prev, segment);
            }
            result = result == null ? prev : new OrDFA(result, prev);
        }
        return result;
    }

    public Set<File> getFiles() {
        HashSet<File> files = new HashSet<File>();
        for (File sub : this.base.listFiles()) {
            this.dfa.match(files, sub);
        }
        return files;
    }

    public boolean isIncluded(File file) {
        URI target = file.toURI();
        URI source = this.base.toURI();
        URI relative = source.relativize(target);
        if (relative.equals(target) || relative.equals(source)) {
            return false;
        }
        String[] segments = relative.getPath().split("/");
        return this.dfa.isIncluded(segments, 0);
    }

    public boolean isIncluded(String relativePath) {
        if (relativePath.startsWith("/")) {
            throw new IllegalArgumentException("FileSet must not start with a /");
        }
        String[] segments = relativePath.split("/");
        return this.dfa.isIncluded(segments, 0);
    }

    public boolean hasOverlap(Collection<File> files) {
        for (File f : files) {
            if (!this.isIncluded(f)) continue;
            return true;
        }
        return false;
    }

    public File findFirst(String file) {
        for (File f : this.getFiles()) {
            if (!f.getName().equals(file)) continue;
            return f;
        }
        return null;
    }

    public String toString() {
        return this.source;
    }

    static class FileMatch
    extends DFA {
        final Glob glob;

        FileMatch(String string) {
            this.glob = new Glob(string);
        }

        @Override
        void match(Collection<File> files, File input) {
            if (input.isFile() && this.glob.matcher(input.getName()).matches()) {
                files.add(input);
            }
        }

        @Override
        boolean isIncluded(String[] segments, int n) {
            if (n != segments.length - 1) {
                return false;
            }
            return this.glob.matcher(segments[n]).matches();
        }
    }

    static class AnyDir
    extends DFA {
        final DFA next;

        AnyDir(DFA next) {
            this.next = next;
        }

        @Override
        void match(Collection<File> files, File input) {
            if (input.isDirectory()) {
                for (File sub : input.listFiles()) {
                    this.next.match(files, sub);
                    this.match(files, sub);
                }
            } else {
                this.next.match(files, input);
            }
        }

        @Override
        boolean isIncluded(String[] segments, int n) {
            if (n >= segments.length - 1) {
                return false;
            }
            return this.next.isIncluded(segments, n + 1) || this.isIncluded(segments, n + 1);
        }
    }

    static class DirMatch
    extends DFA {
        final Glob glob;
        final DFA next;

        DirMatch(DFA next, String segment) {
            this.next = next;
            this.glob = new Glob(segment);
        }

        @Override
        void match(Collection<File> files, File input) {
            if (input.isDirectory() && this.glob.matcher(input.getName()).matches()) {
                for (File sub : input.listFiles()) {
                    this.next.match(files, sub);
                }
            }
        }

        @Override
        boolean isIncluded(String[] segments, int n) {
            if (n >= segments.length - 1) {
                return false;
            }
            if (!this.glob.matcher(segments[n]).matches()) {
                return false;
            }
            return this.next.isIncluded(segments, n + 1);
        }
    }

    static class OrDFA
    extends DFA {
        private DFA a;
        private DFA b;

        public OrDFA(DFA a, DFA b) {
            this.a = a;
            this.b = b;
        }

        @Override
        void match(Collection<File> files, File input) {
            this.a.match(files, input);
            this.b.match(files, input);
        }

        @Override
        boolean isIncluded(String[] segments, int n) {
            return this.a.isIncluded(segments, n) || this.b.isIncluded(segments, n);
        }
    }

    static abstract class DFA {
        DFA() {
        }

        abstract void match(Collection<File> var1, File var2);

        abstract boolean isIncluded(String[] var1, int var2);
    }
}

