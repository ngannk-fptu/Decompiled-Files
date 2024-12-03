/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.axis.components.compiler.AbstractCompiler;
import org.apache.axis.components.compiler.CompilerError;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class Jikes
extends AbstractCompiler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$compiler$Jikes == null ? (class$org$apache$axis$components$compiler$Jikes = Jikes.class$("org.apache.axis.components.compiler.Jikes")) : class$org$apache$axis$components$compiler$Jikes).getName());
    static final int OUTPUT_BUFFER_SIZE = 1024;
    static final int BUFFER_SIZE = 512;
    static /* synthetic */ Class class$org$apache$axis$components$compiler$Jikes;

    protected String[] toStringArray(List arguments) {
        int i;
        for (i = 0; i < arguments.size(); ++i) {
            String arg = (String)arguments.get(i);
            if (!arg.equals("-sourcepath")) continue;
            arguments.remove(i);
            arguments.remove(i);
            break;
        }
        String[] args = new String[arguments.size() + this.fileList.size()];
        for (i = 0; i < arguments.size(); ++i) {
            args[i] = (String)arguments.get(i);
        }
        for (int j = 0; j < this.fileList.size(); ++j) {
            args[i] = (String)this.fileList.get(j);
            ++i;
        }
        return args;
    }

    public boolean compile() throws IOException {
        int exitValue;
        ArrayList<String> args = new ArrayList<String>();
        args.add("jikes");
        args.add("+E");
        args.add("-nowarn");
        ByteArrayOutputStream tmpErr = new ByteArrayOutputStream(1024);
        try {
            Process p = Runtime.getRuntime().exec(this.toStringArray(this.fillArguments(args)));
            BufferedInputStream compilerErr = new BufferedInputStream(p.getErrorStream());
            StreamPumper errPumper = new StreamPumper(compilerErr, tmpErr);
            errPumper.start();
            p.waitFor();
            exitValue = p.exitValue();
            errPumper.join();
            compilerErr.close();
            p.destroy();
            tmpErr.close();
            this.errors = new ByteArrayInputStream(tmpErr.toByteArray());
        }
        catch (InterruptedException somethingHappened) {
            log.debug((Object)"Jikes.compile():SomethingHappened", (Throwable)somethingHappened);
            return false;
        }
        return exitValue == 0 && tmpErr.size() == 0;
    }

    protected List parseStream(BufferedReader input) throws IOException {
        ArrayList<CompilerError> errors = null;
        String line = null;
        StringBuffer buffer = null;
        while (true) {
            buffer = new StringBuffer();
            if (line == null) {
                line = input.readLine();
            }
            if (line == null) {
                return errors;
            }
            log.debug((Object)line);
            buffer.append(line);
            while ((line = input.readLine()) != null && (line.length() <= 0 || line.charAt(0) == ' ')) {
                log.debug((Object)line);
                buffer.append('\n');
                buffer.append(line);
            }
            if (errors == null) {
                errors = new ArrayList<CompilerError>();
            }
            errors.add(this.parseError(buffer.toString()));
        }
    }

    private CompilerError parseError(String error) {
        StringTokenizer tokens = new StringTokenizer(error, ":");
        String file = tokens.nextToken();
        if (file.length() == 1) {
            file = file + ":" + tokens.nextToken();
        }
        StringBuffer message = new StringBuffer();
        String type = "";
        int startline = 0;
        int startcolumn = 0;
        int endline = 0;
        int endcolumn = 0;
        try {
            startline = Integer.parseInt(tokens.nextToken());
            startcolumn = Integer.parseInt(tokens.nextToken());
            endline = Integer.parseInt(tokens.nextToken());
            endcolumn = Integer.parseInt(tokens.nextToken());
        }
        catch (Exception e) {
            message.append(Messages.getMessage("compilerFail00"));
            type = "error";
            log.error((Object)Messages.getMessage("compilerFail00"), (Throwable)e);
        }
        if ("".equals(message)) {
            type = tokens.nextToken().trim().toLowerCase();
            message.append(tokens.nextToken("\n").substring(1).trim());
            while (tokens.hasMoreTokens()) {
                message.append("\n").append(tokens.nextToken());
            }
        }
        return new CompilerError(file, type.equals("error"), startline, startcolumn, endline, endcolumn, message.toString());
    }

    public String toString() {
        return Messages.getMessage("ibmJikes");
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private class StreamPumper
    extends Thread {
        private BufferedInputStream stream;
        private boolean endOfStream = false;
        private boolean stopSignal = false;
        private int SLEEP_TIME = 5;
        private OutputStream out;

        public StreamPumper(BufferedInputStream is, OutputStream out) {
            this.stream = is;
            this.out = out;
        }

        public void pumpStream() throws IOException {
            byte[] buf = new byte[512];
            if (!this.endOfStream) {
                int bytesRead = this.stream.read(buf, 0, 512);
                if (bytesRead > 0) {
                    this.out.write(buf, 0, bytesRead);
                } else if (bytesRead == -1) {
                    this.endOfStream = true;
                }
            }
        }

        public void run() {
            try {
                while (!this.endOfStream) {
                    this.pumpStream();
                    StreamPumper.sleep(this.SLEEP_TIME);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

