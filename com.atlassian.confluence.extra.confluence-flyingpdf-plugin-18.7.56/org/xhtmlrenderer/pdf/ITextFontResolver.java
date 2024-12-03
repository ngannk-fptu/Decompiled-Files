/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.sheet.FontFaceRule;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.extend.FontResolver;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFSFont;
import org.xhtmlrenderer.pdf.TrueTypeUtil;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.util.FontUtil;
import org.xhtmlrenderer.util.SupportedEmbeddedFontTypes;
import org.xhtmlrenderer.util.XRLog;
import org.xhtmlrenderer.util.XRRuntimeException;

public class ITextFontResolver
implements FontResolver {
    private Map _fontFamilies = ITextFontResolver.createInitialFontMap();
    private Map _fontCache = new HashMap();
    private final SharedContext _sharedContext;
    private static final String[][] cjkFonts = new String[][]{{"STSong-Light-H", "STSong-Light", "UniGB-UCS2-H"}, {"STSong-Light-V", "STSong-Light", "UniGB-UCS2-V"}, {"STSongStd-Light-H", "STSongStd-Light", "UniGB-UCS2-H"}, {"STSongStd-Light-V", "STSongStd-Light", "UniGB-UCS2-V"}, {"MHei-Medium-H", "MHei-Medium", "UniCNS-UCS2-H"}, {"MHei-Medium-V", "MHei-Medium", "UniCNS-UCS2-V"}, {"MSung-Light-H", "MSung-Light", "UniCNS-UCS2-H"}, {"MSung-Light-V", "MSung-Light", "UniCNS-UCS2-V"}, {"MSungStd-Light-H", "MSungStd-Light", "UniCNS-UCS2-H"}, {"MSungStd-Light-V", "MSungStd-Light", "UniCNS-UCS2-V"}, {"HeiseiMin-W3-H", "HeiseiMin-W3", "UniJIS-UCS2-H"}, {"HeiseiMin-W3-V", "HeiseiMin-W3", "UniJIS-UCS2-V"}, {"HeiseiKakuGo-W5-H", "HeiseiKakuGo-W5", "UniJIS-UCS2-H"}, {"HeiseiKakuGo-W5-V", "HeiseiKakuGo-W5", "UniJIS-UCS2-V"}, {"KozMinPro-Regular-H", "KozMinPro-Regular", "UniJIS-UCS2-HW-H"}, {"KozMinPro-Regular-V", "KozMinPro-Regular", "UniJIS-UCS2-HW-V"}, {"HYGoThic-Medium-H", "HYGoThic-Medium", "UniKS-UCS2-H"}, {"HYGoThic-Medium-V", "HYGoThic-Medium", "UniKS-UCS2-V"}, {"HYSMyeongJo-Medium-H", "HYSMyeongJo-Medium", "UniKS-UCS2-H"}, {"HYSMyeongJo-Medium-V", "HYSMyeongJo-Medium", "UniKS-UCS2-V"}, {"HYSMyeongJoStd-Medium-H", "HYSMyeongJoStd-Medium", "UniKS-UCS2-H"}, {"HYSMyeongJoStd-Medium-V", "HYSMyeongJoStd-Medium", "UniKS-UCS2-V"}};

    public ITextFontResolver(SharedContext sharedContext) {
        this._sharedContext = sharedContext;
    }

    public static Set getDistinctFontFamilyNames(String path, String encoding, boolean embedded) {
        BaseFont font = null;
        try {
            font = BaseFont.createFont(path, encoding, embedded);
            String[] fontFamilyNames = TrueTypeUtil.getFamilyNames(font);
            HashSet<String> distinct = new HashSet<String>();
            for (int i = 0; i < fontFamilyNames.length; ++i) {
                distinct.add(fontFamilyNames[i]);
            }
            return distinct;
        }
        catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FSFont resolveFont(SharedContext renderingContext, FontSpecification spec) {
        return this.resolveFont(renderingContext, spec.families, spec.size, spec.fontWeight, spec.fontStyle, spec.variant);
    }

    @Override
    public void flushCache() {
        this._fontFamilies = ITextFontResolver.createInitialFontMap();
        this._fontCache = new HashMap();
    }

    public void flushFontFaceFonts() {
        this._fontCache = new HashMap();
        Iterator i = this._fontFamilies.values().iterator();
        while (i.hasNext()) {
            FontFamily family = (FontFamily)i.next();
            Iterator j = family.getFontDescriptions().iterator();
            while (j.hasNext()) {
                FontDescription d = (FontDescription)j.next();
                if (!d.isFromFontFace()) continue;
                j.remove();
            }
            if (family.getFontDescriptions().size() != 0) continue;
            i.remove();
        }
    }

    public void importFontFaces(List fontFaces) {
        for (FontFaceRule rule : fontFaces) {
            CalculatedStyle style = rule.getCalculatedStyle();
            FSDerivedValue src = style.valueByName(CSSName.SRC);
            if (src == IdentValue.NONE) continue;
            byte[] font1 = this._sharedContext.getUac().getBinaryResource(src.asString());
            if (font1 == null) {
                XRLog.exception("Could not load font " + src.asString());
                continue;
            }
            byte[] font2 = null;
            FSDerivedValue metricsSrc = style.valueByName(CSSName.FS_FONT_METRIC_SRC);
            if (metricsSrc != IdentValue.NONE && (font2 = this._sharedContext.getUac().getBinaryResource(metricsSrc.asString())) == null) {
                XRLog.exception("Could not load font metric data " + src.asString());
                continue;
            }
            if (font2 != null) {
                byte[] t = font1;
                font1 = font2;
                font2 = t;
            }
            boolean embedded = style.isIdent(CSSName.FS_PDF_FONT_EMBED, IdentValue.EMBED);
            String encoding = style.getStringProperty(CSSName.FS_PDF_FONT_ENCODING);
            String fontFamily = null;
            IdentValue fontWeight = null;
            IdentValue fontStyle = null;
            if (rule.hasFontFamily()) {
                fontFamily = style.valueByName(CSSName.FONT_FAMILY).asString();
            }
            if (rule.hasFontWeight()) {
                fontWeight = style.getIdent(CSSName.FONT_WEIGHT);
            }
            if (rule.hasFontStyle()) {
                fontStyle = style.getIdent(CSSName.FONT_STYLE);
            }
            try {
                this.addFontFaceFont(fontFamily, fontWeight, fontStyle, src.asString(), encoding, embedded, font1, font2);
            }
            catch (DocumentException e) {
                XRLog.exception("Could not load font " + src.asString(), e);
            }
            catch (IOException e) {
                XRLog.exception("Could not load font " + src.asString(), e);
            }
        }
    }

    public void addFontDirectory(String dir, boolean embedded) throws DocumentException, IOException {
        File f = new File(dir);
        if (f.isDirectory()) {
            File[] files = f.listFiles(new FilenameFilter(){

                @Override
                public boolean accept(File dir, String name) {
                    String lower = name.toLowerCase();
                    return lower.endsWith(".otf") || lower.endsWith(".ttf");
                }
            });
            for (int i = 0; i < files.length; ++i) {
                this.addFont(files[i].getAbsolutePath(), embedded);
            }
        }
    }

    public void addFont(String path, boolean embedded) throws DocumentException, IOException {
        this.addFont(path, "Cp1252", embedded);
    }

    public void addFont(String path, String encoding, boolean embedded) throws DocumentException, IOException {
        this.addFont(path, encoding, embedded, null);
    }

    public void addFont(String path, String encoding, boolean embedded, String pathToPFB) throws DocumentException, IOException {
        this.addFont(path, null, encoding, embedded, pathToPFB);
    }

    public void addFont(String path, String fontFamilyNameOverride, String encoding, boolean embedded, String pathToPFB) throws DocumentException, IOException {
        String lower = path.toLowerCase();
        if (lower.endsWith(".otf") || lower.endsWith(".ttf") || lower.indexOf(".ttc,") != -1) {
            BaseFont font = BaseFont.createFont(path, encoding, embedded);
            String[] fontFamilyNames = fontFamilyNameOverride != null ? new String[]{fontFamilyNameOverride} : TrueTypeUtil.getFamilyNames(font);
            for (int i = 0; i < fontFamilyNames.length; ++i) {
                String fontFamilyName = fontFamilyNames[i];
                FontFamily fontFamily = this.getFontFamily(fontFamilyName);
                FontDescription descr = new FontDescription(font);
                try {
                    TrueTypeUtil.populateDescription(path, font, descr);
                }
                catch (Exception e) {
                    throw new XRRuntimeException(e.getMessage(), e);
                }
                fontFamily.addFontDescription(descr);
            }
        } else if (lower.endsWith(".ttc")) {
            String[] names = BaseFont.enumerateTTCNames(path);
            for (int i = 0; i < names.length; ++i) {
                this.addFont(path + "," + i, fontFamilyNameOverride, encoding, embedded, null);
            }
        } else if (lower.endsWith(".afm") || lower.endsWith(".pfm")) {
            if (embedded && pathToPFB == null) {
                throw new IOException("When embedding a font, path to PFB/PFA file must be specified");
            }
            BaseFont font = BaseFont.createFont(path, encoding, embedded, false, null, this.readFile(pathToPFB));
            String fontFamilyName = fontFamilyNameOverride != null ? fontFamilyNameOverride : font.getFamilyFontName()[0][3];
            FontFamily fontFamily = this.getFontFamily(fontFamilyName);
            FontDescription descr = new FontDescription(font);
            fontFamily.addFontDescription(descr);
        } else {
            throw new IOException("Unsupported font type");
        }
    }

    private boolean fontSupported(String uri) {
        String lower = uri.toLowerCase();
        if (FontUtil.isEmbeddedBase64Font(uri).booleanValue()) {
            return SupportedEmbeddedFontTypes.isSupported(uri);
        }
        return lower.endsWith(".otf") || lower.endsWith(".ttf") || lower.contains(".ttc,");
    }

    private void addFontFaceFont(String fontFamilyNameOverride, IdentValue fontWeightOverride, IdentValue fontStyleOverride, String uri, String encoding, boolean embedded, byte[] afmttf, byte[] pfb) throws DocumentException, IOException {
        String lower = uri.toLowerCase();
        if (this.fontSupported(lower)) {
            String fontName = FontUtil.isEmbeddedBase64Font(uri) != false ? fontFamilyNameOverride + SupportedEmbeddedFontTypes.getExtension(uri) : uri;
            BaseFont font = BaseFont.createFont(fontName, encoding, embedded, false, afmttf, pfb);
            String[] fontFamilyNames = fontFamilyNameOverride != null ? new String[]{fontFamilyNameOverride} : TrueTypeUtil.getFamilyNames(font);
            for (int i = 0; i < fontFamilyNames.length; ++i) {
                FontFamily fontFamily = this.getFontFamily(fontFamilyNames[i]);
                FontDescription descr = new FontDescription(font);
                try {
                    TrueTypeUtil.populateDescription(uri, afmttf, font, descr);
                }
                catch (Exception e) {
                    throw new XRRuntimeException(e.getMessage(), e);
                }
                descr.setFromFontFace(true);
                if (fontWeightOverride != null) {
                    descr.setWeight(ITextFontResolver.convertWeightToInt(fontWeightOverride));
                }
                if (fontStyleOverride != null) {
                    descr.setStyle(fontStyleOverride);
                }
                fontFamily.addFontDescription(descr);
            }
        } else if (lower.endsWith(".afm") || lower.endsWith(".pfm") || lower.endsWith(".pfb") || lower.endsWith(".pfa")) {
            if (embedded && pfb == null) {
                throw new IOException("When embedding a font, path to PFB/PFA file must be specified");
            }
            String name = uri.substring(0, uri.length() - 4) + ".afm";
            BaseFont font = BaseFont.createFont(name, encoding, embedded, false, afmttf, pfb);
            String fontFamilyName = font.getFamilyFontName()[0][3];
            FontFamily fontFamily = this.getFontFamily(fontFamilyName);
            FontDescription descr = new FontDescription(font);
            descr.setFromFontFace(true);
            fontFamily.addFontDescription(descr);
        } else {
            throw new IOException("Unsupported font type");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] readFile(String path) throws IOException {
        File f = new File(path);
        if (f.exists()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream((int)f.length());
            FileInputStream is = null;
            try {
                int i;
                is = new FileInputStream(path);
                byte[] buf = new byte[10240];
                while ((i = ((InputStream)is).read(buf)) != -1) {
                    result.write(buf, 0, i);
                }
                ((InputStream)is).close();
                is = null;
                byte[] byArray = result.toByteArray();
                return byArray;
            }
            finally {
                if (is != null) {
                    try {
                        ((InputStream)is).close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
        throw new IOException("File " + path + " does not exist or is not accessible");
    }

    public FontFamily getFontFamily(String fontFamilyName) {
        FontFamily fontFamily = (FontFamily)this._fontFamilies.get(fontFamilyName);
        if (fontFamily == null) {
            fontFamily = new FontFamily();
            fontFamily.setName(fontFamilyName);
            this._fontFamilies.put(fontFamilyName, fontFamily);
        }
        return fontFamily;
    }

    private FSFont resolveFont(SharedContext ctx, String[] families, float size, IdentValue weight, IdentValue style, IdentValue variant) {
        if (style != IdentValue.NORMAL && style != IdentValue.OBLIQUE && style != IdentValue.ITALIC) {
            style = IdentValue.NORMAL;
        }
        if (families != null) {
            for (int i = 0; i < families.length; ++i) {
                FSFont font = this.resolveFont(ctx, families[i], size, weight, style, variant);
                if (font == null) continue;
                return font;
            }
        }
        return this.resolveFont(ctx, "Serif", size, weight, style, variant);
    }

    private String normalizeFontFamily(String fontFamily) {
        String result = fontFamily;
        if (result.startsWith("\"")) {
            result = result.substring(1);
        }
        if (result.endsWith("\"")) {
            result = result.substring(0, result.length() - 1);
        }
        if (result.equalsIgnoreCase("serif")) {
            result = "Serif";
        } else if (result.equalsIgnoreCase("sans-serif")) {
            result = "SansSerif";
        } else if (result.equalsIgnoreCase("monospace")) {
            result = "Monospaced";
        }
        return result;
    }

    private FSFont resolveFont(SharedContext ctx, String fontFamily, float size, IdentValue weight, IdentValue style, IdentValue variant) {
        String normalizedFontFamily = this.normalizeFontFamily(fontFamily);
        String cacheKey = ITextFontResolver.getHashName(normalizedFontFamily, weight, style);
        FontDescription result = (FontDescription)this._fontCache.get(cacheKey);
        if (result != null) {
            return new ITextFSFont(result, size);
        }
        FontFamily family = (FontFamily)this._fontFamilies.get(normalizedFontFamily);
        if (family != null && (result = family.match(ITextFontResolver.convertWeightToInt(weight), style)) != null) {
            this._fontCache.put(cacheKey, result);
            return new ITextFSFont(result, size);
        }
        return null;
    }

    public static int convertWeightToInt(IdentValue weight) {
        if (weight == IdentValue.NORMAL) {
            return 400;
        }
        if (weight == IdentValue.BOLD) {
            return 700;
        }
        if (weight == IdentValue.FONT_WEIGHT_100) {
            return 100;
        }
        if (weight == IdentValue.FONT_WEIGHT_200) {
            return 200;
        }
        if (weight == IdentValue.FONT_WEIGHT_300) {
            return 300;
        }
        if (weight == IdentValue.FONT_WEIGHT_400) {
            return 400;
        }
        if (weight == IdentValue.FONT_WEIGHT_500) {
            return 500;
        }
        if (weight == IdentValue.FONT_WEIGHT_600) {
            return 600;
        }
        if (weight == IdentValue.FONT_WEIGHT_700) {
            return 700;
        }
        if (weight == IdentValue.FONT_WEIGHT_800) {
            return 800;
        }
        if (weight == IdentValue.FONT_WEIGHT_900) {
            return 900;
        }
        if (weight == IdentValue.LIGHTER) {
            return 400;
        }
        if (weight == IdentValue.BOLDER) {
            return 700;
        }
        throw new IllegalArgumentException();
    }

    protected static String getHashName(String name, IdentValue weight, IdentValue style) {
        return name + "-" + weight + "-" + style;
    }

    private static Map createInitialFontMap() {
        HashMap result = new HashMap();
        try {
            ITextFontResolver.addCourier(result);
            ITextFontResolver.addTimes(result);
            ITextFontResolver.addHelvetica(result);
            ITextFontResolver.addSymbol(result);
            ITextFontResolver.addZapfDingbats(result);
            if (ITextFontResolver.class.getClassLoader().getResource("com/lowagie/text/pdf/fonts/cjkfonts.properties") != null) {
                ITextFontResolver.addCJKFonts(result);
            }
        }
        catch (DocumentException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    private static BaseFont createFont(String name) throws DocumentException, IOException {
        return ITextFontResolver.createFont(name, "winansi", true);
    }

    private static BaseFont createFont(String name, String encoding, boolean embedded) throws DocumentException, IOException {
        return BaseFont.createFont(name, encoding, embedded);
    }

    private static void addCourier(HashMap result) throws DocumentException, IOException {
        FontFamily courier = new FontFamily();
        courier.setName("Courier");
        courier.addFontDescription(new FontDescription(ITextFontResolver.createFont("Courier-BoldOblique"), IdentValue.OBLIQUE, 700));
        courier.addFontDescription(new FontDescription(ITextFontResolver.createFont("Courier-Oblique"), IdentValue.OBLIQUE, 400));
        courier.addFontDescription(new FontDescription(ITextFontResolver.createFont("Courier-Bold"), IdentValue.NORMAL, 700));
        courier.addFontDescription(new FontDescription(ITextFontResolver.createFont("Courier"), IdentValue.NORMAL, 400));
        result.put("DialogInput", courier);
        result.put("Monospaced", courier);
        result.put("Courier", courier);
    }

    private static void addTimes(HashMap result) throws DocumentException, IOException {
        FontFamily times = new FontFamily();
        times.setName("Times");
        times.addFontDescription(new FontDescription(ITextFontResolver.createFont("Times-BoldItalic"), IdentValue.ITALIC, 700));
        times.addFontDescription(new FontDescription(ITextFontResolver.createFont("Times-Italic"), IdentValue.ITALIC, 400));
        times.addFontDescription(new FontDescription(ITextFontResolver.createFont("Times-Bold"), IdentValue.NORMAL, 700));
        times.addFontDescription(new FontDescription(ITextFontResolver.createFont("Times-Roman"), IdentValue.NORMAL, 400));
        result.put("Serif", times);
        result.put("TimesRoman", times);
    }

    private static void addHelvetica(HashMap result) throws DocumentException, IOException {
        FontFamily helvetica = new FontFamily();
        helvetica.setName("Helvetica");
        helvetica.addFontDescription(new FontDescription(ITextFontResolver.createFont("Helvetica-BoldOblique"), IdentValue.OBLIQUE, 700));
        helvetica.addFontDescription(new FontDescription(ITextFontResolver.createFont("Helvetica-Oblique"), IdentValue.OBLIQUE, 400));
        helvetica.addFontDescription(new FontDescription(ITextFontResolver.createFont("Helvetica-Bold"), IdentValue.NORMAL, 700));
        helvetica.addFontDescription(new FontDescription(ITextFontResolver.createFont("Helvetica"), IdentValue.NORMAL, 400));
        result.put("Dialog", helvetica);
        result.put("SansSerif", helvetica);
        result.put("Helvetica", helvetica);
    }

    private static void addSymbol(Map result) throws DocumentException, IOException {
        FontFamily fontFamily = new FontFamily();
        fontFamily.setName("Symbol");
        fontFamily.addFontDescription(new FontDescription(ITextFontResolver.createFont("Symbol", "Cp1252", false), IdentValue.NORMAL, 400));
        result.put("Symbol", fontFamily);
    }

    private static void addZapfDingbats(Map result) throws DocumentException, IOException {
        FontFamily fontFamily = new FontFamily();
        fontFamily.setName("ZapfDingbats");
        fontFamily.addFontDescription(new FontDescription(ITextFontResolver.createFont("ZapfDingbats", "Cp1252", false), IdentValue.NORMAL, 400));
        result.put("ZapfDingbats", fontFamily);
    }

    private static void addCJKFonts(Map fontFamilyMap) throws DocumentException, IOException {
        for (int i = 0; i < cjkFonts.length; ++i) {
            String fontFamilyName = cjkFonts[i][0];
            String fontName = cjkFonts[i][1];
            String encoding = cjkFonts[i][2];
            ITextFontResolver.addCJKFont(fontFamilyName, fontName, encoding, fontFamilyMap);
        }
    }

    private static void addCJKFont(String fontFamilyName, String fontName, String encoding, Map fontFamilyMap) throws DocumentException, IOException {
        FontFamily fontFamily = new FontFamily();
        fontFamily.setName(fontFamilyName);
        fontFamily.addFontDescription(new FontDescription(ITextFontResolver.createFont(fontName + ",BoldItalic", encoding, false), IdentValue.OBLIQUE, 700));
        fontFamily.addFontDescription(new FontDescription(ITextFontResolver.createFont(fontName + ",Italic", encoding, false), IdentValue.OBLIQUE, 400));
        fontFamily.addFontDescription(new FontDescription(ITextFontResolver.createFont(fontName + ",Bold", encoding, false), IdentValue.NORMAL, 700));
        fontFamily.addFontDescription(new FontDescription(ITextFontResolver.createFont(fontName, encoding, false), IdentValue.NORMAL, 400));
        fontFamilyMap.put(fontFamilyName, fontFamily);
    }

    public static class FontDescription {
        private IdentValue _style;
        private int _weight;
        private BaseFont _font;
        private float _underlinePosition;
        private float _underlineThickness;
        private float _yStrikeoutSize;
        private float _yStrikeoutPosition;
        private boolean _isFromFontFace;

        public FontDescription() {
        }

        public FontDescription(BaseFont font) {
            this(font, IdentValue.NORMAL, 400);
        }

        public FontDescription(BaseFont font, IdentValue style, int weight) {
            this._font = font;
            this._style = style;
            this._weight = weight;
            this.setMetricDefaults();
        }

        public BaseFont getFont() {
            return this._font;
        }

        public void setFont(BaseFont font) {
            this._font = font;
        }

        public int getWeight() {
            return this._weight;
        }

        public void setWeight(int weight) {
            this._weight = weight;
        }

        public IdentValue getStyle() {
            return this._style;
        }

        public void setStyle(IdentValue style) {
            this._style = style;
        }

        public float getUnderlinePosition() {
            return this._underlinePosition;
        }

        public void setUnderlinePosition(float underlinePosition) {
            this._underlinePosition = underlinePosition;
        }

        public float getUnderlineThickness() {
            return this._underlineThickness;
        }

        public void setUnderlineThickness(float underlineThickness) {
            this._underlineThickness = underlineThickness;
        }

        public float getYStrikeoutPosition() {
            return this._yStrikeoutPosition;
        }

        public void setYStrikeoutPosition(float strikeoutPosition) {
            this._yStrikeoutPosition = strikeoutPosition;
        }

        public float getYStrikeoutSize() {
            return this._yStrikeoutSize;
        }

        public void setYStrikeoutSize(float strikeoutSize) {
            this._yStrikeoutSize = strikeoutSize;
        }

        private void setMetricDefaults() {
            this._underlinePosition = -50.0f;
            this._underlineThickness = 50.0f;
            int[] box = this._font.getCharBBox(120);
            if (box != null) {
                this._yStrikeoutPosition = box[3] / 2 + 50;
                this._yStrikeoutSize = 100.0f;
            } else {
                this._yStrikeoutPosition = this._font.getFontDescriptor(8, 1000.0f) / 3.0f;
            }
        }

        public boolean isFromFontFace() {
            return this._isFromFontFace;
        }

        public void setFromFontFace(boolean isFromFontFace) {
            this._isFromFontFace = isFromFontFace;
        }
    }

    private static class FontFamily {
        private String _name;
        private List _fontDescriptions;
        private static final int SM_EXACT = 1;
        private static final int SM_LIGHTER_OR_DARKER = 2;
        private static final int SM_DARKER_OR_LIGHTER = 3;

        public List getFontDescriptions() {
            return this._fontDescriptions;
        }

        public void addFontDescription(FontDescription descr) {
            if (this._fontDescriptions == null) {
                this._fontDescriptions = new ArrayList();
            }
            this._fontDescriptions.add(descr);
            Collections.sort(this._fontDescriptions, new Comparator(){

                public int compare(Object o1, Object o2) {
                    FontDescription f1 = (FontDescription)o1;
                    FontDescription f2 = (FontDescription)o2;
                    return f1.getWeight() - f2.getWeight();
                }
            });
        }

        public String getName() {
            return this._name;
        }

        public void setName(String name) {
            this._name = name;
        }

        public FontDescription match(int desiredWeight, IdentValue style) {
            FontDescription[] matches;
            FontDescription result;
            if (this._fontDescriptions == null) {
                throw new RuntimeException("fontDescriptions is null");
            }
            ArrayList<FontDescription> candidates = new ArrayList<FontDescription>();
            for (FontDescription description : this._fontDescriptions) {
                if (description.getStyle() != style) continue;
                candidates.add(description);
            }
            if (candidates.size() == 0) {
                if (style == IdentValue.ITALIC) {
                    return this.match(desiredWeight, IdentValue.OBLIQUE);
                }
                if (style == IdentValue.OBLIQUE) {
                    return this.match(desiredWeight, IdentValue.NORMAL);
                }
                candidates.addAll(this._fontDescriptions);
            }
            if ((result = this.findByWeight(matches = candidates.toArray(new FontDescription[candidates.size()]), desiredWeight, 1)) != null) {
                return result;
            }
            if (desiredWeight <= 500) {
                return this.findByWeight(matches, desiredWeight, 2);
            }
            return this.findByWeight(matches, desiredWeight, 3);
        }

        private FontDescription findByWeight(FontDescription[] matches, int desiredWeight, int searchMode) {
            if (searchMode == 1) {
                for (int i = 0; i < matches.length; ++i) {
                    FontDescription descr = matches[i];
                    if (descr.getWeight() != desiredWeight) continue;
                    return descr;
                }
                return null;
            }
            if (searchMode == 2) {
                int offset = 0;
                FontDescription descr = null;
                for (offset = 0; offset < matches.length && (descr = matches[offset]).getWeight() <= desiredWeight; ++offset) {
                }
                if (offset > 0 && descr.getWeight() > desiredWeight) {
                    return matches[offset - 1];
                }
                return descr;
            }
            if (searchMode == 3) {
                int offset = 0;
                FontDescription descr = null;
                for (offset = matches.length - 1; offset >= 0 && (descr = matches[offset]).getWeight() >= desiredWeight; --offset) {
                }
                if (offset != matches.length - 1 && descr.getWeight() < desiredWeight) {
                    return matches[offset + 1];
                }
                return descr;
            }
            return null;
        }
    }
}

