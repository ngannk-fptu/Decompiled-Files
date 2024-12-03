/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.TransformationDateFormats;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ThreadLocalTransformationDateFormats
implements TransformationDateFormats {
    private static final ThreadLocal<DateFormat> POSTING_DAY_FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>(){

        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy/MM/dd");
        }
    };

    @Override
    public DateFormat getPostingDayFormat() {
        return POSTING_DAY_FORMAT_THREAD_LOCAL.get();
    }
}

