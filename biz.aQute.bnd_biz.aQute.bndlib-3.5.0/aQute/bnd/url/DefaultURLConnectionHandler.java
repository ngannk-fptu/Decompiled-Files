/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.url;

import aQute.bnd.service.Plugin;
import aQute.bnd.service.Registry;
import aQute.bnd.service.RegistryPlugin;
import aQute.bnd.service.url.URLConnectionHandler;
import aQute.lib.strings.Strings;
import aQute.libg.glob.Glob;
import aQute.libg.slf4j.GradleLogging;
import aQute.service.reporter.Report;
import aQute.service.reporter.Reporter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultURLConnectionHandler
implements URLConnectionHandler,
Plugin,
RegistryPlugin,
Reporter {
    private static final Logger logger = LoggerFactory.getLogger(DefaultURLConnectionHandler.class);
    private final Set<Glob> matchers = new HashSet<Glob>();
    private Reporter reporter;
    protected Registry registry = null;

    @Override
    public void handle(URLConnection connection) throws Exception {
    }

    @Override
    public boolean matches(URL url) {
        if (this.matchers.isEmpty()) {
            return true;
        }
        String string = url.toString();
        for (Glob g : this.matchers) {
            if (!g.matcher(string).matches()) continue;
            return true;
        }
        return false;
    }

    protected boolean matches(URLConnection connection) {
        return this.matches(connection.getURL());
    }

    @Override
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void setProperties(Map<String, String> map) throws Exception {
        String matches = map.get("match");
        if (matches != null) {
            for (String p : matches.split("\\s*,\\s*")) {
                this.matchers.add(new Glob(p));
            }
        }
    }

    @Override
    public void setReporter(Reporter processor) {
        this.reporter = processor;
    }

    @Override
    public List<String> getWarnings() {
        return this.reporter.getWarnings();
    }

    @Override
    public List<String> getErrors() {
        return this.reporter.getErrors();
    }

    @Override
    public Report.Location getLocation(String msg) {
        return this.reporter.getLocation(msg);
    }

    @Override
    public boolean isOk() {
        return this.reporter.isOk();
    }

    @Override
    public Reporter.SetLocation error(String format, Object ... args) {
        return this.reporter.error(format, args);
    }

    @Override
    public Reporter.SetLocation warning(String format, Object ... args) {
        return this.reporter.warning(format, args);
    }

    @Override
    @Deprecated
    public void trace(String format, Object ... args) {
        if (logger.isDebugEnabled()) {
            logger.debug("{}", (Object)Strings.format(format, args));
        }
    }

    @Override
    @Deprecated
    public void progress(float progress, String format, Object ... args) {
        if (logger.isInfoEnabled(GradleLogging.LIFECYCLE)) {
            String message = Strings.format(format, args);
            if (progress > 0.0f) {
                logger.info(GradleLogging.LIFECYCLE, "[{}] {}", (Object)((int)progress), (Object)message);
            } else {
                logger.info(GradleLogging.LIFECYCLE, "{}", (Object)message);
            }
        }
    }

    @Override
    public Reporter.SetLocation exception(Throwable t, String format, Object ... args) {
        return this.reporter.exception(t, format, args);
    }

    @Override
    public boolean isPedantic() {
        return this.reporter.isPedantic();
    }

    static interface Config {
        public String match();
    }
}

