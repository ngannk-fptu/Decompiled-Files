/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.list;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.radeox.macro.list.ListFormatter;
import org.radeox.util.Linkable;
import org.radeox.util.Nameable;

public class AtoZListFormatter
implements ListFormatter {
    public String getName() {
        return "atoz";
    }

    private String removeParents(String name) {
        int index = name.lastIndexOf("/");
        if (-1 == index) {
            return name;
        }
        if (name.length() == index + 1) {
            return name.substring(0, index);
        }
        return name.substring(index + 1);
    }

    public void format(Writer writer, Linkable current, String listComment, Collection c, String emptyText, boolean showSize) throws IOException {
        if (c.size() > 0) {
            Iterator it = c.iterator();
            HashMap atozMap = new HashMap();
            TreeMap<String, String> numberRestList = new TreeMap<String, String>();
            TreeMap<String, String> otherRestList = new TreeMap<String, String>();
            while (it.hasNext()) {
                Object object = it.next();
                String name = object instanceof Nameable ? ((Nameable)object).getName() : object.toString();
                String finalName = this.removeParents(name);
                String indexChar = finalName.substring(0, 1).toUpperCase();
                if (object instanceof Linkable) {
                    name = ((Linkable)object).getLink();
                }
                if (indexChar.charAt(0) >= 'A' && indexChar.charAt(0) <= 'Z') {
                    if (!atozMap.containsKey(indexChar)) {
                        atozMap.put(indexChar, new TreeMap());
                    }
                    Map list = (Map)atozMap.get(indexChar);
                    list.put(finalName, name);
                    continue;
                }
                if (indexChar.charAt(0) >= '0' && indexChar.charAt(0) <= '9') {
                    numberRestList.put(finalName, name);
                    continue;
                }
                otherRestList.put(finalName, name);
            }
            writer.write("<table width=\"100%\" class=\"index-top\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
            int idxChar = 65;
            while (idxChar <= 90) {
                writer.write("<tr>");
                for (int i = 0; i < 6 && idxChar + i <= 90; ++i) {
                    String ch = "" + (char)(idxChar + i);
                    writer.write("<th><b> &nbsp;<a href=\"");
                    writer.write(current.getLink());
                    writer.write("#idx" + ch + "\">");
                    writer.write(ch);
                    writer.write("</a></b></th>");
                    writer.write("<th>...</th><th>");
                    writer.write("" + (atozMap.get(ch) == null ? 0 : ((Map)atozMap.get(ch)).size()));
                    writer.write("&nbsp; </th>");
                }
                if ((idxChar += 6) >= 90) {
                    writer.write("<th><b> &nbsp;<a href=\"");
                    writer.write(current.getLink());
                    writer.write("#idx0-9\">0-9</a></b></th>");
                    writer.write("<th>...</th><th>");
                    writer.write("" + numberRestList.size());
                    writer.write("&nbsp; </th>");
                    writer.write("<th><b> &nbsp;<a href=\"");
                    writer.write(current.getLink());
                    writer.write("#idxAT\">@</a></b></th>");
                    writer.write("<th>...</th><th>");
                    writer.write("" + otherRestList.size());
                    writer.write("&nbsp; </th>");
                    writer.write("<th></th><th></th><th></th><th></th>");
                    writer.write("<th></th><th></th><th></th><th></th>");
                }
                writer.write("</tr>");
            }
            writer.write("</table>");
            writer.write("<div class=\"list-title\">");
            writer.write(listComment);
            if (showSize) {
                writer.write(" (");
                writer.write("" + c.size());
                writer.write(")");
            }
            writer.write("</div>");
            writer.write("<table width=\"100%\" class=\"index\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
            for (int ch = 65; ch <= 90; ch += 2) {
                String left = "" + (char)ch;
                String right = "" + (char)(ch + 1);
                this.insertCharHeader(writer, left, right);
                this.addRows(writer, (Map)atozMap.get(left), (Map)atozMap.get(right));
            }
            this.insertCharHeader(writer, "0-9", "@");
            this.addRows(writer, numberRestList, otherRestList);
            writer.write("</table>");
        } else {
            writer.write(emptyText);
        }
    }

    private void addRows(Writer writer, Map listLeft, Map listRight) throws IOException {
        Iterator rightIt;
        Iterator leftIt = listLeft != null ? listLeft.values().iterator() : new EmptyIterator();
        Iterator iterator = rightIt = listRight != null ? listRight.values().iterator() : new EmptyIterator();
        while (leftIt.hasNext() || rightIt.hasNext()) {
            String leftName = leftIt != null && leftIt.hasNext() ? leftIt.next() : null;
            String rightName = rightIt != null && rightIt.hasNext() ? rightIt.next() : null;
            this.insertRow(writer, leftName, rightName, false);
        }
    }

    private void insertCharHeader(Writer writer, String leftHeader, String rightHeader) throws IOException {
        writer.write("<tr><th>");
        writer.write("<b><a name=\"idx");
        writer.write("@".equals(leftHeader) ? "AT" : leftHeader);
        writer.write("\"></a>");
        writer.write(leftHeader);
        writer.write("</b></th><th> </th><th>");
        writer.write("<b><a name=\"idx");
        writer.write("@".equals(rightHeader) ? "AT" : rightHeader);
        writer.write("\"></a>");
        writer.write(rightHeader);
        writer.write("</b></th></tr>");
    }

    private void insertRow(Writer writer, String left, String right, boolean odd) throws IOException {
        writer.write("<tr><td>");
        if (left != null) {
            writer.write(left);
        }
        writer.write("</td><td> </td><td>");
        if (right != null) {
            writer.write(right);
        }
        writer.write("</td></tr>");
    }

    private class EmptyIterator
    implements Iterator {
        private EmptyIterator() {
        }

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            return null;
        }

        public void remove() {
        }
    }
}

