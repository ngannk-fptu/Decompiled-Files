/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.template.soy.internal.base.UnescapeUtils;
import com.google.template.soy.parsepasses.contextautoesc.Context;
import com.google.template.soy.parsepasses.contextautoesc.JsUtil;
import com.google.template.soy.parsepasses.contextautoesc.SlicedRawTextNode;
import com.google.template.soy.parsepasses.contextautoesc.SoyAutoescapeException;
import com.google.template.soy.soytree.RawTextNode;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class RawTextContextUpdater {
    private int numCharsConsumed;
    private Context next;
    private static final Set<String> URI_ATTR_NAMES = ImmutableSet.of((Object)"action", (Object)"archive", (Object)"base", (Object)"background", (Object)"cite", (Object)"classid", (Object[])new String[]{"codebase", "data", "dsync", "formaction", "href", "icon", "longdesc", "manifest", "poster", "src", "usemap", "entity"});
    private static final Pattern CUSTOM_URI_ATTR_NAMING_CONVENTION = Pattern.compile("\\bur[il]|ur[il]s?$");
    private static final Transition TRANSITION_TO_SELF = RawTextContextUpdater.makeTransitionToSelf("\\z");
    private static final Transition URI_PART_TRANSITION = new Transition("[?#]|\\z"){

        @Override
        boolean isApplicableTo(Context prior, Matcher matcher) {
            return true;
        }

        @Override
        Context computeNextContext(Context prior, Matcher matcher) {
            Context.UriPart uriPart = prior.uriPart;
            if (uriPart == Context.UriPart.START) {
                uriPart = Context.UriPart.PRE_QUERY;
            }
            if (uriPart != Context.UriPart.FRAGMENT) {
                String match = matcher.group(0);
                if ("?".equals(match) && uriPart != Context.UriPart.UNKNOWN) {
                    uriPart = Context.UriPart.QUERY;
                } else if ("#".equals(match)) {
                    uriPart = Context.UriPart.FRAGMENT;
                }
            }
            return prior.derive(uriPart);
        }
    };
    private static final String JS_LINEBREAKS = "\r\n\u2028\u2029";
    private static final Map<Context.State, List<Transition>> TRANSITIONS = ImmutableMap.builder().put((Object)Context.State.HTML_PCDATA, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionTo("<!--", Context.HTML_COMMENT), (Object)RawTextContextUpdater.makeTransitionToTag("(?i)<script(?=[\\s>/]|\\z)", Context.ElementType.SCRIPT), (Object)RawTextContextUpdater.makeTransitionToTag("(?i)<style(?=[\\s>/]|\\z)", Context.ElementType.STYLE), (Object)RawTextContextUpdater.makeTransitionToTag("(?i)<textarea(?=[\\s>/]|\\z)", Context.ElementType.TEXTAREA), (Object)RawTextContextUpdater.makeTransitionToTag("(?i)<title(?=[\\s>/]|\\z)", Context.ElementType.TITLE), (Object)RawTextContextUpdater.makeTransitionToTag("(?i)<xmp(?=[\\s>/]|\\z)", Context.ElementType.XMP), (Object)RawTextContextUpdater.makeTransitionTo("</?", Context.HTML_BEFORE_TAG_NAME), (Object)RawTextContextUpdater.makeTransitionToSelf("[^<]+"))).put((Object)Context.State.HTML_BEFORE_TAG_NAME, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionTo("^[a-zA-Z]+", Context.HTML_TAG_NAME), (Object)RawTextContextUpdater.makeTransitionTo("^(?=[^a-zA-Z])", Context.HTML_PCDATA))).put((Object)Context.State.HTML_TAG_NAME, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToSelf("^[a-zA-Z0-9:-]*(?:[a-zA-Z0-9]|\\z)"), (Object)RawTextContextUpdater.makeTransitionToTag("^(?=[/\\s>])", Context.ElementType.NORMAL))).put((Object)Context.State.HTML_TAG, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToAttrName("(?i)^\\s*([a-z](?:[a-z0-9_:\\-]*[a-z0-9])?)"), (Object)new Transition("^\\s*/?>"){

        @Override
        Context computeNextContext(Context prior, Matcher matcher) {
            switch (prior.elType) {
                case SCRIPT: {
                    return Context.JS;
                }
                case STYLE: {
                    return Context.CSS;
                }
                case NORMAL: {
                    return Context.HTML_PCDATA;
                }
                case NONE: {
                    throw new IllegalStateException();
                }
                case LISTING: 
                case TEXTAREA: 
                case TITLE: 
                case XMP: {
                    return new Context(Context.State.HTML_RCDATA, prior.elType, Context.AttributeType.NONE, Context.AttributeEndDelimiter.NONE, Context.JsFollowingSlash.NONE, Context.UriPart.NONE);
                }
            }
            throw new AssertionError((Object)("Unrecognized state " + (Object)((Object)prior.elType)));
        }
    }, (Object)RawTextContextUpdater.makeTransitionToSelf("^\\s+\\z"))).put((Object)Context.State.HTML_ATTRIBUTE_NAME, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToState("^\\s*=", Context.State.HTML_BEFORE_ATTRIBUTE_VALUE), (Object)RawTextContextUpdater.makeTransitionBackToTag("^"))).put((Object)Context.State.HTML_BEFORE_ATTRIBUTE_VALUE, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToAttrValue("^\\s*\"", Context.AttributeEndDelimiter.DOUBLE_QUOTE), (Object)RawTextContextUpdater.makeTransitionToAttrValue("^\\s*'", Context.AttributeEndDelimiter.SINGLE_QUOTE), (Object)RawTextContextUpdater.makeTransitionToAttrValue("^(?=[^\"'\\s>])", Context.AttributeEndDelimiter.SPACE_OR_TAG_END), (Object)RawTextContextUpdater.makeTransitionBackToTag("^(?=>|\\s+[\\w-]+\\s*=)"), (Object)RawTextContextUpdater.makeTransitionToSelf("^\\s+"))).put((Object)Context.State.HTML_COMMENT, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionTo("-->", Context.HTML_PCDATA), (Object)TRANSITION_TO_SELF)).put((Object)Context.State.HTML_NORMAL_ATTR_VALUE, (Object)ImmutableList.of((Object)TRANSITION_TO_SELF)).put((Object)Context.State.CSS, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToState("/\\*", Context.State.CSS_COMMENT), (Object)RawTextContextUpdater.makeTransitionToState("\"", Context.State.CSS_DQ_STRING), (Object)RawTextContextUpdater.makeTransitionToState("'", Context.State.CSS_SQ_STRING), (Object)RawTextContextUpdater.makeCssUriTransition("(?i)\\burl\\s*\\(\\s*(['\"]?)"), (Object)RawTextContextUpdater.makeEndTagTransition("style"), (Object)TRANSITION_TO_SELF)).put((Object)Context.State.CSS_COMMENT, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToState("\\*/", Context.State.CSS), (Object)RawTextContextUpdater.makeEndTagTransition("style"), (Object)TRANSITION_TO_SELF)).put((Object)Context.State.CSS_DQ_STRING, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToState("\"", Context.State.CSS), (Object)RawTextContextUpdater.makeTransitionToSelf("\\\\(?:\r\n?|[\n\f\"])"), (Object)RawTextContextUpdater.makeTransitionTo("[\n\r\f]", Context.ERROR), (Object)RawTextContextUpdater.makeEndTagTransition("style"), (Object)TRANSITION_TO_SELF)).put((Object)Context.State.CSS_SQ_STRING, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToState("'", Context.State.CSS), (Object)RawTextContextUpdater.makeTransitionToSelf("\\\\(?:\r\n?|[\n\f'])"), (Object)RawTextContextUpdater.makeTransitionTo("[\n\r\f]", Context.ERROR), (Object)RawTextContextUpdater.makeEndTagTransition("style"), (Object)TRANSITION_TO_SELF)).put((Object)Context.State.CSS_URI, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToState("[\\)\\s]", Context.State.CSS), (Object)URI_PART_TRANSITION, (Object)RawTextContextUpdater.makeTransitionToState("[\"']", Context.State.ERROR), (Object)RawTextContextUpdater.makeEndTagTransition("style"))).put((Object)Context.State.CSS_SQ_URI, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToState("'", Context.State.CSS), (Object)URI_PART_TRANSITION, (Object)RawTextContextUpdater.makeTransitionToSelf("\\\\(?:\r\n?|[\n\f'])"), (Object)RawTextContextUpdater.makeTransitionTo("[\n\r\f]", Context.ERROR), (Object)RawTextContextUpdater.makeEndTagTransition("style"))).put((Object)Context.State.CSS_DQ_URI, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToState("\"", Context.State.CSS), (Object)URI_PART_TRANSITION, (Object)RawTextContextUpdater.makeTransitionToSelf("\\\\(?:\r\n?|[\n\f\"])"), (Object)RawTextContextUpdater.makeTransitionTo("[\n\r\f]", Context.ERROR), (Object)RawTextContextUpdater.makeEndTagTransition("style"))).put((Object)Context.State.JS, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToState("/\\*", Context.State.JS_BLOCK_COMMENT), (Object)RawTextContextUpdater.makeTransitionToState("//", Context.State.JS_LINE_COMMENT), (Object)RawTextContextUpdater.makeTransitionToJsString("\"", Context.State.JS_DQ_STRING), (Object)RawTextContextUpdater.makeTransitionToJsString("'", Context.State.JS_SQ_STRING), (Object)new Transition("/"){

        @Override
        Context computeNextContext(Context prior, Matcher matcher) throws SoyAutoescapeException {
            switch (prior.slashType) {
                case DIV_OP: {
                    return new Context(Context.State.JS, prior.elType, prior.attrType, prior.delimType, Context.JsFollowingSlash.REGEX, prior.uriPart);
                }
                case REGEX: {
                    return new Context(Context.State.JS_REGEX, prior.elType, prior.attrType, prior.delimType, Context.JsFollowingSlash.NONE, prior.uriPart);
                }
            }
            StringBuffer rest = new StringBuffer();
            matcher.appendTail(rest);
            throw SoyAutoescapeException.createWithoutMetaInfo("Slash (/) cannot follow the preceding branches since it is unclear whether the slash is a RegExp literal or division operator.  Please add parentheses in the branches leading to `" + rest + "`");
        }
    }, (Object)new Transition("(?i)(?:[^</\"'\\s\\\\]|<(?!/script))+"){

        @Override
        Context computeNextContext(Context prior, Matcher matcher) {
            return prior.derive(JsUtil.isRegexPreceder(matcher.group()) ? Context.JsFollowingSlash.REGEX : Context.JsFollowingSlash.DIV_OP);
        }
    }, (Object)RawTextContextUpdater.makeTransitionToSelf("\\s+"), (Object)RawTextContextUpdater.makeEndTagTransition("script"))).put((Object)Context.State.JS_BLOCK_COMMENT, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToState("\\*/", Context.State.JS), (Object)RawTextContextUpdater.makeEndTagTransition("script"), (Object)TRANSITION_TO_SELF)).put((Object)Context.State.JS_LINE_COMMENT, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeTransitionToState("[\r\n\u2028\u2029]", Context.State.JS), (Object)RawTextContextUpdater.makeEndTagTransition("script"), (Object)TRANSITION_TO_SELF)).put((Object)Context.State.JS_DQ_STRING, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeDivPreceder("\""), (Object)RawTextContextUpdater.makeEndTagTransition("script"), (Object)RawTextContextUpdater.makeTransitionToSelf("(?i)^(?:[^\"\\\\\r\n\u2028\u2029<]|\\\\(?:\\r\\n?|[^\\r<]|<(?!/script))|<(?!/script))+"))).put((Object)Context.State.JS_SQ_STRING, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeDivPreceder("'"), (Object)RawTextContextUpdater.makeEndTagTransition("script"), (Object)RawTextContextUpdater.makeTransitionToSelf("(?i)^(?:[^'\\\\\r\n\u2028\u2029<]|\\\\(?:\\r\\n?|[^\\r<]|<(?!/script))|<(?!/script))+"))).put((Object)Context.State.JS_REGEX, (Object)ImmutableList.of((Object)RawTextContextUpdater.makeDivPreceder("/"), (Object)RawTextContextUpdater.makeEndTagTransition("script"), (Object)RawTextContextUpdater.makeTransitionToSelf("(?i)^(?:[^\\[\\\\/<\r\n\u2028\u2029]|\\\\[^\r\n\u2028\u2029]|\\\\?<(?!/script)|\\[(?:[^\\]\\\\<\r\n\u2028\u2029]|\\\\(?:[^\r\n\u2028\u2029]))*|\\\\?<(?!/script)\\])+"))).put((Object)Context.State.URI, (Object)ImmutableList.of((Object)URI_PART_TRANSITION)).put((Object)Context.State.HTML_RCDATA, (Object)ImmutableList.of((Object)new Transition("</(\\w+)\\b"){

        @Override
        boolean isApplicableTo(Context prior, Matcher matcher) {
            String tagName = matcher.group(1).toUpperCase(Locale.ENGLISH);
            return prior.elType.name().equals(tagName);
        }

        @Override
        Context computeNextContext(Context prior, Matcher matcher) {
            return new Context(Context.State.HTML_TAG, Context.ElementType.NORMAL, Context.AttributeType.NONE, Context.AttributeEndDelimiter.NONE, Context.JsFollowingSlash.NONE, Context.UriPart.NONE);
        }
    }, (Object)TRANSITION_TO_SELF)).put((Object)Context.State.TEXT, (Object)ImmutableList.of((Object)TRANSITION_TO_SELF)).build();

    public static SlicedRawTextNode processRawText(RawTextNode rawTextNode, Context context) throws SoyAutoescapeException {
        SlicedRawTextNode slicedRawTextNode = new SlicedRawTextNode(rawTextNode, context);
        String rawText = rawTextNode.getRawText();
        int offset = 0;
        int length = rawText.length();
        while (offset < length) {
            Context endContext;
            int endOffset;
            String unprocessedRawText = rawText.substring(offset);
            int startOffset = offset;
            Context startContext = context;
            int attrValueEnd = RawTextContextUpdater.findEndOfAttributeValue(unprocessedRawText, context.delimType);
            if (attrValueEnd == -1) {
                RawTextContextUpdater cu = new RawTextContextUpdater();
                cu.processNextToken(unprocessedRawText, context);
                endOffset = offset + cu.numCharsConsumed;
                endContext = cu.next;
            } else {
                int unprocessedRawTextLen = unprocessedRawText.length();
                int attrEnd = attrValueEnd < unprocessedRawTextLen ? attrValueEnd + context.delimType.text.length() : -1;
                String attrValueTail = UnescapeUtils.unescapeHtml(unprocessedRawText.substring(0, attrValueEnd));
                RawTextContextUpdater cu = new RawTextContextUpdater();
                Context attrContext = startContext;
                while (attrValueTail.length() != 0) {
                    cu.processNextToken(attrValueTail, attrContext);
                    attrValueTail = attrValueTail.substring(cu.numCharsConsumed);
                    attrContext = cu.next;
                }
                if (attrEnd != -1) {
                    endOffset = offset + attrEnd;
                    endContext = new Context(Context.State.HTML_TAG, context.elType, Context.AttributeType.NONE, Context.AttributeEndDelimiter.NONE, Context.JsFollowingSlash.NONE, Context.UriPart.NONE);
                } else {
                    if (attrValueEnd != unprocessedRawTextLen) {
                        throw new IllegalStateException();
                    }
                    endOffset = length;
                    endContext = attrContext;
                }
            }
            slicedRawTextNode.addSlice(startOffset, endOffset, startContext);
            context = endContext;
            offset = endOffset;
        }
        slicedRawTextNode.setEndContext(context);
        return slicedRawTextNode;
    }

    private static int findEndOfAttributeValue(String rawText, Context.AttributeEndDelimiter delim) {
        int rawTextLen = rawText.length();
        switch (delim) {
            case DOUBLE_QUOTE: 
            case SINGLE_QUOTE: {
                int quote = rawText.indexOf(delim.text.charAt(0));
                return quote >= 0 ? quote : rawTextLen;
            }
            case SPACE_OR_TAG_END: {
                for (int i = 0; i < rawTextLen; ++i) {
                    char ch = rawText.charAt(i);
                    if (ch != '>' && !Character.isWhitespace(ch)) continue;
                    return i;
                }
                return rawTextLen;
            }
            case NONE: {
                return -1;
            }
        }
        throw new AssertionError((Object)("Unrecognized delimiter " + (Object)((Object)delim)));
    }

    private RawTextContextUpdater() {
    }

    private void processNextToken(String text, Context context) throws SoyAutoescapeException {
        if (context.isErrorContext()) {
            this.numCharsConsumed = text.length();
            this.next = context;
            return;
        }
        int earliestStart = Integer.MAX_VALUE;
        int earliestEnd = -1;
        Transition earliestTransition = null;
        Matcher earliestMatcher = null;
        for (Transition transition : TRANSITIONS.get((Object)context.state)) {
            int start;
            Matcher matcher = transition.pattern.matcher(text);
            if (!matcher.find() || (start = matcher.start()) >= earliestStart) continue;
            int end = matcher.end();
            if (!transition.isApplicableTo(context, matcher)) continue;
            earliestStart = start;
            earliestEnd = end;
            earliestTransition = transition;
            earliestMatcher = matcher;
        }
        if (earliestTransition != null) {
            this.next = earliestTransition.computeNextContext(context, earliestMatcher);
            this.numCharsConsumed = earliestEnd;
        } else {
            this.next = Context.ERROR;
            this.numCharsConsumed = text.length();
        }
        if (this.numCharsConsumed == 0 && this.next.state == context.state) {
            throw new IllegalStateException("Infinite loop at `" + text + "` / " + context);
        }
    }

    private static Transition makeTransitionTo(String regex, final Context dest) {
        return new Transition(regex){

            @Override
            Context computeNextContext(Context prior, Matcher matcher) {
                return dest;
            }
        };
    }

    private static Transition makeTransitionToTag(String regex, final Context.ElementType el) {
        return new Transition(regex){

            @Override
            Context computeNextContext(Context prior, Matcher matcher) {
                return new Context(Context.State.HTML_TAG, el, Context.AttributeType.NONE, Context.AttributeEndDelimiter.NONE, Context.JsFollowingSlash.NONE, Context.UriPart.NONE);
            }
        };
    }

    private static Transition makeTransitionBackToTag(String regex) {
        return new Transition(regex){

            @Override
            Context computeNextContext(Context prior, Matcher matcher) {
                return new Context(Context.State.HTML_TAG, prior.elType, Context.AttributeType.NONE, Context.AttributeEndDelimiter.NONE, Context.JsFollowingSlash.NONE, Context.UriPart.NONE);
            }
        };
    }

    private static Transition makeTransitionToAttrName(String regex) {
        return new Transition(regex){

            @Override
            Context computeNextContext(Context prior, Matcher matcher) {
                int colon;
                String attrName = matcher.group(1).toLowerCase(Locale.ENGLISH);
                String localName = attrName.substring((colon = attrName.lastIndexOf(58)) + 1);
                Context.AttributeType attr = localName.startsWith("on") ? Context.AttributeType.SCRIPT : ("style".equals(localName) ? Context.AttributeType.STYLE : (URI_ATTR_NAMES.contains(localName) || CUSTOM_URI_ATTR_NAMING_CONVENTION.matcher(localName).find() || "xmlns".equals(attrName) || attrName.startsWith("xmlns:") ? Context.AttributeType.URI : Context.AttributeType.PLAIN_TEXT));
                return new Context(Context.State.HTML_ATTRIBUTE_NAME, prior.elType, attr, Context.AttributeEndDelimiter.NONE, Context.JsFollowingSlash.NONE, Context.UriPart.NONE);
            }
        };
    }

    private static Transition makeTransitionToAttrValue(String regex, final Context.AttributeEndDelimiter delim) {
        return new Transition(regex){

            @Override
            Context computeNextContext(Context prior, Matcher matcher) {
                return Context.computeContextAfterAttributeDelimiter(prior.elType, prior.attrType, delim);
            }
        };
    }

    private static Transition makeTransitionToState(String regex, final Context.State state) {
        return new Transition(regex){

            @Override
            Context computeNextContext(Context prior, Matcher matcher) {
                return prior.derive(state).derive(Context.UriPart.NONE);
            }
        };
    }

    private static Transition makeTransitionToJsString(String regex, final Context.State state) {
        return new Transition(regex){

            @Override
            Context computeNextContext(Context prior, Matcher matcher) {
                return new Context(state, prior.elType, prior.attrType, prior.delimType, Context.JsFollowingSlash.NONE, Context.UriPart.NONE);
            }
        };
    }

    private static Transition makeTransitionToSelf(String regex) {
        return new Transition(regex){

            @Override
            Context computeNextContext(Context prior, Matcher matcher) {
                return prior;
            }
        };
    }

    private static Transition makeEndTagTransition(String tagName) {
        return new Transition("(?i)</" + tagName + "\\b"){

            @Override
            boolean isApplicableTo(Context prior, Matcher matcher) {
                return prior.attrType == Context.AttributeType.NONE;
            }

            @Override
            Context computeNextContext(Context prior, Matcher matcher) {
                return new Context(Context.State.HTML_TAG, Context.ElementType.NORMAL, Context.AttributeType.NONE, Context.AttributeEndDelimiter.NONE, Context.JsFollowingSlash.NONE, Context.UriPart.NONE);
            }
        };
    }

    private static Transition makeCssUriTransition(String regex) {
        return new Transition(regex){

            @Override
            Context computeNextContext(Context prior, Matcher matcher) {
                String delim = matcher.group(1);
                Context.State state = "\"".equals(delim) ? Context.State.CSS_DQ_URI : ("'".equals(delim) ? Context.State.CSS_SQ_URI : Context.State.CSS_URI);
                return new Context(state, prior.elType, prior.attrType, prior.delimType, prior.slashType, Context.UriPart.START);
            }
        };
    }

    private static Transition makeDivPreceder(String regex) {
        return new Transition(regex){

            @Override
            Context computeNextContext(Context prior, Matcher matcher) {
                return new Context(Context.State.JS, prior.elType, prior.attrType, prior.delimType, Context.JsFollowingSlash.DIV_OP, prior.uriPart);
            }
        };
    }

    private static abstract class Transition {
        final Pattern pattern;

        Transition(Pattern pattern) {
            this.pattern = pattern;
        }

        Transition(String regex) {
            this(Pattern.compile(regex, 32));
        }

        boolean isApplicableTo(Context prior, Matcher matcher) {
            return true;
        }

        abstract Context computeNextContext(Context var1, Matcher var2) throws SoyAutoescapeException;
    }
}

