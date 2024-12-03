/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.permissions.Operation
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.permissions.Target
 *  com.atlassian.confluence.api.model.permissions.TargetType
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.AttachmentService
 *  com.atlassian.confluence.api.service.exceptions.InternalServerException
 *  com.atlassian.confluence.api.service.permissions.OperationService
 *  com.atlassian.confluence.api.service.settings.SettingsService
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.jwt.core.TimeUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  org.apache.http.client.utils.URIBuilder
 *  org.apache.http.message.BasicNameValuePair
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.previews.service;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.permissions.Operation;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.permissions.TargetType;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.AttachmentService;
import com.atlassian.confluence.api.service.exceptions.InternalServerException;
import com.atlassian.confluence.api.service.permissions.OperationService;
import com.atlassian.confluence.api.service.settings.SettingsService;
import com.atlassian.confluence.plugins.previews.jwt.JwtTokenGenerator;
import com.atlassian.confluence.plugins.previews.model.CompanionAttachmentModel;
import com.atlassian.confluence.plugins.previews.model.CompanionLinkModel;
import com.atlassian.confluence.plugins.previews.model.TempLinksModel;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.jwt.core.TimeUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TempLinksService {
    public static final String JWT_SUBJECT = "jwt.subject.confluence-previews.templinksresource";
    private final JwtTokenGenerator jwtTokenGenerator;
    private final AttachmentService attachmentService;
    private final UserManager userManager;
    private final SettingsService settingsService;
    private final OperationService operationService;

    @Autowired
    public TempLinksService(JwtTokenGenerator jwtTokenGenerator, @ComponentImport AttachmentService attachmentService, @ComponentImport UserManager userManager, @ComponentImport SettingsService settingsService, @ComponentImport OperationService operationService) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.attachmentService = attachmentService;
        this.userManager = userManager;
        this.settingsService = settingsService;
        this.operationService = operationService;
    }

    public Optional<CompanionLinkModel> resolveCompanionLink(long attachmentId) throws URISyntaxException {
        Optional attachmentOptional = this.attachmentService.find(Expansions.of((String[])new String[]{"container", "space"}).toArray()).withId(ContentId.of((long)attachmentId)).fetch();
        if (!attachmentOptional.isPresent()) {
            return Optional.empty();
        }
        Container container = ((Content)attachmentOptional.get()).getContainer();
        ValidationResult validationResult = this.operationService.canPerform((Person)User.fromUserkey((UserKey)AuthenticatedUserThreadLocal.get().getKey()), (Operation)OperationKey.CREATE, Target.forChildrenOfContainer((Container)container, (TargetType)TargetType.ATTACHMENT));
        if (!validationResult.isSuccessful()) {
            return Optional.empty();
        }
        String baseUrl = this.settingsService.getGlobalSettings().getBaseUrl();
        String companionLink = baseUrl + "/rest/token-auth/api/previews/templinksresource/companion/attachment?attachmentId=" + attachmentId;
        String jwtId = UUID.randomUUID().toString();
        String signedCompanionLink = this.sign(companionLink, "GET", TimeUtil.currentTimePlusNSeconds((long)JwtTokenGenerator.JWT_SHORT_EXPIRY_WINDOW_SECONDS), jwtId);
        return Optional.of(new CompanionLinkModel(signedCompanionLink, jwtId));
    }

    public Optional<CompanionAttachmentModel> resolveCompanionAttachmentMeta(long attachmentId) {
        Optional attachmentOptional = this.attachmentService.find(Expansions.of((String[])new String[]{"container", "space"}).toArray()).withId(ContentId.of((long)attachmentId)).fetch();
        long attachmentMaxSize = this.settingsService.getGlobalSettings().getAttachmentMaxSizeBytes();
        ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
        String baseUrl = this.settingsService.getGlobalSettings().getBaseUrl();
        if (!attachmentOptional.isPresent()) {
            return Optional.empty();
        }
        Content item = (Content)attachmentOptional.get();
        Container container = item.getContainer();
        ValidationResult validationResult = this.operationService.canPerform((Person)User.fromUserkey((UserKey)AuthenticatedUserThreadLocal.get().getKey()), (Operation)OperationKey.CREATE, Target.forChildrenOfContainer((Container)container, (TargetType)TargetType.ATTACHMENT));
        if (!validationResult.isSuccessful()) {
            return Optional.empty();
        }
        Content containerAsContent = (Content)container;
        try {
            Map metadata = item.getMetadata();
            String mimeType = metadata.getOrDefault("mediaType", "");
            String previewAttachmentUrl = baseUrl + ((Link)item.getLinks().get(LinkType.WEB_UI)).getPath();
            String downloadUrl = TempLinksService.setQueryParameter(baseUrl + ((Link)item.getLinks().get(LinkType.DOWNLOAD)).getPath().replace("/attachments/", "/token-auth/attachments/"), "download", "true");
            String jwtId = UUID.randomUUID().toString();
            String signedDownloadUrl = this.sign(downloadUrl, "GET", TimeUtil.currentTimePlusNSeconds((long)JwtTokenGenerator.JWT_SHORT_EXPIRY_WINDOW_SECONDS), jwtId);
            String uploadUrl = String.format(baseUrl + "/rest/token-auth/api/content/%s/child/attachment/%s/data", containerAsContent.getId().serialise(), attachmentId);
            String signedUploadUrl = this.sign(uploadUrl, "POST");
            String companionActionCallbackUrl = String.format(baseUrl + "/rest/token-auth/api/previews/templinksresource/companion/%s/action", attachmentId);
            String signedFileDiscardUrl = this.sign(companionActionCallbackUrl, "POST");
            String analyticsPublishPath = baseUrl + "/rest/analytics/1.0/publish/bulk";
            String signedAnalyticsPublishPath = this.sign(analyticsPublishPath, "POST");
            String attachmentHistoryPath = String.format(baseUrl + "/rest/token-auth/api/content/%s/history", attachmentId);
            String signedAttachmentHistoryPath = this.sign(attachmentHistoryPath, "GET");
            return Optional.of(new CompanionAttachmentModel(attachmentId, item.getTitle(), mimeType, this.getFileExtension(item.getTitle()), signedDownloadUrl, signedUploadUrl, attachmentMaxSize, containerAsContent.getId().asLong(), containerAsContent.getTitle(), loginUser.getKey().getStringValue(), previewAttachmentUrl, signedAnalyticsPublishPath, signedAttachmentHistoryPath, signedFileDiscardUrl));
        }
        catch (URISyntaxException e) {
            throw new InternalServerException((Throwable)e);
        }
    }

    public Optional<TempLinksModel> resolveLinks(long attachmentId) {
        Optional attachment = this.attachmentService.find(Expansions.of((String[])new String[]{"container"}).toArray()).withId(ContentId.of((long)attachmentId)).fetch();
        return attachment.map(item -> {
            try {
                Content content = (Content)item.getContainer();
                String downloadUrl = TempLinksService.setQueryParameter(((Link)item.getLinks().get(LinkType.DOWNLOAD)).getPath().replace("/attachments/", "/token-auth/attachments/"), "download", "true");
                String signedDownloadUrl = this.sign(downloadUrl, "GET");
                String uploadUrl = String.format("/rest/token-auth/api/content/%s/child/attachment/%s/data", content.getId().serialise(), attachmentId);
                String signedUploadUrl = this.sign(uploadUrl, "POST");
                String analyticsPublishPath = "/rest/analytics/1.0/publish/bulk";
                String signedAnalyticsPublishPath = this.sign("/rest/analytics/1.0/publish/bulk", "POST");
                String attachmentHistoryPath = String.format("/rest/token-auth/api/content/%s/history", attachmentId);
                String signedAttachmentHistoryPath = this.sign(attachmentHistoryPath, "GET");
                return new TempLinksModel(attachmentId, downloadUrl, signedDownloadUrl, uploadUrl, signedUploadUrl, signedAnalyticsPublishPath, signedAttachmentHistoryPath);
            }
            catch (URISyntaxException e) {
                throw new InternalServerException((Throwable)e);
            }
        });
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int indexOfDot = fileName.lastIndexOf(".");
        if (indexOfDot == -1) {
            return "";
        }
        return fileName.substring(indexOfDot + 1).toLowerCase();
    }

    private String sign(String path, String httpMethod) throws URISyntaxException {
        return this.sign(path, httpMethod, -1L, UUID.randomUUID().toString());
    }

    private String sign(String path, String httpMethod, long expireTime, String jwtId) throws URISyntaxException {
        UserKey userKey = Objects.requireNonNull(this.userManager.getRemoteUserKey());
        String jwtToken = this.jwtTokenGenerator.generate(JWT_SUBJECT, httpMethod, URI.create(path), userKey.getStringValue(), expireTime, jwtId);
        return TempLinksService.setQueryParameter(path, "jwt", jwtToken);
    }

    private static String setQueryParameter(String uri, String name, String value) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(uri);
        List queryParameters = uriBuilder.getQueryParams().stream().filter(p -> !p.getName().toLowerCase().equals(name)).collect(Collectors.toList());
        queryParameters.add(new BasicNameValuePair(name, value));
        uriBuilder.setParameters(queryParameters);
        return uriBuilder.build().toString();
    }
}

