/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.MatchResult;

public abstract class Pattern {
    private static final Pattern ALL_PATTERN = new Pattern(){

        @Override
        protected Context match(Context input) {
            return input.matchToEnd();
        }

        public String toString() {
            return "[ALL]";
        }
    };
    private static final Pattern NOTHING_PATTERN = new Pattern(){

        @Override
        protected Context match(Context input) {
            return input.match(0);
        }

        public String toString() {
            return "[NOTHING]";
        }
    };

    public MatchResult match(Path input) {
        try {
            return this.match(new Context(input)).getMatchResult();
        }
        catch (RepositoryException e) {
            throw (IllegalArgumentException)new IllegalArgumentException("Path not normalized").initCause(e);
        }
    }

    protected abstract Context match(Context var1) throws RepositoryException;

    public static Pattern path(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }
        return new PathPattern(path);
    }

    public static Pattern name(Name name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        return new NamePattern(name);
    }

    public static Pattern name(String namespaceUri, String localName) {
        if (namespaceUri == null || localName == null) {
            throw new IllegalArgumentException("neither namespaceUri nor localName can be null");
        }
        return new RegexPattern(namespaceUri, localName);
    }

    public static Pattern all() {
        return ALL_PATTERN;
    }

    public static Pattern nothing() {
        return NOTHING_PATTERN;
    }

    public static Pattern selection(Pattern pattern1, Pattern pattern2) {
        if (pattern1 == null || pattern2 == null) {
            throw new IllegalArgumentException("Neither pattern can be null");
        }
        return new SelectPattern(pattern1, pattern2);
    }

    public static Pattern sequence(Pattern pattern1, Pattern pattern2) {
        if (pattern1 == null || pattern2 == null) {
            throw new IllegalArgumentException("Neither pattern can be null");
        }
        return new SequencePattern(pattern1, pattern2);
    }

    public static Pattern repeat(Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern can not be null");
        }
        return new RepeatPattern(pattern);
    }

    public static Pattern repeat(Pattern pattern, int min, int max) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern can not be null");
        }
        return new RepeatPattern(pattern, min, max);
    }

    private static class RegexPattern
    extends AbstractNamePattern {
        private final java.util.regex.Pattern namespaceUri;
        private final java.util.regex.Pattern localName;
        private final String localNameStr;
        private final String namespaceUriStr;

        public RegexPattern(String namespaceUri, String localName) {
            this.namespaceUri = java.util.regex.Pattern.compile(namespaceUri);
            this.localName = java.util.regex.Pattern.compile(localName);
            this.namespaceUriStr = namespaceUri;
            this.localNameStr = localName;
        }

        @Override
        protected boolean matches(Path.Element element) {
            Name name = element.getName();
            boolean nsMatches = this.namespaceUri.matcher(name.getNamespaceURI()).matches();
            boolean localMatches = this.localName.matcher(name.getLocalName()).matches();
            return nsMatches && localMatches;
        }

        public String toString() {
            return new StringBuffer().append("\"{").append(this.namespaceUriStr).append("}").append(this.localNameStr).append("\"").toString();
        }
    }

    private static class NamePattern
    extends AbstractNamePattern {
        private final Name name;

        public NamePattern(Name name) {
            this.name = name;
        }

        @Override
        protected boolean matches(Path.Element element) {
            return this.name.equals(element.getName());
        }

        public String toString() {
            return new StringBuffer().append("\"").append(this.name).append("\"").toString();
        }
    }

    private static abstract class AbstractNamePattern
    extends Pattern {
        private AbstractNamePattern() {
        }

        protected abstract boolean matches(Path.Element var1);

        @Override
        protected Context match(Context input) throws RepositoryException {
            if (input.isExhausted()) {
                return input.noMatch();
            }
            Path inputPath = input.getRemainder();
            if (!inputPath.isNormalized()) {
                throw new IllegalArgumentException("Not normalized");
            }
            Path.Element[] inputElements = inputPath.getElements();
            if (inputElements.length < 1 || !this.matches(inputElements[0])) {
                return input.noMatch();
            }
            return input.match(1);
        }
    }

    private static class PathPattern
    extends Pattern {
        private final Path path;
        private final Path.Element[] patternElements;

        public PathPattern(Path path) {
            this.path = path;
            this.patternElements = path.getElements();
        }

        @Override
        protected Context match(Context input) throws RepositoryException {
            if (input.isExhausted()) {
                return input;
            }
            Path inputPath = input.getRemainder();
            if (!inputPath.isNormalized()) {
                throw new IllegalArgumentException("Not normalized");
            }
            int patternLength = this.patternElements.length;
            Path.Element[] inputElements = inputPath.getElements();
            int inputLength = inputElements.length;
            if (patternLength > inputLength) {
                return input.noMatch();
            }
            for (int k = 0; k < patternLength; ++k) {
                if (this.patternElements[k].equals(inputElements[k])) continue;
                return input.noMatch();
            }
            return input.match(patternLength);
        }

        public String toString() {
            return new StringBuffer().append("\"").append(this.path).append("\"").toString();
        }
    }

    private static class RepeatPattern
    extends Pattern {
        private final Pattern pattern;
        private final int min;
        private final int max;
        private boolean hasBounds;

        public RepeatPattern(Pattern pattern) {
            this(pattern, 0, 0);
            this.hasBounds = false;
        }

        public RepeatPattern(Pattern pattern, int min, int max) {
            this.pattern = pattern;
            this.min = min;
            this.max = max;
            this.hasBounds = true;
        }

        @Override
        protected Context match(Context input) throws RepositoryException {
            Context nextInput;
            Context output = input.match(0);
            int matchCount = -1;
            do {
                nextInput = output;
                output = this.pattern.match(nextInput);
                ++matchCount;
            } while (output.isMatch() && output.pos > nextInput.pos);
            if (!this.hasBounds() || this.min <= matchCount && matchCount <= this.max) {
                return nextInput;
            }
            return input.noMatch();
        }

        private boolean hasBounds() {
            return this.hasBounds;
        }

        public String toString() {
            return new StringBuffer().append("(").append(this.pattern).append(")*").toString();
        }
    }

    private static class SequencePattern
    extends Pattern {
        private final Pattern pattern1;
        private final Pattern pattern2;

        public SequencePattern(Pattern pattern1, Pattern pattern2) {
            this.pattern1 = pattern1;
            this.pattern2 = pattern2;
        }

        @Override
        protected Context match(Context input) throws RepositoryException {
            Context context1 = this.pattern1.match(input);
            if (context1.isMatch()) {
                return this.pattern2.match(context1);
            }
            return input.noMatch();
        }

        public String toString() {
            return new StringBuffer().append("(").append(this.pattern1).append(", ").append(this.pattern2).append(")").toString();
        }
    }

    private static class SelectPattern
    extends Pattern {
        private final Pattern pattern1;
        private final Pattern pattern2;

        public SelectPattern(Pattern pattern1, Pattern pattern2) {
            this.pattern1 = pattern1;
            this.pattern2 = pattern2;
        }

        @Override
        protected Context match(Context input) throws RepositoryException {
            Context remainder1 = this.pattern1.match(input);
            Context remainder2 = this.pattern2.match(input);
            return remainder1.pos > remainder2.pos ? remainder1 : remainder2;
        }

        public String toString() {
            return new StringBuffer().append("(").append(this.pattern1).append("|").append(this.pattern2).append(")").toString();
        }
    }

    private static class Context {
        private final Path path;
        private final int length;
        private final int pos;
        private final boolean isMatch;

        public Context(Path path) {
            this.path = path;
            this.length = path.getLength();
            this.isMatch = false;
            this.pos = 0;
        }

        public Context(Context context, int pos, boolean matched) {
            this.path = context.path;
            this.length = context.length;
            this.pos = pos;
            this.isMatch = matched;
            if (pos > this.length) {
                throw new IllegalArgumentException("Cannot match beyond end of input");
            }
        }

        public Context matchToEnd() {
            return new Context(this, this.length, true);
        }

        public Context match(int count) {
            return new Context(this, this.pos + count, true);
        }

        public Context noMatch() {
            return new Context(this, this.pos, false);
        }

        public boolean isMatch() {
            return this.isMatch;
        }

        public Path getRemainder() throws RepositoryException {
            if (this.pos >= this.length) {
                return null;
            }
            return this.path.subPath(this.pos, this.length);
        }

        public boolean isExhausted() {
            return this.pos == this.length;
        }

        public MatchResult getMatchResult() {
            return new MatchResult(this.path, this.isMatch ? this.pos : 0);
        }

        public String toString() {
            return this.pos + " @ " + this.path;
        }
    }
}

