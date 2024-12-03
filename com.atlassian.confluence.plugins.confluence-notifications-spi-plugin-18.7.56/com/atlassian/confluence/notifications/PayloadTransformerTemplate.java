/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.plugin.util.ClassUtils
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.notifications;

import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.ParticipantTemplate;
import com.atlassian.confluence.notifications.PayloadTransformer;
import com.atlassian.fugue.Maybe;
import com.atlassian.plugin.util.ClassUtils;
import com.google.common.base.Preconditions;
import java.util.List;

public abstract class PayloadTransformerTemplate<SOURCE, PAYLOAD extends NotificationPayload>
extends ParticipantTemplate<PAYLOAD>
implements PayloadTransformer<SOURCE, PAYLOAD> {
    private final Class<SOURCE> sourceType;

    protected PayloadTransformerTemplate() {
        List typeArguments = ClassUtils.getTypeArguments(PayloadTransformerTemplate.class, this.getClass());
        this.sourceType = (Class)typeArguments.get(0);
        Preconditions.checkNotNull(this.sourceType, (String)"[%s] did not convey its type arguments as expected. It should have been parameterized with a type as a first argument indicating the source type it can consume.", (Object)this.getClass().getName());
    }

    @Override
    public final Maybe<PAYLOAD> create(SOURCE source) {
        Preconditions.checkNotNull(source, (Object)"Given source argument is null.");
        Preconditions.checkArgument((boolean)this.sourceType.isAssignableFrom(source.getClass()), (String)"Given source is of type [%s] which is not a subtype of the notification payload [%s].", source.getClass(), (Object)this.sourceType.getName());
        return this.checkedCreate(source);
    }

    protected abstract Maybe<PAYLOAD> checkedCreate(SOURCE var1);

    @Override
    public final Class<SOURCE> getSourceType() {
        return this.sourceType;
    }
}

