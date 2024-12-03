/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.internal.atom.abdera;

import com.atlassian.streams.internal.atom.abdera.ActivityObject;
import com.atlassian.streams.internal.atom.abdera.AtomConstants;
import com.atlassian.streams.internal.atom.abdera.AuthorisationMessage;
import org.apache.abdera.util.AbstractExtensionFactory;

public class ActivityStreamsExtensionFactory
extends AbstractExtensionFactory {
    public ActivityStreamsExtensionFactory() {
        super("http://activitystrea.ms/spec/1.0/", "http://streams.atlassian.com/syndication/general/1.0");
        this.addImpl(AtomConstants.ACTIVITY_OBJECT, ActivityObject.class);
        this.addImpl(AtomConstants.ACTIVITY_TARGET, ActivityObject.class);
        this.addImpl(AtomConstants.ATLASSIAN_AUTHORISATION_MESSAGE, AuthorisationMessage.class);
    }
}

