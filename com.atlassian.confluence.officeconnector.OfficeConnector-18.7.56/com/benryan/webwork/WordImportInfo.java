/*
 * Decompiled with CFR 0.152.
 */
package com.benryan.webwork;

public class WordImportInfo {
    public static final int CONFLICT_VERSION = 0;
    public static final int CONFLICT_RENAME = 1;
    public static final int CONFLICT_DELETE = 2;
    private int _lvl;
    private boolean _importSpace;
    private String _title;
    private int _conflict;
    private boolean _overwriteAll;
    private int _treeDepth;

    public boolean isImportSpace() {
        return this._importSpace;
    }

    public void setImportSpace(boolean space) {
        this._importSpace = space;
    }

    public int getLvl() {
        return this._lvl;
    }

    public void setLvl(int lvl) {
        this._lvl = lvl;
    }

    public String getTitle() {
        return this._title;
    }

    public void setTitle(String title) {
        this._title = title.trim();
    }

    public void setConflict(int conflict) {
        this._conflict = conflict;
    }

    public int getConflict() {
        return this._conflict;
    }

    public void setOverwriteAll(boolean overwrite) {
        this._overwriteAll = overwrite;
    }

    public boolean getOverwriteAll() {
        return this._overwriteAll;
    }

    public void setTreeDepth(int depth) {
        this._treeDepth = depth;
    }

    public int getTreeDepth() {
        return this._treeDepth;
    }
}

