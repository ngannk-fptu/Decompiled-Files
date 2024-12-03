/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.api.client;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.developer.WSBindingProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceFeature;

public abstract class ServiceInterceptor {
    public List<WebServiceFeature> preCreateBinding(@NotNull WSPortInfo port, @Nullable Class<?> serviceEndpointInterface, @NotNull WSFeatureList defaultFeatures) {
        return Collections.emptyList();
    }

    public void postCreateProxy(@NotNull WSBindingProvider bp, @NotNull Class<?> serviceEndpointInterface) {
    }

    public void postCreateDispatch(@NotNull WSBindingProvider bp) {
    }

    public static ServiceInterceptor aggregate(final ServiceInterceptor ... interceptors) {
        if (interceptors.length == 1) {
            return interceptors[0];
        }
        return new ServiceInterceptor(){

            @Override
            public List<WebServiceFeature> preCreateBinding(@NotNull WSPortInfo port, @Nullable Class<?> portInterface, @NotNull WSFeatureList defaultFeatures) {
                ArrayList<WebServiceFeature> r = new ArrayList<WebServiceFeature>();
                for (ServiceInterceptor si : interceptors) {
                    r.addAll(si.preCreateBinding(port, portInterface, defaultFeatures));
                }
                return r;
            }

            @Override
            public void postCreateProxy(@NotNull WSBindingProvider bp, @NotNull Class<?> serviceEndpointInterface) {
                for (ServiceInterceptor si : interceptors) {
                    si.postCreateProxy(bp, serviceEndpointInterface);
                }
            }

            @Override
            public void postCreateDispatch(@NotNull WSBindingProvider bp) {
                for (ServiceInterceptor si : interceptors) {
                    si.postCreateDispatch(bp);
                }
            }
        };
    }
}

