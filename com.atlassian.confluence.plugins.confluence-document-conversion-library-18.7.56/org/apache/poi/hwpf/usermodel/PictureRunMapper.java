/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;

public class PictureRunMapper {
    private PicturesTable picturesTable;
    private Set<Picture> claimed = new HashSet<Picture>();
    private Map<Integer, Picture> lookup;
    private List<Picture> nonU1based;
    private List<Picture> all;
    private int pn;

    public PictureRunMapper(HWPFDocument doc) {
        this.picturesTable = doc.getPicturesTable();
        this.all = this.picturesTable.getAllPictures();
        this.lookup = new HashMap<Integer, Picture>();
        for (Picture p : this.all) {
            this.lookup.put(p.getStartOffset(), p);
        }
        this.nonU1based = new ArrayList<Picture>();
        this.nonU1based.addAll(this.all);
        Range r = doc.getRange();
        for (int i = 0; i < r.numCharacterRuns(); ++i) {
            CharacterRun cr = r.getCharacterRun(i);
            if (!this.picturesTable.hasPicture(cr)) continue;
            Picture p = this.getFor(cr);
            int at = this.nonU1based.indexOf(p);
            this.nonU1based.set(at, null);
        }
    }

    public boolean hasPicture(CharacterRun cr) {
        return this.picturesTable.hasPicture(cr);
    }

    public Picture getFor(CharacterRun cr) {
        return this.lookup.get(cr.getPicOffset());
    }

    public void markAsClaimed(Picture picture) {
        this.claimed.add(picture);
    }

    public boolean hasBeenClaimed(Picture picture) {
        return this.claimed.contains(picture);
    }

    public int pictureNumber(Picture picture) {
        return this.all.indexOf(picture) + 1;
    }

    public Picture nextUnclaimed() {
        Picture p = null;
        while (this.pn < this.nonU1based.size()) {
            p = this.nonU1based.get(this.pn);
            ++this.pn;
            if (p == null) continue;
            this.claimed.add(p);
            return p;
        }
        return null;
    }
}

