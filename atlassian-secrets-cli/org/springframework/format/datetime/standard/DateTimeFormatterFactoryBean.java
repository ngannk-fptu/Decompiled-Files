/*
 * Decompiled with CFR 0.152.
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

    @Override
    public void afterPropertiesSet() {
        this.dateTimeFormatter = this.createDateTimeFormatter();
    }

    @Override
    @Nullable
    public DateTimeFormatter getObject() {
        return this.dateTimeFormatter;
    }

    @Override
    public Class<?> getObjectType() {
        return DateTimeFormatter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

