/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.event.EventDispatcher
 *  org.apache.batik.util.ParsedURL
 *  org.w3c.dom.svg.SVGAElement
 *  org.w3c.dom.svg.SVGDocument
 */
package org.apache.batik.bridge;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import org.apache.batik.bridge.BridgeExtension;
import org.apache.batik.bridge.ExternalResourceSecurity;
import org.apache.batik.bridge.FontFamilyResolver;
import org.apache.batik.bridge.Mark;
import org.apache.batik.bridge.ScriptSecurity;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAElement;
import org.w3c.dom.svg.SVGDocument;

public interface UserAgent {
    public EventDispatcher getEventDispatcher();

    public Dimension2D getViewportSize();

    public void displayError(Exception var1);

    public void displayMessage(String var1);

    public void showAlert(String var1);

    public String showPrompt(String var1);

    public String showPrompt(String var1, String var2);

    public boolean showConfirm(String var1);

    public float getPixelUnitToMillimeter();

    public float getPixelToMM();

    public float getMediumFontSize();

    public float getLighterFontWeight(float var1);

    public float getBolderFontWeight(float var1);

    public String getDefaultFontFamily();

    public String getLanguages();

    public String getUserStyleSheetURI();

    public void openLink(SVGAElement var1);

    public void setSVGCursor(Cursor var1);

    public void setTextSelection(Mark var1, Mark var2);

    public void deselectAll();

    public String getXMLParserClassName();

    public boolean isXMLParserValidating();

    public AffineTransform getTransform();

    public void setTransform(AffineTransform var1);

    public String getMedia();

    public String getAlternateStyleSheet();

    public Point getClientAreaLocationOnScreen();

    public boolean hasFeature(String var1);

    public boolean supportExtension(String var1);

    public void registerExtension(BridgeExtension var1);

    public void handleElement(Element var1, Object var2);

    public ScriptSecurity getScriptSecurity(String var1, ParsedURL var2, ParsedURL var3);

    public void checkLoadScript(String var1, ParsedURL var2, ParsedURL var3) throws SecurityException;

    public ExternalResourceSecurity getExternalResourceSecurity(ParsedURL var1, ParsedURL var2);

    public void checkLoadExternalResource(ParsedURL var1, ParsedURL var2) throws SecurityException;

    public SVGDocument getBrokenLinkDocument(Element var1, String var2, String var3);

    public void loadDocument(String var1);

    public FontFamilyResolver getFontFamilyResolver();
}

