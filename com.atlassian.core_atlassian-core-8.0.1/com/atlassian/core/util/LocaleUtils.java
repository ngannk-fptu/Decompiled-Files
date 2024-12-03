/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.util;

import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.core.util.LocaleComparator;
import com.atlassian.core.util.XMLUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class LocaleUtils {
    private static final Logger log = LoggerFactory.getLogger(LocaleUtils.class);
    public static final String LANGUAGE_DESCRIPTOR_FILENAME = "language-descriptor.xml";
    public static final String LOCALE_TAG_NAME = "locale";
    public List<Locale> installedLocales;

    public List<Locale> getInstalledLocales() throws IOException {
        if (this.installedLocales == null) {
            this.installedLocales = new ArrayList<Locale>();
            URL url = null;
            Enumeration localeDescriptors = ClassLoaderUtils.getResources(LANGUAGE_DESCRIPTOR_FILENAME, this.getClass());
            while (localeDescriptors.hasMoreElements()) {
                try {
                    url = (URL)localeDescriptors.nextElement();
                    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document xmlDoc = db.parse(url.openConnection().getInputStream());
                    Element root = xmlDoc.getDocumentElement();
                    String locale = XMLUtils.getContainedText(root, LOCALE_TAG_NAME);
                    if (!StringUtils.isBlank((CharSequence)locale)) {
                        this.installedLocales.add(this.getLocale(locale));
                        continue;
                    }
                    throw new IllegalArgumentException("The locale element must be set in " + url);
                }
                catch (IOException | ParserConfigurationException | SAXException e) {
                    log.error("Error while reading language descriptor '" + url + "'.", (Throwable)e);
                }
            }
            this.installedLocales.sort(new LocaleComparator());
        }
        return this.installedLocales;
    }

    public Locale getLocale(String locale) {
        if (!StringUtils.isBlank((CharSequence)locale)) {
            int _pos = locale.indexOf("_");
            if (_pos != -1) {
                return new Locale(locale.substring(0, _pos), locale.substring(_pos + 1));
            }
            return new Locale(locale, "");
        }
        return null;
    }
}

