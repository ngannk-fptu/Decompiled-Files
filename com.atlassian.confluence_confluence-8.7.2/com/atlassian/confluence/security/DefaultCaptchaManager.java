/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.Group
 *  com.atlassian.user.search.page.Pager
 *  com.octo.captcha.engine.CaptchaEngine
 *  com.octo.captcha.service.CaptchaServiceException
 *  com.octo.captcha.service.captchastore.CaptchaStore
 *  com.octo.captcha.service.image.ImageCaptchaService
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.security.ConfluenceCaptchaEngine;
import com.atlassian.confluence.security.ConfluenceImageCaptchaService;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.Group;
import com.atlassian.user.search.page.Pager;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.image.ImageCaptchaService;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class DefaultCaptchaManager
implements CaptchaManager,
InitializingBean,
DisposableBean {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Logger log = LoggerFactory.getLogger(DefaultCaptchaManager.class);
    private ImageCaptchaService imageCaptchaService;
    private SettingsManager settingsManager;
    private UserAccessor userAccessor;
    private String captchaEngineClassName;
    private EventPublisher eventPublisher;
    private CaptchaStore captchaStore;

    public void setCaptchaStore(CaptchaStore captchaStore) {
        this.captchaStore = captchaStore;
    }

    public void setCaptchaEngineClassName(String captchaEngineClassName) {
        this.captchaEngineClassName = captchaEngineClassName;
    }

    public void afterPropertiesSet() throws Exception {
        try {
            if (this.imageCaptchaService == null) {
                this.imageCaptchaService = new ConfluenceImageCaptchaService(this.captchaStore, this.createCaptchaEngine());
            }
        }
        catch (Throwable t) {
            log.error("Could not initialise CAPTCHA service. The most likely reason for thisis that Java's graphics subsystem is not properly configured. Try startingConfluence's JVM with the -Djava.awt.headless=true option. " + t.getMessage(), t);
        }
    }

    public void destroy() throws Exception {
    }

    @Override
    public boolean isCaptchaAvailable() {
        return this.imageCaptchaService != null;
    }

    private CaptchaEngine createCaptchaEngine() {
        if (StringUtils.isNotBlank((CharSequence)this.captchaEngineClassName)) {
            try {
                Class<?> cls = Class.forName(this.captchaEngineClassName);
                if (CaptchaEngine.class.isAssignableFrom(cls)) {
                    CaptchaEngine e = (CaptchaEngine)cls.newInstance();
                    log.debug("Using the {} captcha engine for captcha image generation.", (Object)this.captchaEngineClassName);
                    return e;
                }
                log.warn("The class [{}] needs to implement {}}", (Object)this.captchaEngineClassName, (Object)CaptchaEngine.class.getName());
            }
            catch (ReflectiveOperationException e) {
                log.warn("Unable to instantiate the captcha engine class '{}' ", (Object)this.captchaEngineClassName);
            }
        }
        return new ConfluenceCaptchaEngine();
    }

    @Override
    public boolean validateCaptcha(String captchaId, String captchaResponse) {
        if (!this.showCaptchaForCurrentUser()) {
            return true;
        }
        return this.forceValidateCaptcha(captchaId, captchaResponse);
    }

    @Override
    public boolean forceValidateCaptcha(String captchaId, String captchaResponse) {
        if (StringUtils.isBlank((CharSequence)captchaId)) {
            return false;
        }
        Boolean isResponseCorrect = Boolean.FALSE;
        if (this.isDebugEnabled()) {
            return "DEBUG".equals(captchaResponse);
        }
        try {
            isResponseCorrect = this.imageCaptchaService.validateResponseForID(captchaId, (Object)captchaResponse);
        }
        catch (CaptchaServiceException e) {
            log.error(String.format("The ImageCaptchaService encountered an error while attempting to validate the captcha response for captcha id %s and response %s", captchaId, captchaResponse), (Throwable)e);
        }
        return isResponseCorrect;
    }

    @Override
    public boolean isCaptchaEnabled() {
        return this.settingsManager.getGlobalSettings().getCaptchaSettings().isEnableCaptcha();
    }

    @Override
    public void setCaptchaEnabled(boolean value) {
        Settings settings = this.settingsManager.getGlobalSettings();
        Settings oldSettings = new Settings(settings);
        settings.getCaptchaSettings().setEnableCaptcha(value);
        this.settingsManager.updateGlobalSettings(settings);
        this.eventPublisher.publish((Object)new GlobalSettingsChangedEvent(this, oldSettings, settings));
    }

    @Override
    public boolean isDebugEnabled() {
        return this.settingsManager.getGlobalSettings().getCaptchaSettings().isEnableDebug();
    }

    @Override
    public void setDebugMode(boolean value) {
        Settings settings = this.settingsManager.getGlobalSettings();
        Settings oldSettings = new Settings(settings);
        settings.getCaptchaSettings().setEnableDebug(value);
        this.settingsManager.updateGlobalSettings(settings);
        this.eventPublisher.publish((Object)new GlobalSettingsChangedEvent(this, oldSettings, settings));
    }

    @Override
    public void excludeNone() {
        this.setExclude("none");
    }

    @Override
    public void excludeRegisteredUsers() {
        this.setExclude("registered");
    }

    @Override
    public void excludeGroups() {
        this.setExclude("groups");
    }

    @Override
    public String getExclude() {
        return this.settingsManager.getGlobalSettings().getCaptchaSettings().getExclude();
    }

    @Override
    public void setExclude(String value) {
        Settings settings = this.settingsManager.getGlobalSettings();
        Settings oldSettings = new Settings(settings);
        if (!value.equals(oldSettings.getCaptchaSettings().getExclude())) {
            settings.getCaptchaSettings().setExclude(value);
            this.settingsManager.updateGlobalSettings(settings);
            this.eventPublisher.publish((Object)new GlobalSettingsChangedEvent(this, oldSettings, settings));
        }
    }

    @Override
    public void setCaptchaGroups(Collection groupList) {
        HashSet<String> captchaGroups = new HashSet<String>(groupList);
        Settings settings = this.settingsManager.getGlobalSettings();
        Settings oldSettings = new Settings(settings);
        if (!CollectionUtils.isEqualCollection(captchaGroups, oldSettings.getCaptchaSettings().getCaptchaGroups())) {
            settings.getCaptchaSettings().setCaptchaGroups(captchaGroups);
            this.settingsManager.updateGlobalSettings(settings);
            this.eventPublisher.publish((Object)new GlobalSettingsChangedEvent(this, oldSettings, settings));
        }
    }

    @Override
    public Collection addCaptchaGroups(Collection groupList) {
        Settings settings = this.settingsManager.getGlobalSettings();
        Settings oldSettings = new Settings(settings);
        HashSet<String> captchaGroups = new HashSet<String>(settings.getCaptchaSettings().getCaptchaGroups());
        captchaGroups.addAll(groupList);
        if (!CollectionUtils.isEqualCollection(captchaGroups, oldSettings.getCaptchaSettings().getCaptchaGroups())) {
            settings.getCaptchaSettings().setCaptchaGroups(captchaGroups);
            this.settingsManager.updateGlobalSettings(settings);
            this.eventPublisher.publish((Object)new GlobalSettingsChangedEvent(this, oldSettings, settings));
        }
        return captchaGroups;
    }

    @Override
    public void removeCaptchaGroup(String group) {
        Settings settings = this.settingsManager.getGlobalSettings();
        Settings oldSettings = new Settings(settings);
        Collection<String> captchaGroups = settings.getCaptchaSettings().getCaptchaGroups();
        captchaGroups.remove(group);
        if (!captchaGroups.equals(oldSettings.getCaptchaSettings().getCaptchaGroups())) {
            settings.getCaptchaSettings().setCaptchaGroups(captchaGroups);
            this.settingsManager.updateGlobalSettings(settings);
            this.eventPublisher.publish((Object)new GlobalSettingsChangedEvent(this, oldSettings, settings));
        }
    }

    @Override
    public boolean showCaptchaForCurrentUser() {
        if (this.isDebugEnabled()) {
            return true;
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Settings settings = this.settingsManager.getGlobalSettings();
        Collection<String> captchaGroups = settings.getCaptchaSettings().getCaptchaGroups();
        if (this.isCaptchaEnabled()) {
            if (user == null) {
                return true;
            }
            if ("registered".equals(settings.getCaptchaSettings().getExclude())) {
                return false;
            }
            if ("groups".equals(settings.getCaptchaSettings().getExclude())) {
                if (captchaGroups.size() == 0) {
                    return true;
                }
                Pager usersPager = this.userAccessor.getGroups(user);
                for (Group group : usersPager) {
                    if (!captchaGroups.contains(group.getName())) continue;
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Deprecated
    public UserAccessor getUserAccessor() {
        return this.userAccessor;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Deprecated
    public void setImageCaptchaService(ImageCaptchaService imageCaptchaService) {
        this.imageCaptchaService = imageCaptchaService;
    }

    @Override
    @Deprecated
    public ImageCaptchaService getImageCaptchaService() {
        return this.imageCaptchaService;
    }

    @Override
    public String generateCaptchaId() {
        return String.valueOf(RANDOM.nextInt());
    }
}

