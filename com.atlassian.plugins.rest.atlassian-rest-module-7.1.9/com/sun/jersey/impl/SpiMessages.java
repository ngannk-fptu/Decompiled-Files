/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.impl;

import com.sun.jersey.localization.Localizable;
import com.sun.jersey.localization.LocalizableMessageFactory;
import com.sun.jersey.localization.Localizer;

public final class SpiMessages {
    private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.jersey.impl.spi");
    private static final Localizer localizer = new Localizer();

    public static Localizable localizableILLEGAL_CONFIG_SYNTAX() {
        return messageFactory.getMessage("illegal.config.syntax", new Object[0]);
    }

    public static String ILLEGAL_CONFIG_SYNTAX() {
        return localizer.localize(SpiMessages.localizableILLEGAL_CONFIG_SYNTAX());
    }

    public static Localizable localizablePROVIDER_COULD_NOT_BE_CREATED(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("provider.could.not.be.created", arg0, arg1, arg2);
    }

    public static String PROVIDER_COULD_NOT_BE_CREATED(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(SpiMessages.localizablePROVIDER_COULD_NOT_BE_CREATED(arg0, arg1, arg2));
    }

    public static Localizable localizableOSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(Object arg0) {
        return messageFactory.getMessage("osgi.registry.error.processing.resource.stream", arg0);
    }

    public static String OSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(Object arg0) {
        return localizer.localize(SpiMessages.localizableOSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(arg0));
    }

    public static Localizable localizableTEMPLATE_NAME_TO_VALUE_NOT_NULL() {
        return messageFactory.getMessage("template.name.to.value.not.null", new Object[0]);
    }

    public static String TEMPLATE_NAME_TO_VALUE_NOT_NULL() {
        return localizer.localize(SpiMessages.localizableTEMPLATE_NAME_TO_VALUE_NOT_NULL());
    }

    public static Localizable localizableILLEGAL_PROVIDER_CLASS_NAME(Object arg0) {
        return messageFactory.getMessage("illegal.provider.class.name", arg0);
    }

    public static String ILLEGAL_PROVIDER_CLASS_NAME(Object arg0) {
        return localizer.localize(SpiMessages.localizableILLEGAL_PROVIDER_CLASS_NAME(arg0));
    }

    public static Localizable localizableDEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("dependent.class.of.provider.format.error", arg0, arg1, arg2);
    }

    public static String DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(SpiMessages.localizableDEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(arg0, arg1, arg2));
    }

    public static Localizable localizableDEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("dependent.class.of.provider.not.found", arg0, arg1, arg2);
    }

    public static String DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(SpiMessages.localizableDEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(arg0, arg1, arg2));
    }

    public static Localizable localizableURITEMPLATE_CANNOT_BE_NULL() {
        return messageFactory.getMessage("uritemplate.cannot.be.null", new Object[0]);
    }

    public static String URITEMPLATE_CANNOT_BE_NULL() {
        return localizer.localize(SpiMessages.localizableURITEMPLATE_CANNOT_BE_NULL());
    }

    public static Localizable localizablePROVIDER_NOT_FOUND(Object arg0, Object arg1) {
        return messageFactory.getMessage("provider.not.found", arg0, arg1);
    }

    public static String PROVIDER_NOT_FOUND(Object arg0, Object arg1) {
        return localizer.localize(SpiMessages.localizablePROVIDER_NOT_FOUND(arg0, arg1));
    }

    public static Localizable localizableOSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(Object arg0) {
        return messageFactory.getMessage("osgi.registry.error.opening.resource.stream", arg0);
    }

    public static String OSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(Object arg0) {
        return localizer.localize(SpiMessages.localizableOSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(arg0));
    }

    public static Localizable localizablePROVIDER_CLASS_COULD_NOT_BE_LOADED(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("provider.class.could.not.be.loaded", arg0, arg1, arg2);
    }

    public static String PROVIDER_CLASS_COULD_NOT_BE_LOADED(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(SpiMessages.localizablePROVIDER_CLASS_COULD_NOT_BE_LOADED(arg0, arg1, arg2));
    }
}

