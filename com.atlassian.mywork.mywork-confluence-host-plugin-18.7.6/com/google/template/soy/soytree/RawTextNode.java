/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableMap;
import com.google.template.soy.soytree.AbstractSoyNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RawTextNode
extends AbstractSoyNode
implements SoyNode.StandaloneNode {
    private static final Pattern SPECIAL_CHARS_TO_ESCAPE = Pattern.compile("[\n\r\t{}]");
    private static final Map<String, String> SPECIAL_CHAR_TO_TAG = ImmutableMap.builder().put((Object)"\n", (Object)"{\\n}").put((Object)"\r", (Object)"{\\r}").put((Object)"\t", (Object)"{\\t}").put((Object)"{", (Object)"{lb}").put((Object)"}", (Object)"{rb}").build();
    private final String rawText;

    public RawTextNode(int id, String rawText) {
        super(id);
        this.rawText = rawText;
    }

    protected RawTextNode(RawTextNode orig) {
        super(orig);
        this.rawText = orig.rawText;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.RAW_TEXT_NODE;
    }

    public String getRawText() {
        return this.rawText;
    }

    @Override
    public String toSourceString() {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = SPECIAL_CHARS_TO_ESCAPE.matcher(this.rawText);
        while (matcher.find()) {
            String specialCharTag = SPECIAL_CHAR_TO_TAG.get(matcher.group());
            matcher.appendReplacement(sb, Matcher.quoteReplacement(specialCharTag));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public RawTextNode clone() {
        return new RawTextNode(this);
    }
}

