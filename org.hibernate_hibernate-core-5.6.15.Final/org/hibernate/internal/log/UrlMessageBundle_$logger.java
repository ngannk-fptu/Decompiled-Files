/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.internal.log;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import org.hibernate.internal.log.UrlMessageBundle;
import org.jboss.logging.Logger;

public class UrlMessageBundle_$logger
implements UrlMessageBundle,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FQCN = UrlMessageBundle_$logger.class.getName();
    protected final Logger log;
    private static final Locale LOCALE = Locale.ROOT;

    public UrlMessageBundle_$logger(Logger log) {
        this.log = log;
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override
    public final void logMalformedUrl(URL jarUrl, URISyntaxException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.logMalformedUrl$str(), (Object)jarUrl);
    }

    protected String logMalformedUrl$str() {
        return "HHH10000001: Malformed URL: %s";
    }

    @Override
    public final void logUnableToFindFileByUrl(URL url, Exception e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.logUnableToFindFileByUrl$str(), (Object)url);
    }

    protected String logUnableToFindFileByUrl$str() {
        return "HHH10000002: File or directory named by URL [%s] could not be found.  URL will be ignored";
    }

    @Override
    public final void logFileDoesNotExist(URL url) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logFileDoesNotExist$str(), (Object)url);
    }

    protected String logFileDoesNotExist$str() {
        return "HHH10000003: File or directory named by URL [%s] did not exist.  URL will be ignored";
    }

    @Override
    public final void logFileIsNotDirectory(URL url) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logFileIsNotDirectory$str(), (Object)url);
    }

    protected String logFileIsNotDirectory$str() {
        return "HHH10000004: Expecting resource named by URL [%s] to be a directory, but it was not.  URL will be ignored";
    }

    protected String fileDoesNotExist$str() {
        return "HHH10000005: File [%s] referenced by given URL [%s] does not exist";
    }

    @Override
    public final String fileDoesNotExist(String filePart, URL url) {
        return String.format(this.getLoggingLocale(), this.fileDoesNotExist$str(), filePart, url);
    }
}

