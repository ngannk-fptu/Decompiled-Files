/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.LinkedEntity;
import com.atlassian.applinks.core.rest.model.adapter.OptionalURIAdapter;
import com.atlassian.plugins.rest.common.Link;
import java.net.URI;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement(name="consumer")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ConsumerEntity
extends LinkedEntity {
    @XmlElement(name="key")
    private String key;
    @XmlElement(name="name")
    private String name;
    @XmlElement(name="description")
    private String description;
    @XmlElement(name="signatureMethod")
    private String signatureMethod;
    @XmlElement(name="publicKey")
    private String publicKey;
    @XmlElement(name="sharedSecret")
    private String sharedSecret;
    @XmlJavaTypeAdapter(value=OptionalURIAdapter.class)
    @XmlElement(name="callback")
    private URI callback;
    @XmlElement(name="twoLOAllowed")
    private Boolean twoLOAllowed;
    @XmlElement(name="executingTwoLOUser")
    private String executingTwoLOUser;
    @XmlElement(name="twoLOImpersonationAllowed")
    private Boolean twoLOImpersonationAllowed;
    @XmlElement(name="outgoing")
    private Boolean outgoing;

    public ConsumerEntity() {
    }

    public ConsumerEntity(Link self, String key, String name, String description, String signatureMethod, String publicKey, URI callback, boolean twoLOAllowed, String executingTwoLOUser, boolean twoLOImpersonationAllowed) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.signatureMethod = signatureMethod;
        this.publicKey = publicKey;
        this.callback = callback;
        this.twoLOAllowed = twoLOAllowed;
        this.executingTwoLOUser = executingTwoLOUser;
        this.twoLOImpersonationAllowed = twoLOImpersonationAllowed;
        this.outgoing = false;
        this.addLink(self);
    }

    public ConsumerEntity(Link self, String key, String name, String description, String signatureMethod, String publicKey, URI callback, boolean twoLOAllowed, String executingTwoLOUser, boolean twoLOImpersonationAllowed, boolean outgoing) {
        this(self, key, name, description, signatureMethod, publicKey, callback, twoLOAllowed, executingTwoLOUser, twoLOImpersonationAllowed);
        this.outgoing = outgoing;
    }

    @Nullable
    public String getKey() {
        return this.key;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    @Nullable
    public String getSignatureMethod() {
        return this.signatureMethod;
    }

    @Nullable
    public String getPublicKey() {
        return this.publicKey;
    }

    @Nullable
    public URI getCallback() {
        return this.callback;
    }

    public boolean isTwoLOAllowed() {
        return Boolean.TRUE.equals(this.twoLOAllowed);
    }

    @Nullable
    public String getExecutingTwoLOUser() {
        return this.executingTwoLOUser;
    }

    public boolean isTwoLOImpersonationAllowed() {
        return Boolean.TRUE.equals(this.twoLOImpersonationAllowed);
    }

    @Nullable
    public String getSharedSecret() {
        return this.sharedSecret;
    }

    public boolean isOutgoing() {
        return Boolean.TRUE.equals(this.outgoing);
    }
}

