/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.consoleapp;

import aQute.lib.collections.ExtList;
import aQute.lib.env.Env;
import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.CommandLine;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;
import aQute.lib.justif.Justif;
import aQute.lib.settings.Settings;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Formatter;
import java.util.List;

public abstract class AbstractConsoleApp
extends Env {
    Settings settings;
    protected final PrintStream err;
    protected final PrintStream out;
    static String encoding = System.getProperty("file.encoding");
    int width = 120;
    int[] tabs = new int[]{40, 48, 56, 64, 72, 80, 88, 96, 104, 112};
    private final Object target;

    public AbstractConsoleApp(Object target) throws UnsupportedEncodingException {
        this.target = target == null ? this : target;
        this.err = new PrintStream((OutputStream)System.err, true, encoding);
        this.out = new PrintStream((OutputStream)System.out, true, encoding);
    }

    public AbstractConsoleApp() throws UnsupportedEncodingException {
        this((Object)null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run(String[] args) throws Exception {
        try {
            CommandLine cl = new CommandLine(this);
            ExtList<String> list = new ExtList<String>(args);
            String help = cl.execute(this.target, "_main", list);
            this.check(new String[0]);
            if (help != null) {
                this.err.println(help);
            }
        }
        finally {
            this.err.flush();
            this.out.flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Description(value="")
    public void __main(MainOptions opts) throws IOException {
        try {
            this.setExceptions(opts.exceptions());
            this.setTrace(opts.trace());
            this.setPedantic(opts.pedantic());
            if (opts.base() != null) {
                this.setBase(IO.getFile(this.getBase(), opts.base()));
            } else {
                this.setBase(IO.work);
            }
            if (opts.width() > 0) {
                this.width = opts.width();
            }
            CommandLine handler = opts._command();
            List<String> arguments = opts._arguments();
            if (arguments.isEmpty()) {
                Justif j = new Justif();
                Formatter f = j.formatter();
                handler.help(f, this);
                this.err.println(j.wrap());
            } else {
                String cmd = arguments.remove(0);
                String help = handler.execute(this, cmd, arguments);
                if (help != null) {
                    this.err.println(help);
                }
            }
        }
        catch (InvocationTargetException t) {
            Throwable tt = t;
            while (tt instanceof InvocationTargetException) {
                tt = tt.getTargetException();
            }
            this.exception(tt, "%s", tt);
        }
        catch (Throwable t) {
            this.exception(t, "Failed %s", t);
        }
        finally {
            if (opts.key()) {
                System.out.println("Hit a key to continue ...");
                System.in.read();
            }
        }
        if (!this.check(opts.failok())) {
            System.exit(this.getErrors().size());
        }
    }

    static {
        if (encoding == null) {
            encoding = Charset.defaultCharset().name();
        }
    }

    @Arguments(arg={"cmd ..."})
    @Description(value="Options valid for all commands. Must be given before sub command")
    protected static interface MainOptions
    extends Options {
        @Description(value="Print exception stack traces when they occur.")
        public boolean exceptions();

        @Description(value="Trace on.")
        public boolean trace();

        @Description(value="Be pedantic about all details.")
        public boolean pedantic();

        @Description(value="Specify a new base directory (default working directory).")
        public String base();

        @Description(value="Do not return error status for error that match this given regular expression.")
        public String[] failok();

        @Description(value="Wait for a key press, might be useful when you want to see the result before it is overwritten by a next command")
        public boolean key();

        @Description(value="The output width, used for wrapping diagnostic output")
        public int width();
    }
}

