/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.core;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface FormatSettingsManager {
    @Transactional(readOnly=true)
    public String getDateFormat();

    public void setDateFormat(String var1);

    @Transactional(readOnly=true)
    public String getTimeFormat();

    public void setTimeFormat(String var1);

    @Transactional(readOnly=true)
    public String getDateTimeFormat();

    public void setDateTimeFormat(String var1);

    @Transactional(readOnly=true)
    public String getLongNumberFormat();

    public void setLongNumberFormat(String var1);

    @Transactional(readOnly=true)
    public String getDecimalNumberFormat();

    @Transactional(readOnly=true)
    public String getEditorBlogPostDate();

    @Transactional(readOnly=true)
    public String getEditorBlogPostTime();

    public void setDecimalNumberFormat(String var1);
}

