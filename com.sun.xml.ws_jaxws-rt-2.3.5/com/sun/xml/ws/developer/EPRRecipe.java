/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.developer;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Header;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Source;

public final class EPRRecipe {
    private final List<Header> referenceParameters = new ArrayList<Header>();
    private final List<Source> metadata = new ArrayList<Source>();

    @NotNull
    public List<Header> getReferenceParameters() {
        return this.referenceParameters;
    }

    @NotNull
    public List<Source> getMetadata() {
        return this.metadata;
    }

    public EPRRecipe addReferenceParameter(Header h) {
        if (h == null) {
            throw new IllegalArgumentException();
        }
        this.referenceParameters.add(h);
        return this;
    }

    public EPRRecipe addReferenceParameters(Header ... headers) {
        for (Header h : headers) {
            this.addReferenceParameter(h);
        }
        return this;
    }

    public EPRRecipe addReferenceParameters(Iterable<? extends Header> headers) {
        for (Header header : headers) {
            this.addReferenceParameter(header);
        }
        return this;
    }

    public EPRRecipe addMetadata(Source source) {
        if (source == null) {
            throw new IllegalArgumentException();
        }
        this.metadata.add(source);
        return this;
    }

    public EPRRecipe addMetadata(Source ... sources) {
        for (Source s : sources) {
            this.addMetadata(s);
        }
        return this;
    }

    public EPRRecipe addMetadata(Iterable<? extends Source> sources) {
        for (Source source : sources) {
            this.addMetadata(source);
        }
        return this;
    }
}

