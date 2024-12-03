/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.core.Environment;
import freemarker.template.LoggingAttemptExceptionReporter;
import freemarker.template.TemplateException;

public interface AttemptExceptionReporter {
    public static final AttemptExceptionReporter LOG_ERROR_REPORTER = new LoggingAttemptExceptionReporter(false);
    public static final AttemptExceptionReporter LOG_WARN_REPORTER = new LoggingAttemptExceptionReporter(true);

    public void report(TemplateException var1, Environment var2);
}

