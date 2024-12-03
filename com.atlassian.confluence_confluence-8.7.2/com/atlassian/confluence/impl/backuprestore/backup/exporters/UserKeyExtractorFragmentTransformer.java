/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.ExportingReference;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.ReferencesFromBodyContentExtractorMarkerV2;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserKeyExtractorFragmentTransformer
implements FragmentTransformer,
ReferencesFromBodyContentExtractorMarkerV2 {
    private static final Logger logger = LoggerFactory.getLogger(UserKeyExtractorFragmentTransformer.class);
    private final Unmarshaller<UserResourceIdentifier> userResourceIdentifierUnmarshaller;
    private final Marshaller<UserResourceIdentifier> userResourceIdentifierMarshaller;
    private final Set<UserKey> mentionedUsers;

    public UserKeyExtractorFragmentTransformer(Unmarshaller<UserResourceIdentifier> userResourceIdentifierUnmarshaller, Marshaller<UserResourceIdentifier> userResourceIdentifierMarshaller) {
        this.userResourceIdentifierUnmarshaller = userResourceIdentifierUnmarshaller;
        this.userResourceIdentifierMarshaller = userResourceIdentifierMarshaller;
        this.mentionedUsers = new HashSet<UserKey>();
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.userResourceIdentifierUnmarshaller.handles(startElementEvent, conversionContext);
    }

    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        UserResourceIdentifier userResourceIdentifier = this.userResourceIdentifierUnmarshaller.unmarshal(reader, mainFragmentTransformer, conversionContext);
        this.mentionedUsers.add(userResourceIdentifier.getUserKey());
        return this.userResourceIdentifierMarshaller.marshal(userResourceIdentifier, conversionContext);
    }

    @Override
    public Collection<ExportingReference> getReferences() {
        logger.debug("There are {} user is/are found in body content", (Object)this.mentionedUsers.size());
        return this.mentionedUsers.stream().filter(Objects::nonNull).map(userKey -> new ExportingReference("userFromBodyContent", ConfluenceUserImpl.class, userKey.getStringValue())).collect(Collectors.toList());
    }

    @Override
    public FragmentTransformer createNewInstance() {
        return new UserKeyExtractorFragmentTransformer(this.userResourceIdentifierUnmarshaller, this.userResourceIdentifierMarshaller);
    }
}

