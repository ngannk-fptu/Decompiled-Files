/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.reporter;

import aQute.lib.strings.Strings;
import aQute.libg.generics.Create;
import aQute.libg.reporter.ReporterMessages;
import aQute.service.reporter.Report;
import aQute.service.reporter.Reporter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ReporterAdapter
implements Reporter,
Report,
Runnable {
    final List<String> errors = new ArrayList<String>();
    final List<String> warnings = new ArrayList<String>();
    final List<LocationImpl> locations = new ArrayList<LocationImpl>();
    final Formatter out;
    boolean trace;
    boolean pedantic;
    boolean exceptions;

    public boolean isExceptions() {
        return this.exceptions;
    }

    public void setExceptions(boolean exceptions) {
        this.exceptions = exceptions;
    }

    public Formatter getOut() {
        return this.out;
    }

    public boolean isTrace() {
        return this.trace;
    }

    public void setPedantic(boolean pedantic) {
        this.pedantic = pedantic;
    }

    public ReporterAdapter() {
        this.out = null;
    }

    public ReporterAdapter(Appendable app) {
        this.out = new Formatter(app);
    }

    @Override
    public Reporter.SetLocation error(String s, Object ... args) {
        String e = Strings.format(s, args);
        this.errors.add(e);
        this.trace("ERROR: %s", e);
        return this.location(e);
    }

    @Override
    public Reporter.SetLocation exception(Throwable t, String s, Object ... args) {
        StackTraceElement[] stackTrace = t.getStackTrace();
        String method = stackTrace[0].getMethodName();
        String cname = stackTrace[0].getClassName();
        String e = "[" + this.shorten(cname) + "." + method + "] " + Strings.format(s, args);
        this.errors.add(e);
        this.trace("ERROR: %s", e);
        if (this.isExceptions() || this.isTrace()) {
            if (t instanceof InvocationTargetException) {
                t.getCause().printStackTrace(System.err);
            } else {
                t.printStackTrace(System.err);
            }
        }
        return this.location(e);
    }

    private String shorten(String cname) {
        int index = cname.lastIndexOf(36);
        if (index < 0) {
            index = cname.lastIndexOf(46);
        }
        return cname.substring(index + 1);
    }

    @Override
    public Reporter.SetLocation warning(String s, Object ... args) {
        String e = Strings.format(s, args);
        this.warnings.add(e);
        this.trace("warning: %s", e);
        return this.location(e);
    }

    private Reporter.SetLocation location(String e) {
        LocationImpl loc = new LocationImpl(e);
        this.locations.add(loc);
        return loc;
    }

    @Override
    @Deprecated
    public void progress(float progress, String s, Object ... args) {
        if (this.out != null) {
            this.out.format(s, args);
            if (!s.endsWith(String.format("%n", new Object[0]))) {
                this.out.format("%n", new Object[0]);
            }
        }
    }

    @Override
    @Deprecated
    public void trace(String s, Object ... args) {
        if (this.trace && this.out != null) {
            this.out.format("# " + s + "%n", args);
            this.out.flush();
        }
    }

    @Override
    public List<String> getWarnings() {
        return this.warnings;
    }

    @Override
    public List<String> getErrors() {
        return this.errors;
    }

    @Override
    public boolean isPedantic() {
        return false;
    }

    public void setTrace(boolean b) {
        this.trace = b;
    }

    @Override
    public boolean isOk() {
        return this.errors.isEmpty();
    }

    public boolean isPerfect() {
        return this.isOk() && this.warnings.isEmpty();
    }

    public boolean check(String ... pattern) {
        Set missed = Create.set();
        if (pattern != null) {
            for (String p : pattern) {
                boolean match = false;
                Pattern pat = Pattern.compile(p);
                Iterator<String> i = this.errors.iterator();
                while (i.hasNext()) {
                    if (!pat.matcher(i.next()).find()) continue;
                    i.remove();
                    match = true;
                }
                i = this.warnings.iterator();
                while (i.hasNext()) {
                    if (!pat.matcher(i.next()).find()) continue;
                    i.remove();
                    match = true;
                }
                if (match) continue;
                missed.add(p);
            }
        }
        if (missed.isEmpty() && this.isPerfect()) {
            return true;
        }
        if (!missed.isEmpty()) {
            System.err.println("Missed the following patterns in the warnings or errors: " + missed);
        }
        this.report(System.err);
        return false;
    }

    public void report(Appendable out) {
        Formatter f = new Formatter(out);
        this.report("Error", this.getErrors(), f);
        this.report("Warning", this.getWarnings(), f);
        f.flush();
    }

    void report(String title, Collection<String> list, Formatter f) {
        if (list.isEmpty()) {
            return;
        }
        f.format("%s%s%n", title, list.size() > 1 ? "s" : "");
        int n = 0;
        for (String s : list) {
            f.format("%3s. %s%n", n++, s);
        }
    }

    public boolean getInfo(Report other) {
        return this.getInfo(other, null);
    }

    public boolean getInfo(Report other, String prefix) {
        this.addErrors(prefix, other.getErrors());
        this.addWarnings(prefix, other.getWarnings());
        return other.isOk();
    }

    @Override
    public Report.Location getLocation(String msg) {
        for (LocationImpl loc : this.locations) {
            if (loc.message == null || !loc.message.equals(msg)) continue;
            return loc;
        }
        return null;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Must be implemented by subclass");
    }

    public <T> T getMessages(Class<T> c) {
        return ReporterMessages.base(this, c);
    }

    public void addErrors(String prefix, Collection<String> errors) {
        prefix = prefix == null ? "" : prefix + ": ";
        for (String s : errors) {
            this.errors.add(prefix + s);
        }
    }

    public void addWarnings(String prefix, Collection<String> warnings) {
        prefix = prefix == null ? "" : prefix + ": ";
        for (String s : warnings) {
            this.warnings.add(prefix + s);
        }
    }

    static class LocationImpl
    extends Report.Location
    implements Reporter.SetLocation {
        public LocationImpl(String e) {
            this.message = e;
        }

        @Override
        public Reporter.SetLocation file(String file) {
            this.file = file;
            return this;
        }

        @Override
        public Reporter.SetLocation header(String header) {
            this.header = header;
            return this;
        }

        @Override
        public Reporter.SetLocation context(String context) {
            this.context = context;
            return this;
        }

        @Override
        public Reporter.SetLocation method(String methodName) {
            this.methodName = methodName;
            return this;
        }

        @Override
        public Reporter.SetLocation line(int line) {
            this.line = line;
            return this;
        }

        @Override
        public Reporter.SetLocation reference(String reference) {
            this.reference = reference;
            return this;
        }

        @Override
        public Reporter.SetLocation details(Object details) {
            this.details = details;
            return this;
        }

        @Override
        public Report.Location location() {
            return this;
        }

        @Override
        public Reporter.SetLocation length(int length) {
            this.length = length;
            return this;
        }
    }
}

