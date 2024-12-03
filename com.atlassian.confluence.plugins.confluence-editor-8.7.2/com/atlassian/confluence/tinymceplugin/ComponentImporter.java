/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.admin.criteria.MailServerExistsCriteria
 *  com.atlassian.confluence.api.service.content.ContentPropertyService
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser
 *  com.atlassian.confluence.content.render.xhtml.storage.macro.MacroBodyTransformationCondition
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.confluence.core.HeartbeatManager
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.macro.MacroDefinitionDeserializer
 *  com.atlassian.confluence.macro.browser.MacroIconManager
 *  com.atlassian.confluence.macro.browser.MacroMetadataManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.CaptchaManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.tinymceplugin;

import com.atlassian.confluence.admin.criteria.MailServerExistsCriteria;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser;
import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroBodyTransformationCondition;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.core.HeartbeatManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.macro.MacroDefinitionDeserializer;
import com.atlassian.confluence.macro.browser.MacroIconManager;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class ComponentImporter {
    @ComponentImport
    private TransactionTemplate transactionTemplate;
    @ComponentImport
    private I18nResolver i18nResolver;
    @ComponentImport
    private XhtmlContent xhtmlContent;
    @ComponentImport
    private MailServerExistsCriteria mailServerExistsCriteria;
    @ComponentImport
    private MacroBodyTransformationCondition macroBodyTransformationCondition;
    @ComponentImport
    private XmlEventReaderFactory xmlEventReaderFactory;
    @ComponentImport
    private FragmentTransformer fragmentTransformer;
    @ComponentImport
    private XmlOutputFactory xmlOutputFactory;
    @ComponentImport
    private MacroParameterTypeParser macroParameterTypeParser;
    @ComponentImport
    private EventPublisher eventPublisher;
    @ComponentImport
    private ContentPropertyService contentPropertyService;
    @ComponentImport
    private VelocityHelperService velocityHelperService;
    @ComponentImport
    private WebInterfaceManager webInterfaceManager;
    @ComponentImport
    private CaptchaManager captchaManager;
    @ComponentImport
    private GlobalSettingsManager settingsManager;
    @ComponentImport
    private PermissionManager permissionManager;
    @ComponentImport
    private UserAccessor userAccessor;
    @ComponentImport
    private HeartbeatManager heartbeatManager;
    @ComponentImport
    private LicenseService licenseService;
    @ComponentImport
    private MacroIconManager macroIconManager;
    @ComponentImport(value="macroDefinitionRequestDeserializer")
    private MacroDefinitionDeserializer macroDefinitionDeserializer;
    @ComponentImport
    private MacroMetadataManager macroMetadataManager;
}

