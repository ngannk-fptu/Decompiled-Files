/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.email;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.email.PlainMailer;
import org.apache.tools.ant.util.UUEncoder;

class UUMailer
extends PlainMailer {
    UUMailer() {
    }

    @Override
    protected void attach(File file, PrintStream out) throws IOException {
        if (!file.exists() || !file.canRead()) {
            throw new BuildException("File \"%s\" does not exist or is not readable.", file.getAbsolutePath());
        }
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(file.toPath(), new OpenOption[0]));){
            new UUEncoder(file.getName()).encode(in, out);
        }
    }
}

