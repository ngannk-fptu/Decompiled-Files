/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.property.PropertyManager
 *  com.atlassian.crowd.model.application.Application
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.manager.validation;

import com.atlassian.crowd.manager.property.PropertyManager;
import com.atlassian.crowd.manager.validation.ApplicationRemoteAddressValidator;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.util.ClusterAwareInetAddressCache;
import com.google.common.base.Preconditions;
import java.net.InetAddress;

public class CachedApplicationRemoteAddressValidator
implements ApplicationRemoteAddressValidator {
    private final PropertyManager propertyManager;
    private final ClusterAwareInetAddressCache cacheUtil;
    private final ApplicationRemoteAddressValidator delegate;

    public CachedApplicationRemoteAddressValidator(PropertyManager propertyManager, ClusterAwareInetAddressCache cacheUtil, ApplicationRemoteAddressValidator delegate) {
        this.delegate = (ApplicationRemoteAddressValidator)Preconditions.checkNotNull((Object)delegate);
        this.propertyManager = (PropertyManager)Preconditions.checkNotNull((Object)propertyManager);
        this.cacheUtil = (ClusterAwareInetAddressCache)Preconditions.checkNotNull((Object)cacheUtil);
    }

    @Override
    public boolean validate(Application application, InetAddress clientAddress) {
        if (this.propertyManager.isCacheEnabled()) {
            Boolean addressValid = this.cacheUtil.getPermitted(application, clientAddress);
            if (addressValid == null) {
                addressValid = this.delegate.validate(application, clientAddress);
                this.cacheUtil.setPermitted(application, clientAddress, addressValid);
            }
            return addressValid;
        }
        return this.delegate.validate(application, clientAddress);
    }
}

