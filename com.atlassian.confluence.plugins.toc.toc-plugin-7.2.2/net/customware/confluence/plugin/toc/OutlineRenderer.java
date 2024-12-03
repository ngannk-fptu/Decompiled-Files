/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.HtmlUtil
 *  org.apache.commons.lang3.StringUtils
 */
package net.customware.confluence.plugin.toc;

import com.atlassian.confluence.util.HtmlUtil;
import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import net.customware.confluence.plugin.toc.DocumentOutline;
import net.customware.confluence.plugin.toc.OutputHandler;
import org.apache.commons.lang3.StringUtils;

public class OutlineRenderer {
    private final String cssClass;
    private final int minLevel;
    private final int maxLevel;
    private final String urlPrefix;
    private final String includeRegex;
    private final String excludeRegex;
    private final boolean outlineNumbering;

    public OutlineRenderer(String cssClass, int minLevel, int maxLevel, String urlPrefix, String includeRegex, String excludeRegex, boolean outlineNumbering) {
        this.cssClass = cssClass;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.urlPrefix = urlPrefix;
        this.includeRegex = includeRegex;
        this.excludeRegex = excludeRegex;
        this.outlineNumbering = outlineNumbering;
    }

    public String render(DocumentOutline outline, OutputHandler handler) throws IOException {
        StringBuilder buffer = new StringBuilder(1000);
        String cssClass = this.appendCssClass(handler, buffer);
        buffer.append("<div class='toc-macro");
        if (StringUtils.isNotBlank((CharSequence)cssClass)) {
            buffer.append(" ").append(HtmlUtil.htmlEncode((String)cssClass));
        }
        buffer.append("'>");
        handler.appendPrefix(buffer);
        int previousLevel = -1;
        int startLevel = -1;
        LinkedList<Integer> outlineStack = new LinkedList<Integer>();
        int outlineNumber = 0;
        Iterator<DocumentOutline.Heading> headingIterator = outline.iterator(this.minLevel, this.maxLevel, this.includeRegex, this.excludeRegex);
        while (headingIterator.hasNext()) {
            DocumentOutline.Heading heading = headingIterator.next();
            if (previousLevel == -1) {
                startLevel = previousLevel = heading.getEffectiveLevel();
            } else {
                while (previousLevel < heading.getEffectiveLevel()) {
                    handler.appendIncLevel(buffer);
                    if (this.outlineNumbering) {
                        outlineNumber = OutlineRenderer.pushOutline(outlineNumber, outlineStack);
                    }
                    ++previousLevel;
                }
                while (previousLevel > heading.getEffectiveLevel()) {
                    handler.appendDecLevel(buffer);
                    if (this.outlineNumbering) {
                        outlineNumber = OutlineRenderer.popOutline(outlineStack);
                    }
                    --previousLevel;
                }
            }
            handler.appendSeparator(buffer);
            handler.appendHeading(buffer, this.buildItem(outlineStack, ++outlineNumber, heading));
        }
        while (previousLevel > startLevel) {
            handler.appendDecLevel(buffer);
            if (!this.outlineNumbering || --previousLevel <= startLevel) continue;
            OutlineRenderer.popOutline(outlineStack);
        }
        handler.appendPostfix(buffer);
        buffer.append("</div>");
        return buffer.toString();
    }

    private String buildItem(Deque<Integer> outlineStack, int outlineNumber, DocumentOutline.Heading heading) {
        StringBuilder item = new StringBuilder();
        if (this.outlineNumbering) {
            OutlineRenderer.appendOutline(item, outlineStack, outlineNumber);
        }
        if (StringUtils.isNotBlank((CharSequence)heading.getAnchor())) {
            item.append("<a href='").append(this.urlPrefix).append("#").append(HtmlUtil.htmlEncode((String)heading.getAnchor())).append("'>");
        }
        item.append(HtmlUtil.htmlEncode((String)heading.getName()));
        if (StringUtils.isNotBlank((CharSequence)heading.getAnchor())) {
            item.append("</a>");
        }
        return item.toString();
    }

    private String appendCssClass(OutputHandler handler, StringBuilder buffer) throws IOException {
        if (StringUtils.isBlank((CharSequence)this.cssClass)) {
            return handler.appendStyle(buffer);
        }
        return this.cssClass;
    }

    private static int popOutline(Deque<Integer> outlineStack) {
        return outlineStack.pop();
    }

    private static int pushOutline(int itemNum, Deque<Integer> outlineStack) {
        if (itemNum == 0) {
            itemNum = 1;
        }
        outlineStack.push(itemNum);
        return 0;
    }

    private static void appendOutline(StringBuilder out, Deque<Integer> outlineStack, int itemNum) {
        out.append("<span class='TOCOutline'>");
        Iterator<Integer> iter = outlineStack.descendingIterator();
        while (iter.hasNext()) {
            out.append(iter.next()).append('.');
        }
        out.append(itemNum).append("</span> ");
    }
}

