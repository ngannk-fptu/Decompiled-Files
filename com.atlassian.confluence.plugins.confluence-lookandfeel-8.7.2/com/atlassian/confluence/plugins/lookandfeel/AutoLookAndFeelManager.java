/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.themes.BaseColourScheme
 *  com.atlassian.confluence.themes.ColourSchemeManager
 *  com.atlassian.lookandfeel.ColorScheme
 *  com.atlassian.lookandfeel.ColorSchemeGenerator
 *  com.atlassian.lookandfeel.HSBColor
 *  com.atlassian.lookandfeel.ImageInfo
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.ImmutableMap
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.lookandfeel;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugins.lookandfeel.SiteLogoManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.themes.BaseColourScheme;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.lookandfeel.ColorScheme;
import com.atlassian.lookandfeel.ColorSchemeGenerator;
import com.atlassian.lookandfeel.HSBColor;
import com.atlassian.lookandfeel.ImageInfo;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.ImmutableMap;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutoLookAndFeelManager {
    private static final String BACKUP_COLOUR_SCHEME = "atlassian.confluence.colour.scheme.backup";
    private final Map<LookAndFeelColour, String> genericToProductSpecificColour = ImmutableMap.builder().put((Object)LookAndFeelColour.HEADER, (Object)"property.style.topbarcolour").put((Object)LookAndFeelColour.HEADER_HIGHLIGHT, (Object)"property.style.topbarmenuselectedbgcolour").put((Object)LookAndFeelColour.HEADER_SEPARATOR, (Object)"property.style.bordercolour").put((Object)LookAndFeelColour.HEADER_TEXT, (Object)"property.style.breadcrumbstextcolour").put((Object)LookAndFeelColour.HEADER_HIGHLIGHT_TEXT, (Object)"property.style.topbarmenuselectedtextcolour").put((Object)LookAndFeelColour.HEADER_BUTTON_BASEBGCOLOUR, (Object)"property.style.headerbuttonbasebgcolour").put((Object)LookAndFeelColour.HEADER_BUTTON_TEXTCOLOUR, (Object)"property.style.headerbuttontextcolour").put((Object)LookAndFeelColour.MENU_BGCOLOUR, (Object)"property.style.menuitemselectedbgcolour").put((Object)LookAndFeelColour.MENU_TEXTCOLOUR, (Object)"property.style.menuitemselectedtextcolour").build();
    private final ColourSchemeManager colourSchemeManager;
    private final SiteLogoManager siteLogoManager;
    private final BandanaManager bandanaManager;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public AutoLookAndFeelManager(@ComponentImport ColourSchemeManager colourSchemeManager, SiteLogoManager siteLogoManager, @ComponentImport BandanaManager bandanaManager, @ComponentImport TransactionTemplate transactionTemplate) {
        this.colourSchemeManager = colourSchemeManager;
        this.siteLogoManager = siteLogoManager;
        this.bandanaManager = bandanaManager;
        this.transactionTemplate = transactionTemplate;
    }

    public void generateFromSiteLogo() {
        ImageInfo logoInfo = this.getLogoInfo();
        ColorScheme colorScheme = ColorSchemeGenerator.generateFromLogo((ImageInfo)logoInfo);
        if (colorScheme != null) {
            this.applyColorScheme(colorScheme);
        } else {
            this.restoreDefaultColorScheme();
        }
    }

    private void applyColorScheme(ColorScheme colorScheme) {
        this.setBaseColors(colorScheme);
    }

    public void setBaseColors(ColorScheme colorScheme) {
        BaseColourScheme editableColourScheme = this.colourSchemeManager.getGlobalColourSchemeIsolated();
        this.setColor(editableColourScheme, LookAndFeelColour.HEADER, colorScheme.getHeader());
        this.setColor(editableColourScheme, LookAndFeelColour.HEADER_HIGHLIGHT, colorScheme.getHeaderHighlight());
        this.setColor(editableColourScheme, LookAndFeelColour.HEADER_SEPARATOR, colorScheme.getHeaderSeparator());
        this.setColor(editableColourScheme, LookAndFeelColour.MENU_BGCOLOUR, colorScheme.getHeaderHighlight());
        this.setColor(editableColourScheme, LookAndFeelColour.HEADER_BUTTON_BASEBGCOLOUR, colorScheme.getHeroButton());
        this.setColor(editableColourScheme, LookAndFeelColour.HEADER_TEXT, colorScheme.getHeaderText());
        this.setColor(editableColourScheme, LookAndFeelColour.HEADER_HIGHLIGHT_TEXT, colorScheme.getHeaderHighlightText());
        this.setColor(editableColourScheme, LookAndFeelColour.MENU_TEXTCOLOUR, colorScheme.getHeaderHighlightText());
        this.setColor(editableColourScheme, LookAndFeelColour.HEADER_BUTTON_TEXTCOLOUR, colorScheme.getHeroButtonText());
        this.colourSchemeManager.saveGlobalColourScheme(editableColourScheme);
    }

    public void restoreDefaultColorScheme() {
        this.colourSchemeManager.resetColourScheme(null);
    }

    public void backupColorScheme() {
        BaseColourScheme globalColourScheme = this.colourSchemeManager.getGlobalColourSchemeIsolated();
        this.transactionTemplate.execute(() -> {
            this.bandanaManager.removeValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, BACKUP_COLOUR_SCHEME);
            this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, BACKUP_COLOUR_SCHEME, (Object)globalColourScheme);
            return null;
        });
    }

    public void restoreBackupColorScheme() {
        this.transactionTemplate.execute(() -> {
            BaseColourScheme backupColorScheme = (BaseColourScheme)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, BACKUP_COLOUR_SCHEME);
            this.colourSchemeManager.saveGlobalColourScheme(backupColorScheme);
            return null;
        });
    }

    private void setColor(BaseColourScheme colorScheme, LookAndFeelColour lookAndFeelColour, HSBColor color) {
        String colourKey = this.getProductSpecificKey(lookAndFeelColour);
        if (colourKey != null) {
            colorScheme.set(colourKey, color.getHexString());
        }
    }

    private ImageInfo getLogoInfo() {
        ImageInfo imageInfo;
        block8: {
            InputStream logoContent = this.siteLogoManager.getCurrent().getContent();
            try {
                BufferedImage image = ImageIO.read(logoContent);
                imageInfo = new ImageInfo(image);
                if (logoContent == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (logoContent != null) {
                        try {
                            logoContent.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            logoContent.close();
        }
        return imageInfo;
    }

    private String getProductSpecificKey(LookAndFeelColour colour) {
        return this.genericToProductSpecificColour.get((Object)colour);
    }

    private static enum LookAndFeelColour {
        HEADER,
        HEADER_HIGHLIGHT,
        HEADER_SEPARATOR,
        HEADER_TEXT,
        HEADER_HIGHLIGHT_TEXT,
        MENU_BGCOLOUR,
        MENU_TEXTCOLOUR,
        HEADER_BUTTON_BASEBGCOLOUR,
        HEADER_BUTTON_TEXTCOLOUR;

    }
}

