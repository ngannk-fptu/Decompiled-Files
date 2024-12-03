/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.Request
 *  com.atlassian.oauth.ServiceProvider
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth.consumer;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.Request;
import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.consumer.ConsumerCreationException;
import com.atlassian.oauth.consumer.ConsumerToken;
import java.net.URI;
import java.security.PrivateKey;
import javax.annotation.Nullable;

public interface ConsumerService {
    public Consumer getConsumer() throws ConsumerCreationException;

    public Consumer getConsumer(String var1);

    public Consumer getConsumerByKey(String var1);

    public Iterable<Consumer> getAllServiceProviders();

    public void add(String var1, Consumer var2, PrivateKey var3);

    public void add(String var1, Consumer var2, String var3);

    public void removeConsumerByKey(String var1);

    public Consumer updateHostConsumerInformation(String var1, @Nullable String var2, @Nullable URI var3);

    public Request sign(Request var1, ServiceProvider var2);

    public Request sign(Request var1, String var2, ServiceProvider var3);

    public Request sign(Request var1, ServiceProvider var2, ConsumerToken var3);
}

