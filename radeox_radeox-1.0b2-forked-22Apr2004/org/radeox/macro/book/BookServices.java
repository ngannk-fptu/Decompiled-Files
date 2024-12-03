/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.book;

import org.radeox.macro.book.TextFileUrlMapper;
import org.radeox.macro.book.UrlMapper;

public class BookServices
extends TextFileUrlMapper {
    private static BookServices instance;
    static /* synthetic */ Class class$org$radeox$macro$book$BookServices;

    public BookServices() {
        super(class$org$radeox$macro$book$BookServices == null ? (class$org$radeox$macro$book$BookServices = BookServices.class$("org.radeox.macro.book.BookServices")) : class$org$radeox$macro$book$BookServices);
    }

    public static synchronized UrlMapper getInstance() {
        if (null == instance) {
            instance = new BookServices();
        }
        return instance;
    }

    public String getFileName() {
        return "conf/bookservices.txt";
    }

    public String getKeyName() {
        return "isbn";
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

