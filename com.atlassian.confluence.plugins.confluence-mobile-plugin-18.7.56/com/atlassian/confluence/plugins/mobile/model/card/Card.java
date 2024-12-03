/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.model.card;

import com.atlassian.confluence.plugins.mobile.model.card.CardActivity;
import com.atlassian.confluence.plugins.mobile.model.card.CardObject;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class Card<T extends CardObject> {
    @JsonProperty
    private String id;
    @JsonProperty
    private Long time;
    @JsonProperty
    private T object;
    @JsonProperty
    Map<String, CardActivity> activities;

    private Card(CardBuilder<T> builder) {
        this.object = builder.cardObject;
        this.activities = builder.activities;
        this.id = builder.id;
        this.time = builder.time;
    }

    public String getId() {
        return this.id;
    }

    public Long getTime() {
        return this.time;
    }

    public T getObject() {
        return this.object;
    }

    public Map<String, CardActivity> getActivities() {
        return this.activities;
    }

    @JsonCreator
    public static CardBuilder builder() {
        return new CardBuilder();
    }

    public static final class CardBuilder<T extends CardObject> {
        private String id;
        private T cardObject;
        private Long time;
        private Map<String, CardActivity> activities;

        private CardBuilder() {
        }

        public Card<T> build() {
            return new Card(this);
        }

        public CardBuilder<T> id(String id) {
            this.id = id;
            return this;
        }

        public CardBuilder<T> time(Long time) {
            this.time = time;
            return this;
        }

        public CardBuilder<T> cardObject(T cardObject) {
            this.cardObject = cardObject;
            return this;
        }

        public CardBuilder<T> activities(Map<String, CardActivity> activities) {
            this.activities = activities;
            return this;
        }
    }
}

