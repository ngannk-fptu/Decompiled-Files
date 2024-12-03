/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.zdu.rest.dto;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Schema(description="HATEOAS link")
@ParametersAreNonnullByDefault
public class Link {
    @JsonProperty
    @Schema(description="rel")
    private String rel;
    @JsonProperty
    @Schema(description="uri")
    private URI href;

    @JsonCreator
    public Link(@JsonProperty(value="rel") String rel, @JsonProperty(value="href") URI href) {
        this.rel = rel;
        this.href = href;
    }

    public String getRel() {
        return this.rel;
    }

    public URI getHref() {
        return this.href;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Link link = (Link)o;
        return Objects.equal((Object)this.rel, (Object)link.rel) && Objects.equal((Object)this.href, (Object)link.href);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.rel, this.href});
    }
}

