/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceReference
 *  org.osgi.service.log.LogEntry
 *  org.osgi.service.log.LogService
 */
package aQute.bnd.testing;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.metatype.Configurable;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogService;

@Component(designate=Config.class)
public class TestingLog
implements LogService {
    boolean stacktrace;
    boolean direct;
    int level;
    long start = System.currentTimeMillis();
    List<LogEntry> entries = new ArrayList<LogEntry>();
    List<Pattern> filters = new ArrayList<Pattern>();
    Config config;

    @Activate
    void activate(Map<String, Object> props) {
        this.config = Configurable.createConfigurable(Config.class, props);
        if (this.config.stacktrace()) {
            this.stacktrace();
        }
        if (this.config.direct()) {
            this.direct();
        }
        this.level(this.config.level());
        if (this.config.filters() != null) {
            for (String pattern : this.config.filters()) {
                this.filter(pattern);
            }
        }
    }

    public void log(int level, String message) {
        this.log(null, level, message, null);
    }

    public void log(int level, String message, Throwable exception) {
        this.log(null, level, message, exception);
    }

    public void log(ServiceReference sr, int level, String message) {
        this.log(sr, level, message, null);
    }

    public synchronized void log(final ServiceReference sr, final int level, final String message, final Throwable exception) {
        if (exception != null && this.stacktrace) {
            exception.printStackTrace();
        }
        if (level < this.level) {
            return;
        }
        for (Pattern p : this.filters) {
            if (!p.matcher(message).find()) continue;
            return;
        }
        final long now = System.currentTimeMillis();
        LogEntry entry = new LogEntry(){

            public long getTime() {
                return now;
            }

            public ServiceReference getServiceReference() {
                return sr;
            }

            public String getMessage() {
                return message;
            }

            public int getLevel() {
                return level;
            }

            public Throwable getException() {
                return exception;
            }

            public Bundle getBundle() {
                return null;
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                try (Formatter f = new Formatter(sb);){
                    f.format("%6s %-4s %s %s", (now - TestingLog.this.start + 500L) / 1000L, sr == null ? "" : sr.getProperty("service.id"), message, exception == null ? "" : exception);
                    String string = sb.toString();
                    return string;
                }
            }
        };
        this.entries.add(entry);
        if (this.direct) {
            System.out.println(entry);
        }
    }

    public List<LogEntry> getEntries() {
        return this.entries;
    }

    public TestingLog filter(String pattern) {
        this.filters.add(Pattern.compile(pattern));
        return this;
    }

    public TestingLog stacktrace() {
        this.stacktrace = true;
        return this;
    }

    public TestingLog direct() {
        this.direct = true;
        return this;
    }

    public TestingLog errors() {
        return this.level(1);
    }

    public TestingLog warnings() {
        return this.level(2);
    }

    public TestingLog infos() {
        return this.level(3);
    }

    public TestingLog debugs() {
        return this.level(4);
    }

    public TestingLog level(int level) {
        this.level = level;
        return this;
    }

    public TestingLog full() {
        this.stacktrace = true;
        this.direct = true;
        this.level = Integer.MIN_VALUE;
        return this;
    }

    public boolean check(String ... patterns) {
        if (this.entries.isEmpty()) {
            return true;
        }
        int n = this.entries.size();
        for (LogEntry le : this.entries) {
            for (String pattern : patterns) {
                if (le.getMessage().contains(pattern)) {
                    --n;
                    continue;
                }
                System.out.println(le);
            }
        }
        this.entries.clear();
        return n != 0;
    }

    static interface Config {
        public boolean stacktrace();

        public boolean direct();

        public int level();

        public String[] filters();
    }
}

