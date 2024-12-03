/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.model.card;

import com.atlassian.confluence.plugins.mobile.model.card.ActivityObject;
import com.atlassian.confluence.plugins.mobile.model.card.ActivityType;
import org.codehaus.jackson.annotate.JsonProperty;

public final class CardActivity {
    @JsonProperty
    private ActivityType activityType;
    @JsonProperty
    private int count;
    @JsonProperty
    private ActivityObject lastActivity;

    private CardActivity(CardActivityBuilder builder) {
        this.activityType = builder.activityType;
        this.count = builder.count;
        this.lastActivity = builder.lastActivity;
    }

    public ActivityType getActivityType() {
        return this.activityType;
    }

    public ActivityObject getLastActivity() {
        return this.lastActivity;
    }

    public int getCount() {
        return this.count;
    }

    public static CardActivityBuilder builder() {
        return new CardActivityBuilder();
    }

    public static final class CardActivityBuilder {
        private ActivityType activityType;
        private int count;
        private ActivityObject lastActivity;

        private CardActivityBuilder() {
        }

        public CardActivity build() {
            return new CardActivity(this);
        }

        public CardActivityBuilder type(ActivityType activityType) {
            this.activityType = activityType;
            return this;
        }

        public CardActivityBuilder count(int count) {
            this.count = count;
            return this;
        }

        public CardActivityBuilder lastActivity(ActivityObject lastActivity) {
            this.lastActivity = lastActivity;
            return this;
        }
    }
}

