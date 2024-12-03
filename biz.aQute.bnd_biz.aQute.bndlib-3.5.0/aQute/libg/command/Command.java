/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.libg.command;

import aQute.lib.io.IO;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Command {
    private static final Logger logger = LoggerFactory.getLogger(Command.class);
    boolean trace;
    Reporter reporter;
    List<String> arguments = new ArrayList<String>();
    Map<String, String> variables = new LinkedHashMap<String, String>();
    long timeout = 0L;
    File cwd = new File("").getAbsoluteFile();
    static Timer timer = new Timer(Command.class.getName(), true);
    Process process;
    volatile boolean timedout;
    String fullCommand;
    private boolean useThreadForInput;

    public Command(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    public Command() {
    }

    public int execute(Appendable stdout, Appendable stderr) throws Exception {
        return this.execute((InputStream)null, stdout, stderr);
    }

    public int execute(String input, Appendable stdout, Appendable stderr) throws Exception {
        InputStream in = IO.stream(input, StandardCharsets.UTF_8);
        return this.execute(in, stdout, stderr);
    }

    public static boolean needsWindowsQuoting(String s) {
        int len = s.length();
        if (len == 0) {
            return true;
        }
        for (int i = 0; i < len; ++i) {
            switch (s.charAt(i)) {
                case '\t': 
                case ' ': 
                case '\"': 
                case '\\': {
                    return true;
                }
            }
        }
        return false;
    }

    public static String windowsQuote(String s) {
        if (!Command.needsWindowsQuoting(s)) {
            return s;
        }
        s = s.replaceAll("([\\\\]*)\"", "$1$1\\\\\"");
        s = s.replaceAll("([\\\\]*)\\z", "$1$1");
        return "\"" + s + "\"";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int execute(final InputStream in, Appendable stdout, Appendable stderr) throws Exception {
        ProcessBuilder p;
        logger.debug("executing cmd: {}", this.arguments);
        if (this.fullCommand != null) {
            p = new ProcessBuilder(this.fullCommand.split("\\s+"));
        } else if (System.getProperty("os.name").startsWith("Windows")) {
            LinkedList<String> adjustedStrings = new LinkedList<String>();
            for (String string : this.arguments) {
                adjustedStrings.add(Command.windowsQuote(string));
            }
            p = new ProcessBuilder(adjustedStrings);
        } else {
            p = new ProcessBuilder(this.arguments);
        }
        Map<String, String> env = p.environment();
        for (Map.Entry entry : this.variables.entrySet()) {
            env.put((String)entry.getKey(), (String)entry.getValue());
        }
        p.directory(this.cwd);
        this.process = p.start();
        Runnable r = new Runnable(){

            @Override
            public void run() {
                Command.this.process.destroy();
            }
        };
        Thread thread = new Thread(r, this.arguments.toString());
        Runtime.getRuntime().addShutdownHook(thread);
        TimerTask timer = null;
        final OutputStream stdin = this.process.getOutputStream();
        Thread rdInThread = null;
        if (this.timeout != 0L) {
            timer = new TimerTask(){

                @Override
                public void run() {
                    Command.this.timedout = true;
                    Command.this.process.destroy();
                }
            };
            Command.timer.schedule(timer, this.timeout);
        }
        final AtomicBoolean finished = new AtomicBoolean(false);
        try (InputStream out = this.process.getInputStream();
             InputStream err = this.process.getErrorStream();){
            Collector cout = new Collector(out, stdout);
            cout.start();
            Collector cerr = new Collector(err, stderr);
            cerr.start();
            if (in != null) {
                if (in == System.in || this.useThreadForInput) {
                    rdInThread = new Thread("Read Input Thread"){

                        /*
                         * WARNING - Removed try catching itself - possible behaviour change.
                         * Enabled aggressive block sorting
                         * Enabled unnecessary exception pruning
                         * Enabled aggressive exception aggregation
                         */
                        @Override
                        public void run() {
                            try {
                                while (!finished.get()) {
                                    int n = in.available();
                                    if (n == 0) {
                                        3.sleep(100L);
                                        continue;
                                    }
                                    int c = in.read();
                                    if (c < 0) {
                                        stdin.close();
                                        return;
                                    }
                                    stdin.write(c);
                                    if (c != 10) continue;
                                    stdin.flush();
                                }
                                return;
                            }
                            catch (InterruptedIOException e) {
                                return;
                            }
                            catch (Exception exception) {
                                return;
                            }
                            finally {
                                IO.close(stdin);
                            }
                        }
                    };
                    rdInThread.setDaemon(true);
                    rdInThread.start();
                } else {
                    IO.copy(in, stdin);
                    stdin.close();
                }
            }
            logger.debug("exited process");
            cerr.join();
            cout.join();
            logger.debug("stdout/stderr streams have finished");
        }
        finally {
            if (timer != null) {
                timer.cancel();
            }
            Runtime.getRuntime().removeShutdownHook(thread);
        }
        byte exitValue = (byte)this.process.waitFor();
        finished.set(true);
        if (rdInThread != null) {
            IO.close(in);
            rdInThread.interrupt();
        }
        logger.debug("cmd {} executed with result={}, result: {}/{}, timedout={}", new Object[]{this.arguments, exitValue, stdout, stderr, this.timedout});
        if (this.timedout) {
            return Integer.MIN_VALUE;
        }
        return exitValue;
    }

    public void add(String ... args) {
        for (String arg : args) {
            this.arguments.add(arg);
        }
    }

    public void addAll(Collection<String> args) {
        this.arguments.addAll(args);
    }

    public void setTimeout(long duration, TimeUnit unit) {
        this.timeout = unit.toMillis(duration);
    }

    public void setTrace() {
        this.trace = true;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public void setCwd(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Working directory must be a directory: " + dir);
        }
        this.cwd = dir;
    }

    public void cancel() {
        this.process.destroy();
    }

    public Command var(String name, String value) {
        this.variables.put(name, value);
        return this;
    }

    public Command arg(String ... args) {
        this.add(args);
        return this;
    }

    public Command full(String full) {
        this.fullCommand = full;
        return this;
    }

    public void inherit() {
        ProcessBuilder pb = new ProcessBuilder(new String[0]);
        for (Map.Entry<String, String> e : pb.environment().entrySet()) {
            this.var(e.getKey(), e.getValue());
        }
    }

    public String var(String name) {
        return this.variables.get(name);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (String argument : this.arguments) {
            sb.append(del);
            sb.append(argument);
            del = " ";
        }
        return sb.toString();
    }

    public List<String> getArguments() {
        return this.arguments;
    }

    public void setUseThreadForInput(boolean useThreadForInput) {
        this.useThreadForInput = useThreadForInput;
    }

    public void var(Map<String, String> env) {
        for (Map.Entry<String, String> e : env.entrySet()) {
            this.var(e.getKey(), e.getValue());
        }
    }

    class Collector
    extends Thread {
        final InputStream in;
        final Appendable sb;

        Collector(InputStream inputStream, Appendable sb) {
            this.in = inputStream;
            this.sb = sb;
            this.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                int c = this.in.read();
                while (c >= 0) {
                    this.sb.append((char)c);
                    c = this.in.read();
                }
            }
            catch (IOException e) {
            }
            catch (Exception e) {
                try {
                    this.sb.append("\n**************************************\n");
                    this.sb.append(e.toString());
                    this.sb.append("\n**************************************\n");
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                logger.debug("cmd exec", (Throwable)e);
            }
        }
    }
}

