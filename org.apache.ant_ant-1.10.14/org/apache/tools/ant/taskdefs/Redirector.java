/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.StreamPumper;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.util.ConcatFileInputStream;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.KeepAliveOutputStream;
import org.apache.tools.ant.util.LazyFileOutputStream;
import org.apache.tools.ant.util.LeadPipeInputStream;
import org.apache.tools.ant.util.LineOrientedOutputStreamRedirector;
import org.apache.tools.ant.util.NullOutputStream;
import org.apache.tools.ant.util.OutputStreamFunneler;
import org.apache.tools.ant.util.ReaderInputStream;
import org.apache.tools.ant.util.TeeOutputStream;

public class Redirector {
    private static final int STREAMPUMPER_WAIT_INTERVAL = 1000;
    private static final String DEFAULT_ENCODING = System.getProperty("file.encoding");
    private File[] input;
    private File[] out;
    private File[] error;
    private boolean logError = false;
    private PropertyOutputStream baos = null;
    private PropertyOutputStream errorBaos = null;
    private String outputProperty;
    private String errorProperty;
    private String inputString;
    private boolean appendOut = false;
    private boolean appendErr = false;
    private boolean alwaysLogOut = false;
    private boolean alwaysLogErr = false;
    private boolean createEmptyFilesOut = true;
    private boolean createEmptyFilesErr = true;
    private final ProjectComponent managingTask;
    private OutputStream outputStream = null;
    private OutputStream errorStream = null;
    private InputStream inputStream = null;
    private PrintStream outPrintStream = null;
    private PrintStream errorPrintStream = null;
    private Vector<FilterChain> outputFilterChains;
    private Vector<FilterChain> errorFilterChains;
    private Vector<FilterChain> inputFilterChains;
    private String outputEncoding = DEFAULT_ENCODING;
    private String errorEncoding = DEFAULT_ENCODING;
    private String inputEncoding = DEFAULT_ENCODING;
    private boolean appendProperties = true;
    private final ThreadGroup threadGroup = new ThreadGroup("redirector");
    private boolean logInputString = true;
    private final Object inMutex = new Object();
    private final Object outMutex = new Object();
    private final Object errMutex = new Object();
    private boolean outputIsBinary = false;
    private boolean discardOut = false;
    private boolean discardErr = false;

    public Redirector(Task managingTask) {
        this((ProjectComponent)managingTask);
    }

    public Redirector(ProjectComponent managingTask) {
        this.managingTask = managingTask;
    }

    public void setInput(File input) {
        File[] fileArray;
        if (input == null) {
            fileArray = null;
        } else {
            File[] fileArray2 = new File[1];
            fileArray = fileArray2;
            fileArray2[0] = input;
        }
        this.setInput(fileArray);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setInput(File[] input) {
        Object object = this.inMutex;
        synchronized (object) {
            this.input = input == null ? null : (File[])input.clone();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setInputString(String inputString) {
        Object object = this.inMutex;
        synchronized (object) {
            this.inputString = inputString;
        }
    }

    public void setLogInputString(boolean logInputString) {
        this.logInputString = logInputString;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void setInputStream(InputStream inputStream) {
        Object object = this.inMutex;
        synchronized (object) {
            this.inputStream = inputStream;
        }
    }

    public void setOutput(File out) {
        File[] fileArray;
        if (out == null) {
            fileArray = null;
        } else {
            File[] fileArray2 = new File[1];
            fileArray = fileArray2;
            fileArray2[0] = out;
        }
        this.setOutput(fileArray);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOutput(File[] out) {
        Object object = this.outMutex;
        synchronized (object) {
            this.out = out == null ? null : (File[])out.clone();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOutputEncoding(String outputEncoding) {
        if (outputEncoding == null) {
            throw new IllegalArgumentException("outputEncoding must not be null");
        }
        Object object = this.outMutex;
        synchronized (object) {
            this.outputEncoding = outputEncoding;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setErrorEncoding(String errorEncoding) {
        if (errorEncoding == null) {
            throw new IllegalArgumentException("errorEncoding must not be null");
        }
        Object object = this.errMutex;
        synchronized (object) {
            this.errorEncoding = errorEncoding;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setInputEncoding(String inputEncoding) {
        if (inputEncoding == null) {
            throw new IllegalArgumentException("inputEncoding must not be null");
        }
        Object object = this.inMutex;
        synchronized (object) {
            this.inputEncoding = inputEncoding;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setLogError(boolean logError) {
        Object object = this.errMutex;
        synchronized (object) {
            this.logError = logError;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAppendProperties(boolean appendProperties) {
        Object object = this.outMutex;
        synchronized (object) {
            this.appendProperties = appendProperties;
        }
    }

    public void setError(File error) {
        File[] fileArray;
        if (error == null) {
            fileArray = null;
        } else {
            File[] fileArray2 = new File[1];
            fileArray = fileArray2;
            fileArray2[0] = error;
        }
        this.setError(fileArray);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setError(File[] error) {
        Object object = this.errMutex;
        synchronized (object) {
            this.error = error == null ? null : (File[])error.clone();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOutputProperty(String outputProperty) {
        if (outputProperty == null || !outputProperty.equals(this.outputProperty)) {
            Object object = this.outMutex;
            synchronized (object) {
                this.outputProperty = outputProperty;
                this.baos = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAppend(boolean append) {
        Object object = this.outMutex;
        synchronized (object) {
            this.appendOut = append;
        }
        object = this.errMutex;
        synchronized (object) {
            this.appendErr = append;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDiscardOutput(boolean discard) {
        Object object = this.outMutex;
        synchronized (object) {
            this.discardOut = discard;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDiscardError(boolean discard) {
        Object object = this.errMutex;
        synchronized (object) {
            this.discardErr = discard;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAlwaysLog(boolean alwaysLog) {
        Object object = this.outMutex;
        synchronized (object) {
            this.alwaysLogOut = alwaysLog;
        }
        object = this.errMutex;
        synchronized (object) {
            this.alwaysLogErr = alwaysLog;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setCreateEmptyFiles(boolean createEmptyFiles) {
        Object object = this.outMutex;
        synchronized (object) {
            this.createEmptyFilesOut = createEmptyFiles;
        }
        object = this.outMutex;
        synchronized (object) {
            this.createEmptyFilesErr = createEmptyFiles;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setErrorProperty(String errorProperty) {
        Object object = this.errMutex;
        synchronized (object) {
            if (errorProperty == null || !errorProperty.equals(this.errorProperty)) {
                this.errorProperty = errorProperty;
                this.errorBaos = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setInputFilterChains(Vector<FilterChain> inputFilterChains) {
        Object object = this.inMutex;
        synchronized (object) {
            this.inputFilterChains = inputFilterChains;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOutputFilterChains(Vector<FilterChain> outputFilterChains) {
        Object object = this.outMutex;
        synchronized (object) {
            this.outputFilterChains = outputFilterChains;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setErrorFilterChains(Vector<FilterChain> errorFilterChains) {
        Object object = this.errMutex;
        synchronized (object) {
            this.errorFilterChains = errorFilterChains;
        }
    }

    public void setBinaryOutput(boolean b) {
        this.outputIsBinary = b;
    }

    private void setPropertyFromBAOS(ByteArrayOutputStream baos, String propertyName) {
        BufferedReader in = new BufferedReader(new StringReader(Execute.toString(baos)));
        this.managingTask.getProject().setNewProperty(propertyName, in.lines().collect(Collectors.joining(System.lineSeparator())));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void createStreams() {
        Thread t;
        ChainReaderHelper helper;
        Reader reader;
        LeadPipeInputStream snk;
        Object object = this.outMutex;
        synchronized (object) {
            this.outStreams();
            if (this.alwaysLogOut || this.outputStream == null) {
                LogOutputStream outputLog = new LogOutputStream(this.managingTask, 2);
                OutputStream outputStream = this.outputStream = this.outputStream == null ? outputLog : new TeeOutputStream(outputLog, this.outputStream);
            }
            if (this.outputFilterChains != null && this.outputFilterChains.size() > 0 || !this.outputEncoding.equalsIgnoreCase(this.inputEncoding)) {
                try {
                    snk = new LeadPipeInputStream();
                    snk.setManagingComponent(this.managingTask);
                    InputStream outPumpIn = snk;
                    reader = new InputStreamReader(outPumpIn, this.inputEncoding);
                    if (this.outputFilterChains != null && this.outputFilterChains.size() > 0) {
                        helper = new ChainReaderHelper();
                        helper.setProject(this.managingTask.getProject());
                        helper.setPrimaryReader(reader);
                        helper.setFilterChains(this.outputFilterChains);
                        reader = helper.getAssembledReader();
                    }
                    outPumpIn = new ReaderInputStream(reader, this.outputEncoding);
                    t = new Thread(this.threadGroup, new StreamPumper(outPumpIn, this.outputStream, true), "output pumper");
                    t.setPriority(10);
                    this.outputStream = new PipedOutputStream(snk);
                    t.start();
                }
                catch (IOException eyeOhEx) {
                    throw new BuildException("error setting up output stream", eyeOhEx);
                }
            }
        }
        object = this.errMutex;
        synchronized (object) {
            this.errorStreams();
            if (this.alwaysLogErr || this.errorStream == null) {
                LogOutputStream errorLog = new LogOutputStream(this.managingTask, 1);
                OutputStream outputStream = this.errorStream = this.errorStream == null ? errorLog : new TeeOutputStream(errorLog, this.errorStream);
            }
            if (this.errorFilterChains != null && this.errorFilterChains.size() > 0 || !this.errorEncoding.equalsIgnoreCase(this.inputEncoding)) {
                try {
                    snk = new LeadPipeInputStream();
                    snk.setManagingComponent(this.managingTask);
                    InputStream errPumpIn = snk;
                    reader = new InputStreamReader(errPumpIn, this.inputEncoding);
                    if (this.errorFilterChains != null && this.errorFilterChains.size() > 0) {
                        helper = new ChainReaderHelper();
                        helper.setProject(this.managingTask.getProject());
                        helper.setPrimaryReader(reader);
                        helper.setFilterChains(this.errorFilterChains);
                        reader = helper.getAssembledReader();
                    }
                    errPumpIn = new ReaderInputStream(reader, this.errorEncoding);
                    t = new Thread(this.threadGroup, new StreamPumper(errPumpIn, this.errorStream, true), "error pumper");
                    t.setPriority(10);
                    this.errorStream = new PipedOutputStream(snk);
                    t.start();
                }
                catch (IOException eyeOhEx) {
                    throw new BuildException("error setting up error stream", eyeOhEx);
                }
            }
        }
        object = this.inMutex;
        synchronized (object) {
            if (this.input != null && this.input.length > 0) {
                this.managingTask.log("Redirecting input from file" + (this.input.length == 1 ? "" : "s"), 3);
                try {
                    this.inputStream = new ConcatFileInputStream(this.input);
                }
                catch (IOException eyeOhEx) {
                    throw new BuildException(eyeOhEx);
                }
                ((ConcatFileInputStream)this.inputStream).setManagingComponent(this.managingTask);
            } else if (this.inputString != null) {
                StringBuilder buf = new StringBuilder("Using input ");
                if (this.logInputString) {
                    buf.append('\"').append(this.inputString).append('\"');
                } else {
                    buf.append("string");
                }
                this.managingTask.log(buf.toString(), 3);
                this.inputStream = new ByteArrayInputStream(this.inputString.getBytes());
            }
            if (this.inputStream != null && this.inputFilterChains != null && this.inputFilterChains.size() > 0) {
                ChainReaderHelper helper2 = new ChainReaderHelper();
                helper2.setProject(this.managingTask.getProject());
                try {
                    helper2.setPrimaryReader(new InputStreamReader(this.inputStream, this.inputEncoding));
                }
                catch (IOException eyeOhEx) {
                    throw new BuildException("error setting up input stream", eyeOhEx);
                }
                helper2.setFilterChains(this.inputFilterChains);
                this.inputStream = new ReaderInputStream((Reader)helper2.getAssembledReader(), this.inputEncoding);
            }
        }
    }

    private void outStreams() {
        boolean haveOutputFiles;
        boolean bl = haveOutputFiles = this.out != null && this.out.length > 0;
        if (this.discardOut) {
            if (haveOutputFiles || this.outputProperty != null) {
                throw new BuildException("Cant discard output when output or outputProperty are set");
            }
            this.managingTask.log("Discarding output", 3);
            this.outputStream = NullOutputStream.INSTANCE;
            return;
        }
        if (haveOutputFiles) {
            String logHead = "Output " + (this.appendOut ? "appended" : "redirected") + " to ";
            this.outputStream = this.foldFiles(this.out, logHead, 3, this.appendOut, this.createEmptyFilesOut);
        }
        if (this.outputProperty != null) {
            if (this.baos == null) {
                this.baos = new PropertyOutputStream(this.outputProperty);
                this.managingTask.log("Output redirected to property: " + this.outputProperty, 3);
            }
            KeepAliveOutputStream keepAliveOutput = new KeepAliveOutputStream(this.baos);
            this.outputStream = this.outputStream == null ? keepAliveOutput : new TeeOutputStream(this.outputStream, keepAliveOutput);
        } else {
            this.baos = null;
        }
    }

    private void errorStreams() {
        boolean haveErrorFiles;
        boolean bl = haveErrorFiles = this.error != null && this.error.length > 0;
        if (this.discardErr) {
            if (haveErrorFiles || this.errorProperty != null || this.logError) {
                throw new BuildException("Cant discard error output when error, errorProperty or logError are set");
            }
            this.managingTask.log("Discarding error output", 3);
            this.errorStream = NullOutputStream.INSTANCE;
            return;
        }
        if (haveErrorFiles) {
            String logHead = "Error " + (this.appendErr ? "appended" : "redirected") + " to ";
            this.errorStream = this.foldFiles(this.error, logHead, 3, this.appendErr, this.createEmptyFilesErr);
        } else if (!this.logError && this.outputStream != null && this.errorProperty == null) {
            long funnelTimeout = 0L;
            OutputStreamFunneler funneler = new OutputStreamFunneler(this.outputStream, 0L);
            try {
                this.outputStream = funneler.getFunnelInstance();
                this.errorStream = funneler.getFunnelInstance();
                if (!this.outputIsBinary) {
                    this.outputStream = new LineOrientedOutputStreamRedirector(this.outputStream);
                    this.errorStream = new LineOrientedOutputStreamRedirector(this.errorStream);
                }
            }
            catch (IOException eyeOhEx) {
                throw new BuildException("error splitting output/error streams", eyeOhEx);
            }
        }
        if (this.errorProperty != null) {
            if (this.errorBaos == null) {
                this.errorBaos = new PropertyOutputStream(this.errorProperty);
                this.managingTask.log("Error redirected to property: " + this.errorProperty, 3);
            }
            KeepAliveOutputStream keepAliveError = new KeepAliveOutputStream(this.errorBaos);
            this.errorStream = this.error == null || this.error.length == 0 ? keepAliveError : new TeeOutputStream(this.errorStream, keepAliveError);
        } else {
            this.errorBaos = null;
        }
    }

    public ExecuteStreamHandler createHandler() throws BuildException {
        this.createStreams();
        boolean nonBlockingRead = this.input == null && this.inputString == null;
        return new PumpStreamHandler(this.getOutputStream(), this.getErrorStream(), this.getInputStream(), nonBlockingRead);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void handleOutput(String output) {
        Object object = this.outMutex;
        synchronized (object) {
            if (this.outPrintStream == null) {
                this.outPrintStream = new PrintStream(this.outputStream);
            }
            this.outPrintStream.print(output);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected int handleInput(byte[] buffer, int offset, int length) throws IOException {
        Object object = this.inMutex;
        synchronized (object) {
            if (this.inputStream == null) {
                return this.managingTask.getProject().defaultInput(buffer, offset, length);
            }
            return this.inputStream.read(buffer, offset, length);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void handleFlush(String output) {
        Object object = this.outMutex;
        synchronized (object) {
            if (this.outPrintStream == null) {
                this.outPrintStream = new PrintStream(this.outputStream);
            }
            this.outPrintStream.print(output);
            this.outPrintStream.flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void handleErrorOutput(String output) {
        Object object = this.errMutex;
        synchronized (object) {
            if (this.errorPrintStream == null) {
                this.errorPrintStream = new PrintStream(this.errorStream);
            }
            this.errorPrintStream.print(output);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void handleErrorFlush(String output) {
        Object object = this.errMutex;
        synchronized (object) {
            if (this.errorPrintStream == null) {
                this.errorPrintStream = new PrintStream(this.errorStream);
            }
            this.errorPrintStream.print(output);
            this.errorPrintStream.flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public OutputStream getOutputStream() {
        Object object = this.outMutex;
        synchronized (object) {
            return this.outputStream;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public OutputStream getErrorStream() {
        Object object = this.errMutex;
        synchronized (object) {
            return this.errorStream;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InputStream getInputStream() {
        Object object = this.inMutex;
        synchronized (object) {
            return this.inputStream;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void complete() throws IOException {
        System.out.flush();
        System.err.flush();
        Object object = this.inMutex;
        synchronized (object) {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
        }
        object = this.outMutex;
        synchronized (object) {
            this.outputStream.flush();
            this.outputStream.close();
        }
        object = this.errMutex;
        synchronized (object) {
            this.errorStream.flush();
            this.errorStream.close();
        }
        object = this;
        synchronized (object) {
            while (this.threadGroup.activeCount() > 0) {
                try {
                    this.managingTask.log("waiting for " + this.threadGroup.activeCount() + " Threads:", 4);
                    Thread[] thread = new Thread[this.threadGroup.activeCount()];
                    this.threadGroup.enumerate(thread);
                    for (int i = 0; i < thread.length && thread[i] != null; ++i) {
                        try {
                            this.managingTask.log(thread[i].toString(), 4);
                            continue;
                        }
                        catch (NullPointerException nullPointerException) {
                            // empty catch block
                        }
                    }
                    this.wait(1000L);
                }
                catch (InterruptedException eyeEx) {
                    Thread[] thread = new Thread[this.threadGroup.activeCount()];
                    this.threadGroup.enumerate(thread);
                    for (int i = 0; i < thread.length && thread[i] != null; ++i) {
                        thread[i].interrupt();
                    }
                }
            }
        }
        this.setProperties();
        object = this.inMutex;
        synchronized (object) {
            this.inputStream = null;
        }
        object = this.outMutex;
        synchronized (object) {
            this.outputStream = null;
            this.outPrintStream = null;
        }
        object = this.errMutex;
        synchronized (object) {
            this.errorStream = null;
            this.errorPrintStream = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setProperties() {
        Object object = this.outMutex;
        synchronized (object) {
            FileUtils.close(this.baos);
        }
        object = this.errMutex;
        synchronized (object) {
            FileUtils.close(this.errorBaos);
        }
    }

    private OutputStream foldFiles(File[] file, String logHead, int loglevel, boolean append, boolean createEmptyFiles) {
        LazyFileOutputStream result = new LazyFileOutputStream(file[0], append, createEmptyFiles);
        this.managingTask.log(logHead + file[0], loglevel);
        char[] c = new char[logHead.length()];
        Arrays.fill(c, ' ');
        String indent = new String(c);
        for (int i = 1; i < file.length; ++i) {
            this.outputStream = new TeeOutputStream(this.outputStream, new LazyFileOutputStream(file[i], append, createEmptyFiles));
            this.managingTask.log(indent + file[i], loglevel);
        }
        return result;
    }

    private class PropertyOutputStream
    extends ByteArrayOutputStream {
        private final String property;
        private boolean closed = false;

        PropertyOutputStream(String property) {
            this.property = property;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() throws IOException {
            Object object = Redirector.this.outMutex;
            synchronized (object) {
                if (!(this.closed || Redirector.this.appendOut && Redirector.this.appendProperties)) {
                    Redirector.this.setPropertyFromBAOS(this, this.property);
                    this.closed = true;
                }
            }
        }
    }
}

