/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.validation.ValidationError
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.api.impl.model.validation.CoreValidationResultFactory;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.RenderingEventPublisher;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlParsingException;
import com.atlassian.confluence.content.render.xhtml.editor.EditorConverter;
import com.atlassian.confluence.content.render.xhtml.editor.embed.CannotUnmarshalEmbeddedResourceException;
import com.atlassian.confluence.content.render.xhtml.editor.link.CannotUnmarshalLinkException;
import com.atlassian.confluence.content.render.xhtml.view.RenderResult;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.macro.count.DefaultMacroCounter;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import com.atlassian.renderer.RenderContext;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFormatConverter
implements FormatConverter {
    private static final Logger log = LoggerFactory.getLogger(DefaultFormatConverter.class);
    protected EditorConverter editConverter;
    protected Renderer editRenderer;
    protected Renderer viewRenderer;
    protected StorageFormatCleaner storageFormatCleaner;
    protected RenderingEventPublisher renderingEventPublisher;

    @Deprecated
    public DefaultFormatConverter(EditorConverter editConverter, Renderer editRenderer, Renderer viewRenderer, StorageFormatCleaner storageFormatCleaner) {
        this(editConverter, editRenderer, viewRenderer, storageFormatCleaner, null);
    }

    public DefaultFormatConverter(EditorConverter editConverter, Renderer editRenderer, Renderer viewRenderer, StorageFormatCleaner storageFormatCleaner, RenderingEventPublisher renderingEventPublisher) {
        this.editConverter = editConverter;
        this.editRenderer = editRenderer;
        this.viewRenderer = viewRenderer;
        this.storageFormatCleaner = storageFormatCleaner;
        this.renderingEventPublisher = renderingEventPublisher;
    }

    @Override
    public String validateAndConvertToStorageFormat(ConfluenceActionSupport action, String wysiwygContent, RenderContext renderContext) {
        try {
            return this.validateAndConvertToStorageFormat(wysiwygContent, renderContext);
        }
        catch (BadRequestException e) {
            Optional validationResult = e.optionalValidationResult();
            if (validationResult.isPresent()) {
                for (com.atlassian.confluence.api.model.validation.ValidationError error : ((ValidationResult)validationResult.get()).getErrors()) {
                    Message message = error.getMessage();
                    action.addActionError(message.getKey(), message.getArgs());
                }
            }
            return "";
        }
    }

    @Override
    public String validateAndConvertToStorageFormat(String wysiwygContent, RenderContext renderContext) throws BadRequestException {
        String storageFormat = "";
        try {
            storageFormat = this.convertToStorageFormat(wysiwygContent, renderContext);
        }
        catch (CannotUnmarshalLinkException ex) {
            if (ex.getResourceIdentifier() == null && ex.getLinkAlias() == null) {
                throw this.badRequest("xhtml.transformer.cannot.unmarshal.link", new Object[0]);
            }
            if (ex.getResourceIdentifier() != null && ex.getLinkAlias() != null) {
                throw this.badRequest("xhtml.transformer.cannot.unmarshal.link.withNameAndRi", ex.getLinkAlias(), ex.getResourceIdentifier().toString());
            }
            if (ex.getResourceIdentifier() != null) {
                throw this.badRequest("xhtml.transformer.cannot.unmarshal.link.withResourceIdentifier", ex.getResourceIdentifier().toString());
            }
            if (ex.getLinkAlias() != null) {
                throw this.badRequest("xhtml.transformer.cannot.unmarshal.link.withName", ex.getLinkAlias());
            }
        }
        catch (CannotUnmarshalEmbeddedResourceException ex) {
            if (ex.getResourceIdentifier() == null && ex.getTitle() == null) {
                throw this.badRequest("xhtml.transformer.cannot.unmarshal.image", new Object[0]);
            }
            if (ex.getResourceIdentifier() != null && ex.getTitle() != null) {
                throw this.badRequest("xhtml.transformer.cannot.unmarshal.image.withNameAndRi", ex.getTitle(), ex.getResourceIdentifier().toString());
            }
            if (ex.getResourceIdentifier() != null) {
                throw this.badRequest("xhtml.transformer.cannot.unmarshal.image.withResourceIdentifier", ex.getResourceIdentifier().toString());
            }
            if (ex.getTitle() != null) {
                throw this.badRequest("xhtml.transformer.cannot.unmarshal.image.withName", ex.getTitle());
            }
        }
        catch (XhtmlParsingException ex) {
            throw this.badRequest("xhtml.editor.parse.failed", String.valueOf(ex.getLineNumber()), String.valueOf(ex.getColumnNumber()), PlainTextToHtmlConverter.encodeHtmlEntities(ex.getParserMessage()));
        }
        catch (XhtmlException ex) {
            Object message = "XhtmlException converting editor format to storage format.";
            if (!log.isDebugEnabled()) {
                message = (String)message + " Turn on debug level logging to see editor format data.";
            }
            log.warn((String)message, (Throwable)ex);
            log.debug("The editor data that could not be converted\n: {}", (Object)wysiwygContent);
            throw this.badRequest("xhtml.editor.to.storage.generic.error", new Object[0]);
        }
        return storageFormat;
    }

    private BadRequestException badRequest(String key, Object ... args) {
        ValidationError error = new ValidationError(key, args);
        ArrayList errors = Lists.newArrayList((Object[])new ValidationError[]{error});
        ValidationResult result = CoreValidationResultFactory.create(true, errors);
        return new BadRequestException("Validation failure when converting format", result);
    }

    @Override
    public String convertToStorageFormat(String wysiwygContent, RenderContext renderContext) throws XhtmlException {
        ConversionContext conversionContext = this.getConversionContext(renderContext);
        DefaultMacroCounter macroCounter = new DefaultMacroCounter();
        conversionContext.setProperty("macroCounter", macroCounter);
        String storage = this.editConverter.convert(wysiwygContent, conversionContext);
        if (this.renderingEventPublisher != null) {
            this.renderingEventPublisher.publish(this, conversionContext);
        }
        return this.storageFormatCleaner.cleanQuietly(storage);
    }

    @Override
    public String convertToEditorFormat(String storageFormat, RenderContext renderContext) {
        return this.editRenderer.render(storageFormat, this.getConversionContext(renderContext));
    }

    @Override
    public RenderResult convertToEditorFormatWithResult(String storageFormat, RenderContext renderContext) {
        return this.editRenderer.renderWithResult(storageFormat, this.getConversionContext(renderContext));
    }

    @Override
    public String convertToViewFormat(String storageFormat, RenderContext renderContext) {
        return this.viewRenderer.render(storageFormat, this.getConversionContext(renderContext));
    }

    @Override
    public String cleanEditorFormat(String wysiwygContent, RenderContext renderContext) throws XhtmlException {
        ConversionContext conversionContext = this.getConversionContext(renderContext);
        return this.editRenderer.render(this.storageFormatCleaner.cleanQuietly(this.editConverter.convert(wysiwygContent, conversionContext)), conversionContext);
    }

    @Override
    public String cleanStorageFormat(String storageFormat) {
        return this.storageFormatCleaner.cleanQuietly(storageFormat);
    }

    private ConversionContext getConversionContext(RenderContext renderContext) {
        return new DefaultConversionContext(renderContext);
    }
}

