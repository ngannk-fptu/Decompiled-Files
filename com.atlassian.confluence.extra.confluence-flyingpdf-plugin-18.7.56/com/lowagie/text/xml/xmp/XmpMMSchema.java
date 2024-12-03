/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.xml.xmp.XmpSchema;

public class XmpMMSchema
extends XmpSchema {
    private static final long serialVersionUID = 1408509596611634862L;
    public static final String DEFAULT_XPATH_ID = "xmpMM";
    public static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/xap/1.0/mm/";
    public static final String DERIVEDFROM = "xmpMM:DerivedFrom";
    public static final String DOCUMENTID = "xmpMM:DocumentID";
    public static final String HISTORY = "xmpMM:History";
    public static final String MANAGEDFROM = "xmpMM:ManagedFrom";
    public static final String MANAGER = "xmpMM:Manager";
    public static final String MANAGETO = "xmpMM:ManageTo";
    public static final String MANAGEUI = "xmpMM:ManageUI";
    public static final String MANAGERVARIANT = "xmpMM:ManagerVariant";
    public static final String RENDITIONCLASS = "xmpMM:RenditionClass";
    public static final String RENDITIONPARAMS = "xmpMM:RenditionParams";
    public static final String VERSIONID = "xmpMM:VersionID";
    public static final String VERSIONS = "xmpMM:Versions";

    public XmpMMSchema() {
        super("xmlns:xmpMM=\"http://ns.adobe.com/xap/1.0/mm/\"");
    }
}

