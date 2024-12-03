/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.format.DateTimeFormat
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public abstract class AbstractBuildInformationManager
implements BuildInformationManager {
    private static final DateTime BUILD_DATE = DateTimeFormat.forPattern((String)"yyyyMMddHHssZ").withOffsetParsed().parseDateTime("202311292143+0000");

    @Override
    public DateTime getBuildDate() {
        return BUILD_DATE;
    }
}

