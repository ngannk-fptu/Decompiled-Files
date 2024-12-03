/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public class RegexFileFilter
extends AbstractFileFilter
implements Serializable {
    private static final long serialVersionUID = 4269646126155225062L;
    private final Pattern pattern;
    private final Function<Path, String> pathToString;

    private static Pattern compile(String pattern, int flags) {
        Objects.requireNonNull(pattern, "pattern");
        return Pattern.compile(pattern, flags);
    }

    private static int toFlags(IOCase ioCase) {
        return IOCase.isCaseSensitive(ioCase) ? 0 : 2;
    }

    public RegexFileFilter(Pattern pattern) {
        this(pattern, (Function<Path, String> & Serializable)p -> p.getFileName().toString());
    }

    public RegexFileFilter(Pattern pattern, Function<Path, String> pathToString) {
        Objects.requireNonNull(pattern, "pattern");
        this.pattern = pattern;
        this.pathToString = pathToString;
    }

    public RegexFileFilter(String pattern) {
        this(pattern, 0);
    }

    public RegexFileFilter(String pattern, int flags) {
        this(RegexFileFilter.compile(pattern, flags));
    }

    public RegexFileFilter(String pattern, IOCase ioCase) {
        this(RegexFileFilter.compile(pattern, RegexFileFilter.toFlags(ioCase)));
    }

    @Override
    public boolean accept(File dir, String name) {
        return this.pattern.matcher(name).matches();
    }

    @Override
    public FileVisitResult accept(Path path, BasicFileAttributes attributes) {
        return this.toFileVisitResult(this.pattern.matcher(this.pathToString.apply(path)).matches());
    }

    @Override
    public String toString() {
        return "RegexFileFilter [pattern=" + this.pattern + "]";
    }
}

