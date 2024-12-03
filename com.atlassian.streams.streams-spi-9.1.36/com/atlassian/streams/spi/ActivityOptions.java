/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.atlassian.streams.api.ActivityVerb
 *  com.atlassian.streams.api.common.Pair
 *  com.google.common.base.Function
 */
package com.atlassian.streams.spi;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.spi.StreamsFilterOptionProvider;
import com.google.common.base.Function;

public final class ActivityOptions {
    private ActivityOptions() {
    }

    @Deprecated
    public static Function<Pair<ActivityObjectType, ActivityVerb>, StreamsFilterOptionProvider.ActivityOption> toActivityOption(I18nResolver i18nResolver, String messageKeyPrefix) {
        return new ToActivityOption(i18nResolver, messageKeyPrefix);
    }

    public static java.util.function.Function<Pair<ActivityObjectType, ActivityVerb>, StreamsFilterOptionProvider.ActivityOption> toActivityOptionFunc(I18nResolver i18nResolver, String messageKeyPrefix) {
        return new ToActivityOption(i18nResolver, messageKeyPrefix);
    }

    @Deprecated
    public static Function<StreamsFilterOptionProvider.ActivityOption, String> toActivityOptionKey() {
        return ActivityOptionValue.INSTANCE;
    }

    @Deprecated
    private static enum ActivityOptionValue implements Function<StreamsFilterOptionProvider.ActivityOption, String>
    {
        INSTANCE;


        public String apply(StreamsFilterOptionProvider.ActivityOption a) {
            return a.getType().key() + ":" + a.getVerb().key();
        }
    }

    private static final class ToActivityOption
    implements Function<Pair<ActivityObjectType, ActivityVerb>, StreamsFilterOptionProvider.ActivityOption> {
        private final I18nResolver i18nResolver;
        private final String messageKeyPrefix;

        public ToActivityOption(I18nResolver i18nResolver, String messageKeyPrefix) {
            this.i18nResolver = i18nResolver;
            this.messageKeyPrefix = messageKeyPrefix;
        }

        public StreamsFilterOptionProvider.ActivityOption apply(Pair<ActivityObjectType, ActivityVerb> a) {
            String name = this.i18nResolver.getText(this.messageKeyPrefix + "." + ((ActivityObjectType)a.first()).key() + "." + ((ActivityVerb)a.second()).key());
            return new StreamsFilterOptionProvider.ActivityOption(name, (ActivityObjectType)a.first(), (ActivityVerb)a.second());
        }
    }
}

