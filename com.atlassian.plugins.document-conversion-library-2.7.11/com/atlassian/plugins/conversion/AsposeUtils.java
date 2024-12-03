/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.cells.FontConfigs
 *  com.aspose.cells.License
 *  com.aspose.imaging.License
 *  com.aspose.pdf.License
 *  com.aspose.psd.License
 *  com.aspose.slides.FontsLoader
 *  com.aspose.slides.License
 *  com.aspose.slides.exceptions.Exception
 *  com.aspose.words.FontSettings
 *  com.aspose.words.License
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.conversion;

import com.aspose.cells.FontConfigs;
import com.aspose.cells.License;
import com.aspose.slides.FontsLoader;
import com.aspose.words.FontSettings;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsposeUtils {
    private static final Logger log = LoggerFactory.getLogger(AsposeUtils.class);
    private static final String PROP_SEARCH_FONT_PATH = "confluence.document.conversion.fontpath";
    private static final String PROP_WORDS_DEFAULT_FONT_NAME = "confluence.document.conversion.words.defaultfontname";
    private static final String PROP_SLIDES_DEFAULT_REGULAR_FONT_NAME = "confluence.document.conversion.slides.defaultfontname.regular";
    private static final String PROP_SLIDES_DEFAULT_ASIAN_FONT_NAME = "confluence.document.conversion.slides.defaultfontname.asian";
    private static final String PROP_SLIDES_DEFAULT_SYMBOL_FONT_NAME = "confluence.document.conversion.slides.defaultfontname.symbol";
    private static final String OS_NAME = System.getProperty("os.name");
    private static final boolean IS_OS_WINDOWS = OS_NAME.startsWith("Windows");
    private static final boolean IS_OS_MAC = OS_NAME.startsWith("Mac");
    private static final String DEFAULT_SEARCH_FONT_PATH = IS_OS_WINDOWS ? System.getenv("WINDIR") + "\\Fonts" : (IS_OS_MAC ? "/Library/Fonts" : "/usr/share/fonts");
    private static final String DEFAULT_SLIDES_DEFAULT_ASIAN_FONT = "TakaoPGothic";
    public static final String SLIDES_DEFAULT_REGULAR_FONT = System.getProperty("confluence.document.conversion.slides.defaultfontname.regular");
    public static final String SLIDES_DEFAULT_ASIAN_FONT = System.getProperty("confluence.document.conversion.slides.defaultfontname.asian", "TakaoPGothic");
    public static final String SLIDES_DEFAULT_SYMBOL_FONT = System.getProperty("confluence.document.conversion.slides.defaultfontname.symbol");
    private static final String AsposeLicense = "<License>\n  <Data>\n    <LicensedTo>Atlassian Pty Ltd</LicensedTo>\n    <EmailTo>jtblin@atlassian.com</EmailTo>\n    <LicenseType>Site OEM</LicenseType>\n    <LicenseNote>Limited to 10 developers, unlimited physical locations</LicenseNote>\n    <OrderID>200825182509</OrderID>\n    <UserID>83733</UserID>\n    <OEM>This is a redistributable license</OEM>\n    <Products>\n      <Product>Aspose.Total for Java</Product>\n    </Products>\n    <EditionType>Enterprise</EditionType>\n    <SerialNumber>3461b15c-6e17-42ec-aa75-5561b9cf1d80</SerialNumber>\n    <SubscriptionExpiry>20210903</SubscriptionExpiry>\n    <LicenseVersion>3.0</LicenseVersion>\n    <LicenseInstructions>https://purchase.aspose.com/policies/use-license</LicenseInstructions>\n  </Data>\n  <Signature>yfglnh7SXWk9yh3j6C+Yqeg2km3VS0E6hEydy+h1IjcF85uT5sC1NgOYKbkNBIUikG+IP/YsHEHijBpiy5u1fnHneW8NmOgfiUjRE3qNa4hNibTK2OL6TbwMtWh1VrtyuM0GiQFHc9W5uUlTw5buSnsB/5Gv/EXDfSkdHlVkeyI=</Signature>\n</License>";

    public static void license() {
        log.info("Loading licences for Aspose");
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            com.aspose.psd.License licenseImg;
            Thread.currentThread().setContextClassLoader(AsposeUtils.class.getClassLoader());
            try {
                log.debug("Loading license for aspose-psd");
                licenseImg = new com.aspose.psd.License();
                licenseImg.setLicense((InputStream)new ByteArrayInputStream(AsposeLicense.getBytes()));
            }
            catch (Exception e) {
                log.error("Failed to load license for aspose-imaging", (Throwable)e);
            }
            try {
                log.debug("Loading license for aspose-imaging");
                licenseImg = new com.aspose.imaging.License();
                licenseImg.setLicense((InputStream)new ByteArrayInputStream(AsposeLicense.getBytes()));
            }
            catch (Exception e) {
                log.error("Failed to load license for aspose-imaging", (Throwable)e);
            }
            try {
                log.debug("Loading license for aspose-slides");
                com.aspose.slides.License licenseSlides = new com.aspose.slides.License();
                licenseSlides.setLicense((InputStream)new ByteArrayInputStream(AsposeLicense.getBytes()));
            }
            catch (Exception e) {
                log.error("Failed to load license for aspose-slides", (Throwable)e);
            }
            try {
                log.debug("Loading license for aspose-words");
                com.aspose.words.License licenseWords = new com.aspose.words.License();
                licenseWords.setLicense((InputStream)new ByteArrayInputStream(AsposeLicense.getBytes()));
            }
            catch (Exception e) {
                log.error("Failed to load license for aspose-words", (Throwable)e);
            }
            try {
                log.debug("Loading license for aspose-cells");
                License licenseCells = new License();
                licenseCells.setLicense((InputStream)new ByteArrayInputStream(AsposeLicense.getBytes()));
            }
            catch (Exception e) {
                log.error("Failed to load license for aspose-cells", (Throwable)e);
            }
            try {
                log.debug("Loading license for aspose-pdf");
                com.aspose.pdf.License licensePdf = new com.aspose.pdf.License();
                licensePdf.setLicense((InputStream)new ByteArrayInputStream(AsposeLicense.getBytes()));
            }
            catch (Exception e) {
                log.error("Failed to load license for aspose-pdf", (Throwable)e);
            }
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    public static void configureFonts() {
        log.info("Configuring Aspose Fonts");
        String defaultWordsFont = System.getProperty(PROP_WORDS_DEFAULT_FONT_NAME);
        if (defaultWordsFont != null) {
            log.info("Setting default font for aspose words to [{}]", (Object)defaultWordsFont);
            FontSettings.getDefaultInstance().getSubstitutionSettings().getDefaultFontSubstitution().setDefaultFontName(defaultWordsFont);
        }
        String path = System.getProperty(PROP_SEARCH_FONT_PATH, DEFAULT_SEARCH_FONT_PATH);
        File basePath = new File(path);
        log.info("Trying font path [{}]", (Object)path);
        if (!basePath.isDirectory() && !path.equals(DEFAULT_SEARCH_FONT_PATH)) {
            log.warn("Configured font path [{}] is not a directory; trying again with the default path [{}]", (Object)path, (Object)DEFAULT_SEARCH_FONT_PATH);
            path = DEFAULT_SEARCH_FONT_PATH;
            if (!new File(path).isDirectory()) {
                log.warn("Default font path [{}] is not a directory, cannot configure fonts", (Object)path);
                return;
            }
        }
        log.info("Configuring aspose-words with font path [{}]", (Object)path);
        FontSettings.getDefaultInstance().setFontsFolder(path, true);
        try {
            log.info("Loading aspose-slides external fonts from [{}]", (Object)path);
            FontsLoader.loadExternalFonts((String[])new String[]{path});
        }
        catch (com.aspose.slides.exceptions.Exception ex) {
            log.warn("aspose-slides failed to load external fonts from path [{}]", (Object)path, (Object)ex);
        }
        log.info("Configuring aspose-cells with font path [{}]", (Object)path);
        FontConfigs.setFontFolder((String)path, (boolean)true);
    }
}

