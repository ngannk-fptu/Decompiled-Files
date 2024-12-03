/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.impl.StorageFormatUserRewriter;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.Operation;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
class XHtmlBodyContentPropertyUserRewriter {
    static final String BODY_PROPERTY_NAME = "body";
    private static final Logger log = LoggerFactory.getLogger(XHtmlBodyContentPropertyUserRewriter.class);
    private final ImportProcessorContext context;
    private final StorageFormatUserRewriter storageFormatUserRewriter;
    private final ImportedObject importedObject;
    private final TransientHibernateHandle bodyContentHandle;
    private final Function<UserKey, UserKey> userKeyTranslator;

    public XHtmlBodyContentPropertyUserRewriter(ImportProcessorContext context, StorageFormatUserRewriter storageFormatUserRewriter, ImportedObject importedObject, TransientHibernateHandle bodyContentHandle) {
        this.context = context;
        this.storageFormatUserRewriter = storageFormatUserRewriter;
        this.importedObject = importedObject;
        this.bodyContentHandle = bodyContentHandle;
        this.userKeyTranslator = userKeyFromContent -> (UserKey)context.getIdMappingFor(TransientHibernateHandle.create(ConfluenceUserImpl.class, (Serializable)userKeyFromContent));
    }

    public boolean canHandle(ImportedProperty importedProperty) {
        return BodyContent.class.equals((Object)this.bodyContentHandle.getClazz()) && BODY_PROPERTY_NAME.equals(importedProperty.getName()) && importedProperty instanceof PrimitiveProperty;
    }

    public PrimitiveProperty translateBodyContentXhtmlProperty(PrimitiveProperty importedBodyContentProperty) {
        Preconditions.checkArgument((boolean)BODY_PROPERTY_NAME.equals(importedBodyContentProperty.getName()));
        try {
            String importedBodyContentXhtml = GeneralUtil.unescapeCDATA(importedBodyContentProperty.getValue());
            StorageFormatUserRewriter.RewriteResult rewriteResult = this.rewriteUserKeysInContent(importedBodyContentXhtml);
            if (rewriteResult.wereAnyUserKeysNotTranslated()) {
                ImmutableMap.Builder deferredOperations = ImmutableMap.builder();
                for (UserKey userKey : rewriteResult.getUntranslatedUserKeys()) {
                    DeferredOperation operation = new DeferredOperation(userKey);
                    deferredOperations.put((Object)TransientHibernateHandle.create(ConfluenceUserImpl.class, (Serializable)userKey), Collections.singleton(operation));
                }
                this.context.deferOperations(this.importedObject.getIdProperty(), (Map<TransientHibernateHandle, Set<Operation>>)deferredOperations.build());
            }
            if (rewriteResult.wereAnyUserKeysTranslated()) {
                return new PrimitiveProperty(importedBodyContentProperty.getName(), importedBodyContentProperty.getType(), rewriteResult.getRewrittenContent());
            }
            return importedBodyContentProperty;
        }
        catch (Exception ex) {
            log.warn("Unable to parse text of BodyContent {} as XHTML storage format: {}", (Object)this.importedObject.getIdPropertyStr(), (Object)ex.getMessage());
            return importedBodyContentProperty;
        }
    }

    private StorageFormatUserRewriter.RewriteResult rewriteUserKeysInContent(String importedBodyContentXhtml) throws XMLStreamException, XhtmlException, IOException {
        return this.storageFormatUserRewriter.transformUserKeysInContent(importedBodyContentXhtml, this.userKeyTranslator, new DefaultConversionContext(new RenderContext()));
    }

    class DeferredOperation
    implements Operation {
        final UserKey userKey;

        public DeferredOperation(UserKey userKey) {
            this.userKey = userKey;
        }

        @Override
        public void execute() throws Exception {
            Serializable bodyContentId = XHtmlBodyContentPropertyUserRewriter.this.context.getIdMappingFor(XHtmlBodyContentPropertyUserRewriter.this.bodyContentHandle);
            BodyContent bodyContent = (BodyContent)XHtmlBodyContentPropertyUserRewriter.this.context.getSession().get(BodyContent.class, bodyContentId);
            Preconditions.checkState((bodyContent != null ? 1 : 0) != 0, (Object)("No BodyContent found in session for ID " + bodyContentId));
            String bodyContentXhtml = bodyContent.getBody();
            StorageFormatUserRewriter.RewriteResult rewriteResult = XHtmlBodyContentPropertyUserRewriter.this.rewriteUserKeysInContent(bodyContentXhtml);
            if (rewriteResult.wasUserKeyTranslated(this.userKey)) {
                bodyContent.setBody(rewriteResult.getRewrittenContent());
            }
        }

        @Override
        public String getDescription() throws Exception {
            return String.format("Translate userkeys in %s", XHtmlBodyContentPropertyUserRewriter.this.bodyContentHandle);
        }
    }
}

