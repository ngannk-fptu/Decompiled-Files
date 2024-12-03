/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STPlaceholderType
extends XmlToken {
    public static final SimpleTypeFactory<STPlaceholderType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stplaceholdertypeca72type");
    public static final SchemaType type = Factory.getType();
    public static final Enum TITLE = Enum.forString("title");
    public static final Enum BODY = Enum.forString("body");
    public static final Enum CTR_TITLE = Enum.forString("ctrTitle");
    public static final Enum SUB_TITLE = Enum.forString("subTitle");
    public static final Enum DT = Enum.forString("dt");
    public static final Enum SLD_NUM = Enum.forString("sldNum");
    public static final Enum FTR = Enum.forString("ftr");
    public static final Enum HDR = Enum.forString("hdr");
    public static final Enum OBJ = Enum.forString("obj");
    public static final Enum CHART = Enum.forString("chart");
    public static final Enum TBL = Enum.forString("tbl");
    public static final Enum CLIP_ART = Enum.forString("clipArt");
    public static final Enum DGM = Enum.forString("dgm");
    public static final Enum MEDIA = Enum.forString("media");
    public static final Enum SLD_IMG = Enum.forString("sldImg");
    public static final Enum PIC = Enum.forString("pic");
    public static final int INT_TITLE = 1;
    public static final int INT_BODY = 2;
    public static final int INT_CTR_TITLE = 3;
    public static final int INT_SUB_TITLE = 4;
    public static final int INT_DT = 5;
    public static final int INT_SLD_NUM = 6;
    public static final int INT_FTR = 7;
    public static final int INT_HDR = 8;
    public static final int INT_OBJ = 9;
    public static final int INT_CHART = 10;
    public static final int INT_TBL = 11;
    public static final int INT_CLIP_ART = 12;
    public static final int INT_DGM = 13;
    public static final int INT_MEDIA = 14;
    public static final int INT_SLD_IMG = 15;
    public static final int INT_PIC = 16;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_TITLE = 1;
        static final int INT_BODY = 2;
        static final int INT_CTR_TITLE = 3;
        static final int INT_SUB_TITLE = 4;
        static final int INT_DT = 5;
        static final int INT_SLD_NUM = 6;
        static final int INT_FTR = 7;
        static final int INT_HDR = 8;
        static final int INT_OBJ = 9;
        static final int INT_CHART = 10;
        static final int INT_TBL = 11;
        static final int INT_CLIP_ART = 12;
        static final int INT_DGM = 13;
        static final int INT_MEDIA = 14;
        static final int INT_SLD_IMG = 15;
        static final int INT_PIC = 16;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("title", 1), new Enum("body", 2), new Enum("ctrTitle", 3), new Enum("subTitle", 4), new Enum("dt", 5), new Enum("sldNum", 6), new Enum("ftr", 7), new Enum("hdr", 8), new Enum("obj", 9), new Enum("chart", 10), new Enum("tbl", 11), new Enum("clipArt", 12), new Enum("dgm", 13), new Enum("media", 14), new Enum("sldImg", 15), new Enum("pic", 16)});
        private static final long serialVersionUID = 1L;

        public static Enum forString(String s) {
            return (Enum)table.forString(s);
        }

        public static Enum forInt(int i) {
            return (Enum)table.forInt(i);
        }

        private Enum(String s, int i) {
            super(s, i);
        }

        private Object readResolve() {
            return Enum.forInt(this.intValue());
        }
    }
}

