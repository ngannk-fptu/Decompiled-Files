/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.index.attachment.AttachmentTextExtractor;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.util.io.InputStreamSource;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class DelegatingAttachmentTextExtractor {
    private final Supplier<Stream<AttachmentTextExtractor>> pluginAttachmentTextExtractorsProvider;
    private final BiPredicate<AttachmentTextExtractor, Attachment> shouldExtractPredicate;

    public DelegatingAttachmentTextExtractor(Supplier<Stream<AttachmentTextExtractor>> pluginAttachmentTextExtractorsProvider, BiPredicate<AttachmentTextExtractor, Attachment> shouldExtractPredicate) {
        this.pluginAttachmentTextExtractorsProvider = Objects.requireNonNull(pluginAttachmentTextExtractorsProvider);
        this.shouldExtractPredicate = Objects.requireNonNull(shouldExtractPredicate);
    }

    public Optional<InputStreamSource> extract(Attachment attachment) {
        return this.pluginAttachmentTextExtractorsProvider.get().filter(x -> this.shouldExtractPredicate.test((AttachmentTextExtractor)x, attachment)).findFirst().flatMap(x -> x.extract(attachment));
    }
}

