/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.notifications.Participant
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 */
package com.atlassian.confluence.notifications.batch.service;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.Participant;
import com.atlassian.confluence.notifications.batch.service.BatchTarget;
import com.atlassian.confluence.notifications.batch.service.BatchingRoleRecipient;
import com.atlassian.confluence.notifications.batch.template.BatchSection;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import java.util.List;
import java.util.Optional;

@ExperimentalApi
public interface BatchSectionProvider<CONTEXT>
extends Participant {
    default public BatchOutput handle(BatchingRoleRecipient recipient, List<CONTEXT> context) {
        return new BatchOutput();
    }

    default public BatchOutput handle(BatchingRoleRecipient recipient, List<CONTEXT> context, ServerConfiguration serverConfiguration) {
        return this.handle(recipient, context);
    }

    @Deprecated
    default public Option<BatchSection> process(BatchingRoleRecipient recipient, List<CONTEXT> context) {
        return this.handle(recipient, context).section();
    }

    @Deprecated
    default public Option<BatchSection> process(BatchingRoleRecipient recipient, List<CONTEXT> context, ServerConfiguration serverConfiguration) {
        return this.handle(recipient, context, serverConfiguration).section();
    }

    public static class BatchOutput {
        private final BatchSection section;
        private final BatchTarget target;

        public BatchOutput(BatchSection section, BatchTarget target) {
            this.section = section;
            this.target = target;
        }

        public BatchOutput() {
            this(null, null);
        }

        @Deprecated
        public Option<BatchSection> section() {
            return Option.option((Object)this.section);
        }

        public Optional<BatchSection> optionalSection() {
            return Optional.ofNullable(this.section);
        }

        @Deprecated
        public Option<BatchTarget> target() {
            return Option.option((Object)this.target);
        }

        public Optional<BatchTarget> optionalTarget() {
            return Optional.ofNullable(this.target);
        }
    }
}

