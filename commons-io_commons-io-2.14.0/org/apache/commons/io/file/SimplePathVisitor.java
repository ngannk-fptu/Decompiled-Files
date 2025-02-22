/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.Objects;
import org.apache.commons.io.file.PathVisitor;
import org.apache.commons.io.function.IOBiFunction;

public abstract class SimplePathVisitor
extends SimpleFileVisitor<Path>
implements PathVisitor {
    private final IOBiFunction<Path, IOException, FileVisitResult> visitFileFailedFunction;

    protected SimplePathVisitor() {
        this.visitFileFailedFunction = (x$0, x$1) -> super.visitFileFailed(x$0, (IOException)x$1);
    }

    protected SimplePathVisitor(IOBiFunction<Path, IOException, FileVisitResult> visitFileFailed) {
        this.visitFileFailedFunction = Objects.requireNonNull(visitFileFailed, "visitFileFailed");
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return this.visitFileFailedFunction.apply(file, exc);
    }
}

