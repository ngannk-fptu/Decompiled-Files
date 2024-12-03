/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 */
package com.google.template.soy.msgs.internal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.msgs.restricted.MsgPartUtils;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.msgs.restricted.SoyMsgPlaceholderPart;
import com.google.template.soy.msgs.restricted.SoyMsgPluralCaseSpec;
import com.google.template.soy.msgs.restricted.SoyMsgPluralPart;
import com.google.template.soy.msgs.restricted.SoyMsgPluralRemainderPart;
import com.google.template.soy.msgs.restricted.SoyMsgRawTextPart;
import com.google.template.soy.msgs.restricted.SoyMsgSelectPart;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IcuSyntaxUtils {
    private static final Pattern ICU_SYNTAX_CHAR_NEEDING_ESCAPE_PATTERN = Pattern.compile(" ' (?= ['{}\\#] ) | ' $ | [{}] ", 4);
    private static final Map<String, String> ICU_SYNTAX_CHAR_ESCAPE_MAP = ImmutableMap.of((Object)"'", (Object)"''", (Object)"{", (Object)"'{'", (Object)"}", (Object)"'}'");
    private static final Pattern ICU_SYNTAX_CHAR_NOT_SINGLE_QUOTE_PATTERN = Pattern.compile("[{}]");

    private IcuSyntaxUtils() {
    }

    public static ImmutableList<SoyMsgPart> convertMsgPartsToEmbeddedIcuSyntax(List<SoyMsgPart> origMsgParts, boolean allowIcuEscapingInRawText) {
        if (!MsgPartUtils.hasPlrselPart(origMsgParts)) {
            return ImmutableList.copyOf(origMsgParts);
        }
        ImmutableList.Builder newMsgPartsBuilder = ImmutableList.builder();
        StringBuilder currRawTextSb = new StringBuilder();
        IcuSyntaxUtils.convertMsgPartsHelper((ImmutableList.Builder<SoyMsgPart>)newMsgPartsBuilder, currRawTextSb, origMsgParts, false, allowIcuEscapingInRawText);
        if (currRawTextSb.length() > 0) {
            newMsgPartsBuilder.add((Object)SoyMsgRawTextPart.of(currRawTextSb.toString()));
        }
        return newMsgPartsBuilder.build();
    }

    private static void convertMsgPartsHelper(ImmutableList.Builder<SoyMsgPart> newMsgPartsBuilder, StringBuilder currRawTextSb, List<SoyMsgPart> origMsgParts, boolean isInPlrselPart, boolean allowIcuEscapingInRawText) {
        for (SoyMsgPart origMsgPart : origMsgParts) {
            if (origMsgPart instanceof SoyMsgRawTextPart) {
                String rawText = ((SoyMsgRawTextPart)origMsgPart).getRawText();
                if (isInPlrselPart) {
                    if (allowIcuEscapingInRawText) {
                        rawText = IcuSyntaxUtils.icuEscape(rawText);
                    } else {
                        IcuSyntaxUtils.checkIcuEscapingIsNotNeeded(rawText);
                    }
                }
                currRawTextSb.append(rawText);
                continue;
            }
            if (origMsgPart instanceof SoyMsgPlaceholderPart) {
                if (currRawTextSb.length() > 0) {
                    newMsgPartsBuilder.add((Object)SoyMsgRawTextPart.of(currRawTextSb.toString()));
                    currRawTextSb.setLength(0);
                }
                newMsgPartsBuilder.add((Object)origMsgPart);
                continue;
            }
            if (origMsgPart instanceof SoyMsgPluralRemainderPart) {
                currRawTextSb.append(IcuSyntaxUtils.getPluralRemainderString());
                continue;
            }
            if (origMsgPart instanceof SoyMsgPluralPart) {
                IcuSyntaxUtils.convertPluralPartHelper(newMsgPartsBuilder, currRawTextSb, (SoyMsgPluralPart)origMsgPart, allowIcuEscapingInRawText);
                continue;
            }
            if (!(origMsgPart instanceof SoyMsgSelectPart)) continue;
            IcuSyntaxUtils.convertSelectPartHelper(newMsgPartsBuilder, currRawTextSb, (SoyMsgSelectPart)origMsgPart, allowIcuEscapingInRawText);
        }
    }

    private static void convertPluralPartHelper(ImmutableList.Builder<SoyMsgPart> newMsgPartsBuilder, StringBuilder currRawTextSb, SoyMsgPluralPart origPluralPart, boolean allowIcuEscapingInRawText) {
        currRawTextSb.append(IcuSyntaxUtils.getPluralOpenString(origPluralPart.getPluralVarName(), origPluralPart.getOffset()));
        for (Pair pluralCase : origPluralPart.getCases()) {
            currRawTextSb.append(IcuSyntaxUtils.getPluralCaseOpenString((SoyMsgPluralCaseSpec)pluralCase.first));
            IcuSyntaxUtils.convertMsgPartsHelper(newMsgPartsBuilder, currRawTextSb, (List)pluralCase.second, true, allowIcuEscapingInRawText);
            currRawTextSb.append(IcuSyntaxUtils.getPluralCaseCloseString());
        }
        currRawTextSb.append(IcuSyntaxUtils.getPluralCloseString());
    }

    private static void convertSelectPartHelper(ImmutableList.Builder<SoyMsgPart> newMsgPartsBuilder, StringBuilder currRawTextSb, SoyMsgSelectPart origSelectPart, boolean allowIcuEscapingInRawText) {
        currRawTextSb.append(IcuSyntaxUtils.getSelectOpenString(origSelectPart.getSelectVarName()));
        for (Pair selectCase : origSelectPart.getCases()) {
            currRawTextSb.append(IcuSyntaxUtils.getSelectCaseOpenString((String)selectCase.first));
            IcuSyntaxUtils.convertMsgPartsHelper(newMsgPartsBuilder, currRawTextSb, (List)selectCase.second, true, allowIcuEscapingInRawText);
            currRawTextSb.append(IcuSyntaxUtils.getSelectCaseCloseString());
        }
        currRawTextSb.append(IcuSyntaxUtils.getSelectCloseString());
    }

    @VisibleForTesting
    static String icuEscape(String rawText) {
        Matcher matcher = ICU_SYNTAX_CHAR_NEEDING_ESCAPE_PATTERN.matcher(rawText);
        if (!matcher.find()) {
            return rawText;
        }
        StringBuffer escapedTextSb = new StringBuffer();
        do {
            String repl = ICU_SYNTAX_CHAR_ESCAPE_MAP.get(matcher.group());
            matcher.appendReplacement(escapedTextSb, repl);
        } while (matcher.find());
        matcher.appendTail(escapedTextSb);
        return escapedTextSb.toString();
    }

    @VisibleForTesting
    static void checkIcuEscapingIsNotNeeded(String rawText) {
        Matcher matcher = ICU_SYNTAX_CHAR_NEEDING_ESCAPE_PATTERN.matcher(rawText);
        if (!matcher.find()) {
            return;
        }
        if (ICU_SYNTAX_CHAR_NOT_SINGLE_QUOTE_PATTERN.matcher(rawText).find()) {
            throw SoySyntaxException.createWithoutMetaInfo("Apologies, Soy currently does not support open/close brace characters in plural/gender source msgs.");
        }
        if (!matcher.group().equals("'")) {
            throw new AssertionError();
        }
        String errorMsgSuffix = " One possible workaround is to use the Unicode RIGHT SINGLE QUOTATION MARK character (\\u2019) instead of a basic apostrophe.";
        if (matcher.end() == rawText.length()) {
            throw SoySyntaxException.createWithoutMetaInfo("Apologies, Soy currently does not support a single quote character at the end of a text part in plural/gender source msgs (including immediately preceding an HTML tag or Soy tag)." + errorMsgSuffix);
        }
        if (rawText.charAt(matcher.end()) == '#') {
            throw SoySyntaxException.createWithoutMetaInfo("Apologies, Soy currently does not support a single quote character preceding a hash character in plural/gender source msgs." + errorMsgSuffix);
        }
        if (rawText.charAt(matcher.end()) == '\'') {
            throw SoySyntaxException.createWithoutMetaInfo("Apologies, Soy currently does not support consecutive single quote characters in plural/gender source msgs." + errorMsgSuffix);
        }
        throw new AssertionError();
    }

    private static String getPluralOpenString(String varName, int offset) {
        StringBuilder openingPartSb = new StringBuilder();
        openingPartSb.append('{').append(varName).append(",plural,");
        if (offset != 0) {
            openingPartSb.append("offset:").append(offset).append(' ');
        }
        return openingPartSb.toString();
    }

    private static String getPluralCloseString() {
        return "}";
    }

    private static String getPluralCaseOpenString(SoyMsgPluralCaseSpec pluralCaseSpec) {
        String icuCaseName = pluralCaseSpec.getType() == SoyMsgPluralCaseSpec.Type.EXPLICIT ? "=" + pluralCaseSpec.getExplicitValue() : pluralCaseSpec.getType().name().toLowerCase();
        return icuCaseName + "{";
    }

    private static String getPluralCaseCloseString() {
        return "}";
    }

    private static String getPluralRemainderString() {
        return "#";
    }

    private static String getSelectOpenString(String varName) {
        return "{" + varName + ",select,";
    }

    private static String getSelectCloseString() {
        return "}";
    }

    private static String getSelectCaseOpenString(String caseValue) {
        return (caseValue != null ? caseValue : "other") + "{";
    }

    private static String getSelectCaseCloseString() {
        return "}";
    }
}

