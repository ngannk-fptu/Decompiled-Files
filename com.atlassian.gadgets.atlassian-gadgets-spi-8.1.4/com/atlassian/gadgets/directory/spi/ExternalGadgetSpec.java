/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.directory.spi;

import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URI;
import net.jcip.annotations.Immutable;

@Immutable
public final class ExternalGadgetSpec
implements Serializable {
    private static final long serialVersionUID = 8476725773908350812L;
    private final ExternalGadgetSpecId id;
    private final URI specUri;

    public ExternalGadgetSpec(ExternalGadgetSpecId id, URI specUri) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null. specUri = " + specUri);
        }
        if (specUri == null) {
            throw new IllegalArgumentException("specUri cannot be null. id = " + id);
        }
        this.id = id;
        this.specUri = specUri;
    }

    public ExternalGadgetSpecId getId() {
        return this.id;
    }

    public URI getSpecUri() {
        return this.specUri;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (other.getClass() != ExternalGadgetSpec.class) {
            return false;
        }
        ExternalGadgetSpec that = (ExternalGadgetSpec)other;
        return this.id.equals(that.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        if (this.id == null) {
            throw new InvalidObjectException("id cannot be null");
        }
        if (this.specUri == null) {
            throw new InvalidObjectException("specUri cannot be null");
        }
    }
}

