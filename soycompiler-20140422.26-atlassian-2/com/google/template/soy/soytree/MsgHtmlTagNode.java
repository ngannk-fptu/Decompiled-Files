/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableMap;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.soytree.AbstractBlockNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class MsgHtmlTagNode
extends AbstractBlockNode
implements SoyNode.MsgPlaceholderInitialNode {
    private static final Pattern PHNAME_ATTR_PATTERN = Pattern.compile("\\s phname=\" ( [^\"]* ) \"", 4);
    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("(?<= ^< ) /? [a-zA-Z0-9]+", 4);
    private static final Map<String, String> LC_TAG_NAME_TO_PLACEHOLDER_NAME_MAP = ImmutableMap.builder().put((Object)"a", (Object)"link").put((Object)"br", (Object)"break").put((Object)"b", (Object)"bold").put((Object)"i", (Object)"italic").put((Object)"li", (Object)"item").put((Object)"ol", (Object)"ordered_list").put((Object)"ul", (Object)"unordered_list").put((Object)"p", (Object)"paragraph").put((Object)"img", (Object)"image").put((Object)"em", (Object)"emphasis").build();
    private final String lcTagName;
    private final boolean isSelfEnding;
    private final boolean isOnlyRawText;
    @Nullable
    private final String fullTagText;
    @Nullable
    private final String userSuppliedPlaceholderName;

    public MsgHtmlTagNode(int id, List<SoyNode.StandaloneNode> children) throws SoySyntaxException {
        super(id);
        int numChildren = children.size();
        String userSuppliedPlaceholderName = null;
        for (int i = 0; i < numChildren; ++i) {
            boolean didReplaceChild;
            SoyNode.StandaloneNode child = children.get(i);
            if (!(child instanceof RawTextNode)) continue;
            do {
                String rawText;
                Matcher matcher;
                if ((matcher = PHNAME_ATTR_PATTERN.matcher(rawText = ((RawTextNode)child).getRawText())).find()) {
                    if (userSuppliedPlaceholderName != null) {
                        throw SoySyntaxException.createWithoutMetaInfo("Found multiple 'phname' attributes in HTML tag (phname=\"" + userSuppliedPlaceholderName + "\" and phname=\"" + matcher.group(1) + "\").");
                    }
                    userSuppliedPlaceholderName = matcher.group(1);
                    if (!BaseUtils.isIdentifier(userSuppliedPlaceholderName)) {
                        throw SoySyntaxException.createWithoutMetaInfo("Found 'phname' attribute in HTML tag that is not a valid identifier (phname=\"" + userSuppliedPlaceholderName + "\").");
                    }
                    RawTextNode replacementChild = new RawTextNode(child.getId(), rawText.replaceFirst(matcher.group(), ""));
                    children.set(i, replacementChild);
                    child = replacementChild;
                    didReplaceChild = true;
                    continue;
                }
                didReplaceChild = false;
            } while (didReplaceChild);
        }
        this.userSuppliedPlaceholderName = userSuppliedPlaceholderName;
        String firstChildText = ((RawTextNode)children.get(0)).getRawText();
        Matcher matcher = TAG_NAME_PATTERN.matcher(firstChildText);
        if (!matcher.find()) {
            if (firstChildText.startsWith("<!--")) {
                throw SoySyntaxException.createWithoutMetaInfo("Found HTML comment within 'msg' block: " + firstChildText);
            }
            throw SoySyntaxException.createWithoutMetaInfo("HTML tag within 'msg' block has no tag name: " + firstChildText);
        }
        this.lcTagName = matcher.group().toLowerCase(Locale.ENGLISH);
        String lastChildText = ((RawTextNode)children.get(numChildren - 1)).getRawText();
        this.isSelfEnding = lastChildText.endsWith("/>");
        boolean bl = this.isOnlyRawText = numChildren == 1;
        if (this.isOnlyRawText) {
            StringBuilder fullTagTextSb = new StringBuilder();
            for (SoyNode.StandaloneNode child : children) {
                fullTagTextSb.append(child.toSourceString());
            }
            this.fullTagText = fullTagTextSb.toString();
        } else {
            this.fullTagText = null;
        }
        this.addChildren(children);
    }

    protected MsgHtmlTagNode(MsgHtmlTagNode orig) {
        super(orig);
        this.lcTagName = orig.lcTagName;
        this.isSelfEnding = orig.isSelfEnding;
        this.isOnlyRawText = orig.isOnlyRawText;
        this.fullTagText = orig.fullTagText;
        this.userSuppliedPlaceholderName = orig.userSuppliedPlaceholderName;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.MSG_HTML_TAG_NODE;
    }

    public String getLcTagName() {
        return this.lcTagName;
    }

    @Nullable
    public String getFullTagText() {
        return this.fullTagText;
    }

    @Override
    public String getUserSuppliedPhName() {
        return this.userSuppliedPlaceholderName;
    }

    @Override
    public String genBasePhName() {
        String basePlaceholderName;
        String baseLcTagName;
        boolean isEndTag;
        if (this.userSuppliedPlaceholderName != null) {
            return BaseUtils.convertToUpperUnderscore(this.userSuppliedPlaceholderName);
        }
        if (this.lcTagName.startsWith("/")) {
            isEndTag = true;
            baseLcTagName = this.lcTagName.substring(1);
        } else {
            isEndTag = false;
            baseLcTagName = this.lcTagName;
        }
        String string = basePlaceholderName = LC_TAG_NAME_TO_PLACEHOLDER_NAME_MAP.containsKey(baseLcTagName) ? LC_TAG_NAME_TO_PLACEHOLDER_NAME_MAP.get(baseLcTagName) : baseLcTagName;
        if (isEndTag) {
            basePlaceholderName = "end_" + basePlaceholderName;
        } else if (!this.isSelfEnding) {
            basePlaceholderName = "start_" + basePlaceholderName;
        }
        return basePlaceholderName.toUpperCase();
    }

    @Override
    public Object genSamenessKey() {
        return this.isOnlyRawText ? Pair.of(this.userSuppliedPlaceholderName, this.fullTagText) : Integer.valueOf(this.getId());
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        this.appendSourceStringForChildren(sb);
        if (this.userSuppliedPlaceholderName != null) {
            int indexBeforeClose;
            if (this.isSelfEnding ? !sb.substring(indexBeforeClose = sb.length() - 2).equals("/>") : !sb.substring(indexBeforeClose = sb.length() - 1).equals(">")) {
                throw new AssertionError();
            }
            sb.insert(indexBeforeClose, " phname=\"" + this.userSuppliedPlaceholderName + "\"");
        }
        return sb.toString();
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public MsgHtmlTagNode clone() {
        return new MsgHtmlTagNode(this);
    }
}

