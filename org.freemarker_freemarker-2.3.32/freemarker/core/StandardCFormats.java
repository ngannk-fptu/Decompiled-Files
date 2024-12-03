/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CFormat;
import freemarker.core.JSONCFormat;
import freemarker.core.JavaCFormat;
import freemarker.core.JavaScriptCFormat;
import freemarker.core.JavaScriptOrJSONCFormat;
import freemarker.core.LegacyCFormat;
import freemarker.core.XSCFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class StandardCFormats {
    static final Map<String, CFormat> STANDARD_C_FORMATS;

    private StandardCFormats() {
    }

    private static void addStandardCFormat(Map<String, CFormat> map, CFormat cFormat) {
        map.put(cFormat.getName(), cFormat);
    }

    static {
        LinkedHashMap<String, CFormat> map = new LinkedHashMap<String, CFormat>();
        StandardCFormats.addStandardCFormat(map, JavaScriptOrJSONCFormat.INSTANCE);
        StandardCFormats.addStandardCFormat(map, JSONCFormat.INSTANCE);
        StandardCFormats.addStandardCFormat(map, JavaScriptCFormat.INSTANCE);
        StandardCFormats.addStandardCFormat(map, JavaCFormat.INSTANCE);
        StandardCFormats.addStandardCFormat(map, XSCFormat.INSTANCE);
        StandardCFormats.addStandardCFormat(map, LegacyCFormat.INSTANCE);
        STANDARD_C_FORMATS = Collections.unmodifiableMap(map);
    }
}

