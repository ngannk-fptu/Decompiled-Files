/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.apache.commons.lang3.Range
 *  org.apache.commons.lang3.StringUtils
 */
package net.customware.confluence.plugin.toc;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import net.customware.confluence.plugin.toc.SeparatorType;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

class ClientTocMacroTemplateModel {
    private static final Collection<MacroParameterProcessor> PARAMETER_PROCESSORS = ImmutableList.of((Object)ClientTocMacroTemplateModel.singleDataAttribute("type", "structure"), (Object)ClientTocMacroTemplateModel.singleDataAttribute("outline", "numberedoutline"), (Object)ClientTocMacroTemplateModel.singleDataAttribute("style", "cssliststyle"), (Object)ClientTocMacroTemplateModel.singleDataAttribute("indent", "csslistindent"), (Object)ClientTocMacroTemplateModel.separatorDataAttributes("separator", "preseparator", "midseparator", "postseparator"), (Object)ClientTocMacroTemplateModel.headingMinMaxProcessor("minLevel", "maxLevel", "headerelements"), (Object)ClientTocMacroTemplateModel.singleDataAttribute("include", "includeheaderregex"), (Object)ClientTocMacroTemplateModel.singleDataAttribute("exclude", "excludeheaderregex"));
    private static final int MIN_HEADER_LEVEL = 1;
    private static final int MAX_HEADER_LEVEL = 7;
    private static final Range<Integer> HEADER_LEVEL_RANGE = Range.between((Comparable)Integer.valueOf(1), (Comparable)Integer.valueOf(7));

    ClientTocMacroTemplateModel() {
    }

    static ImmutableMap<String, Object> buildTemplateModel(Map<String, String> macroParameters) {
        return ImmutableMap.of((Object)"dataAttributes", ClientTocMacroTemplateModel.processMacroParameters(macroParameters), (Object)"nonPrintable", (Object)(!Boolean.parseBoolean(StringUtils.defaultString((String)macroParameters.get("printable"), (String)"true")) ? 1 : 0), (Object)"customCssClass", (Object)StringUtils.defaultString((String)macroParameters.get("class")));
    }

    private static Map<String, String> processMacroParameters(Map<String, String> macroParameters) {
        ImmutableMap.Builder mappedParameters = ImmutableMap.builder();
        for (MacroParameterProcessor parameterProcessor : PARAMETER_PROCESSORS) {
            mappedParameters.putAll(parameterProcessor.getDataAttributes(macroParameters));
        }
        return mappedParameters.build();
    }

    private static MacroParameterProcessor singleDataAttribute(final String macroParameterName, final String dataAttributeName) {
        return new MacroParameterProcessor(){

            @Override
            public Map<String, String> getDataAttributes(Map<String, String> macroParameters) {
                String parameterValue = macroParameters.get(macroParameterName);
                if (StringUtils.isBlank((CharSequence)parameterValue)) {
                    return Collections.emptyMap();
                }
                return Collections.singletonMap(dataAttributeName, StringUtils.trimToEmpty((String)parameterValue));
            }
        };
    }

    private static MacroParameterProcessor separatorDataAttributes(final String macroParameterName, final String preseparator, final String midseparator, final String postseparator) {
        return new MacroParameterProcessor(){

            @Override
            public Map<String, String> getDataAttributes(Map<String, String> macroParameters) {
                String parameterValue = macroParameters.get(macroParameterName);
                if (StringUtils.isBlank((CharSequence)parameterValue)) {
                    return Collections.emptyMap();
                }
                SeparatorType separatorType = SeparatorType.valueOfSeparator(parameterValue);
                if (separatorType != null) {
                    return this.dataAttributes(separatorType.getPre(), separatorType.getMid(), separatorType.getPost());
                }
                return this.dataAttributes("", parameterValue, "");
            }

            private Map<String, String> dataAttributes(String pre, String mid, String post) {
                return ImmutableMap.of((Object)preseparator, (Object)pre, (Object)midseparator, (Object)mid, (Object)postseparator, (Object)post);
            }
        };
    }

    private static MacroParameterProcessor headingMinMaxProcessor(String minMacroParameterName, String maxMacroParameterName, String dataAttributeName) {
        return macroParameters -> {
            BiFunction<String, Integer, Integer> headingLevelParser = (parameterName, defaultValue) -> {
                Integer value = Integer.valueOf(StringUtils.defaultString((String)((String)macroParameters.get(parameterName)), (String)String.valueOf(defaultValue)));
                return HEADER_LEVEL_RANGE.contains((Object)value) ? value : defaultValue;
            };
            int min = headingLevelParser.apply(minMacroParameterName, 1);
            int max = headingLevelParser.apply(maxMacroParameterName, 7);
            ImmutableList.Builder headerElements = ImmutableList.builder();
            for (int i = min; i <= max; ++i) {
                headerElements.add((Object)("H" + i));
            }
            return ImmutableMap.of((Object)dataAttributeName, (Object)Joiner.on((char)',').join((Iterable)headerElements.build()));
        };
    }

    private static interface MacroParameterProcessor {
        public Map<String, String> getDataAttributes(Map<String, String> var1);
    }
}

