/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.SubRenderer
 *  com.atlassian.renderer.v2.components.table.Table
 *  com.atlassian.renderer.v2.components.table.TableBlockRenderer
 *  com.atlassian.renderer.v2.components.table.TableCell
 *  com.atlassian.renderer.v2.components.table.TableRow
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.renderer.v2;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.table.Table;
import com.atlassian.renderer.v2.components.table.TableBlockRenderer;
import com.atlassian.renderer.v2.components.table.TableCell;
import com.atlassian.renderer.v2.components.table.TableRow;
import org.apache.commons.lang3.StringUtils;

public class ConfluenceTableBlockRenderer
extends TableBlockRenderer {
    protected String renderTable(Table table, SubRenderer subRenderer, RenderContext renderContext) {
        StringBuilder result = new StringBuilder("<table class=\"confluenceTable\"><tbody>\n");
        for (TableRow row : table.getRows()) {
            result.append("<tr>\n");
            for (TableCell cell : row.getCells()) {
                String renderedContent = "";
                String cellContents = StringUtils.defaultString((String)cell.getContent());
                if (StringUtils.isNotBlank((CharSequence)cellContents)) {
                    renderedContent = subRenderer.render(cellContents, renderContext, RenderMode.TABLE_CELL);
                }
                if (StringUtils.isBlank((CharSequence)renderedContent)) {
                    renderedContent = "<p>&nbsp;</p>";
                }
                if (cell.isHeader()) {
                    result.append("<th class=\"confluenceTh\">").append(renderedContent).append("</th>");
                } else {
                    result.append("<td class=\"confluenceTd\">").append(renderedContent).append("</td>");
                }
                result.append("\n");
            }
            result.append("</tr>\n");
        }
        result.append("</tbody></table>\n");
        return result.toString();
    }
}

