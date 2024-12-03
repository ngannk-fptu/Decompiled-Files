/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Touch;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Resource;

public class IsLastModified
extends ProjectComponent
implements Condition {
    private long millis = -1L;
    private String dateTime = null;
    private Touch.DateFormatFactory dfFactory = Touch.DEFAULT_DF_FACTORY;
    private Resource resource;
    private CompareMode mode = CompareMode.access$000();

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public void setDatetime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setPattern(final String pattern) {
        this.dfFactory = new Touch.DateFormatFactory(){

            @Override
            public DateFormat getPrimaryFormat() {
                return new SimpleDateFormat(pattern);
            }

            @Override
            public DateFormat getFallbackFormat() {
                return null;
            }
        };
    }

    public void add(Resource r) {
        if (this.resource != null) {
            throw new BuildException("only one resource can be tested");
        }
        this.resource = r;
    }

    public void setMode(CompareMode mode) {
        this.mode = mode;
    }

    protected void validate() throws BuildException {
        if (this.millis >= 0L && this.dateTime != null) {
            throw new BuildException("Only one of dateTime and millis can be set");
        }
        if (this.millis < 0L && this.dateTime == null) {
            throw new BuildException("millis or dateTime is required");
        }
        if (this.resource == null) {
            throw new BuildException("resource is required");
        }
    }

    protected long getMillis() throws BuildException {
        if (this.millis >= 0L) {
            return this.millis;
        }
        if ("now".equalsIgnoreCase(this.dateTime)) {
            return System.currentTimeMillis();
        }
        DateFormat df = this.dfFactory.getPrimaryFormat();
        try {
            return df.parse(this.dateTime).getTime();
        }
        catch (ParseException peOne) {
            ParseException pe;
            df = this.dfFactory.getFallbackFormat();
            if (df == null) {
                pe = peOne;
            } else {
                try {
                    return df.parse(this.dateTime).getTime();
                }
                catch (ParseException peTwo) {
                    pe = peTwo;
                }
            }
            throw new BuildException(pe.getMessage(), pe, this.getLocation());
        }
    }

    @Override
    public boolean eval() throws BuildException {
        this.validate();
        long expected = this.getMillis();
        long actual = this.resource.getLastModified();
        this.log("expected timestamp: " + expected + " (" + new Date(expected) + "), actual timestamp: " + actual + " (" + new Date(actual) + ")", 3);
        if ("equals".equals(this.mode.getValue())) {
            return expected == actual;
        }
        if ("before".equals(this.mode.getValue())) {
            return expected > actual;
        }
        if ("not-before".equals(this.mode.getValue())) {
            return expected <= actual;
        }
        if ("after".equals(this.mode.getValue())) {
            return expected < actual;
        }
        if ("not-after".equals(this.mode.getValue())) {
            return expected >= actual;
        }
        throw new BuildException("Unknown mode " + this.mode.getValue());
    }

    public static class CompareMode
    extends EnumeratedAttribute {
        private static final String EQUALS_TEXT = "equals";
        private static final String BEFORE_TEXT = "before";
        private static final String AFTER_TEXT = "after";
        private static final String NOT_BEFORE_TEXT = "not-before";
        private static final String NOT_AFTER_TEXT = "not-after";
        private static final CompareMode EQUALS = new CompareMode("equals");

        public CompareMode() {
            this(EQUALS_TEXT);
        }

        public CompareMode(String s) {
            this.setValue(s);
        }

        @Override
        public String[] getValues() {
            return new String[]{EQUALS_TEXT, BEFORE_TEXT, AFTER_TEXT, NOT_BEFORE_TEXT, NOT_AFTER_TEXT};
        }

        static /* synthetic */ CompareMode access$000() {
            return EQUALS;
        }
    }
}

