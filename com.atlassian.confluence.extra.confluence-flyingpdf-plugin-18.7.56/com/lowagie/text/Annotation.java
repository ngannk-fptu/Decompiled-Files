/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Annotation
implements Element {
    public static final int TEXT = 0;
    public static final int URL_NET = 1;
    public static final int URL_AS_STRING = 2;
    public static final int FILE_DEST = 3;
    public static final int FILE_PAGE = 4;
    public static final int NAMED_DEST = 5;
    public static final int LAUNCH = 6;
    public static final int SCREEN = 7;
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String URL = "url";
    public static final String FILE = "file";
    public static final String DESTINATION = "destination";
    public static final String PAGE = "page";
    public static final String NAMED = "named";
    public static final String APPLICATION = "application";
    public static final String PARAMETERS = "parameters";
    public static final String OPERATION = "operation";
    public static final String DEFAULTDIR = "defaultdir";
    public static final String LLX = "llx";
    public static final String LLY = "lly";
    public static final String URX = "urx";
    public static final String URY = "ury";
    public static final String MIMETYPE = "mime";
    protected int annotationtype;
    protected Map<String, Object> annotationAttributes = new HashMap<String, Object>();
    protected float llx = Float.NaN;
    protected float lly = Float.NaN;
    protected float urx = Float.NaN;
    protected float ury = Float.NaN;

    private Annotation(float llx, float lly, float urx, float ury) {
        this.llx = llx;
        this.lly = lly;
        this.urx = urx;
        this.ury = ury;
    }

    public Annotation(Annotation an) {
        this.annotationtype = an.annotationtype;
        this.annotationAttributes = an.annotationAttributes;
        this.llx = an.llx;
        this.lly = an.lly;
        this.urx = an.urx;
        this.ury = an.ury;
    }

    public Annotation(String title, String text) {
        this.annotationtype = 0;
        this.annotationAttributes.put(TITLE, title);
        this.annotationAttributes.put(CONTENT, text);
    }

    public Annotation(String title, String text, float llx, float lly, float urx, float ury) {
        this(llx, lly, urx, ury);
        this.annotationtype = 0;
        this.annotationAttributes.put(TITLE, title);
        this.annotationAttributes.put(CONTENT, text);
    }

    public Annotation(float llx, float lly, float urx, float ury, URL url) {
        this(llx, lly, urx, ury);
        this.annotationtype = 1;
        this.annotationAttributes.put(URL, url);
    }

    public Annotation(float llx, float lly, float urx, float ury, String url) {
        this(llx, lly, urx, ury);
        this.annotationtype = 2;
        this.annotationAttributes.put(FILE, url);
    }

    public Annotation(float llx, float lly, float urx, float ury, String file, String dest) {
        this(llx, lly, urx, ury);
        this.annotationtype = 3;
        this.annotationAttributes.put(FILE, file);
        this.annotationAttributes.put(DESTINATION, dest);
    }

    public Annotation(float llx, float lly, float urx, float ury, String moviePath, String mimeType, boolean showOnDisplay) {
        this(llx, lly, urx, ury);
        this.annotationtype = 7;
        this.annotationAttributes.put(FILE, moviePath);
        this.annotationAttributes.put(MIMETYPE, mimeType);
        this.annotationAttributes.put(PARAMETERS, new boolean[]{false, showOnDisplay});
    }

    public Annotation(float llx, float lly, float urx, float ury, String file, int page) {
        this(llx, lly, urx, ury);
        this.annotationtype = 4;
        this.annotationAttributes.put(FILE, file);
        this.annotationAttributes.put(PAGE, page);
    }

    public Annotation(float llx, float lly, float urx, float ury, int named) {
        this(llx, lly, urx, ury);
        this.annotationtype = 5;
        this.annotationAttributes.put(NAMED, named);
    }

    public Annotation(float llx, float lly, float urx, float ury, String application, String parameters, String operation, String defaultdir) {
        this(llx, lly, urx, ury);
        this.annotationtype = 6;
        this.annotationAttributes.put(APPLICATION, application);
        this.annotationAttributes.put(PARAMETERS, parameters);
        this.annotationAttributes.put(OPERATION, operation);
        this.annotationAttributes.put(DEFAULTDIR, defaultdir);
    }

    @Override
    public int type() {
        return 29;
    }

    @Override
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (DocumentException de) {
            return false;
        }
    }

    @Override
    public ArrayList<Element> getChunks() {
        return new ArrayList<Element>();
    }

    public void setDimensions(float llx, float lly, float urx, float ury) {
        this.llx = llx;
        this.lly = lly;
        this.urx = urx;
        this.ury = ury;
    }

    public float llx() {
        return this.llx;
    }

    public float lly() {
        return this.lly;
    }

    public float urx() {
        return this.urx;
    }

    public float ury() {
        return this.ury;
    }

    public float llx(float def) {
        if (Float.isNaN(this.llx)) {
            return def;
        }
        return this.llx;
    }

    public float lly(float def) {
        if (Float.isNaN(this.lly)) {
            return def;
        }
        return this.lly;
    }

    public float urx(float def) {
        if (Float.isNaN(this.urx)) {
            return def;
        }
        return this.urx;
    }

    public float ury(float def) {
        if (Float.isNaN(this.ury)) {
            return def;
        }
        return this.ury;
    }

    public int annotationType() {
        return this.annotationtype;
    }

    public String title() {
        String s = (String)this.annotationAttributes.get(TITLE);
        if (s == null) {
            s = "";
        }
        return s;
    }

    public String content() {
        String s = (String)this.annotationAttributes.get(CONTENT);
        if (s == null) {
            s = "";
        }
        return s;
    }

    @Deprecated
    public HashMap attributes() {
        return (HashMap)this.annotationAttributes;
    }

    public Map<String, Object> getAttributes() {
        return this.annotationAttributes;
    }

    @Override
    public boolean isContent() {
        return true;
    }

    @Override
    public boolean isNestable() {
        return true;
    }
}

