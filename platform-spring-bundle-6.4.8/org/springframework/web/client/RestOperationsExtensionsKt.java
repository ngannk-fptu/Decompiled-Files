/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 */
package org.springframework.web.client;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u0000:\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u000f\u001a;\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0002\b\u0003\u0018\u00010\tH\u0086\b\u001aT\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0002\b\u0003\u0018\u00010\t2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b\u00a2\u0006\u0002\u0010\u000e\u001aM\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0002\b\u0003\u0018\u00010\t2\u0010\u0010\u000b\u001a\f\u0012\u0004\u0012\u00020\n\u0012\u0002\b\u00030\u000fH\u0086\b\u001a'\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\n\u0010\b\u001a\u0006\u0012\u0002\b\u00030\u0010H\u0086\b\u001a#\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0086\b\u001a<\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b\u00a2\u0006\u0002\u0010\u0012\u001a5\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0010\u0010\u000b\u001a\f\u0012\u0004\u0012\u00020\n\u0012\u0002\b\u00030\u000fH\u0086\b\u001a\"\u0010\u0013\u001a\u0002H\u0002\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0086\b\u00a2\u0006\u0002\u0010\u0014\u001a6\u0010\u0013\u001a\u0002H\u0002\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b\u00a2\u0006\u0002\u0010\u0015\u001a8\u0010\u0013\u001a\u0002H\u0002\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0014\u0010\u000b\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u000fH\u0086\b\u00a2\u0006\u0002\u0010\u0016\u001a.\u0010\u0017\u001a\u0002H\u0002\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\rH\u0086\b\u00a2\u0006\u0002\u0010\u0019\u001aB\u0010\u0017\u001a\u0002H\u0002\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b\u00a2\u0006\u0002\u0010\u001a\u001a@\u0010\u0017\u001a\u0002H\u0002\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0010\u0010\u000b\u001a\f\u0012\u0004\u0012\u00020\n\u0012\u0002\b\u00030\u000fH\u0086\b\u00a2\u0006\u0002\u0010\u001b\u001a/\u0010\u001c\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\rH\u0086\b\u001aH\u0010\u001c\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b\u00a2\u0006\u0002\u0010\u001d\u001aA\u0010\u001c\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0010\u0010\u000b\u001a\f\u0012\u0004\u0012\u00020\n\u0012\u0002\b\u00030\u000fH\u0086\b\u001a.\u0010\u001e\u001a\u0002H\u0002\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\rH\u0086\b\u00a2\u0006\u0002\u0010\u0019\u001aB\u0010\u001e\u001a\u0002H\u0002\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b\u00a2\u0006\u0002\u0010\u001a\u001a@\u0010\u001e\u001a\u0002H\u0002\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0010\u0010\u000b\u001a\f\u0012\u0004\u0012\u00020\n\u0012\u0002\b\u00030\u000fH\u0086\b\u00a2\u0006\u0002\u0010\u001b\u00a8\u0006\u001f"}, d2={"exchange", "Lorg/springframework/http/ResponseEntity;", "T", "Lorg/springframework/web/client/RestOperations;", "url", "Ljava/net/URI;", "method", "Lorg/springframework/http/HttpMethod;", "requestEntity", "Lorg/springframework/http/HttpEntity;", "", "uriVariables", "", "", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Lorg/springframework/http/HttpMethod;Lorg/springframework/http/HttpEntity;[Ljava/lang/Object;)Lorg/springframework/http/ResponseEntity;", "", "Lorg/springframework/http/RequestEntity;", "getForEntity", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;[Ljava/lang/Object;)Lorg/springframework/http/ResponseEntity;", "getForObject", "(Lorg/springframework/web/client/RestOperations;Ljava/net/URI;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/util/Map;)Ljava/lang/Object;", "patchForObject", "request", "(Lorg/springframework/web/client/RestOperations;Ljava/net/URI;Ljava/lang/Object;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/lang/Object;Ljava/util/Map;)Ljava/lang/Object;", "postForEntity", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)Lorg/springframework/http/ResponseEntity;", "postForObject", "spring-web"})
public final class RestOperationsExtensionsKt {
    public static final /* synthetic */ <T> T getForObject(RestOperations $this$getForObject, String url, Object ... uriVariables) throws RestClientException {
        int $i$f$getForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getForObject, (String)"$this$getForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$getForObject.getForObject(url, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static final /* synthetic */ <T> T getForObject(RestOperations $this$getForObject, String url, Map<String, ? extends Object> uriVariables) throws RestClientException {
        int $i$f$getForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getForObject, (String)"$this$getForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull(uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$getForObject.getForObject(url, Object.class, uriVariables);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static final /* synthetic */ <T> T getForObject(RestOperations $this$getForObject, URI url) throws RestClientException {
        int $i$f$getForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getForObject, (String)"$this$getForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$getForObject.getForObject(url, Object.class);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> getForEntity(RestOperations $this$getForEntity, URI url) throws RestClientException {
        int $i$f$getForEntity = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getForEntity, (String)"$this$getForEntity");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        ResponseEntity<Object> responseEntity = $this$getForEntity.getForEntity(url, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"getForEntity(url, T::class.java)");
        return responseEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> getForEntity(RestOperations $this$getForEntity, String url, Object ... uriVariables) throws RestClientException {
        int $i$f$getForEntity = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getForEntity, (String)"$this$getForEntity");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        ResponseEntity<Object> responseEntity = $this$getForEntity.getForEntity(url, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"getForEntity(url, T::class.java, *uriVariables)");
        return responseEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> getForEntity(RestOperations $this$getForEntity, String url, Map<String, ?> uriVariables) throws RestClientException {
        int $i$f$getForEntity = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getForEntity, (String)"$this$getForEntity");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull(uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        ResponseEntity<Object> responseEntity = $this$getForEntity.getForEntity(url, Object.class, uriVariables);
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"getForEntity(url, T::class.java, uriVariables)");
        return responseEntity;
    }

    public static final /* synthetic */ <T> T patchForObject(RestOperations $this$patchForObject, String url, Object request, Object ... uriVariables) throws RestClientException {
        int $i$f$patchForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$patchForObject, (String)"$this$patchForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$patchForObject.patchForObject(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static /* synthetic */ Object patchForObject$default(RestOperations $this$patchForObject, String url, Object request, Object[] uriVariables, int n, Object object) throws RestClientException {
        if ((n & 2) != 0) {
            request = null;
        }
        boolean $i$f$patchForObject = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$patchForObject, (String)"$this$patchForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object2 = $this$patchForObject.patchForObject(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return object2;
    }

    public static final /* synthetic */ <T> T patchForObject(RestOperations $this$patchForObject, String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        int $i$f$patchForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$patchForObject, (String)"$this$patchForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull(uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$patchForObject.patchForObject(url, request, Object.class, uriVariables);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static /* synthetic */ Object patchForObject$default(RestOperations $this$patchForObject, String url, Object request, Map uriVariables, int n, Object object) throws RestClientException {
        if ((n & 2) != 0) {
            request = null;
        }
        boolean $i$f$patchForObject = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$patchForObject, (String)"$this$patchForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object2 = $this$patchForObject.patchForObject(url, request, Object.class, uriVariables);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return object2;
    }

    public static final /* synthetic */ <T> T patchForObject(RestOperations $this$patchForObject, URI url, Object request) throws RestClientException {
        int $i$f$patchForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$patchForObject, (String)"$this$patchForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$patchForObject.patchForObject(url, request, Object.class);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static /* synthetic */ Object patchForObject$default(RestOperations $this$patchForObject, URI url, Object request, int n, Object object) throws RestClientException {
        if ((n & 2) != 0) {
            request = null;
        }
        boolean $i$f$patchForObject = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$patchForObject, (String)"$this$patchForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object2 = $this$patchForObject.patchForObject(url, request, Object.class);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return object2;
    }

    public static final /* synthetic */ <T> T postForObject(RestOperations $this$postForObject, String url, Object request, Object ... uriVariables) throws RestClientException {
        int $i$f$postForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForObject, (String)"$this$postForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$postForObject.postForObject(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static /* synthetic */ Object postForObject$default(RestOperations $this$postForObject, String url, Object request, Object[] uriVariables, int n, Object object) throws RestClientException {
        if ((n & 2) != 0) {
            request = null;
        }
        boolean $i$f$postForObject = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForObject, (String)"$this$postForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object2 = $this$postForObject.postForObject(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return object2;
    }

    public static final /* synthetic */ <T> T postForObject(RestOperations $this$postForObject, String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        int $i$f$postForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForObject, (String)"$this$postForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull(uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$postForObject.postForObject(url, request, Object.class, uriVariables);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static /* synthetic */ Object postForObject$default(RestOperations $this$postForObject, String url, Object request, Map uriVariables, int n, Object object) throws RestClientException {
        if ((n & 2) != 0) {
            request = null;
        }
        boolean $i$f$postForObject = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForObject, (String)"$this$postForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object2 = $this$postForObject.postForObject(url, request, Object.class, uriVariables);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return object2;
    }

    public static final /* synthetic */ <T> T postForObject(RestOperations $this$postForObject, URI url, Object request) throws RestClientException {
        int $i$f$postForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForObject, (String)"$this$postForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$postForObject.postForObject(url, request, Object.class);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static /* synthetic */ Object postForObject$default(RestOperations $this$postForObject, URI url, Object request, int n, Object object) throws RestClientException {
        if ((n & 2) != 0) {
            request = null;
        }
        boolean $i$f$postForObject = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForObject, (String)"$this$postForObject");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object2 = $this$postForObject.postForObject(url, request, Object.class);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return object2;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> postForEntity(RestOperations $this$postForEntity, String url, Object request, Object ... uriVariables) throws RestClientException {
        int $i$f$postForEntity = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForEntity, (String)"$this$postForEntity");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        ResponseEntity<Object> responseEntity = $this$postForEntity.postForEntity(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"postForEntity(url, reque\u2026lass.java, *uriVariables)");
        return responseEntity;
    }

    public static /* synthetic */ ResponseEntity postForEntity$default(RestOperations $this$postForEntity, String url, Object request, Object[] uriVariables, int n, Object object) throws RestClientException {
        if ((n & 2) != 0) {
            request = null;
        }
        boolean $i$f$postForEntity = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForEntity, (String)"$this$postForEntity");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        ResponseEntity<Object> responseEntity = $this$postForEntity.postForEntity(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"postForEntity(url, reque\u2026lass.java, *uriVariables)");
        return responseEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> postForEntity(RestOperations $this$postForEntity, String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        int $i$f$postForEntity = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForEntity, (String)"$this$postForEntity");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull(uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        ResponseEntity<Object> responseEntity = $this$postForEntity.postForEntity(url, request, Object.class, uriVariables);
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"postForEntity(url, reque\u2026class.java, uriVariables)");
        return responseEntity;
    }

    public static /* synthetic */ ResponseEntity postForEntity$default(RestOperations $this$postForEntity, String url, Object request, Map uriVariables, int n, Object object) throws RestClientException {
        if ((n & 2) != 0) {
            request = null;
        }
        boolean $i$f$postForEntity = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForEntity, (String)"$this$postForEntity");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        ResponseEntity<Object> responseEntity = $this$postForEntity.postForEntity(url, request, Object.class, uriVariables);
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"postForEntity(url, reque\u2026class.java, uriVariables)");
        return responseEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> postForEntity(RestOperations $this$postForEntity, URI url, Object request) throws RestClientException {
        int $i$f$postForEntity = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForEntity, (String)"$this$postForEntity");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        ResponseEntity<Object> responseEntity = $this$postForEntity.postForEntity(url, request, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"postForEntity(url, request, T::class.java)");
        return responseEntity;
    }

    public static /* synthetic */ ResponseEntity postForEntity$default(RestOperations $this$postForEntity, URI url, Object request, int n, Object object) throws RestClientException {
        if ((n & 2) != 0) {
            request = null;
        }
        boolean $i$f$postForEntity = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$postForEntity, (String)"$this$postForEntity");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        ResponseEntity<Object> responseEntity = $this$postForEntity.postForEntity(url, request, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"postForEntity(url, request, T::class.java)");
        return responseEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> exchange(RestOperations $this$exchange, String url, HttpMethod method, HttpEntity<?> requestEntity, Object ... uriVariables) throws RestClientException {
        int $i$f$exchange = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$exchange, (String)"$this$exchange");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)((Object)method), (String)"method");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.needClassReification();
        ResponseEntity responseEntity = $this$exchange.exchange(url, method, requestEntity, new ParameterizedTypeReference<T>(){}, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"exchange(url, method, re\u2026e<T>() {}, *uriVariables)");
        return responseEntity;
    }

    public static /* synthetic */ ResponseEntity exchange$default(RestOperations $this$exchange, String url, HttpMethod method, HttpEntity requestEntity, Object[] uriVariables, int n, Object object) throws RestClientException {
        if ((n & 4) != 0) {
            requestEntity = null;
        }
        boolean $i$f$exchange = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$exchange, (String)"$this$exchange");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)((Object)method), (String)"method");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.needClassReification();
        ResponseEntity responseEntity = $this$exchange.exchange(url, method, requestEntity, new /* invalid duplicate definition of identical inner class */, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"exchange(url, method, re\u2026e<T>() {}, *uriVariables)");
        return responseEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> exchange(RestOperations $this$exchange, String url, HttpMethod method, HttpEntity<?> requestEntity, Map<String, ?> uriVariables) throws RestClientException {
        int $i$f$exchange = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$exchange, (String)"$this$exchange");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)((Object)method), (String)"method");
        Intrinsics.checkParameterIsNotNull(uriVariables, (String)"uriVariables");
        Intrinsics.needClassReification();
        ResponseEntity responseEntity = $this$exchange.exchange(url, method, requestEntity, new ParameterizedTypeReference<T>(){}, uriVariables);
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"exchange(url, method, re\u2026ce<T>() {}, uriVariables)");
        return responseEntity;
    }

    public static /* synthetic */ ResponseEntity exchange$default(RestOperations $this$exchange, String url, HttpMethod method, HttpEntity requestEntity, Map uriVariables, int n, Object object) throws RestClientException {
        if ((n & 4) != 0) {
            requestEntity = null;
        }
        boolean $i$f$exchange = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$exchange, (String)"$this$exchange");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)((Object)method), (String)"method");
        Intrinsics.checkParameterIsNotNull((Object)uriVariables, (String)"uriVariables");
        Intrinsics.needClassReification();
        ResponseEntity responseEntity = $this$exchange.exchange(url, method, requestEntity, new /* invalid duplicate definition of identical inner class */, uriVariables);
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"exchange(url, method, re\u2026ce<T>() {}, uriVariables)");
        return responseEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> exchange(RestOperations $this$exchange, URI url, HttpMethod method, HttpEntity<?> requestEntity) throws RestClientException {
        int $i$f$exchange = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$exchange, (String)"$this$exchange");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)((Object)method), (String)"method");
        Intrinsics.needClassReification();
        ResponseEntity responseEntity = $this$exchange.exchange(url, method, requestEntity, new ParameterizedTypeReference<T>(){});
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"exchange(url, method, re\u2026zedTypeReference<T>() {})");
        return responseEntity;
    }

    public static /* synthetic */ ResponseEntity exchange$default(RestOperations $this$exchange, URI url, HttpMethod method, HttpEntity requestEntity, int n, Object object) throws RestClientException {
        if ((n & 4) != 0) {
            requestEntity = null;
        }
        boolean $i$f$exchange = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$exchange, (String)"$this$exchange");
        Intrinsics.checkParameterIsNotNull((Object)url, (String)"url");
        Intrinsics.checkParameterIsNotNull((Object)((Object)method), (String)"method");
        Intrinsics.needClassReification();
        ResponseEntity responseEntity = $this$exchange.exchange(url, method, requestEntity, new /* invalid duplicate definition of identical inner class */);
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"exchange(url, method, re\u2026zedTypeReference<T>() {})");
        return responseEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> exchange(RestOperations $this$exchange, RequestEntity<?> requestEntity) throws RestClientException {
        int $i$f$exchange = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$exchange, (String)"$this$exchange");
        Intrinsics.checkParameterIsNotNull(requestEntity, (String)"requestEntity");
        Intrinsics.needClassReification();
        ResponseEntity responseEntity = $this$exchange.exchange(requestEntity, new ParameterizedTypeReference<T>(){});
        Intrinsics.checkExpressionValueIsNotNull(responseEntity, (String)"exchange(requestEntity, \u2026zedTypeReference<T>() {})");
        return responseEntity;
    }
}

