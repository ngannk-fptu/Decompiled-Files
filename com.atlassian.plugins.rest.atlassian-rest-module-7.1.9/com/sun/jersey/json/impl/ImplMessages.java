/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.localization.Localizable;
import com.sun.jersey.localization.LocalizableMessageFactory;
import com.sun.jersey.localization.Localizer;

public final class ImplMessages {
    private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.jersey.json.impl.impl");
    private static final Localizer localizer = new Localizer();

    public static Localizable localizableERROR_JAXB_RI_2_1_12_MISSING() {
        return messageFactory.getMessage("error.jaxb.ri.2.1.12.missing", new Object[0]);
    }

    public static String ERROR_JAXB_RI_2_1_12_MISSING() {
        return localizer.localize(ImplMessages.localizableERROR_JAXB_RI_2_1_12_MISSING());
    }

    public static Localizable localizableERROR_WRITING_JSON_ARRAY() {
        return messageFactory.getMessage("error.writing.json.array", new Object[0]);
    }

    public static String ERROR_WRITING_JSON_ARRAY() {
        return localizer.localize(ImplMessages.localizableERROR_WRITING_JSON_ARRAY());
    }

    public static Localizable localizableERROR_JAXB_RI_2_1_10_MISSING() {
        return messageFactory.getMessage("error.jaxb.ri.2.1.10.missing", new Object[0]);
    }

    public static String ERROR_JAXB_RI_2_1_10_MISSING() {
        return localizer.localize(ImplMessages.localizableERROR_JAXB_RI_2_1_10_MISSING());
    }

    public static Localizable localizableERROR_WRITING_JSON_OBJECT() {
        return messageFactory.getMessage("error.writing.json.object", new Object[0]);
    }

    public static String ERROR_WRITING_JSON_OBJECT() {
        return localizer.localize(ImplMessages.localizableERROR_WRITING_JSON_OBJECT());
    }

    public static Localizable localizableERROR_PARSING_JSON_ARRAY() {
        return messageFactory.getMessage("error.parsing.json.array", new Object[0]);
    }

    public static String ERROR_PARSING_JSON_ARRAY() {
        return localizer.localize(ImplMessages.localizableERROR_PARSING_JSON_ARRAY());
    }

    public static Localizable localizableERROR_PARSING_JSON_OBJECT() {
        return messageFactory.getMessage("error.parsing.json.object", new Object[0]);
    }

    public static String ERROR_PARSING_JSON_OBJECT() {
        return localizer.localize(ImplMessages.localizableERROR_PARSING_JSON_OBJECT());
    }

    public static Localizable localizableERROR_JSONP_MSG_BODY_WRITER_NOT_FOUND(Object arg0, Object arg1) {
        return messageFactory.getMessage("error.jsonp.msg.body.writer.not.found", arg0, arg1);
    }

    public static String ERROR_JSONP_MSG_BODY_WRITER_NOT_FOUND(Object arg0, Object arg1) {
        return localizer.localize(ImplMessages.localizableERROR_JSONP_MSG_BODY_WRITER_NOT_FOUND(arg0, arg1));
    }

    public static Localizable localizableERROR_NONGE_JSONP_MSG_BODY_WRITER_NOT_FOUND(Object arg0, Object arg1) {
        return messageFactory.getMessage("error.nonge.jsonp.msg.body.writer.not.found", arg0, arg1);
    }

    public static String ERROR_NONGE_JSONP_MSG_BODY_WRITER_NOT_FOUND(Object arg0, Object arg1) {
        return localizer.localize(ImplMessages.localizableERROR_NONGE_JSONP_MSG_BODY_WRITER_NOT_FOUND(arg0, arg1));
    }
}

