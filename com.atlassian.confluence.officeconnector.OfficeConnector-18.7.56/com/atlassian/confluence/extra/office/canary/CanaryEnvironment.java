/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.SystemUtils
 */
package com.atlassian.confluence.extra.office.canary;

import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

enum CanaryEnvironment {
    WINDOWS(';', "java.exe"),
    NIX(':', "java");

    private final char classpathSeparator;
    private final String javaExeFilename;

    private CanaryEnvironment(char classpathSeparator, String javaExeFilename) {
        this.classpathSeparator = classpathSeparator;
        this.javaExeFilename = Objects.requireNonNull(javaExeFilename);
    }

    @Nonnull
    public String createClasspathArgument(Iterable<String> classpathComponents) {
        return StringUtils.join(classpathComponents, (char)this.classpathSeparator);
    }

    @Nonnull
    public Maybe<Path> findJavaExePath() {
        File javaHome = SystemUtils.getJavaHome();
        if (javaHome.exists()) {
            return Option.some((Object)javaHome.toPath().resolve("bin").resolve(this.javaExeFilename).toAbsolutePath());
        }
        return Option.none();
    }

    @Nonnull
    public Path getJavaExePath() {
        return (Path)this.findJavaExePath().get();
    }

    @Nonnull
    public static CanaryEnvironment currentEnv() {
        return SystemUtils.IS_OS_WINDOWS ? WINDOWS : NIX;
    }
}

