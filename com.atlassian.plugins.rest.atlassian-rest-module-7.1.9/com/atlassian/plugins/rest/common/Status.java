/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.plugins.rest.common;

import com.atlassian.plugins.rest.common.Link;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement
public class Status {
    @XmlElement
    private final Plugin plugin;
    @XmlElement(name="status-code")
    private final Integer code;
    @XmlElement(name="sub-code")
    private final Integer subCode;
    @XmlElement
    private final String message;
    @XmlElement(name="etag")
    private final String eTag;
    @XmlElementWrapper(name="resources-created")
    @XmlElement(name="link")
    private final Collection<Link> resourcesCreated;
    @XmlElementWrapper(name="resources-updated")
    @XmlElement(name="link")
    private final Collection<Link> resourcesUpdated;
    private static final List<Variant> POSSIBLE_VARIANTS = Variant.mediaTypes(MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE).add().build();

    private Status() {
        this.plugin = null;
        this.code = -1;
        this.subCode = -1;
        this.message = null;
        this.eTag = null;
        this.resourcesCreated = null;
        this.resourcesUpdated = null;
    }

    private Status(Plugin plugin, Integer code, Integer subCode, String message, String eTag, Collection<Link> resourcesCreated, Collection<Link> resourcesUpdated) {
        this.plugin = plugin;
        this.code = code;
        this.subCode = subCode;
        this.message = message;
        this.eTag = eTag;
        this.resourcesCreated = resourcesCreated;
        this.resourcesUpdated = resourcesUpdated;
    }

    public static StatusResponseBuilder ok() {
        return new StatusResponseBuilder(Response.Status.OK);
    }

    public static StatusResponseBuilder notFound() {
        return new StatusResponseBuilder(Response.Status.NOT_FOUND);
    }

    public static StatusResponseBuilder error() {
        return new StatusResponseBuilder(Response.Status.INTERNAL_SERVER_ERROR).noCache().noStore();
    }

    public static StatusResponseBuilder badRequest() {
        return new StatusResponseBuilder(Response.Status.BAD_REQUEST).noCache().noStore();
    }

    public static StatusResponseBuilder forbidden() {
        return new StatusResponseBuilder(Response.Status.FORBIDDEN);
    }

    public static StatusResponseBuilder unauthorized() {
        return new StatusResponseBuilder(Response.Status.UNAUTHORIZED);
    }

    public static StatusResponseBuilder created(Link link) {
        return new StatusResponseBuilder(Response.Status.CREATED).created(Objects.requireNonNull(link));
    }

    public static StatusResponseBuilder conflict() {
        return new StatusResponseBuilder(Response.Status.CONFLICT);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public int getCode() {
        return this.code;
    }

    public int getSubCode() {
        return this.subCode;
    }

    public String getMessage() {
        return this.message;
    }

    public String getETag() {
        return this.eTag;
    }

    public Collection<Link> getResourcesCreated() {
        return Collections.unmodifiableCollection(this.resourcesCreated);
    }

    public Collection<Link> getResourcesUpdated() {
        return Collections.unmodifiableCollection(this.resourcesUpdated);
    }

    public static MediaType variantFor(Request request) {
        Variant v = request.selectVariant(POSSIBLE_VARIANTS);
        if (v == null) {
            v = POSSIBLE_VARIANTS.get(0);
        }
        return v.getMediaType();
    }

    public static class StatusResponseBuilder {
        private final CacheControl cacheControl;
        private final Response.Status status;
        private String eTag;
        private Plugin plugin;
        private String message;
        private List<Link> created;
        private List<Link> updated;

        private StatusResponseBuilder(Response.Status status) {
            this(status, new CacheControl());
        }

        private StatusResponseBuilder(Response.Status status, CacheControl cacheControl) {
            this.status = Objects.requireNonNull(status, "status can't be null");
            this.cacheControl = Objects.requireNonNull(cacheControl, "cacheControl can't be null");
        }

        public StatusResponseBuilder plugin(String key, String version) {
            this.plugin = new Plugin(key, version);
            return this;
        }

        public StatusResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public StatusResponseBuilder tag(String eTag) {
            this.eTag = eTag;
            return this;
        }

        public StatusResponseBuilder noCache() {
            this.cacheControl.setNoCache(true);
            return this;
        }

        public StatusResponseBuilder noStore() {
            this.cacheControl.setNoStore(true);
            return this;
        }

        public Status build() {
            return new Status(this.plugin, this.status.getStatusCode(), null, this.message, this.eTag, this.created, this.updated);
        }

        public Response response() {
            return this.responseBuilder().build();
        }

        public Response.ResponseBuilder responseBuilder() {
            Response.ResponseBuilder builder = Response.status(this.status).cacheControl(this.cacheControl).tag(this.eTag).entity(this.build()).type("application/xml");
            List<Link> c = this.getCreated();
            List<Link> u = this.getUpdated();
            if (c.size() == 1 && u.isEmpty()) {
                builder.location(c.get(0).getHref());
            } else if (u.size() == 1 && c.isEmpty()) {
                builder.location(u.get(0).getHref());
            }
            return builder;
        }

        public StatusResponseBuilder created(Link link) {
            this.getCreated().add(link);
            return this;
        }

        public StatusResponseBuilder updated(Link link) {
            this.getUpdated().add(link);
            return this;
        }

        private List<Link> getCreated() {
            if (this.created == null) {
                this.created = new LinkedList<Link>();
            }
            return this.created;
        }

        private List<Link> getUpdated() {
            if (this.updated == null) {
                this.updated = new LinkedList<Link>();
            }
            return this.updated;
        }
    }

    @XmlRootElement
    public static class Plugin {
        @XmlAttribute
        private final String key;
        @XmlAttribute
        private final String version;

        private Plugin() {
            this.key = null;
            this.version = null;
        }

        public Plugin(String key, String version) {
            this.key = Objects.requireNonNull(key, "key can't be null");
            this.version = Objects.requireNonNull(version, "version can't be null");
        }

        public String getKey() {
            return this.key;
        }

        public String getVersion() {
            return this.version;
        }

        public int hashCode() {
            return new HashCodeBuilder(3, 5).append((Object)this.key).append((Object)this.version).toHashCode();
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != this.getClass()) {
                return false;
            }
            Plugin plugin = (Plugin)obj;
            return new EqualsBuilder().append((Object)this.key, (Object)plugin.key).append((Object)this.version, (Object)plugin.version).isEquals();
        }
    }
}

