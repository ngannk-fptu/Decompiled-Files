/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.ext.bidi;

import java.util.Locale;
import javax.xml.namespace.QName;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.i18n.text.Bidi;
import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BidiHelper {
    public static final QName DIR = new QName("dir");

    BidiHelper() {
    }

    public static <T extends Element> void setDirection(Bidi.Direction direction, T element) {
        if (direction != Bidi.Direction.UNSPECIFIED) {
            element.setAttributeValue(DIR, direction.toString().toLowerCase());
        } else if (direction == Bidi.Direction.UNSPECIFIED) {
            element.setAttributeValue(DIR, "");
        } else if (direction == null) {
            element.removeAttribute(DIR);
        }
    }

    public static <T extends Element> Bidi.Direction getDirection(T element) {
        Object parent;
        Bidi.Direction direction = Bidi.Direction.UNSPECIFIED;
        String dir = element.getAttributeValue("dir");
        if (dir != null && dir.length() > 0) {
            direction = Bidi.Direction.valueOf(dir.toUpperCase());
        } else if (dir == null && (parent = element.getParentElement()) != null && parent instanceof Element) {
            direction = BidiHelper.getDirection((Element)parent);
        }
        return direction;
    }

    public static String getBidiText(Bidi.Direction direction, String text) {
        switch (direction) {
            case LTR: {
                return CharUtils.wrapBidi(text, '\u202a');
            }
            case RTL: {
                return CharUtils.wrapBidi(text, '\u202b');
            }
        }
        return text;
    }

    public static <T extends Element> String getBidiChildText(T element, QName child) {
        Object el = element.getFirstChild(child);
        return el != null ? BidiHelper.getBidiText(BidiHelper.getDirection(el), el.getText()) : null;
    }

    public static <T extends Element> String getBidiElementText(T element) {
        return BidiHelper.getBidiText(BidiHelper.getDirection(element), element.getText());
    }

    public static <T extends Element> String getBidiAttributeValue(T element, String name) {
        return BidiHelper.getBidiText(BidiHelper.getDirection(element), element.getAttributeValue(name));
    }

    public static <T extends Element> String getBidiAttributeValue(T element, QName name) {
        return BidiHelper.getBidiText(BidiHelper.getDirection(element), element.getAttributeValue(name));
    }

    public static <T extends Element> Bidi.Direction guessDirectionFromLanguage(T element) {
        return BidiHelper.guessDirectionFromLanguage(element, false);
    }

    public static <T extends Element> Bidi.Direction guessDirectionFromLanguage(T element, boolean ignoredir) {
        if (!ignoredir && BidiHelper.hasDirection(element)) {
            return BidiHelper.getDirection(element);
        }
        String language = element.getLanguage();
        Lang lang = language != null ? new Lang(language) : new Lang(Locale.getDefault());
        return Bidi.guessDirectionFromLanguage(lang);
    }

    public static <T extends Element> Bidi.Direction guessDirectionFromEncoding(T element) {
        return BidiHelper.guessDirectionFromEncoding(element, false);
    }

    public static <T extends Element> Bidi.Direction guessDirectionFromEncoding(T element, boolean ignoredir) {
        if (!ignoredir && BidiHelper.hasDirection(element)) {
            return BidiHelper.getDirection(element);
        }
        Document doc = element.getDocument();
        if (doc == null) {
            return Bidi.Direction.UNSPECIFIED;
        }
        return Bidi.guessDirectionFromEncoding(doc.getCharset());
    }

    public static <T extends Element> Bidi.Direction guessDirectionFromTextProperties(T element) {
        return BidiHelper.guessDirectionFromTextProperties(element, false);
    }

    public static <T extends Element> Bidi.Direction guessDirectionFromTextProperties(T element, boolean ignoredir) {
        if (!ignoredir && BidiHelper.hasDirection(element)) {
            return BidiHelper.getDirection(element);
        }
        return Bidi.guessDirectionFromTextProperties(element.getText());
    }

    public static <T extends Element> Bidi.Direction guessDirectionFromJavaBidi(T element) {
        return BidiHelper.guessDirectionFromJavaBidi(element, false);
    }

    public static <T extends Element> Bidi.Direction guessDirectionFromJavaBidi(T element, boolean ignoredir) {
        if (!ignoredir && BidiHelper.hasDirection(element)) {
            return BidiHelper.getDirection(element);
        }
        return Bidi.guessDirectionFromJavaBidi(element.getText());
    }

    private static <T extends Element> boolean hasDirection(T element) {
        Object parent;
        String dir = element.getAttributeValue("dir");
        if (dir != null && dir.length() > 0) {
            return true;
        }
        if (dir == null && (parent = element.getParentElement()) != null && parent instanceof Element) {
            return BidiHelper.hasDirection((Element)parent);
        }
        return false;
    }
}

