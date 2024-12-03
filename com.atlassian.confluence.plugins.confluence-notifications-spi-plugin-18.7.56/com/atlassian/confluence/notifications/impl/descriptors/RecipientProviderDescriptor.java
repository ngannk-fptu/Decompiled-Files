/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.notifications.impl.descriptors;

import com.atlassian.confluence.notifications.RecipientsProvider;
import com.atlassian.confluence.notifications.impl.descriptors.AbstractParticipantDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class RecipientProviderDescriptor
extends AbstractParticipantDescriptor<RecipientsProvider> {
    public RecipientProviderDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }
}

