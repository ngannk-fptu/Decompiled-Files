/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.xml.xmp.XmpArray;
import com.lowagie.text.xml.xmp.XmpSchema;
import java.util.Arrays;

public class XmpBasicSchema
extends XmpSchema {
    private static final long serialVersionUID = -2416613941622479298L;
    public static final String DEFAULT_XPATH_ID = "xmp";
    public static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/xap/1.0/";
    public static final String ADVISORY = "xmp:Advisory";
    public static final String BASEURL = "xmp:BaseURL";
    public static final String CREATEDATE = "xmp:CreateDate";
    public static final String CREATORTOOL = "xmp:CreatorTool";
    public static final String IDENTIFIER = "xmp:Identifier";
    public static final String METADATADATE = "xmp:MetadataDate";
    public static final String MODIFYDATE = "xmp:ModifyDate";
    public static final String NICKNAME = "xmp:Nickname";
    public static final String THUMBNAILS = "xmp:Thumbnails";

    public XmpBasicSchema() {
        super("xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\"");
    }

    public void addCreatorTool(String creator) {
        this.setProperty(CREATORTOOL, creator);
    }

    public void addCreateDate(String date) {
        this.setProperty(CREATEDATE, date);
    }

    public void addModDate(String date) {
        this.setProperty(MODIFYDATE, date);
    }

    public void addMetaDataDate(String date) {
        this.setProperty(METADATADATE, date);
    }

    public void addIdentifiers(String[] id) {
        XmpArray array = new XmpArray("rdf:Bag");
        array.addAll(Arrays.asList(id));
        this.setProperty(IDENTIFIER, array);
    }

    public void addNickname(String name) {
        this.setProperty(NICKNAME, name);
    }
}

