/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.table;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.radeox.macro.Repository;
import org.radeox.macro.table.Function;
import org.radeox.macro.table.FunctionRepository;

public class Table {
    private int indexRow = 0;
    private int indexCol = 0;
    private List rows = new ArrayList(10);
    private List currentRow = new ArrayList(10);
    private List functionOccurences;
    private Repository functions = FunctionRepository.getInstance();

    private void addFunction(Function function) {
        this.functions.put(function.getName().toLowerCase(), function);
    }

    public Object getXY(int x, int y) {
        return ((List)this.rows.get(y)).get(x);
    }

    public void setXY(int x, int y, Object content) {
        ((List)this.rows.get(y)).set(x, content);
    }

    public void addCell(String content) {
        if ((content = content.trim()).startsWith("=")) {
            if (null == this.functionOccurences) {
                this.functionOccurences = new ArrayList();
            }
            this.functionOccurences.add(new int[]{this.indexCol, this.indexRow});
        }
        this.currentRow.add(content);
        ++this.indexCol;
    }

    public void newRow() {
        this.rows.add(this.currentRow);
        ++this.indexRow;
        this.currentRow = new ArrayList(this.indexCol);
        this.indexCol = 0;
    }

    public void calc() {
        if (null != this.functionOccurences) {
            Iterator iterator = this.functionOccurences.iterator();
            while (iterator.hasNext()) {
                int tmp;
                int[] position = (int[])iterator.next();
                String functionString = ((String)this.getXY(position[0], position[1])).trim();
                String name = functionString.substring(1, functionString.indexOf("(")).trim().toLowerCase();
                String range = functionString.substring(functionString.indexOf("(") + 1, functionString.indexOf(")"));
                int colon = range.indexOf(":");
                String start = range.substring(0, colon).trim();
                String end = range.substring(colon + 1).trim();
                int startX = start.charAt(0) - 65;
                int startY = Integer.parseInt(start.substring(1)) - 1;
                int endX = end.charAt(0) - 65;
                int endY = Integer.parseInt(end.substring(1)) - 1;
                if (startX > endX) {
                    tmp = startX;
                    startX = endX;
                    endX = tmp;
                }
                if (startY > endY) {
                    tmp = startY;
                    startY = endY;
                    endY = tmp;
                }
                if (!this.functions.containsKey(name)) continue;
                Function function = (Function)this.functions.get(name);
                function.execute(this, position[0], position[1], startX, startY, endX, endY);
            }
        }
    }

    public Writer appendTo(Writer writer) throws IOException {
        writer.write("<table class=\"wiki-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
        List[] outputRows = this.rows.toArray(new List[0]);
        int rowSize = outputRows.length;
        boolean odd = true;
        for (int i = 0; i < rowSize; ++i) {
            writer.write("<tr");
            if (i == 0) {
                writer.write(">");
            } else if (odd) {
                writer.write(" class=\"table-odd\">");
                odd = false;
            } else {
                writer.write(" class=\"table-even\">");
                odd = true;
            }
            String[] outputCols = outputRows[i].toArray(new String[0]);
            int colSize = outputCols.length;
            for (int j = 0; j < colSize; ++j) {
                writer.write(i == 0 ? "<th>" : "<td>");
                if (outputCols[j] == null || outputCols[j].trim().length() == 0) {
                    writer.write("&#160;");
                } else {
                    writer.write(outputCols[j]);
                }
                writer.write(i == 0 ? "</th>" : "</td>");
            }
            writer.write("</tr>");
        }
        writer.write("</table>");
        return writer;
    }
}

