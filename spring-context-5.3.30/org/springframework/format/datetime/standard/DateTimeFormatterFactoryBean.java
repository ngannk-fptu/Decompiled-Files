/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.format.datetime.standard;

import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.lang.Nullable;

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

