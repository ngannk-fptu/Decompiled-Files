/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Range;

public interface Field {
    public Range firstSubrange(Range var1);

    public int getFieldEndOffset();

    public int getFieldStartOffset();

    public CharacterRun getMarkEndCharacterRun(Range var1);

    public int getMarkEndOffset();

    public CharacterRun getMarkSeparatorCharacterRun(Range var1);

    public int getMarkSeparatorOffset();

    public CharacterRun getMarkStartCharacterRun(Range var1);

    public int getMarkStartOffset();

    public int getType();

    public boolean hasSeparator();

    public boolean isHasSep();

    public boolean isLocked();

    public boolean isNested();

    public boolean isPrivateResult();

    public boolean isResultDirty();

    public boolean isResultEdited();

    public boolean isZombieEmbed();

    public Range secondSubrange(Range var1);
}

