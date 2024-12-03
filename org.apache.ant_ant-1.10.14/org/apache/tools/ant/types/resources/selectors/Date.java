/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.selectors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.TimeComparison;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.util.FileUtils;

public class Date
implements ResourceSelector {
    private static final String MILLIS_OR_DATETIME = "Either the millis or the datetime attribute must be set.";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private Long millis = null;
    private String dateTime = null;
    private String pattern = null;
    private TimeComparison when = TimeComparison.EQUAL;
    private long granularity = FILE_UTILS.getFileTimestampGranularity();

    public synchronized void setMillis(long m) {
        this.millis = m;
    }

    public synchronized long getMillis() {
        return this.millis == null ? -1L : this.millis;
    }

    public synchronized void setDateTime(String s) {
        this.dateTime = s;
        this.millis = null;
    }

    public synchronized String getDatetime() {
        return this.dateTime;
    }

    public synchronized void setGranularity(long g) {
        this.granularity = g;
    }

    public synchronized long getGranularity() {
        return this.granularity;
    }

    public synchronized void setPattern(String p) {
        this.pattern = p;
    }

    public synchronized String getPattern() {
        return this.pattern;
    }

    public synchronized void setWhen(TimeComparison c) {
        this.when = c;
    }

    public synchronized TimeComparison getWhen() {
        return this.when;
    }

    @Override
    public synchronized boolean isSelected(Resource r) {
        if (this.dateTime == null && this.millis == null) {
            throw new BuildException(MILLIS_OR_DATETIME);
        }
        if (this.millis == null) {
            String p = this.pattern == null ? "MM/dd/yyyy hh:mm a" : this.pattern;
            SimpleDateFormat df = this.pattern == null ? new SimpleDateFormat(p, Locale.US) : new SimpleDateFormat(p);
            try {
                long m = df.parse(this.dateTime).getTime();
                if (m < 0L) {
                    throw new BuildException("Date of %s results in negative milliseconds value relative to epoch (January 1, 1970, 00:00:00 GMT).", this.dateTime);
                }
                this.setMillis(m);
            }
            catch (ParseException pe) {
                throw new BuildException("Date of %s Cannot be parsed correctly. It should be in '%s' format.", this.dateTime, p);
            }
        }
        return this.when.evaluate(r.getLastModified(), this.millis, this.granularity);
    }
}

