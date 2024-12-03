/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  io.atlassian.fugue.Either
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.navlink.consumer.projectshortcuts.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import io.atlassian.fugue.Either;
import java.net.URI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UnauthenticatedRemoteApplication {
    @XmlElement
    public final String id;
    @XmlElement
    public final String appName;
    @XmlElement
    public final String appUri;
    @XmlElement
    public final String authUri;

    public UnauthenticatedRemoteApplication(ApplicationId id, String appName, Either<URI, String> appUri, URI authUri) {
        this.id = id.get();
        this.appName = appName;
        this.appUri = (String)appUri.fold((java.util.function.Function)new Function<URI, String>(){

            public String apply(URI from) {
                return from.toString();
            }
        }, (java.util.function.Function)Functions.identity());
        this.authUri = authUri.toString();
    }
}

