/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.sal.websudo;

import com.atlassian.plugins.rest.common.sal.websudo.WebSudoResourceContext;
import com.atlassian.plugins.rest.common.sal.websudo.WebSudoResourceFilter;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.ext.Provider;

@Provider
public class WebSudoResourceFilterFactory
implements ResourceFilterFactory {
    private final WebSudoResourceContext authenticationContext;

    public WebSudoResourceFilterFactory(WebSudoResourceContext authenticationContext) {
        this.authenticationContext = Objects.requireNonNull(authenticationContext);
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod abstractMethod) {
        return Collections.singletonList(new WebSudoResourceFilter(abstractMethod, this.authenticationContext));
    }
}

