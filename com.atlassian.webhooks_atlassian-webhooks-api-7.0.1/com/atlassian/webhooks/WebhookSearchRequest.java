/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.AbstractBulkWebhookRequest;
import com.atlassian.webhooks.util.PropertyUtil;
import javax.annotation.Nonnull;

public class WebhookSearchRequest
extends AbstractBulkWebhookRequest {
    private static final int DEFAULT_LIMIT = PropertyUtil.getProperty("search.default.limit", 250);
    private final int limit;
    private final int offset;

    WebhookSearchRequest(Builder builder) {
        super(builder);
        this.limit = builder.limit;
        this.offset = builder.offset;
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public static Builder builder(@Nonnull AbstractBulkWebhookRequest request) {
        return new Builder(request);
    }

    @Nonnull
    public static Builder builder(@Nonnull WebhookSearchRequest request) {
        return new Builder(request);
    }

    public int getLimit() {
        return this.limit;
    }

    public int getOffset() {
        return this.offset;
    }

    public static class Builder
    extends AbstractBulkWebhookRequest.AbstractBuilder<Builder> {
        private int limit;
        private int offset;

        public Builder() {
            this.offset = 0;
            this.limit = DEFAULT_LIMIT;
        }

        public Builder(@Nonnull AbstractBulkWebhookRequest request) {
            super(request);
            this.offset = 0;
            this.limit = DEFAULT_LIMIT;
        }

        public Builder(@Nonnull WebhookSearchRequest request) {
            super(request);
            this.limit = request.getLimit();
            this.offset = request.getOffset();
        }

        @Nonnull
        public WebhookSearchRequest build() {
            return new WebhookSearchRequest(this);
        }

        @Nonnull
        public Builder limit(int value) {
            if (value <= 0) {
                throw new IllegalArgumentException("limit must be greater than zero");
            }
            this.limit = value;
            return this.self();
        }

        @Nonnull
        public Builder offset(int value) {
            if (value < 0) {
                throw new IllegalArgumentException("offset cannot be negative");
            }
            this.offset = value;
            return this.self();
        }

        @Override
        @Nonnull
        protected Builder self() {
            return this;
        }
    }
}

