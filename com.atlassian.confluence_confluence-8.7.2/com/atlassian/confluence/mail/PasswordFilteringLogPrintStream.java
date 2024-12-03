/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 */
package com.atlassian.confluence.mail;

import com.atlassian.confluence.logging.LoggingOutputStream;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class PasswordFilteringLogPrintStream
extends LoggingOutputStream {
    private final String password;
    private Pattern replacePattern = null;

    public PasswordFilteringLogPrintStream(@Nonnull Logger log, @Nonnull Level level, String password) {
        super(log, level);
        this.password = password;
        if (password != null) {
            this.replacePattern = Pattern.compile("(.*PASS.*?[ \"]|.*LOGIN.*?[ \"])" + Pattern.quote(password));
        }
    }

    @Override
    public String processLine(String s) {
        if ((s = super.processLine(s)) != null && this.password != null && this.password.length() > 2) {
            s = this.replacePattern.matcher(s).replaceAll("$1<hidden password>");
        }
        return s;
    }
}

