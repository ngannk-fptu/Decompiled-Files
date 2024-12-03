/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.internal.util.ClassLoaderHelper;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public final class ClasspathInterceptorChainFactory {
    private static final String GLOBAL_INTERCEPTOR_PATH = "software/amazon/awssdk/global/handlers/execution.interceptors";

    public List<ExecutionInterceptor> getInterceptors(String resource) {
        return new ArrayList<ExecutionInterceptor>(this.createExecutionInterceptorsFromClasspath(resource));
    }

    public List<ExecutionInterceptor> getGlobalInterceptors() {
        return new ArrayList<ExecutionInterceptor>(this.createExecutionInterceptorsFromClasspath(GLOBAL_INTERCEPTOR_PATH));
    }

    private Collection<ExecutionInterceptor> createExecutionInterceptorsFromClasspath(String path) {
        try {
            return this.createExecutionInterceptorsFromResources(this.classLoader().getResources(path)).collect(Collectors.toMap(p -> p.getClass().getSimpleName(), p -> p, (p1, p2) -> p1)).values();
        }
        catch (IOException e) {
            throw SdkClientException.builder().message("Unable to instantiate execution interceptor chain.").cause(e).build();
        }
    }

    private Stream<ExecutionInterceptor> createExecutionInterceptorsFromResources(Enumeration<URL> resources) {
        if (resources == null) {
            return Stream.empty();
        }
        return Collections.list(resources).stream().flatMap(this::createExecutionInterceptorFromResource);
    }

    private Stream<ExecutionInterceptor> createExecutionInterceptorFromResource(URL resource) {
        try {
            if (resource == null) {
                return Stream.empty();
            }
            ArrayList<ExecutionInterceptor> interceptors = new ArrayList<ExecutionInterceptor>();
            try (InputStream stream = resource.openStream();
                 InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                 BufferedReader fileReader = new BufferedReader(streamReader);){
                String interceptorClassName = fileReader.readLine();
                while (interceptorClassName != null) {
                    ExecutionInterceptor interceptor = this.createExecutionInterceptor(interceptorClassName);
                    if (interceptor != null) {
                        interceptors.add(interceptor);
                    }
                    interceptorClassName = fileReader.readLine();
                }
            }
            return interceptors.stream();
        }
        catch (IOException e) {
            throw SdkClientException.builder().message("Unable to instantiate execution interceptor chain.").cause(e).build();
        }
    }

    private ExecutionInterceptor createExecutionInterceptor(String interceptorClassName) {
        if (interceptorClassName == null) {
            return null;
        }
        if ((interceptorClassName = interceptorClassName.trim()).equals("")) {
            return null;
        }
        try {
            Class<?> executionInterceptorClass = ClassLoaderHelper.loadClass(interceptorClassName, ExecutionInterceptor.class, this.getClass());
            Object executionInterceptorObject = executionInterceptorClass.newInstance();
            if (executionInterceptorObject instanceof ExecutionInterceptor) {
                return (ExecutionInterceptor)executionInterceptorObject;
            }
            throw SdkClientException.builder().message("Unable to instantiate request handler chain for client. Listed request handler ('" + interceptorClassName + "') does not implement the " + ExecutionInterceptor.class + " API.").build();
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw SdkClientException.builder().message("Unable to instantiate executor interceptor for client.").cause(e).build();
        }
    }

    private ClassLoader classLoader() {
        return Validate.notNull(ClassLoaderHelper.classLoader(this.getClass()), "Failed to load the classloader of this class or the system.", new Object[0]);
    }
}

