/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.event.EventDispatcher
 *  org.apache.batik.util.ParsedURL
 *  org.apache.batik.util.SVGFeatureStrings
 *  org.apache.batik.util.XMLResourceDescriptor
 *  org.w3c.dom.svg.SVGAElement
 *  org.w3c.dom.svg.SVGDocument
 */
package org.apache.batik.bridge;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.BridgeExtension;
import org.apache.batik.bridge.DefaultExternalResourceSecurity;
import org.apache.batik.bridge.DefaultFontFamilyResolver;
import org.apache.batik.bridge.DefaultScriptSecurity;
import org.apache.batik.bridge.ExternalResourceSecurity;
import org.apache.batik.bridge.FontFamilyResolver;
import org.apache.batik.bridge.Mark;
import org.apache.batik.bridge.ScriptSecurity;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.SVGFeatureStrings;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAElement;
import org.w3c.dom.svg.SVGDocument;

public class UserAgentAdapter
implements UserAgent {
    protected Set FEATURES = new HashSet();
    protected Set extensions = new HashSet();
    protected BridgeContext ctx;

    public void setBridgeContext(BridgeContext ctx) {
        this.ctx = ctx;
    }

    public void addStdFeatures() {
        SVGFeatureStrings.addSupportedFeatureStrings((Set)this.FEATURES);
    }

    @Override
    public Dimension2D getViewportSize() {
        return new Dimension(1, 1);
    }

    @Override
    public void displayMessage(String message) {
    }

    public void displayError(String message) {
        this.displayMessage(message);
    }

    @Override
    public void displayError(Exception e) {
        this.displayError(e.getMessage());
    }

    @Override
    public void showAlert(String message) {
    }

    @Override
    public String showPrompt(String message) {
        return null;
    }

    @Override
    public String showPrompt(String message, String defaultValue) {
        return null;
    }

    @Override
    public boolean showConfirm(String message) {
        return false;
    }

    @Override
    public float getPixelUnitToMillimeter() {
        return 0.26458332f;
    }

    @Override
    public float getPixelToMM() {
        return this.getPixelUnitToMillimeter();
    }

    @Override
    public String getDefaultFontFamily() {
        return "Arial, Helvetica, sans-serif";
    }

    @Override
    public float getMediumFontSize() {
        return 228.59999f / (72.0f * this.getPixelUnitToMillimeter());
    }

    @Override
    public float getLighterFontWeight(float f) {
        return UserAgentAdapter.getStandardLighterFontWeight(f);
    }

    @Override
    public float getBolderFontWeight(float f) {
        return UserAgentAdapter.getStandardBolderFontWeight(f);
    }

    @Override
    public String getLanguages() {
        return "en";
    }

    @Override
    public String getMedia() {
        return "all";
    }

    @Override
    public String getAlternateStyleSheet() {
        return null;
    }

    @Override
    public String getUserStyleSheetURI() {
        return null;
    }

    @Override
    public String getXMLParserClassName() {
        return XMLResourceDescriptor.getXMLParserClassName();
    }

    @Override
    public boolean isXMLParserValidating() {
        return false;
    }

    @Override
    public EventDispatcher getEventDispatcher() {
        return null;
    }

    @Override
    public void openLink(SVGAElement elt) {
    }

    @Override
    public void setSVGCursor(Cursor cursor) {
    }

    @Override
    public void setTextSelection(Mark start, Mark end) {
    }

    @Override
    public void deselectAll() {
    }

    public void runThread(Thread t) {
    }

    @Override
    public AffineTransform getTransform() {
        return null;
    }

    @Override
    public void setTransform(AffineTransform at) {
    }

    @Override
    public Point getClientAreaLocationOnScreen() {
        return new Point();
    }

    @Override
    public boolean hasFeature(String s) {
        return this.FEATURES.contains(s);
    }

    @Override
    public boolean supportExtension(String s) {
        return this.extensions.contains(s);
    }

    @Override
    public void registerExtension(BridgeExtension ext) {
        Iterator i = ext.getImplementedExtensions();
        while (i.hasNext()) {
            this.extensions.add(i.next());
        }
    }

    @Override
    public void handleElement(Element elt, Object data) {
    }

    @Override
    public ScriptSecurity getScriptSecurity(String scriptType, ParsedURL scriptURL, ParsedURL docURL) {
        return new DefaultScriptSecurity(scriptType, scriptURL, docURL);
    }

    @Override
    public void checkLoadScript(String scriptType, ParsedURL scriptURL, ParsedURL docURL) throws SecurityException {
        ScriptSecurity s = this.getScriptSecurity(scriptType, scriptURL, docURL);
        if (s != null) {
            s.checkLoadScript();
        }
    }

    @Override
    public ExternalResourceSecurity getExternalResourceSecurity(ParsedURL resourceURL, ParsedURL docURL) {
        return new DefaultExternalResourceSecurity(resourceURL, docURL);
    }

    @Override
    public void checkLoadExternalResource(ParsedURL resourceURL, ParsedURL docURL) throws SecurityException {
        ExternalResourceSecurity s = this.getExternalResourceSecurity(resourceURL, docURL);
        if (s != null) {
            s.checkLoadExternalResource();
        }
    }

    public static float getStandardLighterFontWeight(float f) {
        int weight = (int)((f + 50.0f) / 100.0f) * 100;
        switch (weight) {
            case 100: {
                return 100.0f;
            }
            case 200: {
                return 100.0f;
            }
            case 300: {
                return 200.0f;
            }
            case 400: {
                return 300.0f;
            }
            case 500: {
                return 400.0f;
            }
            case 600: {
                return 400.0f;
            }
            case 700: {
                return 400.0f;
            }
            case 800: {
                return 400.0f;
            }
            case 900: {
                return 400.0f;
            }
        }
        throw new IllegalArgumentException("Bad Font Weight: " + f);
    }

    public static float getStandardBolderFontWeight(float f) {
        int weight = (int)((f + 50.0f) / 100.0f) * 100;
        switch (weight) {
            case 100: {
                return 600.0f;
            }
            case 200: {
                return 600.0f;
            }
            case 300: {
                return 600.0f;
            }
            case 400: {
                return 600.0f;
            }
            case 500: {
                return 600.0f;
            }
            case 600: {
                return 700.0f;
            }
            case 700: {
                return 800.0f;
            }
            case 800: {
                return 900.0f;
            }
            case 900: {
                return 900.0f;
            }
        }
        throw new IllegalArgumentException("Bad Font Weight: " + f);
    }

    @Override
    public SVGDocument getBrokenLinkDocument(Element e, String url, String message) {
        throw new BridgeException(this.ctx, e, "uri.image.broken", new Object[]{url, message});
    }

    @Override
    public void loadDocument(String url) {
    }

    @Override
    public FontFamilyResolver getFontFamilyResolver() {
        return DefaultFontFamilyResolver.SINGLETON;
    }
}

