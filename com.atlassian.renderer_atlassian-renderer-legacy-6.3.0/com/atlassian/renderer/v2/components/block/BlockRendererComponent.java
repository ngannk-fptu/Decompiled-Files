/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.v2.components.block;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.RendererComponent;
import com.atlassian.renderer.v2.components.block.BlockRenderer;
import com.atlassian.renderer.v2.components.block.LineWalker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class BlockRendererComponent
implements RendererComponent {
    private static final Pattern BLOCK_AND_WHITESPACE_PATTERN = Pattern.compile("(\\s*" + TokenType.BLOCK.getTokenPatternString() + ")+\\s*", 8);
    private static final Pattern INLINE_BLOCK_AND_WHITESPACE_PATTERN = Pattern.compile("(\\s*" + TokenType.INLINE_BLOCK.getTokenPatternString() + ")+\\s*", 8);
    private static final Pattern SINGLE_LINE_PARA = Pattern.compile("[\\s&&[^\n]]*[\\p{Alnum}&&[^PLhb]][^\n]*");
    private static final Pattern SINGLE_HTML_PARAGRAPH_PATTERN = Pattern.compile("<p>(.*)</p>", 32);
    private static final Pattern INLINE_WIKI_MARKUP_MACRO = Pattern.compile("(^<ac:macro ac:name=\")(unmigrated-inline-wiki-markup)(\".+</ac:macro>$)", 32);
    private BlockRenderer[] blockRenderers;
    private SubRenderer subRenderer;

    public BlockRendererComponent(SubRenderer subRenderer, List<? extends BlockRenderer> blockRenderers) {
        this.subRenderer = subRenderer;
        this.blockRenderers = blockRenderers.toArray(new BlockRenderer[blockRenderers.size()]);
    }

    public void setBlockRenderers(List<? extends BlockRenderer> blockRenderers) {
        this.blockRenderers = blockRenderers.toArray(new BlockRenderer[blockRenderers.size()]);
    }

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderParagraphs();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        if (SINGLE_LINE_PARA.matcher(wiki).matches() && !BlockRendererComponent.containsBlockTokens(wiki) && !BlockRendererComponent.containsInlineBlockTokens(wiki)) {
            String renderedContent = this.renderParagraph(context, wiki, Collections.emptyList());
            return context.addRenderedContent(this.handleFirstParagraphRenderFlag(renderedContent, context), TokenType.BLOCK);
        }
        LineWalker walker = new LineWalker(wiki);
        ArrayList<String> renderedLines = new ArrayList<String>();
        ArrayList<String> paragraph = new ArrayList<String>();
        while (walker.hasNext()) {
            String nextLine = walker.next();
            String rendered = null;
            if (BlockRendererComponent.isOneBlockToken(nextLine)) {
                rendered = nextLine;
            } else if (BlockRendererComponent.isOneInlineBlockToken(nextLine)) {
                rendered = nextLine;
            } else if (!SINGLE_LINE_PARA.matcher(wiki).matches()) {
                rendered = this.applyBlockRenderers(context, walker, nextLine, rendered);
            }
            if (rendered == null) {
                List<String> linePortions = BlockRendererComponent.splitLineByBlockTokens(nextLine);
                for (String linePortion : linePortions) {
                    if (!TokenType.BLOCK.getTokenPattern().matcher(linePortion).matches()) {
                        paragraph.add(linePortion);
                        continue;
                    }
                    this.flushParagraph(renderedLines, paragraph, context);
                    renderedLines.add(linePortion);
                }
                continue;
            }
            this.flushParagraph(renderedLines, paragraph, context);
            renderedLines.add(rendered);
        }
        this.flushParagraph(renderedLines, paragraph, context);
        String renderedContent = renderedLines.size() == 1 ? this.handleFirstParagraphRenderFlag((String)renderedLines.get(0), context) : StringUtils.join(renderedLines.iterator(), (String)"\n");
        return context.addRenderedContent(renderedContent, TokenType.BLOCK);
    }

    private String handleFirstParagraphRenderFlag(String html, RenderContext renderContext) {
        if (renderContext.getRenderMode().renderFirstParagraph()) {
            return html;
        }
        Matcher matcher = SINGLE_HTML_PARAGRAPH_PATTERN.matcher(html);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return html;
    }

    private static boolean isOneBlockToken(String string) {
        return BLOCK_AND_WHITESPACE_PATTERN.matcher(string).matches();
    }

    private static boolean isOneInlineBlockToken(String string) {
        return INLINE_BLOCK_AND_WHITESPACE_PATTERN.matcher(string).matches();
    }

    private static boolean containsBlockTokens(String string) {
        return TokenType.BLOCK.getTokenPattern().matcher(string).find();
    }

    private static boolean containsInlineBlockTokens(String string) {
        return TokenType.INLINE_BLOCK.getTokenPattern().matcher(string).find();
    }

    private String applyBlockRenderers(RenderContext context, LineWalker walker, String nextLine, String rendered) {
        BlockRenderer blockRenderer;
        BlockRenderer[] blockRendererArray = this.blockRenderers;
        int n = blockRendererArray.length;
        for (int i = 0; i < n && (rendered = (blockRenderer = blockRendererArray[i]).renderNextBlock(nextLine, walker, context, this.subRenderer)) == null; ++i) {
        }
        return rendered;
    }

    static List<String> splitLineByBlockTokens(String line) {
        StringBuffer buffer;
        ArrayList<String> result = new ArrayList<String>();
        if (line.length() == 0) {
            result.add(line);
            return result;
        }
        Matcher matcher = TokenType.BLOCK.getTokenPattern().matcher(line);
        while (matcher.find()) {
            buffer = new StringBuffer();
            matcher.appendReplacement(buffer, "");
            if (buffer.length() > 0) {
                result.add(buffer.toString());
            }
            result.add(matcher.group());
        }
        buffer = new StringBuffer();
        matcher.appendTail(buffer);
        if (buffer.length() > 0) {
            result.add(buffer.toString());
        }
        return result;
    }

    private void flushParagraph(List<String> renderedLines, List<String> remainderedLines, RenderContext context) {
        if (remainderedLines.isEmpty()) {
            return;
        }
        String paragraph = StringUtils.join(remainderedLines.iterator(), (String)"\n");
        renderedLines.add(this.renderParagraph(context, paragraph, renderedLines));
        remainderedLines.clear();
    }

    private String renderParagraph(RenderContext context, String paragraph, List<String> renderedLines) {
        return this.wrapInParagraphIfRequired(context, this.subRenderer.render(paragraph, context, context.getRenderMode().and(RenderMode.INLINE)));
    }

    private String wrapInParagraphIfRequired(RenderContext context, String content) {
        if (RenderMode.LIST_ITEM.equals(context.getRenderMode())) {
            return content;
        }
        return "<p>" + content + "</p>";
    }
}

