/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.v2.components.table;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.block.BlockRenderer;
import com.atlassian.renderer.v2.components.block.LineWalker;
import com.atlassian.renderer.v2.components.table.Table;
import com.atlassian.renderer.v2.components.table.TableCell;
import com.atlassian.renderer.v2.components.table.TableRow;
import com.opensymphony.util.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class TableBlockRenderer
implements BlockRenderer {
    private static final Pattern START_TABLE_LINE_PATTERN = Pattern.compile("\\s*\\|.*");
    private static final Pattern END_TABLE_LINE_PATTERN = Pattern.compile(".*\\|\\s*");

    @Override
    public String renderNextBlock(String thisLine, LineWalker nextLines, RenderContext context, SubRenderer subRenderer) {
        if (!context.getRenderMode().renderTables()) {
            return null;
        }
        String line = thisLine;
        Matcher matcher = START_TABLE_LINE_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return null;
        }
        Table table = new Table();
        ArrayList<String> potentialLines = new ArrayList<String>();
        if (END_TABLE_LINE_PATTERN.matcher(line).matches()) {
            table.addRow(this.prerenderLinks(subRenderer, line, context));
        } else {
            potentialLines.add(this.prerenderLinks(subRenderer, line, context));
        }
        while (nextLines.hasNext()) {
            line = nextLines.next();
            if (RenderUtils.isBlank(line) || potentialLines.isEmpty() && !START_TABLE_LINE_PATTERN.matcher(line).matches()) {
                nextLines.pushBack(line);
                break;
            }
            if (START_TABLE_LINE_PATTERN.matcher(line).matches() && !potentialLines.isEmpty()) {
                this.addNextRow(table, potentialLines);
            }
            potentialLines.add(this.prerenderLinks(subRenderer, line, context));
            if (!END_TABLE_LINE_PATTERN.matcher(line).matches()) continue;
            this.addNextRow(table, potentialLines);
        }
        if (!potentialLines.isEmpty()) {
            table.addRow(TextUtils.join((String)"\n", potentialLines));
        }
        return this.renderTable(table, subRenderer, context);
    }

    protected String renderTable(Table table, SubRenderer subRenderer, RenderContext renderContext) {
        StringBuilder result = new StringBuilder("<table><tbody>\n");
        for (TableRow row : table.rows) {
            result.append("<tr>\n");
            for (TableCell cell : row.cells) {
                String renderedContent = "";
                String cellContents = StringUtils.defaultString((String)cell.getContent());
                if (StringUtils.isNotBlank((String)cellContents)) {
                    renderedContent = subRenderer.render(cellContents, renderContext, RenderMode.TABLE_CELL);
                }
                if (StringUtils.isBlank((String)renderedContent)) {
                    renderedContent = "<p>&nbsp;</p>";
                }
                if (cell.isHeader()) {
                    result.append("<th>").append(renderedContent).append("</th>");
                } else {
                    result.append("<td>").append(renderedContent).append("</td>");
                }
                result.append("\n");
            }
            result.append("</tr>\n");
        }
        result.append("</tbody></table>\n");
        return result.toString();
    }

    private String prerenderLinks(SubRenderer subRenderer, String line, RenderContext context) {
        return subRenderer.render(line, context, context.getRenderMode().and(RenderMode.allow(8236L)));
    }

    private void addNextRow(Table table, ArrayList potentialLines) {
        table.addRow(TextUtils.join((String)"\n", (Collection)potentialLines));
        potentialLines.clear();
    }
}

