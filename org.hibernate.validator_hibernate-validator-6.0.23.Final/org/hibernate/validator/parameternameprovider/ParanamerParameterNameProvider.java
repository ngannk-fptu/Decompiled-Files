/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.thoughtworks.paranamer.AdaptiveParanamer
 *  com.thoughtworks.paranamer.CachingParanamer
 *  com.thoughtworks.paranamer.Paranamer
 *  javax.validation.ParameterNameProvider
 */
package org.hibernate.validator.parameternameprovider;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.validation.ParameterNameProvider;
import org.hibernate.validator.internal.engine.DefaultParameterNameProvider;

public class ParanamerParameterNameProvider
implements ParameterNameProvider {
    private final ParameterNameProvider fallBackProvider;
    private final Paranamer paranamer;

    public ParanamerParameterNameProvider() {
        this(null);
    }

    public ParanamerParameterNameProvider(Paranamer paranamer) {
        this.paranamer = paranamer != null ? paranamer : new CachingParanamer((Paranamer)new AdaptiveParanamer());
        this.fallBackProvider = new DefaultParameterNameProvider();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getParameterNames(Constructor<?> constructor) {
        String[] parameterNames;
        Paranamer paranamer = this.paranamer;
        synchronized (paranamer) {
            parameterNames = this.paranamer.lookupParameterNames(constructor, false);
        }
        if (parameterNames != null && parameterNames.length == constructor.getParameterTypes().length) {
            return Arrays.asList(parameterNames);
        }
        return this.fallBackProvider.getParameterNames(constructor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getParameterNames(Method method) {
        String[] parameterNames;
        Paranamer paranamer = this.paranamer;
        synchronized (paranamer) {
            parameterNames = this.paranamer.lookupParameterNames((AccessibleObject)method, false);
        }
        if (parameterNames != null && parameterNames.length == method.getParameterTypes().length) {
            return Arrays.asList(parameterNames);
        }
        return this.fallBackProvider.getParameterNames(method);
    }
}

