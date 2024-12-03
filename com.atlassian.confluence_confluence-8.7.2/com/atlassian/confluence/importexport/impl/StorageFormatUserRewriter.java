/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.UnmarshalMarshalFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.StorageXhtmlTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.DefaultFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.ThrowExceptionOnFragmentTransformationError;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;

public class StorageFormatUserRewriter {
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XmlOutputFactory xmlOutputFactory;
    private final Unmarshaller<UserResourceIdentifier> userResourceIdentifierUnmarshaller;
    private final Marshaller<UserResourceIdentifier> userResourceIdentifierMarshaller;

    public StorageFormatUserRewriter(XmlEventReaderFactory xmlEventReaderFactory, XmlOutputFactory xmlOutputFactory, Unmarshaller<UserResourceIdentifier> userResourceIdentifierUnmarshaller, Marshaller<UserResourceIdentifier> userResourceIdentifierMarshaller) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = xmlOutputFactory;
        this.userResourceIdentifierUnmarshaller = userResourceIdentifierUnmarshaller;
        this.userResourceIdentifierMarshaller = userResourceIdentifierMarshaller;
    }

    @Deprecated
    public RewriteResult rewriteUserKeysInContent(String importedStorageFormat, Function<UserKey, UserKey> userKeyTranslator, ConversionContext conversionContext) throws XMLStreamException, XhtmlException, IOException {
        UserResourceIdentifierTranslator userResourceIdentifierTranslator = new UserResourceIdentifierTranslator(arg_0 -> userKeyTranslator.apply(arg_0));
        Marshaller<UserResourceIdentifier> translatingMarshaller = this.userKeyTranslatingMarshaller(userResourceIdentifierTranslator);
        UnmarshalMarshalFragmentTransformer<UserResourceIdentifier> userResourceIdentifierTransformer = new UnmarshalMarshalFragmentTransformer<UserResourceIdentifier>(this.userResourceIdentifierUnmarshaller, translatingMarshaller);
        DefaultFragmentTransformer fragmentTransformer = new DefaultFragmentTransformer(Collections.singletonList(userResourceIdentifierTransformer), this.xmlOutputFactory, this.xmlEventReaderFactory, new ThrowExceptionOnFragmentTransformationError(), null);
        StorageXhtmlTransformer xhtmlTransformer = new StorageXhtmlTransformer(this.xmlEventReaderFactory, fragmentTransformer);
        String transformedXml = xhtmlTransformer.transform(new StringReader(importedStorageFormat), conversionContext);
        return new RewriteResult(transformedXml, userResourceIdentifierTranslator.translatedUserKeys);
    }

    public RewriteResult transformUserKeysInContent(String importedStorageFormat, java.util.function.Function<UserKey, UserKey> userKeyTranslator, ConversionContext conversionContext) throws XMLStreamException, XhtmlException, IOException {
        return this.rewriteUserKeysInContent(importedStorageFormat, (Function<UserKey, UserKey>)((Function)userKeyTranslator::apply), conversionContext);
    }

    private Marshaller<UserResourceIdentifier> userKeyTranslatingMarshaller(UserResourceIdentifierTranslator userResourceIdentifierTranslator) {
        return (userResourceIdentifier, conversionContext) -> {
            UserResourceIdentifier translatedUserResourceIdentifier = userResourceIdentifierTranslator.apply((UserResourceIdentifier)userResourceIdentifier);
            return this.userResourceIdentifierMarshaller.marshal(translatedUserResourceIdentifier, conversionContext);
        };
    }

    public static class RewriteResult {
        private final Map<UserKey, UserKey> userKeyTranslations;
        private final String rewrittenContent;

        public RewriteResult(String rewrittenContent, Map<UserKey, UserKey> userKeyTranslations) {
            this.userKeyTranslations = Collections.unmodifiableMap(userKeyTranslations);
            this.rewrittenContent = rewrittenContent;
        }

        public Set<UserKey> getUntranslatedUserKeys() {
            return Maps.filterValues(this.userKeyTranslations, (Predicate)Predicates.isNull()).keySet();
        }

        public boolean wereAnyUserKeysTranslated() {
            return !Maps.filterValues(this.userKeyTranslations, (Predicate)Predicates.notNull()).isEmpty();
        }

        public boolean wereAnyUserKeysNotTranslated() {
            return !Maps.filterValues(this.userKeyTranslations, (Predicate)Predicates.isNull()).isEmpty();
        }

        public Set<UserKey> getOriginalUserKeys() {
            return this.userKeyTranslations.keySet();
        }

        public boolean wasUserKeyTranslated(UserKey userKey) {
            return this.userKeyTranslations.containsKey(userKey) && this.userKeyTranslations.get(userKey) != null;
        }

        public String getRewrittenContent() {
            return this.rewrittenContent;
        }
    }

    private static class UserResourceIdentifierTranslator
    implements java.util.function.Function<UserResourceIdentifier, UserResourceIdentifier> {
        private final java.util.function.Function<UserKey, UserKey> userKeyTranslator;
        final Map<UserKey, UserKey> translatedUserKeys = Maps.newHashMap();

        private UserResourceIdentifierTranslator(java.util.function.Function<UserKey, UserKey> userKeyTranslator) {
            this.userKeyTranslator = userKeyTranslator;
        }

        @Override
        public UserResourceIdentifier apply(UserResourceIdentifier inputResource) {
            if (inputResource.hasUserKey()) {
                UserKey translatedUserKey = this.userKeyTranslator.apply(inputResource.getUserKey());
                this.translatedUserKeys.put(inputResource.getUserKey(), translatedUserKey);
                if (translatedUserKey != null) {
                    return UserResourceIdentifier.create(translatedUserKey);
                }
            }
            return inputResource;
        }
    }
}

