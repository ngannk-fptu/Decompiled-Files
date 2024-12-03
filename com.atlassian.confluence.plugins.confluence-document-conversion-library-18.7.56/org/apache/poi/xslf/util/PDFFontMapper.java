/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.rototor.pdfbox.graphics2d.IPdfBoxGraphics2DFontTextDrawer$IFontTextDrawerEnv
 *  de.rototor.pdfbox.graphics2d.PdfBoxGraphics2DFontTextDrawer
 *  org.apache.pdfbox.pdmodel.font.PDFont
 */
package org.apache.poi.xslf.util;

import de.rototor.pdfbox.graphics2d.IPdfBoxGraphics2DFontTextDrawer;
import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2DFontTextDrawer;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class PDFFontMapper
extends PdfBoxGraphics2DFontTextDrawer {
    private static final String DEFAULT_TTF_PATTERN = ".*\\.tt[fc]";
    private static final String FONTDIRS_MAC = "$HOME/Library/Fonts;/Library/Fonts;/Network/Library/Fonts;/System/Library/Fonts;/System Folder/Fonts";
    private static final String FONTDIRS_WIN = "C:\\Windows\\Fonts";
    private static final String FONTDIRS_UNX = "/usr/share/fonts;/usr/local/share/fonts;$HOME/.fonts";
    private final Map<String, File> fonts = new HashMap<String, File>();
    private final Set<String> registered = new HashSet<String>();

    public PDFFontMapper(String fontDir, String fontTtf) {
        this.registerFonts(fontDir, fontTtf);
    }

    private void registerFonts(String fontDir, String fontTtf) {
        if (fontDir == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ROOT);
            fontDir = OS.contains("mac") || OS.contains("darwin") ? FONTDIRS_MAC : (OS.contains("win") ? FONTDIRS_WIN : FONTDIRS_UNX);
        }
        String fd = fontDir.replace("$HOME", System.getProperty("user.home"));
        LinkedList dirs = new LinkedList();
        Stream.of(fd.split(";")).map(File::new).filter(File::isDirectory).forEach(dirs::add);
        Pattern p = Pattern.compile(fontTtf == null ? DEFAULT_TTF_PATTERN : fontTtf);
        while (!dirs.isEmpty()) {
            File[] ttfs = ((File)dirs.removeFirst()).listFiles((f, n) -> {
                File f2 = new File(f, n);
                if (f2.isDirectory()) {
                    dirs.add(f2);
                    return false;
                }
                return p.matcher(n).matches();
            });
            if (ttfs == null) continue;
            for (File f2 : ttfs) {
                try {
                    Font font = Font.createFont(0, f2);
                    this.fonts.put(font.getFontName(Locale.ROOT), f2);
                }
                catch (FontFormatException | IOException exception) {
                    // empty catch block
                }
            }
        }
    }

    protected PDFont mapFont(Font font, IPdfBoxGraphics2DFontTextDrawer.IFontTextDrawerEnv env) throws IOException, FontFormatException {
        String name = font.getFontName(Locale.ROOT);
        if (!this.registered.contains(name)) {
            this.registered.add(name);
            File f = this.fonts.get(name);
            if (f != null) {
                super.registerFont(name, f);
            }
        }
        return super.mapFont(font, env);
    }
}

