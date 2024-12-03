/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.list;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import org.radeox.macro.list.ListFormatter;
import org.radeox.util.Linkable;
import org.radeox.util.Nameable;

public class ExampleListFormatter
implements ListFormatter {
    public String getName() {
        return "number";
    }

    public void format(Writer writer, Linkable current, String listComment, Collection c, String emptyText, boolean showSize) throws IOException {
        writer.write("<div class=\"list\"><div class=\"list-title\">");
        writer.write(listComment);
        if (showSize) {
            writer.write(" (");
            writer.write("" + c.size());
            writer.write(")");
        }
        writer.write("</div>");
        if (c.size() > 0) {
            writer.write("<ol>");
            Iterator nameIterator = c.iterator();
            while (nameIterator.hasNext()) {
                writer.write("<li>");
                Object object = nameIterator.next();
                if (object instanceof Linkable) {
                    writer.write(((Linkable)object).getLink());
                } else if (object instanceof Nameable) {
                    writer.write(((Nameable)object).getName());
                } else {
                    writer.write(object.toString());
                }
                writer.write("</li>");
            }
            writer.write("</ol>");
        } else {
            writer.write(emptyText);
        }
        writer.write("</div>");
    }
}

