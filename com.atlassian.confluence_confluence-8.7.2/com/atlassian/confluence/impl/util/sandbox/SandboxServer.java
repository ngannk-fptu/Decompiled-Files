/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxServerClassLoader;
import com.atlassian.confluence.impl.util.sandbox.SandboxServerContext;
import com.atlassian.confluence.impl.util.sandbox.SandboxServerWorker;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;

final class SandboxServer {
    private final SandboxServerContext context;
    private final SandboxServerWorker worker;

    private SandboxServer(InputStream inputStream, OutputStream outputStream, PrintStream errorStream, Level logLevel) {
        this.context = new SandboxServerContext(inputStream, outputStream, errorStream, logLevel);
        this.worker = new SandboxServerWorker(this.context, SandboxServerClassLoader.factory(this.context));
    }

    public void run() {
        this.context.log(Level.INFO, "I am starting");
        this.context.sendStartMarker();
        while (true) {
            this.worker.processNextMessage();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Missing log level");
            System.exit(1);
        }
        Level logLevel = Level.parse(args[0]);
        SandboxServer server = new SandboxServer(System.in, System.out, System.err, logLevel);
        System.setIn(new ByteArrayInputStream(new byte[0]));
        System.setOut(System.err);
        server.run();
    }
}

