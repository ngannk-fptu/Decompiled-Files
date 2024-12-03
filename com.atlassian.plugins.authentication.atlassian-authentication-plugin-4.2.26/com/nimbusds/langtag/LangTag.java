/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.langtag;

import com.nimbusds.langtag.LangTagException;
import com.nimbusds.langtag.ReadOnlyLangTag;
import java.util.LinkedList;

public class LangTag
implements ReadOnlyLangTag {
    private String primaryLanguage;
    private String[] languageSubtags;
    private String script = null;
    private String region = null;
    private String[] variants = null;
    private String[] extensions = null;
    private String privateUse = null;

    private static void ensureMaxLength(String subtag) throws LangTagException {
        if (subtag.length() > 8 && subtag.charAt(1) != '-' && subtag.length() > 10) {
            throw new LangTagException("Invalid subtag syntax: Max character length exceeded");
        }
    }

    public LangTag(String primaryLanguage) throws LangTagException {
        this(primaryLanguage, new String[0]);
    }

    public LangTag(String primaryLanguage, String ... languageSubtags) throws LangTagException {
        if (primaryLanguage == null && (languageSubtags == null || languageSubtags.length == 0)) {
            throw new LangTagException("Either the primary language or the extended language subtags, or both must be defined");
        }
        this.setPrimaryLanguage(primaryLanguage);
        this.setExtendedLanguageSubtags(languageSubtags);
    }

    @Override
    public String getLanguage() {
        StringBuilder sb = new StringBuilder();
        if (this.primaryLanguage != null) {
            sb.append(this.primaryLanguage);
        }
        if (this.languageSubtags != null && this.languageSubtags.length > 0) {
            for (String tag : this.languageSubtags) {
                if (sb.length() > 0) {
                    sb.append('-');
                }
                sb.append(tag);
            }
        }
        return sb.toString();
    }

    @Override
    public String getPrimaryLanguage() {
        return this.primaryLanguage;
    }

    private static boolean isPrimaryLanguage(String s) {
        return s.matches("[a-zA-Z]{2,3}");
    }

    private void setPrimaryLanguage(String primaryLanguage) throws LangTagException {
        if (primaryLanguage == null) {
            this.primaryLanguage = null;
            return;
        }
        LangTag.ensureMaxLength(primaryLanguage);
        if (!LangTag.isPrimaryLanguage(primaryLanguage)) {
            throw new LangTagException("Invalid primary language subtag: Must be a two or three-letter ISO 639 code");
        }
        this.primaryLanguage = primaryLanguage.toLowerCase();
    }

    @Override
    public String[] getExtendedLanguageSubtags() {
        return this.languageSubtags;
    }

    private static boolean isExtendedLanguageSubtag(String s) {
        return s.matches("[a-zA-Z]{3}");
    }

    private void setExtendedLanguageSubtags(String ... languageSubtags) throws LangTagException {
        if (languageSubtags == null || languageSubtags.length == 0) {
            this.languageSubtags = null;
            return;
        }
        this.languageSubtags = new String[languageSubtags.length];
        for (int i = 0; i < languageSubtags.length; ++i) {
            LangTag.ensureMaxLength(languageSubtags[i]);
            if (!LangTag.isExtendedLanguageSubtag(languageSubtags[i])) {
                throw new LangTagException("Invalid extended language subtag: Must be a three-letter ISO 639-3 code");
            }
            this.languageSubtags[i] = languageSubtags[i].toLowerCase();
        }
    }

    @Override
    public String getScript() {
        return this.script;
    }

    private static boolean isScript(String s) {
        return s.matches("[a-zA-Z]{4}");
    }

    public void setScript(String script) throws LangTagException {
        if (script == null) {
            this.script = null;
            return;
        }
        LangTag.ensureMaxLength(script);
        if (!LangTag.isScript(script)) {
            throw new LangTagException("Invalid script subtag: Must be a four-letter ISO 15924 code");
        }
        this.script = script.substring(0, 1).toUpperCase() + script.substring(1).toLowerCase();
    }

    @Override
    public String getRegion() {
        return this.region;
    }

    private static boolean isRegion(String s) {
        return s.matches("[a-zA-Z]{2}|\\d{3}");
    }

    public void setRegion(String region) throws LangTagException {
        if (region == null) {
            this.region = null;
            return;
        }
        LangTag.ensureMaxLength(region);
        if (!LangTag.isRegion(region)) {
            throw new LangTagException("Invalid region subtag: Must be a two-letter ISO 3166-1 code or a three-digit UN M.49 code");
        }
        this.region = region.toUpperCase();
    }

    @Override
    public String[] getVariants() {
        return this.variants;
    }

    private static boolean isVariant(String s) {
        return s.matches("[a-zA-Z][a-zA-Z0-9]{4,}|[0-9][a-zA-Z0-9]{3,}");
    }

    public void setVariants(String ... variants) throws LangTagException {
        if (variants == null || variants.length == 0) {
            this.variants = null;
            return;
        }
        this.variants = new String[variants.length];
        for (int i = 0; i < variants.length; ++i) {
            LangTag.ensureMaxLength(variants[i]);
            if (!LangTag.isVariant(variants[i])) {
                throw new LangTagException("Invalid variant subtag");
            }
            this.variants[i] = variants[i].toLowerCase();
        }
    }

    @Override
    public String[] getExtensions() {
        return this.extensions;
    }

    private static boolean isExtensionSingleton(String s) {
        return s.matches("[0-9a-wA-Wy-zY-Z]");
    }

    private static boolean isExtension(String s) {
        return s.matches("[0-9a-wA-Wy-zY-Z]-[0-9a-zA-Z]+");
    }

    public void setExtensions(String ... extensions) throws LangTagException {
        if (extensions == null || extensions.length == 0) {
            this.extensions = null;
            return;
        }
        this.extensions = new String[extensions.length];
        for (int i = 0; i < extensions.length; ++i) {
            LangTag.ensureMaxLength(extensions[i]);
            if (!LangTag.isExtension(extensions[i])) {
                throw new LangTagException("Invalid extension subtag");
            }
            this.extensions[i] = extensions[i].toLowerCase();
        }
    }

    @Override
    public String getPrivateUse() {
        return this.privateUse;
    }

    private static boolean isPrivateUse(String s) {
        return s.matches("x-[0-9a-zA-Z]+");
    }

    public void setPrivateUse(String privateUse) throws LangTagException {
        if (privateUse == null) {
            this.privateUse = null;
            return;
        }
        LangTag.ensureMaxLength(privateUse);
        if (!LangTag.isPrivateUse(privateUse)) {
            throw new LangTagException("Invalid private use subtag");
        }
        this.privateUse = privateUse.toLowerCase();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getLanguage());
        if (this.script != null) {
            sb.append('-');
            sb.append(this.script);
        }
        if (this.region != null) {
            sb.append('-');
            sb.append(this.region);
        }
        if (this.variants != null) {
            for (String v : this.variants) {
                sb.append('-');
                sb.append(v);
            }
        }
        if (this.extensions != null) {
            for (String e : this.extensions) {
                sb.append('-');
                sb.append(e);
            }
        }
        if (this.privateUse != null) {
            sb.append('-');
            sb.append(this.privateUse);
        }
        return sb.toString();
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean equals(Object object) {
        return object != null && object instanceof LangTag && this.toString().equals(object.toString());
    }

    public static LangTag parse(String s) throws LangTagException {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        String[] subtags = s.split("-");
        int pos = 0;
        String primaryLang = null;
        LinkedList<String> extLangSubtags = new LinkedList<String>();
        if (LangTag.isPrimaryLanguage(subtags[0])) {
            primaryLang = subtags[pos++];
        }
        while (pos < subtags.length && LangTag.isExtendedLanguageSubtag(subtags[pos])) {
            extLangSubtags.add(subtags[pos++]);
        }
        LangTag langTag = new LangTag(primaryLang, extLangSubtags.toArray(new String[0]));
        if (pos < subtags.length && LangTag.isScript(subtags[pos])) {
            langTag.setScript(subtags[pos++]);
        }
        if (pos < subtags.length && LangTag.isRegion(subtags[pos])) {
            langTag.setRegion(subtags[pos++]);
        }
        LinkedList<String> variantSubtags = new LinkedList<String>();
        while (pos < subtags.length && LangTag.isVariant(subtags[pos])) {
            variantSubtags.add(subtags[pos++]);
        }
        if (!variantSubtags.isEmpty()) {
            langTag.setVariants(variantSubtags.toArray(new String[0]));
        }
        LinkedList<String> extSubtags = new LinkedList<String>();
        while (pos < subtags.length && LangTag.isExtensionSingleton(subtags[pos])) {
            String singleton = subtags[pos++];
            if (pos == subtags.length) {
                throw new LangTagException("Invalid extension subtag");
            }
            extSubtags.add(singleton + "-" + subtags[pos++]);
        }
        if (!extSubtags.isEmpty()) {
            langTag.setExtensions(extSubtags.toArray(new String[0]));
        }
        if (pos < subtags.length && subtags[pos].equals("x")) {
            if (++pos == subtags.length) {
                throw new LangTagException("Invalid private use subtag");
            }
            langTag.setPrivateUse("x-" + subtags[pos++]);
        }
        if (pos < subtags.length) {
            throw new LangTagException("Invalid language tag: Unexpected subtag");
        }
        return langTag;
    }
}

