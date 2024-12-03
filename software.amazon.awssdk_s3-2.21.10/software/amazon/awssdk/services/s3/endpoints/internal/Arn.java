/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.endpoints.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class Arn {
    private final String partition;
    private final String service;
    private final String region;
    private final String accountId;
    private final List<String> resource;

    public Arn(String partition, String service, String region, String accountId, List<String> resource) {
        this.partition = partition;
        this.service = service;
        this.region = region;
        this.accountId = accountId;
        this.resource = resource;
    }

    public static Optional<Arn> parse(String arn) {
        String[] base = arn.split(":", 6);
        if (base.length != 6) {
            return Optional.empty();
        }
        if (!base[0].equals("arn")) {
            return Optional.empty();
        }
        if (base[1].isEmpty() || base[2].isEmpty()) {
            return Optional.empty();
        }
        if (base[5].isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Arn(base[1], base[2], base[3], base[4], Arrays.stream(base[5].split("[:/]", -1)).collect(Collectors.toList())));
    }

    public String partition() {
        return this.partition;
    }

    public String service() {
        return this.service;
    }

    public String region() {
        return this.region;
    }

    public String accountId() {
        return this.accountId;
    }

    public List<String> resource() {
        return this.resource;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Arn arn = (Arn)o;
        if (this.partition != null ? !this.partition.equals(arn.partition) : arn.partition != null) {
            return false;
        }
        if (this.service != null ? !this.service.equals(arn.service) : arn.service != null) {
            return false;
        }
        if (this.region != null ? !this.region.equals(arn.region) : arn.region != null) {
            return false;
        }
        if (this.accountId != null ? !this.accountId.equals(arn.accountId) : arn.accountId != null) {
            return false;
        }
        return this.resource != null ? this.resource.equals(arn.resource) : arn.resource == null;
    }

    public int hashCode() {
        int result = this.partition != null ? this.partition.hashCode() : 0;
        result = 31 * result + (this.service != null ? this.service.hashCode() : 0);
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        result = 31 * result + (this.accountId != null ? this.accountId.hashCode() : 0);
        result = 31 * result + (this.resource != null ? this.resource.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Arn[partition=" + this.partition + ", service=" + this.service + ", region=" + this.region + ", accountId=" + this.accountId + ", resource=" + this.resource + ']';
    }
}

