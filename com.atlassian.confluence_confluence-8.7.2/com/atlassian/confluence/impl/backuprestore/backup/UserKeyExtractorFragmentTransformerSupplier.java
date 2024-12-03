/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.UserKeyExtractorFragmentTransformer;
import java.util.function.Supplier;

public class UserKeyExtractorFragmentTransformerSupplier
implements Supplier<FragmentTransformer> {
    private final Unmarshaller<UserResourceIdentifier> userResourceIdentifierUnmarshaller;
    private final Marshaller<UserResourceIdentifier> userResourceIdentifierMarshaller;

    public UserKeyExtractorFragmentTransformerSupplier(Unmarshaller<UserResourceIdentifier> userResourceIdentifierUnmarshaller, Marshaller<UserResourceIdentifier> userResourceIdentifierMarshaller) {
        this.userResourceIdentifierUnmarshaller = userResourceIdentifierUnmarshaller;
        this.userResourceIdentifierMarshaller = userResourceIdentifierMarshaller;
    }

    @Override
    public UserKeyExtractorFragmentTransformer get() {
        return new UserKeyExtractorFragmentTransformer(this.userResourceIdentifierUnmarshaller, this.userResourceIdentifierMarshaller);
    }
}

