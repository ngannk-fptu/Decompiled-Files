/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter
 *  com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.definition.MacroBody
 *  com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody
 *  com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody
 *  com.atlassian.confluence.content.render.xhtml.editor.macro.InvalidMacroParameterException
 *  com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser
 *  com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroBodyParser
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.browser.MacroMetadataManager
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadata
 *  com.atlassian.confluence.macro.xhtml.MacroManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.confluence.xhtml.api.EditorFormatService
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.atlassian.velocity.htmlsafe.HtmlFragment
 *  com.google.common.base.Preconditions
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  io.atlassian.util.concurrent.Timeout
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.tinymceplugin.rest;

import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.editor.macro.InvalidMacroParameterException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroBodyParser;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.tinymceplugin.rest.entities.Macro;
import com.atlassian.confluence.tinymceplugin.rest.entities.MacroRenderRequest;
import com.atlassian.confluence.tinymceplugin.rest.entities.PreviewMacroRequest;
import com.atlassian.confluence.tinymceplugin.rest.entities.UnmarshalMacroRequest;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.atlassian.velocity.htmlsafe.HtmlFragment;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.UncheckedExecutionException;
import io.atlassian.util.concurrent.Timeout;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/macro")
@AnonymousAllowed
public class MacroResource {
    private static final Logger log = LoggerFactory.getLogger(MacroResource.class);
    private static final long MACRO_PLACEHOLDER_TIMEOUT_DEFAULT = TimeUnit.MILLISECONDS.convert(5L, TimeUnit.SECONDS);
    private static final String MACRO_PLACEHOLDER_TIMEOUT = "confluence.macro.placeholder.timeoutMillis";
    private static final long MACRO_DEFINITION_TIMEOUT_DEFAULT = TimeUnit.MILLISECONDS.convert(5L, TimeUnit.SECONDS);
    private static final String MACRO_DEFINITION_TIMEOUT = "confluence.macro.definition.timeoutMillis";
    private static final long MACRO_PREVIEW_TIMEOUT_DEFAULT = TimeUnit.MILLISECONDS.convert(1L, TimeUnit.HOURS);
    private static final String MACRO_PREVIEW_TIMEOUT = "confluence.macro.preview.timeoutMillis";
    private final XhtmlContent xhtmlContent;
    private final EditorFormatService editorFormatService;
    private final ContentEntityManager contentEntityManager;
    private final HtmlToXmlConverter htmlToXmlConverter;
    private final HttpContext httpContext;
    private final TransactionTemplate transactionTemplate;
    private final MacroManager macroManager;
    private final I18nResolver i18nResolver;
    private final PermissionManager permissionManager;
    private final SpacePermissionManager spacePermissionManager;
    private final MacroParameterTypeParser macroParameterTypeParser;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final FragmentTransformer defaultFragmentTransformer;
    private final StorageMacroBodyParser storageMacroBodyParser;
    private final MacroMetadataManager macroMetadataManager;
    private final StorageFormatCleaner storageFormatCleaner;
    private final VelocityHelperService velocityHelperService;

    public MacroResource(@ComponentImport XhtmlContent xhtmlContent, @ComponentImport EditorFormatService editorFormatService, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport @Qualifier(value="htmlToXmlConverter") HtmlToXmlConverter htmlToXmlConverter, @ComponentImport @Qualifier(value="httpContext") HttpContext httpContext, @ComponentImport(value="xhtmlMacroManager") @Qualifier(value="xhtmlMacroManager") MacroManager macroManager, @ComponentImport I18nResolver i18nResolver, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport PermissionManager permissionManager, @ComponentImport @Qualifier(value="spacePermissionManager") SpacePermissionManager spacePermissionManager, @ComponentImport MacroParameterTypeParser macroParameterTypeParser, @ComponentImport XmlEventReaderFactory xmlEventReaderFactory, @ComponentImport FragmentTransformer defaultFragmentTransformer, @ComponentImport StorageMacroBodyParser storageMacroBodyParser, @ComponentImport MacroMetadataManager macroMetadataManager, @ComponentImport StorageFormatCleaner storageFormatCleaner, @ComponentImport VelocityHelperService velocityHelperService) {
        this.xhtmlContent = xhtmlContent;
        this.editorFormatService = editorFormatService;
        this.contentEntityManager = contentEntityManager;
        this.htmlToXmlConverter = htmlToXmlConverter;
        this.httpContext = httpContext;
        this.macroManager = macroManager;
        this.i18nResolver = i18nResolver;
        this.transactionTemplate = transactionTemplate;
        this.permissionManager = permissionManager;
        this.spacePermissionManager = spacePermissionManager;
        this.macroParameterTypeParser = macroParameterTypeParser;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.defaultFragmentTransformer = defaultFragmentTransformer;
        this.storageMacroBodyParser = storageMacroBodyParser;
        this.macroMetadataManager = macroMetadataManager;
        this.storageFormatCleaner = storageFormatCleaner;
        this.velocityHelperService = velocityHelperService;
    }

    @POST
    @Path(value="/placeholder")
    @Consumes(value={"application/json"})
    @Produces(value={"text/plain"})
    public Response generatePlaceHolder(MacroRenderRequest renderRequest) {
        log.debug("Macro placeholder request for {} received", (Object)renderRequest.getMacro().getName());
        try {
            this.checkUsePermission(AuthenticatedUserThreadLocal.get());
            Long contentId = renderRequest.getContentId();
            Macro macro = renderRequest.getMacro();
            String macroName = renderRequest.getMacro().getName();
            com.atlassian.confluence.macro.Macro realMacro = this.macroManager.getMacroByName(macroName);
            if (realMacro == null) {
                throw new RuntimeException("The macro " + macroName + " is not available. Perhaps it has been disabled or removed.");
            }
            MacroBody macroBody = MacroResource.getMacroBody(macro, realMacro);
            Map<String, String> macroParameters = macro.getParams();
            MacroDefinition macroDefinition = MacroDefinition.builder((String)macro.getName()).withMacroBody(macroBody).withParameters(macroParameters).withStorageVersion("1").withSchemaVersion(this.getSchemaVersion(macro)).build();
            macroDefinition.setDefaultParameterValue(macro.getDefaultParameterValue());
            String entity = this.editorFormatService.convertMacroDefinitionToEdit(macroDefinition, this.getConversionContext(contentId, false, MACRO_PLACEHOLDER_TIMEOUT, MACRO_PLACEHOLDER_TIMEOUT_DEFAULT));
            log.debug("Macro placeholder render complete");
            return Response.ok((Object)entity).build();
        }
        catch (XhtmlException e) {
            throw new RuntimeException(e);
        }
        catch (PermissionException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
    }

    private void checkUsePermission(ConfluenceUser remoteUser) {
        if (!this.spacePermissionManager.hasPermission("USECONFLUENCE", null, (User)remoteUser)) {
            throw new PermissionException();
        }
    }

    private static @Nullable MacroBody getMacroBody(Macro macroEntity, com.atlassian.confluence.macro.Macro realMacro) {
        if (realMacro.getBodyType() == Macro.BodyType.RICH_TEXT) {
            return RichTextMacroBody.withStorage((Streamable)Streamables.from((String)macroEntity.getBody()));
        }
        if (realMacro.getBodyType() == Macro.BodyType.PLAIN_TEXT) {
            return new PlainTextMacroBody(macroEntity.getBody());
        }
        return null;
    }

    private int getSchemaVersion(Macro macro) {
        int schemaVersion = macro.getSchemaVersion();
        if (schemaVersion == 0) {
            MacroMetadata metadata = this.macroMetadataManager.getMacroMetadataByName(macro.getName());
            Preconditions.checkState((metadata != null ? 1 : 0) != 0, (Object)("No macro metadata found for macro '" + macro.getName() + "; cannot determine macro schema version"));
            return metadata.getFormDetails().getSchemaVersion();
        }
        return schemaVersion;
    }

    @POST
    @Path(value="/definition")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response generateDefinition(UnmarshalMacroRequest unmarshalMacroRequest) {
        Long contentId = unmarshalMacroRequest.getContentId();
        try {
            this.checkUsePermission(AuthenticatedUserThreadLocal.get());
            String macroXhtml = this.htmlToXmlConverter.convert(unmarshalMacroRequest.getMacroHtml());
            MacroDefinition macroDefinition = this.editorFormatService.convertEditToMacroDefinition(macroXhtml, this.getConversionContext(contentId, false, MACRO_DEFINITION_TIMEOUT, MACRO_DEFINITION_TIMEOUT_DEFAULT));
            Macro macro = MacroResource.buildMacroEntity(macroDefinition);
            return Response.ok((Object)macro).build();
        }
        catch (XhtmlException | XMLStreamException e) {
            throw new RuntimeException(e);
        }
        catch (PermissionException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
    }

    private static Macro buildMacroEntity(MacroDefinition macroDefinition) {
        Macro macro = new Macro();
        macro.setName(macroDefinition.getName());
        macro.setSchemaVersion(macroDefinition.getSchemaVersion());
        macro.setParams(macroDefinition.getParameters());
        macro.setDefaultParameterValue(macroDefinition.getDefaultParameterValue());
        macro.setBody(macroDefinition.getBodyText());
        return macro;
    }

    @POST
    @Path(value="/preview")
    @Consumes(value={"application/json"})
    @Produces(value={"text/plain"})
    public Response generatePreview(PreviewMacroRequest previewMacroRequest) {
        try {
            this.checkUsePermission(AuthenticatedUserThreadLocal.get());
            String macroName = previewMacroRequest.getMacro().getName();
            com.atlassian.confluence.macro.Macro realMacro = this.macroManager.getMacroByName(macroName);
            String macroPreview = realMacro == null ? this.i18nResolver.getText("tinymce.macro.unknownMacro", new Serializable[]{HtmlUtil.htmlEncode((String)macroName)}) : (String)this.transactionTemplate.execute(() -> {
                try {
                    Macro macro = previewMacroRequest.getMacro();
                    ConversionContext context = this.getConversionContext(previewMacroRequest.getContentId(), true, MACRO_PREVIEW_TIMEOUT, MACRO_PREVIEW_TIMEOUT_DEFAULT);
                    Object macroBody = realMacro.getBodyType() == Macro.BodyType.RICH_TEXT ? this.getRichTextMacroBody(macro, context) : (realMacro.getBodyType() == Macro.BodyType.PLAIN_TEXT ? new PlainTextMacroBody(macro.getBody()) : null);
                    Map typedParameters = this.macroParameterTypeParser.parseMacroParameters(macro.getName(), macro.getParams(), context);
                    MacroDefinition definition = MacroDefinition.builder((String)macro.getName()).withMacroBody(macroBody).withParameters(macro.getParams()).withTypedParameters(typedParameters).withStorageVersion("2").withSchemaVersion(this.getSchemaVersion(macro)).build();
                    definition.setDefaultParameterValue(macro.getDefaultParameterValue());
                    if (macro.getDefaultParameterValue() != null) {
                        definition.setTypedParameter("", this.macroParameterTypeParser.parseMacroParameter(macro.getName(), "", macro.getDefaultParameterValue(), macro.getParams(), context));
                    }
                    return this.xhtmlContent.convertMacroDefinitionToView(definition, context);
                }
                catch (XhtmlException | InvalidMacroParameterException | XMLStreamException e) {
                    return this.i18nResolver.getText("tinymce.macro.preview.exception", new Serializable[]{HtmlUtil.htmlEncode((String)e.getMessage())});
                }
                catch (PermissionException e) {
                    throw new UncheckedExecutionException((Throwable)e);
                }
            });
            Map context = this.velocityHelperService.createDefaultVelocityContext();
            context.put("macroPreview", new HtmlFragment((Object)StringUtils.defaultString((String)macroPreview)));
            context.put("req", this.httpContext.getRequest());
            String output = this.velocityHelperService.getRenderedTemplateWithoutSwallowingErrors("content/render/xhtml/preview-macro-template.vm", context);
            return Response.ok((Object)output).build();
        }
        catch (UncheckedExecutionException e) {
            if (e.getCause() instanceof PermissionException) {
                return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
            }
            throw e;
        }
        catch (PermissionException p) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MacroBody getRichTextMacroBody(Macro macro, ConversionContext context) throws XMLStreamException, XhtmlException {
        return this.storageMacroBodyParser.getMacroBody(macro.getName(), this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader(this.storageFormatCleaner.cleanQuietly(macro.getBody()))), context, this.defaultFragmentTransformer);
    }

    private ConversionContext getConversionContext(Long contentId, boolean isPreview, String timeoutProperty, long timeoutDefault) throws PermissionException {
        PageContext ctx;
        ContentEntityObject contentEntityObject = null;
        if (contentId != null) {
            contentEntityObject = this.contentEntityManager.getById(contentId.longValue());
        }
        long timeoutMillis = Long.getLong(timeoutProperty, timeoutDefault);
        if (contentEntityObject == null) {
            ctx = PageContext.newContextWithTimeout(null, (Timeout)Timeout.getMillisTimeout((long)timeoutMillis, (TimeUnit)TimeUnit.MILLISECONDS));
        } else {
            if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)contentEntityObject)) {
                throw new PermissionException("User " + AuthenticatedUserThreadLocal.get() + " does not have permission to view content with id=" + contentId);
            }
            ctx = PageContext.newContextWithTimeout((ContentEntityObject)contentEntityObject, (Timeout)Timeout.getMillisTimeout((long)timeoutMillis, (TimeUnit)TimeUnit.MILLISECONDS));
        }
        if (isPreview) {
            ctx.setOutputType(ConversionContextOutputType.PREVIEW.value());
        }
        return new DefaultConversionContext((RenderContext)ctx);
    }
}

