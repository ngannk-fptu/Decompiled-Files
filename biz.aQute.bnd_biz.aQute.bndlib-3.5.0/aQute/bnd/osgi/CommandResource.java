/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.WriteResource;
import aQute.lib.io.IO;
import aQute.libg.command.Command;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandResource
extends WriteResource {
    private static final Logger logger = LoggerFactory.getLogger(CommandResource.class);
    final long lastModified;
    final Builder domain;
    final String command;
    final File wd;

    public CommandResource(String command, Builder domain, long lastModified, File wd) {
        this.lastModified = lastModified;
        this.domain = domain;
        this.command = command;
        this.wd = wd;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(OutputStream out) throws IOException, Exception {
        StringBuilder errors = new StringBuilder();
        StringBuilder stdout = new StringBuilder();
        logger.debug("executing command {}", (Object)this.command);
        Command cmd = new Command("sh");
        cmd.setCwd(this.wd);
        cmd.inherit();
        String oldpath = cmd.var("PATH");
        String path = this.domain.getProperty("-PATH");
        if (path != null) {
            path = path.replaceAll("\\s*,\\s*", File.pathSeparator);
            path = path.replaceAll("\\$\\{@\\}", oldpath);
            cmd.var("PATH", path);
            logger.debug("PATH: {}", (Object)path);
        }
        PrintWriter osw = IO.writer(out, StandardCharsets.UTF_8);
        try {
            int result = cmd.execute(this.command, (Appendable)stdout, (Appendable)errors);
            if (result != 0) {
                this.domain.error("Cmd '%s' failed in %s. %n  %s%n  %s", this.command, this.wd, errors, stdout);
            }
        }
        finally {
            ((Writer)osw).append(stdout);
            ((Writer)osw).flush();
        }
    }

    @Override
    public long lastModified() {
        return this.lastModified;
    }
}

