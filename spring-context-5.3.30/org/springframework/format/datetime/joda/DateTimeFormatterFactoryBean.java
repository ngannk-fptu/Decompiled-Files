/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.format.DateTimeFormatter
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.format.datetime.joda;

import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.lang.Nullable;

@Deprecated
public class DateTimeFormatterFactoryBean
extends DateTimeFormatterFactory
implements FactoryBean<DateTimeFormatter>,
InitializingBean {
    @Nullable
    private DateTimeFormatter dateTimeFormatter;

    public void afterPropertiesSet() {
        this.dateTimeFormatter = this.createDateTimeFormatter();
    }

    @Nullable
    public DateTimeFormatter getObject() {
        return this.dateTimeFormatter;
    }

    public Class<?> getObjectType() {
        return DateTimeFormatter.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

