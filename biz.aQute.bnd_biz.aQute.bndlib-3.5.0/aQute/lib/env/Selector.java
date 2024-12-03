/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.env;

import aQute.lib.env.Header;
import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Selector {
    transient Pattern pattern;
    transient boolean optional;
    final String input;
    final String match;
    final boolean negated;
    final boolean duplicate;
    final boolean literal;
    final boolean any;
    final boolean caseInsensitive;

    public Selector(String input) {
        this.input = input;
        String s = Header.removeDuplicateMarker(input);
        boolean bl = this.duplicate = !s.equals(input);
        if (s.startsWith("!")) {
            this.negated = true;
            s = s.substring(1);
        } else {
            this.negated = false;
        }
        if (s.endsWith(":i")) {
            this.caseInsensitive = true;
            s = s.substring(0, s.length() - 2);
        } else {
            this.caseInsensitive = false;
        }
        if (input.equals("*")) {
            this.any = true;
            this.literal = false;
            this.match = null;
            return;
        }
        this.any = false;
        if (s.startsWith("=")) {
            this.match = s.substring(1);
            this.literal = true;
        } else {
            boolean wildcards = false;
            StringBuilder sb = new StringBuilder();
            block7: for (int c = 0; c < s.length(); ++c) {
                switch (s.charAt(c)) {
                    case '.': {
                        if (c == s.length() - 2 && '*' == s.charAt(c + 1)) {
                            sb.append("(\\..*)?");
                            wildcards = true;
                            break block7;
                        }
                        sb.append("\\.");
                        continue block7;
                    }
                    case '*': {
                        sb.append(".*");
                        wildcards = true;
                        continue block7;
                    }
                    case '$': {
                        sb.append("\\$");
                        continue block7;
                    }
                    case '?': {
                        sb.append(".?");
                        wildcards = true;
                        continue block7;
                    }
                    case '|': {
                        sb.append('|');
                        wildcards = true;
                        continue block7;
                    }
                    default: {
                        sb.append(s.charAt(c));
                    }
                }
            }
            if (!wildcards) {
                this.literal = true;
                this.match = s;
            } else {
                this.literal = false;
                this.match = sb.toString();
            }
        }
    }

    public boolean matches(String value) {
        if (this.any) {
            return true;
        }
        if (this.literal) {
            return this.match.equals(value);
        }
        return this.getMatcher(value).matches();
    }

    public boolean isNegated() {
        return this.negated;
    }

    public String getPattern() {
        return this.match;
    }

    public String getInput() {
        return this.input;
    }

    public String toString() {
        return this.input;
    }

    public Matcher getMatcher(String value) {
        if (this.pattern == null) {
            this.pattern = !this.caseInsensitive ? Pattern.compile(this.match) : Pattern.compile(this.match, 2);
        }
        return this.pattern.matcher(value);
    }

    public void setOptional() {
        this.optional = true;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public boolean isLiteral() {
        return this.literal;
    }

    public String getLiteral() {
        assert (this.literal);
        return this.match;
    }

    public boolean isDuplicate() {
        return this.duplicate;
    }

    public boolean isAny() {
        return this.any;
    }

    public boolean finds(String value) {
        return this.getMatcher(value).find();
    }

    public static class Filter
    implements FileFilter {
        private Selector instruction;
        private boolean recursive;
        private Pattern doNotCopy;

        public Filter(Selector instruction, boolean recursive, Pattern doNotCopy) {
            this.instruction = instruction;
            this.recursive = recursive;
            this.doNotCopy = doNotCopy;
        }

        public Filter(Selector instruction, boolean recursive) {
            this(instruction, recursive, Pattern.compile("\\..*"));
        }

        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public boolean accept(File pathname) {
            if (this.doNotCopy != null && this.doNotCopy.matcher(pathname.getName()).matches()) {
                return false;
            }
            if (pathname.isDirectory() && this.isRecursive()) {
                return true;
            }
            if (this.instruction == null) {
                return true;
            }
            return !this.instruction.isNegated() == this.instruction.matches(pathname.getName());
        }
    }
}

