/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.regions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public final class EndpointTag {
    public static final EndpointTag DUALSTACK = EndpointTag.of("dualstack");
    public static final EndpointTag FIPS = EndpointTag.of("fips");
    private static final List<EndpointTag> ENDPOINT_TAGS = Collections.unmodifiableList(Arrays.asList(DUALSTACK, FIPS));
    private final String id;

    private EndpointTag(String id) {
        this.id = id;
    }

    public static EndpointTag of(String id) {
        return EndpointTagCache.put(id);
    }

    public static List<EndpointTag> endpointTags() {
        return ENDPOINT_TAGS;
    }

    public String id() {
        return this.id;
    }

    public String toString() {
        return this.id;
    }

    private static class EndpointTagCache {
        private static final ConcurrentHashMap<String, EndpointTag> IDS = new ConcurrentHashMap();

        private EndpointTagCache() {
        }

        private static EndpointTag put(String id) {
            return IDS.computeIfAbsent(id, x$0 -> new EndpointTag((String)x$0));
        }
    }
}

