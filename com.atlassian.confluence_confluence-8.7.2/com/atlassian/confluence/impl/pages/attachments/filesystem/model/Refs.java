/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem.model;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.spaces.Space;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;
import java.util.Optional;

public final class Refs {
    public static AttachmentRef ref(final Attachment attachment) {
        return new AttachmentRef(){

            @Override
            public long getId() {
                return attachment.getLatestVersionId();
            }

            @Override
            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="A null container is an invalid state, we want to throw an exception in this case")
            public AttachmentRef.Container getContainer() {
                return Refs.ref(Objects.requireNonNull(attachment.getContainer()));
            }

            @Override
            public int getVersion() {
                return attachment.getVersion();
            }

            public String toString() {
                return attachment.toString();
            }
        };
    }

    public static AttachmentRef.Space ref(final Space space) {
        return new AttachmentRef.Space(){

            @Override
            public long getId() {
                return space.getId();
            }

            public String toString() {
                return space.toString();
            }
        };
    }

    public static AttachmentRef.Container ref(final ContentEntityObject attachmentContainer) {
        return new AttachmentRef.Container(){

            @Override
            public long getId() {
                return attachmentContainer.getLatestVersionId();
            }

            @Override
            public Optional<AttachmentRef.Space> getSpace() {
                return Optional.of(attachmentContainer).filter(SpaceContentEntityObject.class::isInstance).map(SpaceContentEntityObject.class::cast).map(SpaceContentEntityObject::getSpace).map(Refs::ref);
            }

            public String toString() {
                return attachmentContainer.toString();
            }
        };
    }

    private Refs() {
    }
}

