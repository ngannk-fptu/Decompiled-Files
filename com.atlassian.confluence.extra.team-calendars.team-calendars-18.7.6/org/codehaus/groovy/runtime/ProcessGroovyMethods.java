/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.IOGroovyMethods;

public class ProcessGroovyMethods
extends DefaultGroovyMethodsSupport {
    public static InputStream getIn(Process self) {
        return self.getInputStream();
    }

    public static String getText(Process self) throws IOException {
        String text = IOGroovyMethods.getText(new BufferedReader(new InputStreamReader(self.getInputStream())));
        ProcessGroovyMethods.closeStreams(self);
        return text;
    }

    public static InputStream getErr(Process self) {
        return self.getErrorStream();
    }

    public static OutputStream getOut(Process self) {
        return self.getOutputStream();
    }

    public static Writer leftShift(Process self, Object value) throws IOException {
        return IOGroovyMethods.leftShift(self.getOutputStream(), value);
    }

    public static OutputStream leftShift(Process self, byte[] value) throws IOException {
        return IOGroovyMethods.leftShift(self.getOutputStream(), value);
    }

    public static void waitForOrKill(Process self, long numberOfMillis) {
        ProcessRunner runnable = new ProcessRunner(self);
        Thread thread = new Thread(runnable);
        thread.start();
        runnable.waitForOrKill(numberOfMillis);
    }

    public static void closeStreams(Process self) {
        try {
            self.getErrorStream().close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            self.getInputStream().close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            self.getOutputStream().close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void consumeProcessOutput(Process self) {
        ProcessGroovyMethods.consumeProcessOutput(self, (OutputStream)null, (OutputStream)null);
    }

    public static void consumeProcessOutput(Process self, Appendable output, Appendable error) {
        ProcessGroovyMethods.consumeProcessOutputStream(self, output);
        ProcessGroovyMethods.consumeProcessErrorStream(self, error);
    }

    public static void consumeProcessOutput(Process self, OutputStream output, OutputStream error) {
        ProcessGroovyMethods.consumeProcessOutputStream(self, output);
        ProcessGroovyMethods.consumeProcessErrorStream(self, error);
    }

    public static void waitForProcessOutput(Process self) {
        ProcessGroovyMethods.waitForProcessOutput(self, (OutputStream)null, (OutputStream)null);
    }

    public static void waitForProcessOutput(Process self, Appendable output, Appendable error) {
        Thread tout = ProcessGroovyMethods.consumeProcessOutputStream(self, output);
        Thread terr = ProcessGroovyMethods.consumeProcessErrorStream(self, error);
        try {
            tout.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        try {
            terr.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        try {
            self.waitFor();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        ProcessGroovyMethods.closeStreams(self);
    }

    public static void waitForProcessOutput(Process self, OutputStream output, OutputStream error) {
        Thread tout = ProcessGroovyMethods.consumeProcessOutputStream(self, output);
        Thread terr = ProcessGroovyMethods.consumeProcessErrorStream(self, error);
        try {
            tout.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        try {
            terr.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        try {
            self.waitFor();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        ProcessGroovyMethods.closeStreams(self);
    }

    public static Thread consumeProcessErrorStream(Process self, OutputStream err) {
        Thread thread = new Thread(new ByteDumper(self.getErrorStream(), err));
        thread.start();
        return thread;
    }

    public static Thread consumeProcessErrorStream(Process self, Appendable error) {
        Thread thread = new Thread(new TextDumper(self.getErrorStream(), error));
        thread.start();
        return thread;
    }

    public static Thread consumeProcessOutputStream(Process self, Appendable output) {
        Thread thread = new Thread(new TextDumper(self.getInputStream(), output));
        thread.start();
        return thread;
    }

    public static Thread consumeProcessOutputStream(Process self, OutputStream output) {
        Thread thread = new Thread(new ByteDumper(self.getInputStream(), output));
        thread.start();
        return thread;
    }

    public static void withWriter(final Process self, final Closure closure) {
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    IOGroovyMethods.withWriter(new BufferedOutputStream(ProcessGroovyMethods.getOut(self)), closure);
                }
                catch (IOException e) {
                    throw new GroovyRuntimeException("exception while reading process stream", e);
                }
            }
        }).start();
    }

    public static void withOutputStream(final Process self, final Closure closure) {
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    IOGroovyMethods.withStream(new BufferedOutputStream(ProcessGroovyMethods.getOut(self)), closure);
                }
                catch (IOException e) {
                    throw new GroovyRuntimeException("exception while reading process stream", e);
                }
            }
        }).start();
    }

    public static Process pipeTo(final Process left, final Process right) throws IOException {
        new Thread(new Runnable(){

            @Override
            public void run() {
                BufferedInputStream in = new BufferedInputStream(ProcessGroovyMethods.getIn(left));
                BufferedOutputStream out = new BufferedOutputStream(ProcessGroovyMethods.getOut(right));
                byte[] buf = new byte[8192];
                try {
                    int next;
                    while ((next = ((InputStream)in).read(buf)) != -1) {
                        ((OutputStream)out).write(buf, 0, next);
                    }
                }
                catch (IOException e) {
                    throw new GroovyRuntimeException("exception while reading process stream", e);
                }
                finally {
                    DefaultGroovyMethodsSupport.closeWithWarning(out);
                    DefaultGroovyMethodsSupport.closeWithWarning(in);
                }
            }
        }).start();
        return right;
    }

    public static Process or(Process left, Process right) throws IOException {
        return ProcessGroovyMethods.pipeTo(left, right);
    }

    public static Process execute(String self) throws IOException {
        return Runtime.getRuntime().exec(self);
    }

    public static Process execute(String self, String[] envp, File dir) throws IOException {
        return Runtime.getRuntime().exec(self, envp, dir);
    }

    public static Process execute(String self, List envp, File dir) throws IOException {
        return ProcessGroovyMethods.execute(self, ProcessGroovyMethods.stringify(envp), dir);
    }

    public static Process execute(String[] commandArray) throws IOException {
        return Runtime.getRuntime().exec(commandArray);
    }

    public static Process execute(String[] commandArray, String[] envp, File dir) throws IOException {
        return Runtime.getRuntime().exec(commandArray, envp, dir);
    }

    public static Process execute(String[] commandArray, List envp, File dir) throws IOException {
        return Runtime.getRuntime().exec(commandArray, ProcessGroovyMethods.stringify(envp), dir);
    }

    public static Process execute(List commands) throws IOException {
        return ProcessGroovyMethods.execute(ProcessGroovyMethods.stringify(commands));
    }

    public static Process execute(List commands, String[] envp, File dir) throws IOException {
        return Runtime.getRuntime().exec(ProcessGroovyMethods.stringify(commands), envp, dir);
    }

    public static Process execute(List commands, List envp, File dir) throws IOException {
        return Runtime.getRuntime().exec(ProcessGroovyMethods.stringify(commands), ProcessGroovyMethods.stringify(envp), dir);
    }

    private static String[] stringify(List orig) {
        if (orig == null) {
            return null;
        }
        String[] result = new String[orig.size()];
        for (int i = 0; i < orig.size(); ++i) {
            result[i] = orig.get(i).toString();
        }
        return result;
    }

    private static class ByteDumper
    implements Runnable {
        InputStream in;
        OutputStream out;

        public ByteDumper(InputStream in, OutputStream out) {
            this.in = new BufferedInputStream(in);
            this.out = out;
        }

        @Override
        public void run() {
            byte[] buf = new byte[8192];
            try {
                int next;
                while ((next = this.in.read(buf)) != -1) {
                    if (this.out == null) continue;
                    this.out.write(buf, 0, next);
                }
            }
            catch (IOException e) {
                throw new GroovyRuntimeException("exception while dumping process stream", e);
            }
        }
    }

    private static class TextDumper
    implements Runnable {
        InputStream in;
        Appendable app;

        public TextDumper(InputStream in, Appendable app) {
            this.in = in;
            this.app = app;
        }

        @Override
        public void run() {
            InputStreamReader isr = new InputStreamReader(this.in);
            BufferedReader br = new BufferedReader(isr);
            try {
                String next;
                while ((next = br.readLine()) != null) {
                    if (this.app == null) continue;
                    this.app.append(next);
                    this.app.append("\n");
                }
            }
            catch (IOException e) {
                throw new GroovyRuntimeException("exception while reading process stream", e);
            }
        }
    }

    protected static class ProcessRunner
    implements Runnable {
        Process process;
        private boolean finished;

        public ProcessRunner(Process process) {
            this.process = process;
        }

        private void doProcessWait() {
            try {
                this.process.waitFor();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            this.doProcessWait();
            ProcessRunner processRunner = this;
            synchronized (processRunner) {
                this.notifyAll();
                this.finished = true;
            }
        }

        public synchronized void waitForOrKill(long millis) {
            if (!this.finished) {
                try {
                    this.wait(millis);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                if (!this.finished) {
                    this.process.destroy();
                    this.doProcessWait();
                }
            }
        }
    }
}

