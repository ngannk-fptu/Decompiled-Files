/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.exceptions;

import com.atlassian.confluence.api.model.content.ContentRepresentation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.StreamSupport;

public class InvalidRepresentationException
extends RuntimeException {
    private final Collection<ContentRepresentation> permittedRepresentations;

    public InvalidRepresentationException(ContentRepresentation unacceptableRepresentation, ContentRepresentation[] permittedRepresentations) {
        super("Unacceptable content representation : " + unacceptableRepresentation + ". Allowed representations: " + Arrays.toString(permittedRepresentations));
        this.permittedRepresentations = Collections.unmodifiableList(Arrays.asList(permittedRepresentations));
    }

    public InvalidRepresentationException(ContentRepresentation representation, Iterable<ContentRepresentation> contentRepresentations) {
        this(representation, InvalidRepresentationException.toArray(contentRepresentations));
    }

    private static ContentRepresentation[] toArray(Iterable<ContentRepresentation> contentRepresentations) {
        return (ContentRepresentation[])StreamSupport.stream(contentRepresentations.spliterator(), false).toArray(ContentRepresentation[]::new);
    }

    public Iterable<ContentRepresentation> getPermittedRepresentations() {
        return this.permittedRepresentations;
    }
}

