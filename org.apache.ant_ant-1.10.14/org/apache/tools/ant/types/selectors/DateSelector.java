/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.TimeComparison;
import org.apache.tools.ant.types.selectors.BaseExtendSelector;
import org.apache.tools.ant.util.FileUtils;

public class DateSelector
extends BaseExtendSelector {
    public static final String MILLIS_KEY = "millis";
    public static final String DATETIME_KEY = "datetime";
    public static final String CHECKDIRS_KEY = "checkdirs";
    public static final String GRANULARITY_KEY = "granularity";
    public static final String WHEN_KEY = "when";
    public static final String PATTERN_KEY = "pattern";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private long millis = -1L;
    private String dateTime = null;
    private boolean includeDirs = false;
    private long granularity = FILE_UTILS.getFileTimestampGranularity();
    private String pattern;
    private TimeComparison when = TimeComparison.EQUAL;

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("{dateselector date: ");
        buf.append(this.dateTime);
        buf.append(" compare: ").append(this.when.getValue());
        buf.append(" granularity: ").append(this.granularity);
        if (this.pattern != null) {
            buf.append(" pattern: ").append(this.pattern);
        }
        buf.append("}");
        return buf.toString();
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        if (this.dateTime != null) {
            this.validate();
        }
        return this.millis;
    }

    public void setDatetime(String dateTime) {
        this.dateTime = dateTime;
        this.millis = -1L;
    }

    public void setCheckdirs(boolean includeDirs) {
        this.includeDirs = includeDirs;
    }

    public void setGranularity(int granularity) {
        this.granularity = granularity;
    }

    public void setWhen(TimeComparisons tcmp) {
        this.setWhen((TimeComparison)tcmp);
    }

    public void setWhen(TimeComparison t) {
        this.when = t;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void setParameters(Parameter ... parameters) {
        super.setParameters(parameters);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                String paramname = parameter.getName();
                if (MILLIS_KEY.equalsIgnoreCase(paramname)) {
                    try {
                        this.setMillis(Long.parseLong(parameter.getValue()));
                    }
                    catch (NumberFormatException nfe) {
                        this.setError("Invalid millisecond setting " + parameter.getValue());
                    }
                    continue;
                }
                if (DATETIME_KEY.equalsIgnoreCase(paramname)) {
                    this.setDatetime(parameter.getValue());
                    continue;
                }
                if (CHECKDIRS_KEY.equalsIgnoreCase(paramname)) {
                    this.setCheckdirs(Project.toBoolean(parameter.getValue()));
                    continue;
                }
                if (GRANULARITY_KEY.equalsIgnoreCase(paramname)) {
                    try {
                        this.setGranularity(Integer.parseInt(parameter.getValue()));
                    }
                    catch (NumberFormatException nfe) {
                        this.setError("Invalid granularity setting " + parameter.getValue());
                    }
                    continue;
                }
                if (WHEN_KEY.equalsIgnoreCase(paramname)) {
                    this.setWhen(new TimeComparison(parameter.getValue()));
                    continue;
                }
                if (PATTERN_KEY.equalsIgnoreCase(paramname)) {
                    this.setPattern(parameter.getValue());
                    continue;
                }
                this.setError("Invalid parameter " + paramname);
            }
        }
    }

    @Override
    public void verifySettings() {
        if (this.dateTime == null && this.millis < 0L) {
            this.setError("You must provide a datetime or the number of milliseconds.");
        } else if (this.millis < 0L && this.dateTime != null) {
            String p = this.pattern == null ? "MM/dd/yyyy hh:mm a" : this.pattern;
            SimpleDateFormat df = this.pattern == null ? new SimpleDateFormat(p, Locale.US) : new SimpleDateFormat(p);
            try {
                this.setMillis(df.parse(this.dateTime).getTime());
                if (this.millis < 0L) {
                    this.setError("Date of " + this.dateTime + " results in negative milliseconds value relative to epoch (January 1, 1970, 00:00:00 GMT).");
                }
            }
            catch (ParseException pe) {
                this.setError("Date of " + this.dateTime + " Cannot be parsed correctly. It should be in '" + p + "' format.", pe);
            }
        }
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        this.validate();
        return file.isDirectory() && !this.includeDirs || this.when.evaluate(file.lastModified(), this.millis, this.granularity);
    }

    public static class TimeComparisons
    extends TimeComparison {
    }
}

