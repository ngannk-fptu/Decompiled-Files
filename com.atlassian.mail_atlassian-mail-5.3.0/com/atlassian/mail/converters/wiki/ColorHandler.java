/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Optional
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.jsoup.nodes.Node
 */
package com.atlassian.mail.converters.wiki;

import com.atlassian.mail.converters.wiki.BlockStyleHandler;
import com.atlassian.mail.converters.wiki.FontStyleHandler;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Node;

@ParametersAreNonnullByDefault
final class ColorHandler {
    public static final String HTML_FONT = "font";
    private static final String WIKI_COLOR_END = "{color}";
    private static final String WIKI_COLOR_START_FORMAT = "{color:%s}";
    private static final String HTML_ATTRIBUTE_STYLE = "style";
    private static final String HTML_ATTRIBUTE_COLOR = "color";
    private static final Pattern RGB_PATTERN = Pattern.compile("^rgb\\((\\d+),(\\d+),(\\d+)\\)$");
    private static final Pattern COLOR_PATTERN = Pattern.compile("^\\s*(\\{color}|\\{color\\s*:\\s*\\p{Graph}+\\s*})(.*|\\n*)*");
    private final Deque<ColorTagProperties> colorsInUse = Lists.newLinkedList();
    private final FontStyleHandler fontStyleHandler;
    private final BlockStyleHandler blockStyleHandler;

    private static String WIKI_COLOR_START(String value) {
        String noWhitespace = StringUtils.remove((String)StringUtils.trimToEmpty((String)(value = StringUtils.defaultString((String)value))), (char)' ');
        Matcher matcher = RGB_PATTERN.matcher(noWhitespace);
        if (matcher.matches()) {
            int red = NumberUtils.toInt((String)matcher.group(1));
            int green = NumberUtils.toInt((String)matcher.group(2));
            int blue = NumberUtils.toInt((String)matcher.group(3));
            String hex = String.format("#%02x%02x%02x", red, green, blue);
            return String.format(WIKI_COLOR_START_FORMAT, hex);
        }
        return String.format(WIKI_COLOR_START_FORMAT, value);
    }

    public ColorHandler(FontStyleHandler fontStyleHandler, BlockStyleHandler blockStyleHandler) {
        this.fontStyleHandler = fontStyleHandler;
        this.blockStyleHandler = blockStyleHandler;
    }

    public String enter(StringBuilder accum, Node node, String name, int depth, boolean isInsideAnyLink, boolean isInsideLinkWithText, boolean isUrlInLinkText, boolean isInsideTable) {
        boolean started;
        String color;
        if (!this.blockStyleHandler.isFormattingPossible()) {
            return "";
        }
        if (HTML_FONT.equals(name)) {
            color = node.attr(HTML_ATTRIBUTE_COLOR);
        } else {
            String style = node.attr(HTML_ATTRIBUTE_STYLE);
            if (StringUtils.isNotBlank((CharSequence)style)) {
                List<String> split = Arrays.asList(StringUtils.split((String)style, (String)";"));
                Optional colorAttr = Iterables.tryFind(split, (Predicate)new Predicate<String>(){

                    public boolean apply(@Nullable String input) {
                        if (StringUtils.isBlank((CharSequence)input)) {
                            return false;
                        }
                        String trimmed = StringUtils.trimToEmpty((String)StringUtils.substringBefore((String)StringUtils.trimToEmpty((String)input), (String)":"));
                        return StringUtils.equalsIgnoreCase((CharSequence)trimmed, (CharSequence)ColorHandler.HTML_ATTRIBUTE_COLOR);
                    }
                });
                color = (String)colorAttr.transform((Function)new Function<String, String>(){

                    @Nullable
                    public String apply(@Nullable String input) {
                        String value = StringUtils.substringAfter((String)StringUtils.trimToEmpty((String)input), (String)":");
                        return StringUtils.trimToEmpty((String)value);
                    }
                }).or((Object)"");
            } else {
                color = "";
            }
        }
        if (StringUtils.isBlank((CharSequence)color)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        ColorTagProperties first = this.colorsInUse.peekFirst();
        if (first != null && first.started) {
            this.fontStyleHandler.stopStylings(accum);
            sb.append(WIKI_COLOR_END);
        }
        if (!isInsideAnyLink || isInsideLinkWithText) {
            if (!isInsideAnyLink || !isUrlInLinkText) {
                sb.append(ColorHandler.WIKI_COLOR_START(color));
                this.fontStyleHandler.startStylings(sb);
                started = true;
            } else {
                started = false;
            }
        } else {
            started = false;
        }
        this.colorsInUse.offerFirst(new ColorTagProperties(color, name, depth, started));
        return sb.toString();
    }

    public String exit(StringBuilder accum, String name, int depth, String precedingWhitespace, boolean isInsideAnyLink, boolean isUrlInLinkText) {
        boolean started;
        if (!this.blockStyleHandler.isFormattingPossible()) {
            return "";
        }
        ColorTagProperties first = this.colorsInUse.pollFirst();
        if (first == null) {
            return "";
        }
        if (!first.tag.equals(name) || first.depth != depth) {
            this.colorsInUse.offerFirst(first);
            return "";
        }
        StringBuilder sb = new StringBuilder();
        ColorHandler.handlePrecedingWhiteSpace(this.fontStyleHandler, accum, sb, precedingWhitespace, first);
        ColorTagProperties nextColor = this.colorsInUse.pollFirst();
        if (!(nextColor == null || isInsideAnyLink && isUrlInLinkText)) {
            sb.append(ColorHandler.WIKI_COLOR_START(nextColor.color));
            this.fontStyleHandler.startStylings(sb);
            started = true;
        } else {
            started = false;
        }
        if (nextColor != null) {
            this.colorsInUse.offerFirst(new ColorTagProperties(nextColor.color, nextColor.tag, nextColor.depth, started));
        }
        return sb.toString();
    }

    public String handleAroundNonSupportedFormatting(StringBuilder accum, String string, String precedingWhitespace, boolean isInsideAnyLink, boolean isInsideLinkWithText, boolean isUrlInLinkText, boolean isStartOrEndOfTable) {
        boolean started;
        string = StringUtils.defaultString((String)string);
        if (!this.blockStyleHandler.isFormattingPossible()) {
            return precedingWhitespace + string;
        }
        if (this.colorsInUse.isEmpty() || isStartOrEndOfTable || isInsideAnyLink && !isInsideLinkWithText) {
            StringBuilder builder = new StringBuilder();
            ColorTagProperties colorTagProperties = this.colorsInUse.pollFirst();
            if (colorTagProperties != null) {
                if (colorTagProperties.started) {
                    this.fontStyleHandler.stopStylings(accum);
                    builder.append(WIKI_COLOR_END);
                }
                this.colorsInUse.offerFirst(new ColorTagProperties(colorTagProperties.color, colorTagProperties.tag, colorTagProperties.depth, false));
            }
            builder.append(precedingWhitespace);
            builder.append(string);
            return builder.toString();
        }
        StringBuilder sb = new StringBuilder();
        ColorTagProperties first = this.colorsInUse.pollFirst();
        ColorHandler.handlePrecedingWhiteSpace(this.fontStyleHandler, accum, sb, precedingWhitespace, first);
        sb.append(string);
        if (!isInsideAnyLink || !isUrlInLinkText) {
            sb.append(ColorHandler.WIKI_COLOR_START(first.color));
            this.fontStyleHandler.startStylings(sb);
            started = true;
        } else {
            started = false;
        }
        this.colorsInUse.offerFirst(new ColorTagProperties(first.color, first.tag, first.depth, started));
        return sb.toString();
    }

    @Nonnull
    public StripResult removeFormatting(String text) {
        Matcher matcher = COLOR_PATTERN.matcher(text);
        String result = text;
        String removed = "";
        while (matcher.matches()) {
            String group = matcher.group(1);
            removed = removed + group;
            result = StringUtils.replaceOnce((String)result, (String)group, (String)"");
            matcher = COLOR_PATTERN.matcher(result);
        }
        return new StripResult(result, removed);
    }

    private static void handlePrecedingWhiteSpace(FontStyleHandler fontStyleHandler, StringBuilder accum, StringBuilder sb, String precedingWhitespace, @Nullable ColorTagProperties colorTagProperties) {
        if (colorTagProperties == null || !colorTagProperties.started) {
            sb.append(precedingWhitespace);
            return;
        }
        fontStyleHandler.stopStylings(accum);
        if (StringUtils.isNotEmpty((CharSequence)precedingWhitespace)) {
            sb.append(WIKI_COLOR_END);
            sb.append(precedingWhitespace);
        } else {
            sb.append(precedingWhitespace);
            sb.append(WIKI_COLOR_END);
        }
    }

    private static class ColorTagProperties {
        final String color;
        final String tag;
        final int depth;
        final boolean started;

        public ColorTagProperties(String color, String tag, int depth, boolean started) {
            this.color = StringUtils.defaultString((String)color);
            this.tag = StringUtils.defaultString((String)tag);
            this.depth = depth;
            this.started = started;
        }
    }

    public static class StripResult {
        private final String result;
        private final String removed;

        public StripResult(String result, String removed) {
            this.result = result;
            this.removed = removed;
        }

        public String getResult() {
            return this.result;
        }

        public String getRemoved() {
            return this.removed;
        }
    }
}

