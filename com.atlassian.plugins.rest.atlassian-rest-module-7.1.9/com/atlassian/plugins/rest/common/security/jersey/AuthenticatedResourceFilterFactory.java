/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.atlassian.plugins.rest.common.security.jersey.AuthenticatedResourceFilter;
import com.atlassian.plugins.rest.common.util.AnnotationUtils;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticatedResourceFilterFactory
implements ResourceFilterFactory {
    private final UserManager userManager;
    private final DarkFeatureManager darkFeatureManager;

    public AuthenticatedResourceFilterFactory(UserManager userManager, DarkFeatureManager darkFeatureManager) {
        this.userManager = Objects.requireNonNull(userManager);
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod abstractMethod) {
        return Collections.singletonList(new AuthenticatedResourceFilter(new AnnotationUtils(abstractMethod), this.userManager, this.darkFeatureManager));
    }
}

