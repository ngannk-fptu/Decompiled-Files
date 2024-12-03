/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.vault.authentication;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;

public class AuthenticationSteps {
    private static final Node<Object> HEAD = new Node();
    final List<Node<?>> steps;

    public static AuthenticationSteps just(VaultToken token) {
        Assert.notNull((Object)token, (String)"Vault token must not be null");
        return new AuthenticationSteps(new ScalarValueStep<VaultToken>(token, HEAD));
    }

    public static AuthenticationSteps just(HttpRequest<VaultResponse> request) {
        Assert.notNull(request, (String)"HttpRequest must not be null");
        return new AuthenticationSteps(new HttpRequestNode<VaultResponse>(request, HEAD));
    }

    public static <T> Node<T> fromValue(T value) {
        Assert.notNull(value, (String)"Value must not be null");
        return new ScalarValueStep<T>(value, HEAD);
    }

    public static <T> Node<T> fromSupplier(Supplier<T> supplier) {
        Assert.notNull(supplier, (String)"Supplier must not be null");
        return new SupplierStep<T>(supplier, HEAD);
    }

    public static <T> Node<T> fromHttpRequest(HttpRequest<T> request) {
        Assert.notNull(request, (String)"HttpRequest must not be null");
        return new HttpRequestNode<T>(request, HEAD);
    }

    AuthenticationSteps(PathAware pathAware) {
        this.steps = AuthenticationSteps.getChain(pathAware);
    }

    static List<Node<?>> getChain(PathAware pathAware) {
        ArrayList steps = new ArrayList();
        PathAware current = pathAware;
        do {
            if (!(current instanceof Node)) continue;
            steps.add((Node)((Object)current));
        } while (current.getPrevious() instanceof PathAware && !Objects.equals(current = (PathAware)((Object)current.getPrevious()), HEAD));
        Collections.reverse(steps);
        return steps;
    }

    public static class Pair<L, R> {
        private final L left;
        private final R right;

        private Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public static <L, R> Pair<L, R> of(L left, R right) {
            return new Pair<L, R>(left, right);
        }

        public L getLeft() {
            return this.left;
        }

        public R getRight() {
            return this.right;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Pair)) {
                return false;
            }
            Pair pair = (Pair)o;
            return this.left.equals(pair.left) && this.right.equals(pair.right);
        }

        public int hashCode() {
            return Objects.hash(this.left, this.right);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(this.getClass().getSimpleName());
            sb.append(" [left=").append(this.left);
            sb.append(", right=").append(this.right);
            sb.append(']');
            return sb.toString();
        }
    }

    static interface PathAware {
        public Node<?> getPrevious();
    }

    static final class SupplierStep<T>
    extends Node<T>
    implements PathAware {
        private final Supplier<T> supplier;
        private final Node<?> previous;

        SupplierStep(Supplier<T> supplier, Node<?> previous) {
            this.supplier = supplier;
            this.previous = previous;
        }

        public T get() {
            return this.supplier.get();
        }

        public String toString() {
            return "Supplier: " + this.supplier.toString();
        }

        public Supplier<T> getSupplier() {
            return this.supplier;
        }

        @Override
        public Node<?> getPrevious() {
            return this.previous;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SupplierStep)) {
                return false;
            }
            SupplierStep that = (SupplierStep)o;
            return this.supplier.equals(that.supplier) && this.previous.equals(that.previous);
        }

        public int hashCode() {
            return Objects.hash(this.supplier, this.previous);
        }
    }

    static final class ScalarValueStep<T>
    extends Node<T>
    implements PathAware {
        private final T value;
        private final Node<?> previous;

        ScalarValueStep(T value, Node<?> previous) {
            this.value = value;
            this.previous = previous;
        }

        public String toString() {
            return "Value: " + this.value.toString();
        }

        public T get() {
            return this.value;
        }

        @Override
        public Node<?> getPrevious() {
            return this.previous;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ScalarValueStep)) {
                return false;
            }
            ScalarValueStep that = (ScalarValueStep)o;
            return this.value.equals(that.value) && this.previous.equals(that.previous);
        }

        public int hashCode() {
            return Objects.hash(this.value, this.previous);
        }
    }

    static final class OnNextStep<T>
    extends Node<T>
    implements PathAware {
        private final Consumer<? super T> consumer;
        private final Node<?> previous;

        OnNextStep(Consumer<? super T> consumer, Node<?> previous) {
            this.consumer = consumer;
            this.previous = previous;
        }

        T apply(T in) {
            this.consumer.accept(in);
            return in;
        }

        public String toString() {
            return "Consumer: " + this.consumer.toString();
        }

        public Consumer<? super T> getConsumer() {
            return this.consumer;
        }

        @Override
        public Node<?> getPrevious() {
            return this.previous;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof OnNextStep)) {
                return false;
            }
            OnNextStep that = (OnNextStep)o;
            return this.consumer.equals(that.consumer) && this.previous.equals(that.previous);
        }

        public int hashCode() {
            return Objects.hash(this.consumer, this.previous);
        }
    }

    static final class ZipStep<L, R>
    extends Node<Pair<L, R>>
    implements PathAware {
        private final Node<?> left;
        private final List<Node<?>> right;

        ZipStep(Node<?> left, PathAware right) {
            this.left = left;
            this.right = AuthenticationSteps.getChain(right);
        }

        @Override
        public Node<?> getPrevious() {
            return this.left;
        }

        public String toString() {
            return "Zip";
        }

        public Node<?> getLeft() {
            return this.left;
        }

        public List<Node<?>> getRight() {
            return this.right;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ZipStep)) {
                return false;
            }
            ZipStep zipStep = (ZipStep)o;
            return this.left.equals(zipStep.left) && this.right.equals(zipStep.right);
        }

        public int hashCode() {
            return Objects.hash(this.left, this.right);
        }
    }

    static final class MapStep<I, O>
    extends Node<O>
    implements PathAware {
        private final Function<? super I, ? extends O> mapper;
        private final Node<?> previous;

        MapStep(Function<? super I, ? extends O> mapper, Node<?> previous) {
            this.mapper = mapper;
            this.previous = previous;
        }

        O apply(I in) {
            return this.mapper.apply(in);
        }

        public String toString() {
            return "Map: " + this.mapper.toString();
        }

        public Function<? super I, ? extends O> getMapper() {
            return this.mapper;
        }

        @Override
        public Node<?> getPrevious() {
            return this.previous;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MapStep)) {
                return false;
            }
            MapStep mapStep = (MapStep)o;
            return this.mapper.equals(mapStep.mapper) && this.previous.equals(mapStep.previous);
        }

        public int hashCode() {
            return Objects.hash(this.mapper, this.previous);
        }
    }

    static final class HttpRequestNode<T>
    extends Node<T>
    implements PathAware {
        private final HttpRequest<T> definition;
        private final Node<?> previous;

        HttpRequestNode(HttpRequest<T> definition, Node<?> previous) {
            this.definition = definition;
            this.previous = previous;
        }

        public String toString() {
            return this.definition.toString();
        }

        public HttpRequest<T> getDefinition() {
            return this.definition;
        }

        @Override
        public Node<?> getPrevious() {
            return this.previous;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof HttpRequestNode)) {
                return false;
            }
            HttpRequestNode that = (HttpRequestNode)o;
            return this.definition.equals(that.definition) && this.previous.equals(that.previous);
        }

        public int hashCode() {
            return Objects.hash(this.definition, this.previous);
        }
    }

    public static class HttpRequest<T> {
        final HttpMethod method;
        @Nullable
        final URI uri;
        @Nullable
        final String uriTemplate;
        @Nullable
        final String[] urlVariables;
        @Nullable
        final HttpEntity<?> entity;
        final Class<T> responseType;

        HttpRequest(HttpRequestBuilder builder, Class<T> responseType) {
            this.method = builder.method;
            this.uri = builder.uri;
            this.uriTemplate = builder.uriTemplate;
            this.urlVariables = builder.urlVariables;
            this.entity = builder.entity;
            this.responseType = responseType;
        }

        public String toString() {
            return String.format("%s %s AS %s", this.getMethod(), this.getUri() != null ? this.getUri() : this.getUriTemplate(), this.getResponseType());
        }

        HttpMethod getMethod() {
            return this.method;
        }

        @Nullable
        URI getUri() {
            return this.uri;
        }

        @Nullable
        String getUriTemplate() {
            return this.uriTemplate;
        }

        @Nullable
        String[] getUrlVariables() {
            return this.urlVariables;
        }

        @Nullable
        HttpEntity<?> getEntity() {
            return this.entity;
        }

        Class<T> getResponseType() {
            return this.responseType;
        }
    }

    public static class HttpRequestBuilder {
        HttpMethod method;
        @Nullable
        URI uri;
        @Nullable
        String uriTemplate;
        @Nullable
        String[] urlVariables;
        @Nullable
        HttpEntity<?> entity;

        public static HttpRequestBuilder get(String uriTemplate, String ... uriVariables) {
            return new HttpRequestBuilder(HttpMethod.GET, uriTemplate, uriVariables);
        }

        public static HttpRequestBuilder get(URI uri) {
            return new HttpRequestBuilder(HttpMethod.GET, uri);
        }

        public static HttpRequestBuilder post(String uriTemplate, String ... uriVariables) {
            return new HttpRequestBuilder(HttpMethod.POST, uriTemplate, uriVariables);
        }

        public static HttpRequestBuilder post(URI uri) {
            return new HttpRequestBuilder(HttpMethod.POST, uri);
        }

        public static HttpRequestBuilder method(HttpMethod method, String uriTemplate, String ... uriVariables) {
            return new HttpRequestBuilder(method, uriTemplate, uriVariables);
        }

        private HttpRequestBuilder(HttpMethod method, URI uri) {
            this.method = method;
            this.uri = uri;
        }

        private HttpRequestBuilder(HttpMethod method, @Nullable String uriTemplate, @Nullable String[] urlVariables) {
            this.method = method;
            this.uriTemplate = uriTemplate;
            this.urlVariables = urlVariables;
        }

        private HttpRequestBuilder(HttpMethod method, @Nullable URI uri, @Nullable String uriTemplate, @Nullable String[] urlVariables, @Nullable HttpEntity<?> entity) {
            this.method = method;
            this.uri = uri;
            this.uriTemplate = uriTemplate;
            this.urlVariables = urlVariables;
            this.entity = entity;
        }

        public HttpRequestBuilder with(HttpEntity<?> httpEntity) {
            Assert.notNull(httpEntity, (String)"HttpEntity must not be null");
            return new HttpRequestBuilder(this.method, this.uri, this.uriTemplate, this.urlVariables, httpEntity);
        }

        public HttpRequestBuilder with(HttpHeaders headers) {
            Assert.notNull((Object)headers, (String)"HttpHeaders must not be null");
            return new HttpRequestBuilder(this.method, this.uri, this.uriTemplate, this.urlVariables, new HttpEntity((MultiValueMap)headers));
        }

        public <T> HttpRequest<T> as(Class<T> type) {
            Assert.notNull(type, (String)"Result type must not be null");
            return new HttpRequest<T>(this, type);
        }
    }

    public static class Node<T> {
        public <R> Node<R> map(Function<? super T, ? extends R> mappingFunction) {
            Assert.notNull(mappingFunction, (String)"Mapping function must not be null");
            return new MapStep<T, R>(mappingFunction, this);
        }

        public <R> Node<Pair<T, R>> zipWith(Node<? extends R> other) {
            Assert.notNull(other, (String)"Other node must not be null");
            Assert.isInstanceOf(PathAware.class, other, (String)"Other node must be PathAware");
            return new ZipStep(this, (PathAware)((Object)other));
        }

        public Node<T> onNext(Consumer<? super T> consumerFunction) {
            Assert.notNull(consumerFunction, (String)"Consumer function must not be null");
            return new OnNextStep<T>(consumerFunction, this);
        }

        public <R> Node<R> request(HttpRequest<R> request) {
            Assert.notNull(request, (String)"HttpRequest must not be null");
            return new HttpRequestNode<R>(request, this);
        }

        public AuthenticationSteps login(String uriTemplate, String ... uriVariables) {
            Assert.hasText((String)uriTemplate, (String)"URI template must not be null or empty");
            return this.login(HttpRequestBuilder.post(uriTemplate, uriVariables).as(VaultResponse.class));
        }

        public AuthenticationSteps login(HttpRequest<VaultResponse> request) {
            Assert.notNull(request, (String)"HttpRequest must not be null");
            return new AuthenticationSteps(new HttpRequestNode<VaultResponse>(request, this));
        }

        public AuthenticationSteps login(Function<? super T, ? extends VaultToken> mappingFunction) {
            Assert.notNull(mappingFunction, (String)"Mapping function must not be null");
            return new AuthenticationSteps(new MapStep<T, VaultToken>(mappingFunction, this));
        }
    }
}

