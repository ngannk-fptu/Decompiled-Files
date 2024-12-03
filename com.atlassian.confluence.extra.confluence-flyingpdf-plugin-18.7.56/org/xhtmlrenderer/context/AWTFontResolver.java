/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.context;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.extend.FontResolver;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.swing.AWTFSFont;

public class AWTFontResolver
implements FontResolver {
    HashMap instance_hash;
    HashMap available_fonts_hash;

    public AWTFontResolver() {
        this.init();
    }

    private void init() {
        GraphicsEnvironment gfx = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] available_fonts = gfx.getAvailableFontFamilyNames();
        this.instance_hash = new HashMap();
        this.available_fonts_hash = new HashMap();
        for (int i = 0; i < available_fonts.length; ++i) {
            this.available_fonts_hash.put(available_fonts[i], "");
        }
        this.available_fonts_hash.put("Serif", new Font("Serif", 0, 1));
        this.available_fonts_hash.put("SansSerif", new Font("SansSerif", 0, 1));
        this.available_fonts_hash.put("Monospaced", new Font("Monospaced", 0, 1));
    }

    @Override
    public void flushCache() {
        this.init();
    }

    public FSFont resolveFont(SharedContext ctx, String[] families, float size, IdentValue weight, IdentValue style, IdentValue variant) {
        if (families != null) {
            for (int i = 0; i < families.length; ++i) {
                Font font = this.resolveFont(ctx, families[i], size, weight, style, variant);
                if (font == null) continue;
                return new AWTFSFont(font);
            }
        }
        String family = "SansSerif";
        if (style == IdentValue.ITALIC) {
            family = "Serif";
        }
        Font fnt = AWTFontResolver.createFont(ctx, (Font)this.available_fonts_hash.get(family), size, weight, style, variant);
        this.instance_hash.put(AWTFontResolver.getFontInstanceHashName(ctx, family, size, weight, style, variant), fnt);
        return new AWTFSFont(fnt);
    }

    public void setFontMapping(String name, Font font) {
        this.available_fonts_hash.put(name, font.deriveFont(1.0f));
    }

    protected static Font createFont(SharedContext ctx, Font root_font, float size, IdentValue weight, IdentValue style, IdentValue variant) {
        int font_const = 0;
        if (weight != null && (weight == IdentValue.BOLD || weight == IdentValue.FONT_WEIGHT_700 || weight == IdentValue.FONT_WEIGHT_800 || weight == IdentValue.FONT_WEIGHT_900)) {
            font_const |= 1;
        }
        if (style != null && (style == IdentValue.ITALIC || style == IdentValue.OBLIQUE)) {
            font_const |= 2;
        }
        Font fnt = root_font.deriveFont(font_const, size *= ctx.getTextRenderer().getFontScale());
        if (variant != null && variant == IdentValue.SMALL_CAPS) {
            fnt = fnt.deriveFont((float)((double)fnt.getSize() * 0.6));
        }
        return fnt;
    }

    protected Font resolveFont(SharedContext ctx, String font, float size, IdentValue weight, IdentValue style, IdentValue variant) {
        String font_instance_name;
        if (font.startsWith("\"")) {
            font = font.substring(1);
        }
        if (font.endsWith("\"")) {
            font = font.substring(0, font.length() - 1);
        }
        if (font.equalsIgnoreCase("serif")) {
            font = "Serif";
        }
        if (font.equalsIgnoreCase("sans-serif")) {
            font = "SansSerif";
        }
        if (font.equalsIgnoreCase("monospace")) {
            font = "Monospaced";
        }
        if (font.equals("Serif") && style == IdentValue.OBLIQUE) {
            font = "SansSerif";
        }
        if (font.equals("SansSerif") && style == IdentValue.ITALIC) {
            font = "Serif";
        }
        if (this.instance_hash.containsKey(font_instance_name = AWTFontResolver.getFontInstanceHashName(ctx, font, size, weight, style, variant))) {
            return (Font)this.instance_hash.get(font_instance_name);
        }
        if (this.available_fonts_hash.containsKey(font)) {
            Object value = this.available_fonts_hash.get(font);
            Font root_font = null;
            if (value instanceof Font) {
                root_font = (Font)value;
            } else {
                root_font = new Font(font, 0, 1);
                this.available_fonts_hash.put(font, root_font);
            }
            Font fnt = AWTFontResolver.createFont(ctx, root_font, size, weight, style, variant);
            this.instance_hash.put(font_instance_name, fnt);
            return fnt;
        }
        return null;
    }

    protected static String getFontInstanceHashName(SharedContext ctx, String name, float size, IdentValue weight, IdentValue style, IdentValue variant) {
        return name + "-" + size * ctx.getTextRenderer().getFontScale() + "-" + weight + "-" + style + "-" + variant;
    }

    @Override
    public FSFont resolveFont(SharedContext renderingContext, FontSpecification spec) {
        return this.resolveFont(renderingContext, spec.families, spec.size, spec.fontWeight, spec.fontStyle, spec.variant);
    }
}

