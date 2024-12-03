/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.FontBoxFont
 *  org.apache.fontbox.ttf.OpenTypeFont
 *  org.apache.fontbox.ttf.TTFParser
 *  org.apache.fontbox.ttf.TrueTypeFont
 *  org.apache.fontbox.type1.Type1Font
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.type1.Type1Font;
import org.apache.pdfbox.pdmodel.font.CIDFontMapping;
import org.apache.pdfbox.pdmodel.font.FileSystemFontProvider;
import org.apache.pdfbox.pdmodel.font.FontCache;
import org.apache.pdfbox.pdmodel.font.FontFormat;
import org.apache.pdfbox.pdmodel.font.FontInfo;
import org.apache.pdfbox.pdmodel.font.FontMapper;
import org.apache.pdfbox.pdmodel.font.FontMapping;
import org.apache.pdfbox.pdmodel.font.FontProvider;
import org.apache.pdfbox.pdmodel.font.PDCIDSystemInfo;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDPanoseClassification;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

final class FontMapperImpl
implements FontMapper {
    private static final Log LOG = LogFactory.getLog(FontMapperImpl.class);
    private static final FontCache fontCache = new FontCache();
    private FontProvider fontProvider;
    private Map<String, FontInfo> fontInfoByName;
    private final TrueTypeFont lastResortFont;
    private final Map<String, List<String>> substitutes = new HashMap<String, List<String>>();

    FontMapperImpl() {
        this.addSubstitutes("Courier", new ArrayList<String>(Arrays.asList("CourierNew", "CourierNewPSMT", "LiberationMono", "NimbusMonL-Regu")));
        this.addSubstitutes("Courier-Bold", new ArrayList<String>(Arrays.asList("CourierNewPS-BoldMT", "CourierNew-Bold", "LiberationMono-Bold", "NimbusMonL-Bold")));
        this.addSubstitutes("Courier-Oblique", new ArrayList<String>(Arrays.asList("CourierNewPS-ItalicMT", "CourierNew-Italic", "LiberationMono-Italic", "NimbusMonL-ReguObli")));
        this.addSubstitutes("Courier-BoldOblique", new ArrayList<String>(Arrays.asList("CourierNewPS-BoldItalicMT", "CourierNew-BoldItalic", "LiberationMono-BoldItalic", "NimbusMonL-BoldObli")));
        this.addSubstitutes("Helvetica", new ArrayList<String>(Arrays.asList("ArialMT", "Arial", "LiberationSans", "NimbusSanL-Regu")));
        this.addSubstitutes("Helvetica-Bold", new ArrayList<String>(Arrays.asList("Arial-BoldMT", "Arial-Bold", "LiberationSans-Bold", "NimbusSanL-Bold")));
        this.addSubstitutes("Helvetica-Oblique", new ArrayList<String>(Arrays.asList("Arial-ItalicMT", "Arial-Italic", "Helvetica-Italic", "LiberationSans-Italic", "NimbusSanL-ReguItal")));
        this.addSubstitutes("Helvetica-BoldOblique", new ArrayList<String>(Arrays.asList("Arial-BoldItalicMT", "Helvetica-BoldItalic", "LiberationSans-BoldItalic", "NimbusSanL-BoldItal")));
        this.addSubstitutes("Times-Roman", new ArrayList<String>(Arrays.asList("TimesNewRomanPSMT", "TimesNewRoman", "TimesNewRomanPS", "LiberationSerif", "NimbusRomNo9L-Regu")));
        this.addSubstitutes("Times-Bold", new ArrayList<String>(Arrays.asList("TimesNewRomanPS-BoldMT", "TimesNewRomanPS-Bold", "TimesNewRoman-Bold", "LiberationSerif-Bold", "NimbusRomNo9L-Medi")));
        this.addSubstitutes("Times-Italic", new ArrayList<String>(Arrays.asList("TimesNewRomanPS-ItalicMT", "TimesNewRomanPS-Italic", "TimesNewRoman-Italic", "LiberationSerif-Italic", "NimbusRomNo9L-ReguItal")));
        this.addSubstitutes("Times-BoldItalic", new ArrayList<String>(Arrays.asList("TimesNewRomanPS-BoldItalicMT", "TimesNewRomanPS-BoldItalic", "TimesNewRoman-BoldItalic", "LiberationSerif-BoldItalic", "NimbusRomNo9L-MediItal")));
        this.addSubstitutes("Symbol", new ArrayList<String>(Arrays.asList("Symbol", "SymbolMT", "StandardSymL")));
        this.addSubstitutes("ZapfDingbats", new ArrayList<String>(Arrays.asList("ZapfDingbatsITCbyBT-Regular", "ZapfDingbatsITC", "Dingbats", "MS-Gothic")));
        for (String baseName : Standard14Fonts.getNames()) {
            if (!this.getSubstitutes(baseName).isEmpty()) continue;
            String mappedName = Standard14Fonts.getMappedFontName(baseName);
            this.addSubstitutes(baseName, this.copySubstitutes(mappedName.toLowerCase(Locale.ENGLISH)));
        }
        try {
            String ttfName = "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf";
            InputStream resourceAsStream = FontMapper.class.getResourceAsStream(ttfName);
            if (resourceAsStream == null) {
                throw new IOException("resource '" + ttfName + "' not found");
            }
            BufferedInputStream ttfStream = new BufferedInputStream(resourceAsStream);
            TTFParser ttfParser = new TTFParser();
            this.lastResortFont = ttfParser.parse((InputStream)ttfStream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void setProvider(FontProvider fontProvider) {
        this.fontInfoByName = this.createFontInfoByName(fontProvider.getFontInfo());
        this.fontProvider = fontProvider;
    }

    public synchronized FontProvider getProvider() {
        if (this.fontProvider == null) {
            this.setProvider(DefaultFontProvider.INSTANCE);
        }
        return this.fontProvider;
    }

    public FontCache getFontCache() {
        return fontCache;
    }

    private Map<String, FontInfo> createFontInfoByName(List<? extends FontInfo> fontInfoList) {
        LinkedHashMap<String, FontInfo> map = new LinkedHashMap<String, FontInfo>();
        for (FontInfo fontInfo : fontInfoList) {
            for (String name : this.getPostScriptNames(fontInfo.getPostScriptName())) {
                map.put(name.toLowerCase(Locale.ENGLISH), fontInfo);
            }
        }
        return map;
    }

    private Set<String> getPostScriptNames(String postScriptName) {
        HashSet<String> names = new HashSet<String>(2);
        names.add(postScriptName);
        names.add(postScriptName.replace("-", ""));
        return names;
    }

    private List<String> copySubstitutes(String postScriptName) {
        return new ArrayList<String>((Collection)this.substitutes.get(postScriptName));
    }

    public void addSubstitute(String match, String replace) {
        String lowerCaseMatch = match.toLowerCase(Locale.ENGLISH);
        if (!this.substitutes.containsKey(lowerCaseMatch)) {
            this.substitutes.put(lowerCaseMatch, new ArrayList());
        }
        this.substitutes.get(lowerCaseMatch).add(replace);
    }

    private void addSubstitutes(String match, List<String> replacements) {
        this.substitutes.put(match.toLowerCase(Locale.ENGLISH), replacements);
    }

    private List<String> getSubstitutes(String postScriptName) {
        List<String> subs = this.substitutes.get(postScriptName.replace(" ", "").toLowerCase(Locale.ENGLISH));
        if (subs != null) {
            return subs;
        }
        return Collections.emptyList();
    }

    private String getFallbackFontName(PDFontDescriptor fontDescriptor) {
        String fontName;
        if (fontDescriptor != null) {
            boolean isBold = false;
            String name = fontDescriptor.getFontName();
            if (name != null) {
                String lower = fontDescriptor.getFontName().toLowerCase();
                boolean bl = isBold = lower.contains("bold") || lower.contains("black") || lower.contains("heavy");
            }
            if (fontDescriptor.isFixedPitch()) {
                fontName = "Courier";
                if (isBold && fontDescriptor.isItalic()) {
                    fontName = fontName + "-BoldOblique";
                } else if (isBold) {
                    fontName = fontName + "-Bold";
                } else if (fontDescriptor.isItalic()) {
                    fontName = fontName + "-Oblique";
                }
            } else if (fontDescriptor.isSerif()) {
                fontName = "Times";
                fontName = isBold && fontDescriptor.isItalic() ? fontName + "-BoldItalic" : (isBold ? fontName + "-Bold" : (fontDescriptor.isItalic() ? fontName + "-Italic" : fontName + "-Roman"));
            } else {
                fontName = "Helvetica";
                if (isBold && fontDescriptor.isItalic()) {
                    fontName = fontName + "-BoldOblique";
                } else if (isBold) {
                    fontName = fontName + "-Bold";
                } else if (fontDescriptor.isItalic()) {
                    fontName = fontName + "-Oblique";
                }
            }
        } else {
            fontName = "Times-Roman";
        }
        return fontName;
    }

    @Override
    public FontMapping<TrueTypeFont> getTrueTypeFont(String baseFont, PDFontDescriptor fontDescriptor) {
        TrueTypeFont ttf = (TrueTypeFont)this.findFont(FontFormat.TTF, baseFont);
        if (ttf != null) {
            return new FontMapping<TrueTypeFont>(ttf, false);
        }
        String fontName = this.getFallbackFontName(fontDescriptor);
        ttf = (TrueTypeFont)this.findFont(FontFormat.TTF, fontName);
        if (ttf == null) {
            ttf = this.lastResortFont;
        }
        return new FontMapping<TrueTypeFont>(ttf, true);
    }

    @Override
    public FontMapping<FontBoxFont> getFontBoxFont(String baseFont, PDFontDescriptor fontDescriptor) {
        FontBoxFont font = this.findFontBoxFont(baseFont);
        if (font != null) {
            return new FontMapping<FontBoxFont>(font, false);
        }
        String fallbackName = this.getFallbackFontName(fontDescriptor);
        font = this.findFontBoxFont(fallbackName);
        if (font == null) {
            font = this.lastResortFont;
        }
        return new FontMapping<FontBoxFont>(font, true);
    }

    private FontBoxFont findFontBoxFont(String postScriptName) {
        Type1Font t1 = (Type1Font)this.findFont(FontFormat.PFB, postScriptName);
        if (t1 != null) {
            return t1;
        }
        TrueTypeFont ttf = (TrueTypeFont)this.findFont(FontFormat.TTF, postScriptName);
        if (ttf != null) {
            return ttf;
        }
        OpenTypeFont otf = (OpenTypeFont)this.findFont(FontFormat.OTF, postScriptName);
        if (otf != null) {
            return otf;
        }
        return null;
    }

    private FontBoxFont findFont(FontFormat format, String postScriptName) {
        FontInfo info;
        if (postScriptName == null) {
            return null;
        }
        if (this.fontProvider == null) {
            this.getProvider();
        }
        if ((info = this.getFont(format, postScriptName)) != null) {
            return info.getFont();
        }
        info = this.getFont(format, postScriptName.replace("-", ""));
        if (info != null) {
            return info.getFont();
        }
        for (String substituteName : this.getSubstitutes(postScriptName)) {
            info = this.getFont(format, substituteName);
            if (info == null) continue;
            return info.getFont();
        }
        info = this.getFont(format, postScriptName.replace(",", "-"));
        if (info != null) {
            return info.getFont();
        }
        info = this.getFont(format, postScriptName + "-Regular");
        if (info != null) {
            return info.getFont();
        }
        return null;
    }

    private FontInfo getFont(FontFormat format, String postScriptName) {
        FontInfo info;
        if (postScriptName.contains("+")) {
            postScriptName = postScriptName.substring(postScriptName.indexOf(43) + 1);
        }
        if ((info = this.fontInfoByName.get(postScriptName.toLowerCase(Locale.ENGLISH))) != null && info.getFormat() == format) {
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)String.format("getFont('%s','%s') returns %s", new Object[]{format, postScriptName, info}));
            }
            return info;
        }
        return null;
    }

    @Override
    public CIDFontMapping getCIDFont(String baseFont, PDFontDescriptor fontDescriptor, PDCIDSystemInfo cidSystemInfo) {
        PriorityQueue<FontMatch> queue;
        FontMatch bestMatch;
        String collection;
        OpenTypeFont otf1 = (OpenTypeFont)this.findFont(FontFormat.OTF, baseFont);
        if (otf1 != null) {
            return new CIDFontMapping(otf1, null, false);
        }
        TrueTypeFont ttf = (TrueTypeFont)this.findFont(FontFormat.TTF, baseFont);
        if (ttf != null) {
            return new CIDFontMapping(null, (FontBoxFont)ttf, false);
        }
        if (cidSystemInfo != null && ((collection = cidSystemInfo.getRegistry() + "-" + cidSystemInfo.getOrdering()).equals("Adobe-GB1") || collection.equals("Adobe-CNS1") || collection.equals("Adobe-Japan1") || collection.equals("Adobe-Korea1")) && (bestMatch = (queue = this.getFontMatches(fontDescriptor, cidSystemInfo)).poll()) != null) {
            FontBoxFont font;
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Best match for '" + baseFont + "': " + bestMatch.info));
            }
            if ((font = bestMatch.info.getFont()) instanceof OpenTypeFont) {
                return new CIDFontMapping((OpenTypeFont)font, null, true);
            }
            if (font != null) {
                return new CIDFontMapping(null, font, true);
            }
        }
        return new CIDFontMapping(null, (FontBoxFont)this.lastResortFont, true);
    }

    private PriorityQueue<FontMatch> getFontMatches(PDFontDescriptor fontDescriptor, PDCIDSystemInfo cidSystemInfo) {
        PriorityQueue<FontMatch> queue = new PriorityQueue<FontMatch>(20);
        for (FontInfo info : this.fontInfoByName.values()) {
            if (cidSystemInfo != null && !this.isCharSetMatch(cidSystemInfo, info)) continue;
            FontMatch match = new FontMatch(info);
            if (fontDescriptor.getPanose() != null && info.getPanose() != null) {
                PDPanoseClassification panose = fontDescriptor.getPanose().getPanose();
                if (panose.getFamilyKind() == info.getPanose().getFamilyKind()) {
                    if (panose.getFamilyKind() == 0 && (info.getPostScriptName().toLowerCase().contains("barcode") || info.getPostScriptName().startsWith("Code")) && !this.probablyBarcodeFont(fontDescriptor)) continue;
                    if (panose.getSerifStyle() == info.getPanose().getSerifStyle()) {
                        match.score += 2.0;
                    } else if (panose.getSerifStyle() >= 2 && panose.getSerifStyle() <= 5 && info.getPanose().getSerifStyle() >= 2 && info.getPanose().getSerifStyle() <= 5) {
                        match.score += 1.0;
                    } else if (panose.getSerifStyle() >= 11 && panose.getSerifStyle() <= 13 && info.getPanose().getSerifStyle() >= 11 && info.getPanose().getSerifStyle() <= 13) {
                        match.score += 1.0;
                    } else if (panose.getSerifStyle() != 0 && info.getPanose().getSerifStyle() != 0) {
                        match.score -= 1.0;
                    }
                    int weight = info.getPanose().getWeight();
                    int weightClass = info.getWeightClassAsPanose();
                    if (Math.abs(weight - weightClass) > 2) {
                        weight = weightClass;
                    }
                    if (panose.getWeight() == weight) {
                        match.score += 2.0;
                    } else if (panose.getWeight() > 1 && weight > 1) {
                        float dist = Math.abs(panose.getWeight() - weight);
                        match.score += 1.0 - (double)dist * 0.5;
                    }
                }
            } else if (fontDescriptor.getFontWeight() > 0.0f && info.getWeightClass() > 0) {
                float dist = Math.abs(fontDescriptor.getFontWeight() - (float)info.getWeightClass());
                match.score += 1.0 - (double)(dist / 100.0f) * 0.5;
            }
            queue.add(match);
        }
        return queue;
    }

    private boolean probablyBarcodeFont(PDFontDescriptor fontDescriptor) {
        String fn;
        String ff = fontDescriptor.getFontFamily();
        if (ff == null) {
            ff = "";
        }
        if ((fn = fontDescriptor.getFontName()) == null) {
            fn = "";
        }
        return ff.startsWith("Code") || ff.toLowerCase().contains("barcode") || fn.startsWith("Code") || fn.toLowerCase().contains("barcode");
    }

    private boolean isCharSetMatch(PDCIDSystemInfo cidSystemInfo, FontInfo info) {
        if (info.getCIDSystemInfo() != null) {
            return info.getCIDSystemInfo().getRegistry().equals(cidSystemInfo.getRegistry()) && info.getCIDSystemInfo().getOrdering().equals(cidSystemInfo.getOrdering());
        }
        long codePageRange = info.getCodePageRange();
        long JIS_JAPAN = 131072L;
        long CHINESE_SIMPLIFIED = 262144L;
        long KOREAN_WANSUNG = 524288L;
        long CHINESE_TRADITIONAL = 0x100000L;
        long KOREAN_JOHAB = 0x200000L;
        if ("MalgunGothic-Semilight".equals(info.getPostScriptName())) {
            codePageRange &= (JIS_JAPAN | CHINESE_SIMPLIFIED | CHINESE_TRADITIONAL) ^ 0xFFFFFFFFFFFFFFFFL;
        }
        if (cidSystemInfo.getOrdering().equals("GB1") && (codePageRange & CHINESE_SIMPLIFIED) == CHINESE_SIMPLIFIED) {
            return true;
        }
        if (cidSystemInfo.getOrdering().equals("CNS1") && (codePageRange & CHINESE_TRADITIONAL) == CHINESE_TRADITIONAL) {
            return true;
        }
        if (cidSystemInfo.getOrdering().equals("Japan1") && (codePageRange & JIS_JAPAN) == JIS_JAPAN) {
            return true;
        }
        return cidSystemInfo.getOrdering().equals("Korea1") && ((codePageRange & KOREAN_WANSUNG) == KOREAN_WANSUNG || (codePageRange & KOREAN_JOHAB) == KOREAN_JOHAB);
    }

    private FontMatch printMatches(PriorityQueue<FontMatch> queue) {
        FontMatch bestMatch = queue.peek();
        System.out.println("-------");
        while (!queue.isEmpty()) {
            FontMatch match = queue.poll();
            FontInfo info = match.info;
            System.out.println(match.score + " | " + info.getMacStyle() + " " + info.getFamilyClass() + " " + info.getPanose() + " " + info.getCIDSystemInfo() + " " + info.getPostScriptName() + " " + (Object)((Object)info.getFormat()));
        }
        System.out.println("-------");
        return bestMatch;
    }

    static /* synthetic */ FontCache access$000() {
        return fontCache;
    }

    private static class FontMatch
    implements Comparable<FontMatch> {
        double score;
        final FontInfo info;

        FontMatch(FontInfo info) {
            this.info = info;
        }

        @Override
        public int compareTo(FontMatch match) {
            return Double.compare(match.score, this.score);
        }
    }

    private static class DefaultFontProvider {
        private static final FontProvider INSTANCE = new FileSystemFontProvider(FontMapperImpl.access$000());

        private DefaultFontProvider() {
        }
    }
}

