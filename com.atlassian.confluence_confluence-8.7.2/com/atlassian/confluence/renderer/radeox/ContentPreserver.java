/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.IconManager
 *  com.atlassian.renderer.util.RegExpUtil
 *  com.atlassian.renderer.util.UrlUtil
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.lang3.StringUtils
 *  org.radeox.util.Encoder
 */
package com.atlassian.confluence.renderer.radeox;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.RegExpProcessor;
import com.atlassian.renderer.IconManager;
import com.atlassian.renderer.util.RegExpUtil;
import com.atlassian.renderer.util.UrlUtil;
import com.atlassian.spring.container.ContainerManager;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.radeox.util.Encoder;

public class ContentPreserver {
    public static final String VALID_EMOTICON_START = "(^|(?<!\\&#?[a-zA-Z0-9]{1,10}))";
    public static final String VALID_EMOTICON_END = "($|(?![a-zA-Z]))";
    private List specialProcessors = new LinkedList();

    public void addSpecial(String pattern) {
        this.addSpecial(RegExpUtil.convertToRegularExpression((String)pattern), ContentPreserver.encodeFirstCharacter(pattern));
    }

    public void addSpecial(String pattern, String replacement) {
        this.specialProcessors.add(new RegExpProcessor(pattern, replacement));
    }

    public void removeSpecial(String pattern) {
        Iterator it = this.specialProcessors.iterator();
        pattern = RegExpUtil.convertToRegularExpression((String)pattern);
        while (it.hasNext()) {
            RegExpProcessor processor = (RegExpProcessor)it.next();
            if (!processor.getPattern().equals(pattern)) continue;
            this.specialProcessors.remove(processor);
            return;
        }
    }

    public void removeAllSpecials() {
        this.specialProcessors.clear();
    }

    public void addEmoticonsAsSpecial() {
        String[] icons = ((IconManager)ContainerManager.getComponent((String)"iconManager")).getEmoticonSymbols();
        for (int i = 0; i < icons.length; ++i) {
            String icon = icons[i];
            String pattern = RegExpUtil.convertToRegularExpression((String)icon) + VALID_EMOTICON_END;
            if (icon.startsWith(";")) {
                pattern = VALID_EMOTICON_START + pattern;
            }
            this.addSpecial(pattern, ContentPreserver.encodeWholeString(icon));
        }
    }

    public void addDefaultSpecials() {
        this.addSpecial("[");
        this.addSpecial("]");
        this.addSpecial("{");
        this.addSpecial("}");
        this.addSpecial("*");
        this.addSpecial("@");
        this.addSpecial("-");
        this.addSpecial("+");
        this.addSpecial("_");
        this.addSpecial("^");
        this.addSpecial("%");
        this.addSpecial("?");
        this.addSpecial("!");
        this.addSpecial("~");
        this.addSpecial("\\");
        this.addSpecial("'");
        for (Object o : UrlUtil.URL_PROTOCOLS) {
            this.addSpecial((String)o);
        }
    }

    public String doPreserve(String content) {
        if (StringUtils.isEmpty((CharSequence)content)) {
            return "";
        }
        Iterator it = this.specialProcessors.iterator();
        String result = content;
        while (it.hasNext()) {
            RegExpProcessor processor = (RegExpProcessor)it.next();
            result = processor.process(result);
        }
        SettingsManager settingsManager = (SettingsManager)ContainerManager.getComponent((String)"settingsManager");
        if (settingsManager.getGlobalSettings().isAllowCamelCase()) {
            RegExpProcessor.RegExpProcessorHandler handler = (stringBuffer, matcher, regExp) -> {
                stringBuffer.append(matcher.group(1));
                stringBuffer.append(Encoder.toEntity((int)matcher.group(2).charAt(0)));
                stringBuffer.append(matcher.group(3));
                stringBuffer.append(matcher.group(5));
            };
            result = new RegExpProcessor("([^a-zA-Z0-9!/\\[]|^)([A-Z])([a-z]+([A-Z][a-zA-Z0-9]+)+)(([^a-zA-Z0-9!\\]])|\r?\n|$)", null, handler).process(result);
        }
        return result;
    }

    private static String encodeWholeString(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            result.append(Encoder.toEntity((int)str.charAt(i)));
        }
        return result.toString();
    }

    private static String encodeFirstCharacter(String str) {
        Object result = Encoder.toEntity((int)str.charAt(0));
        if (str.length() > 1) {
            result = (String)result + str.substring(1);
        }
        return result;
    }
}

