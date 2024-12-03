/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.api.services;

import java.time.format.DateTimeParseException;
import java.util.Locale;

public interface JiraIssuesDateFormatter {
    public String formatDate(Locale var1, String var2);

    public String reformatDateInUserLocale(String var1, Locale var2, String var3);

    public String reformatDateInDefaultLocale(String var1, Locale var2, String var3) throws DateTimeParseException;
}

