/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.Assertions
 *  net.jcip.annotations.Immutable
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.DashboardItemState;
import com.atlassian.gadgets.DashboardItemStateVisitor;
import com.atlassian.gadgets.GadgetId;
import com.atlassian.gadgets.dashboard.Color;
import com.atlassian.plugin.util.Assertions;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Immutable
public class GadgetState
implements DashboardItemState,
Serializable {
    private static final long serialVersionUID = 9016360397733397422L;
    private final GadgetId id;
    private final URI specUri;
    private final Color color;
    private Map<String, String> userPrefs;

    private GadgetState(Builder builder) {
        this.id = builder.id;
        this.specUri = builder.specUri;
        this.color = builder.color;
        this.userPrefs = Collections.unmodifiableMap(new HashMap(builder.userPrefs));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.userPrefs = Collections.unmodifiableMap(new HashMap<String, String>(this.userPrefs));
        if (this.id == null) {
            throw new InvalidObjectException("id cannot be null");
        }
        if (this.specUri == null) {
            throw new InvalidObjectException("specUrl cannot be null");
        }
        if (this.color == null) {
            throw new InvalidObjectException("color cannot be null");
        }
    }

    @Override
    public GadgetId getId() {
        return this.id;
    }

    public URI getGadgetSpecUri() {
        return this.specUri;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    public Map<String, String> getUserPrefs() {
        return this.userPrefs;
    }

    @Override
    public <V> V accept(DashboardItemStateVisitor<V> visitor) {
        return visitor.visit(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GadgetState)) {
            return false;
        }
        GadgetState rhs = (GadgetState)o;
        return new EqualsBuilder().append((Object)this.getId(), (Object)rhs.getId()).append((Object)this.getGadgetSpecUri(), (Object)rhs.getGadgetSpecUri()).append((Object)this.getColor(), (Object)rhs.getColor()).append(this.getUserPrefs(), rhs.getUserPrefs()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getId()).append((Object)this.getGadgetSpecUri()).append((Object)this.getColor()).append(this.getUserPrefs()).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.getId()).append("gadgetSpecUri", (Object)this.getGadgetSpecUri()).append("color", (Object)this.getColor()).append("userPrefs", this.getUserPrefs()).toString();
    }

    public static Builder gadget(GadgetState state) {
        return new Builder(state);
    }

    public static SpecUriBuilder gadget(GadgetId gadgetId) {
        return new SpecUriBuilder((GadgetId)Assertions.notNull((String)"gadgetId", (Object)gadgetId));
    }

    public static class Builder {
        private final GadgetId id;
        private final URI specUri;
        private Color color = Color.defaultColor();
        private Map<String, String> userPrefs = Collections.emptyMap();

        private Builder(GadgetId id, URI specUri) {
            this.id = id;
            this.specUri = specUri;
        }

        public Builder(GadgetState state) {
            Assertions.notNull((String)"state", (Object)state);
            this.id = state.getId();
            this.specUri = state.getGadgetSpecUri();
            this.color = state.getColor();
            this.userPrefs = state.getUserPrefs();
        }

        public Builder color(Color color) {
            this.color = (Color)((Object)Assertions.notNull((String)"color", (Object)((Object)color)));
            return this;
        }

        public Builder userPrefs(Map<String, String> userPrefs) {
            this.userPrefs = (Map)Assertions.notNull((String)"userPrefs", userPrefs);
            return this;
        }

        public GadgetState build() {
            return new GadgetState(this);
        }
    }

    public static class SpecUriBuilder {
        private final GadgetId gadgetId;

        private SpecUriBuilder(GadgetId gadgetId) {
            this.gadgetId = gadgetId;
        }

        public Builder specUri(String specUri) throws URISyntaxException {
            return this.specUri(new URI((String)Assertions.notNull((String)"specUri", (Object)specUri)));
        }

        public Builder specUri(URI specUri) {
            return new Builder(this.gadgetId, (URI)Assertions.notNull((String)"specUri", (Object)specUri));
        }
    }
}

