/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.PropertyOutputStream;

public class Length
extends Task
implements Condition {
    private static final String ALL = "all";
    private static final String EACH = "each";
    private static final String STRING = "string";
    private static final String LENGTH_REQUIRED = "Use of the Length condition requires that the length attribute be set.";
    private String property;
    private String string;
    private Boolean trim;
    private String mode = "all";
    private Comparison when = Comparison.EQUAL;
    private Long length;
    private Resources resources;

    public synchronized void setProperty(String property) {
        this.property = property;
    }

    public synchronized void setResource(Resource resource) {
        this.add(resource);
    }

    public synchronized void setFile(File file) {
        this.add(new FileResource(file));
    }

    public synchronized void add(FileSet fs) {
        this.add((ResourceCollection)fs);
    }

    public synchronized void add(ResourceCollection c) {
        if (c == null) {
            return;
        }
        this.resources = this.resources == null ? new Resources() : this.resources;
        this.resources.add(c);
    }

    public synchronized void setLength(long ell) {
        this.length = ell;
    }

    public synchronized void setWhen(When w) {
        this.setWhen((Comparison)w);
    }

    public synchronized void setWhen(Comparison c) {
        this.when = c;
    }

    public synchronized void setMode(FileMode m) {
        this.mode = m.getValue();
    }

    public synchronized void setString(String string) {
        this.string = string;
        this.mode = STRING;
    }

    public synchronized void setTrim(boolean trim) {
        this.trim = trim;
    }

    public boolean getTrim() {
        return Boolean.TRUE.equals(this.trim);
    }

    @Override
    public void execute() {
        this.validate();
        OutputStream out = this.property == null ? new LogOutputStream(this, 2) : new PropertyOutputStream(this.getProject(), this.property);
        PrintStream ps = new PrintStream(out);
        switch (this.mode) {
            case "string": {
                ps.print(Length.getLength(this.string, this.getTrim()));
                ps.close();
                break;
            }
            case "each": {
                this.handleResources(new EachHandler(ps));
                break;
            }
            case "all": {
                this.handleResources(new AllHandler(ps));
            }
        }
    }

    @Override
    public boolean eval() {
        Long ell;
        this.validate();
        if (this.length == null) {
            throw new BuildException(LENGTH_REQUIRED);
        }
        if (STRING.equals(this.mode)) {
            ell = Length.getLength(this.string, this.getTrim());
        } else {
            AccumHandler h = new AccumHandler();
            this.handleResources(h);
            ell = h.getAccum();
        }
        return this.when.evaluate(ell.compareTo(this.length));
    }

    private void validate() {
        if (this.string != null) {
            if (this.resources != null) {
                throw new BuildException("the string length function is incompatible with the file/resource length function");
            }
            if (!STRING.equals(this.mode)) {
                throw new BuildException("the mode attribute is for use with the file/resource length function");
            }
        } else if (this.resources != null) {
            if (!EACH.equals(this.mode) && !ALL.equals(this.mode)) {
                throw new BuildException("invalid mode setting for file/resource length function: \"" + this.mode + "\"");
            }
            if (this.trim != null) {
                throw new BuildException("the trim attribute is for use with the string length function only");
            }
        } else {
            throw new BuildException("you must set either the string attribute or specify one or more files using the file attribute or nested resource collections");
        }
    }

    private void handleResources(Handler h) {
        for (Resource r : this.resources) {
            if (!r.isExists()) {
                this.log(r + " does not exist", 1);
            }
            if (r.isDirectory()) {
                this.log(r + " is a directory; length may not be meaningful", 1);
            }
            h.handle(r);
        }
        h.complete();
    }

    private static long getLength(String s, boolean t) {
        return (t ? s.trim() : s).length();
    }

    public static class FileMode
    extends EnumeratedAttribute {
        static final String[] MODES = new String[]{"each", "all"};

        @Override
        public String[] getValues() {
            return MODES;
        }
    }

    private class EachHandler
    extends Handler {
        EachHandler(PrintStream ps) {
            super(ps);
        }

        @Override
        protected void handle(Resource r) {
            this.getPs().print(r.toString());
            this.getPs().print(" : ");
            long size = r.getSize();
            if (size == -1L) {
                this.getPs().println("unknown");
            } else {
                this.getPs().println(size);
            }
        }
    }

    private abstract class Handler {
        private PrintStream ps;

        Handler(PrintStream ps) {
            this.ps = ps;
        }

        protected PrintStream getPs() {
            return this.ps;
        }

        protected abstract void handle(Resource var1);

        void complete() {
            FileUtils.close(this.ps);
        }
    }

    private class AllHandler
    extends AccumHandler {
        AllHandler(PrintStream ps) {
            super(ps);
        }

        @Override
        void complete() {
            this.getPs().print(this.getAccum());
            super.complete();
        }
    }

    private class AccumHandler
    extends Handler {
        private long accum;

        AccumHandler() {
            super(null);
            this.accum = 0L;
        }

        protected AccumHandler(PrintStream ps) {
            super(ps);
            this.accum = 0L;
        }

        protected long getAccum() {
            return this.accum;
        }

        @Override
        protected synchronized void handle(Resource r) {
            long size = r.getSize();
            if (size == -1L) {
                Length.this.log("Size unknown for " + r.toString(), 1);
            } else {
                this.accum += size;
            }
        }
    }

    public static class When
    extends Comparison {
    }
}

