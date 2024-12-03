/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.internal.atom.abdera;

import com.atlassian.streams.internal.atom.abdera.AtomConstants;
import com.google.common.base.Preconditions;
import java.net.URI;
import javax.xml.namespace.QName;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

public class AuthorisationMessage
extends ExtensibleElementWrapper {
    public AuthorisationMessage(Element internal) {
        super(internal);
    }

    public AuthorisationMessage(Factory factory, QName qname) {
        super(factory, qname);
    }

    public void setAuthorisationUri(URI uri) {
        this.addSimpleExtension(AtomConstants.ATLASSIAN_AUTHORISATION_URI, ((URI)Preconditions.checkNotNull((Object)uri, (Object)"authorisationUri")).toString());
    }

    public void setApplicationId(String applicationId) {
        this.addSimpleExtension(AtomConstants.ATLASSIAN_APPLICATION_ID, (String)Preconditions.checkNotNull((Object)applicationId, (Object)"applicationId"));
    }

    public void setApplicationName(String applicationName) {
        this.addSimpleExtension(AtomConstants.ATLASSIAN_APPLICATION_NAME, (String)Preconditions.checkNotNull((Object)applicationName, (Object)"applicationName"));
    }

    public void setApplicationUri(URI uri) {
        this.addSimpleExtension(AtomConstants.ATLASSIAN_APPLICATION_URI, ((URI)Preconditions.checkNotNull((Object)uri, (Object)"applicationUri")).toString());
    }
}

