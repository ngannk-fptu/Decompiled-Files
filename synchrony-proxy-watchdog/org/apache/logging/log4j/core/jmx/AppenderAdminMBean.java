/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.jmx;

public interface AppenderAdminMBean {
    public static final String PATTERN = "org.apache.logging.log4j2:type=%s,component=Appenders,name=%s";

    public String getName();

    public String getLayout();

    public boolean isIgnoreExceptions();

    public String getErrorHandler();

    public String getFilter();
}

