/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.book;

import org.radeox.macro.book.TextFileUrlMapper;
import org.radeox.macro.book.UrlMapper;

public class AsinServices
extends TextFileUrlMapper {
    private static AsinServices instance;
    static /* synthetic */ Class class$org$radeox$macro$book$AsinServices;

    public AsinServices() {
        super(class$org$radeox$macro$book$AsinServices == null ? (class$org$radeox$macro$book$AsinServices = AsinServices.class$("org.radeox.macro.book.AsinServices")) : class$org$radeox$macro$book$AsinServices);
    }

    public static synchronized UrlMapper getInstance() {
        if (null == instance) {
            instance = new AsinServices();
        }
        return instance;
    }

    public String getFileName() {
        return "conf/asinservices.txt";
    }

    public String getKeyName() {
        return "asin";
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

