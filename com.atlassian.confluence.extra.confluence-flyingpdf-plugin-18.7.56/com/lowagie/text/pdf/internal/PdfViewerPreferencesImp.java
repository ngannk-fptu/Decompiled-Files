/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.internal;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;

public class PdfViewerPreferencesImp
implements PdfViewerPreferences {
    public static final PdfName[] VIEWER_PREFERENCES = new PdfName[]{PdfName.HIDETOOLBAR, PdfName.HIDEMENUBAR, PdfName.HIDEWINDOWUI, PdfName.FITWINDOW, PdfName.CENTERWINDOW, PdfName.DISPLAYDOCTITLE, PdfName.NONFULLSCREENPAGEMODE, PdfName.DIRECTION, PdfName.VIEWAREA, PdfName.VIEWCLIP, PdfName.PRINTAREA, PdfName.PRINTCLIP, PdfName.PRINTSCALING, PdfName.DUPLEX, PdfName.PICKTRAYBYPDFSIZE, PdfName.PRINTPAGERANGE, PdfName.NUMCOPIES};
    public static final PdfName[] NONFULLSCREENPAGEMODE_PREFERENCES = new PdfName[]{PdfName.USENONE, PdfName.USEOUTLINES, PdfName.USETHUMBS, PdfName.USEOC};
    public static final PdfName[] DIRECTION_PREFERENCES = new PdfName[]{PdfName.L2R, PdfName.R2L};
    public static final PdfName[] PAGE_BOUNDARIES = new PdfName[]{PdfName.MEDIABOX, PdfName.CROPBOX, PdfName.BLEEDBOX, PdfName.TRIMBOX, PdfName.ARTBOX};
    public static final PdfName[] PRINTSCALING_PREFERENCES = new PdfName[]{PdfName.APPDEFAULT, PdfName.NONE};
    public static final PdfName[] DUPLEX_PREFERENCES = new PdfName[]{PdfName.SIMPLEX, PdfName.DUPLEXFLIPSHORTEDGE, PdfName.DUPLEXFLIPLONGEDGE};
    private int pageLayoutAndMode = 0;
    private PdfDictionary viewerPreferences = new PdfDictionary();
    private static final int viewerPreferencesMask = 0xFFF000;

    public int getPageLayoutAndMode() {
        return this.pageLayoutAndMode;
    }

    public PdfDictionary getViewerPreferences() {
        return this.viewerPreferences;
    }

    @Override
    public void setViewerPreferences(int preferences) {
        this.pageLayoutAndMode |= preferences;
        if ((preferences & 0xFFF000) != 0) {
            this.pageLayoutAndMode = 0xFF000FFF & this.pageLayoutAndMode;
            if ((preferences & 0x1000) != 0) {
                this.viewerPreferences.put(PdfName.HIDETOOLBAR, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 0x2000) != 0) {
                this.viewerPreferences.put(PdfName.HIDEMENUBAR, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 0x4000) != 0) {
                this.viewerPreferences.put(PdfName.HIDEWINDOWUI, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 0x8000) != 0) {
                this.viewerPreferences.put(PdfName.FITWINDOW, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 0x10000) != 0) {
                this.viewerPreferences.put(PdfName.CENTERWINDOW, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 0x20000) != 0) {
                this.viewerPreferences.put(PdfName.DISPLAYDOCTITLE, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 0x40000) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USENONE);
            } else if ((preferences & 0x80000) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USEOUTLINES);
            } else if ((preferences & 0x100000) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USETHUMBS);
            } else if ((preferences & 0x200000) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USEOC);
            }
            if ((preferences & 0x400000) != 0) {
                this.viewerPreferences.put(PdfName.DIRECTION, PdfName.L2R);
            } else if ((preferences & 0x800000) != 0) {
                this.viewerPreferences.put(PdfName.DIRECTION, PdfName.R2L);
            }
            if ((preferences & 0x1000000) != 0) {
                this.viewerPreferences.put(PdfName.PRINTSCALING, PdfName.NONE);
            }
        }
    }

    private int getIndex(PdfName key) {
        for (int i = 0; i < VIEWER_PREFERENCES.length; ++i) {
            if (!VIEWER_PREFERENCES[i].equals(key)) continue;
            return i;
        }
        return -1;
    }

    private boolean isPossibleValue(PdfName value, PdfName[] accepted) {
        for (PdfName pdfName : accepted) {
            if (!pdfName.equals(value)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void addViewerPreference(PdfName key, PdfObject value) {
        switch (this.getIndex(key)) {
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 14: {
                if (!(value instanceof PdfBoolean)) break;
                this.viewerPreferences.put(key, value);
                break;
            }
            case 6: {
                if (!(value instanceof PdfName) || !this.isPossibleValue((PdfName)value, NONFULLSCREENPAGEMODE_PREFERENCES)) break;
                this.viewerPreferences.put(key, value);
                break;
            }
            case 7: {
                if (!(value instanceof PdfName) || !this.isPossibleValue((PdfName)value, DIRECTION_PREFERENCES)) break;
                this.viewerPreferences.put(key, value);
                break;
            }
            case 8: 
            case 9: 
            case 10: 
            case 11: {
                if (!(value instanceof PdfName) || !this.isPossibleValue((PdfName)value, PAGE_BOUNDARIES)) break;
                this.viewerPreferences.put(key, value);
                break;
            }
            case 12: {
                if (!(value instanceof PdfName) || !this.isPossibleValue((PdfName)value, PRINTSCALING_PREFERENCES)) break;
                this.viewerPreferences.put(key, value);
                break;
            }
            case 13: {
                if (!(value instanceof PdfName) || !this.isPossibleValue((PdfName)value, DUPLEX_PREFERENCES)) break;
                this.viewerPreferences.put(key, value);
                break;
            }
            case 15: {
                if (!(value instanceof PdfArray)) break;
                this.viewerPreferences.put(key, value);
                break;
            }
            case 16: {
                if (!(value instanceof PdfNumber)) break;
                this.viewerPreferences.put(key, value);
            }
        }
    }

    public void addToCatalog(PdfDictionary catalog) {
        catalog.remove(PdfName.PAGELAYOUT);
        if ((this.pageLayoutAndMode & 1) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.SINGLEPAGE);
        } else if ((this.pageLayoutAndMode & 2) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.ONECOLUMN);
        } else if ((this.pageLayoutAndMode & 4) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.TWOCOLUMNLEFT);
        } else if ((this.pageLayoutAndMode & 8) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.TWOCOLUMNRIGHT);
        } else if ((this.pageLayoutAndMode & 0x10) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.TWOPAGELEFT);
        } else if ((this.pageLayoutAndMode & 0x20) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.TWOPAGERIGHT);
        }
        catalog.remove(PdfName.PAGEMODE);
        if ((this.pageLayoutAndMode & 0x40) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USENONE);
        } else if ((this.pageLayoutAndMode & 0x80) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
        } else if ((this.pageLayoutAndMode & 0x100) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USETHUMBS);
        } else if ((this.pageLayoutAndMode & 0x200) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.FULLSCREEN);
        } else if ((this.pageLayoutAndMode & 0x400) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USEOC);
        } else if ((this.pageLayoutAndMode & 0x800) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USEATTACHMENTS);
        }
        catalog.remove(PdfName.VIEWERPREFERENCES);
        if (this.viewerPreferences.size() > 0) {
            catalog.put(PdfName.VIEWERPREFERENCES, this.viewerPreferences);
        }
    }

    public static PdfViewerPreferencesImp getViewerPreferences(PdfDictionary catalog) {
        PdfViewerPreferencesImp preferences = new PdfViewerPreferencesImp();
        int prefs = 0;
        PdfName name = null;
        PdfObject obj = PdfReader.getPdfObjectRelease(catalog.get(PdfName.PAGELAYOUT));
        if (obj != null && obj.isName()) {
            name = (PdfName)obj;
            if (name.equals(PdfName.SINGLEPAGE)) {
                prefs |= 1;
            } else if (name.equals(PdfName.ONECOLUMN)) {
                prefs |= 2;
            } else if (name.equals(PdfName.TWOCOLUMNLEFT)) {
                prefs |= 4;
            } else if (name.equals(PdfName.TWOCOLUMNRIGHT)) {
                prefs |= 8;
            } else if (name.equals(PdfName.TWOPAGELEFT)) {
                prefs |= 0x10;
            } else if (name.equals(PdfName.TWOPAGERIGHT)) {
                prefs |= 0x20;
            }
        }
        if ((obj = PdfReader.getPdfObjectRelease(catalog.get(PdfName.PAGEMODE))) != null && obj.isName()) {
            name = (PdfName)obj;
            if (name.equals(PdfName.USENONE)) {
                prefs |= 0x40;
            } else if (name.equals(PdfName.USEOUTLINES)) {
                prefs |= 0x80;
            } else if (name.equals(PdfName.USETHUMBS)) {
                prefs |= 0x100;
            } else if (name.equals(PdfName.FULLSCREEN)) {
                prefs |= 0x200;
            } else if (name.equals(PdfName.USEOC)) {
                prefs |= 0x400;
            } else if (name.equals(PdfName.USEATTACHMENTS)) {
                prefs |= 0x800;
            }
        }
        preferences.setViewerPreferences(prefs);
        obj = PdfReader.getPdfObjectRelease(catalog.get(PdfName.VIEWERPREFERENCES));
        if (obj != null && obj.isDictionary()) {
            PdfDictionary vp = (PdfDictionary)obj;
            for (PdfName viewerPreference : VIEWER_PREFERENCES) {
                obj = PdfReader.getPdfObjectRelease(vp.get(viewerPreference));
                preferences.addViewerPreference(viewerPreference, obj);
            }
        }
        return preferences;
    }
}

