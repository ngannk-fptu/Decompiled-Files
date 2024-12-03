/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.sandbox;

import com.atlassian.plugins.conversion.sandbox.FileOperation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DefaultFileOperation
implements FileOperation {
    @Override
    public void move(Path source, Path target) throws IOException {
        Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
    }
}

