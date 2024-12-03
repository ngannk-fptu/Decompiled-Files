/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.inject;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import java.util.List;
import javax.ws.rs.WebApplicationException;

public class InjectableValuesProvider {
    private final List<AbstractHttpContextInjectable> is;

    public InjectableValuesProvider(List<Injectable> is) {
        this.is = AbstractHttpContextInjectable.transform(is);
    }

    public List<AbstractHttpContextInjectable> getInjectables() {
        return this.is;
    }

    public Object[] getInjectableValues(HttpContext context) {
        Object[] params = new Object[this.is.size()];
        try {
            int index = 0;
            for (AbstractHttpContextInjectable i : this.is) {
                params[index++] = i.getValue(context);
            }
            return params;
        }
        catch (WebApplicationException e) {
            throw e;
        }
        catch (ContainerException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw new ContainerException("Exception obtaining parameters", e);
        }
    }
}

