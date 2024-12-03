/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import java.util.ArrayList;
import org.apache.poi.hwpf.sprm.SprmUtils;
import org.apache.poi.hwpf.usermodel.CharacterProperties;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class CharacterSprmCompressor {
    public static byte[] compressCharacterProperty(CharacterProperties newCHP, CharacterProperties oldCHP) {
        int value;
        ArrayList<byte[]> sprmList = new ArrayList<byte[]>();
        int size = 0;
        if (newCHP.isFRMarkDel() != oldCHP.isFRMarkDel()) {
            value = 0;
            if (newCHP.isFRMarkDel()) {
                value = 1;
            }
            size += SprmUtils.addSprm((short)2048, value, null, sprmList);
        }
        if (newCHP.isFRMark() != oldCHP.isFRMark()) {
            value = 0;
            if (newCHP.isFRMark()) {
                value = 1;
            }
            size += SprmUtils.addSprm((short)2049, value, null, sprmList);
        }
        if (newCHP.isFFldVanish() != oldCHP.isFFldVanish()) {
            value = 0;
            if (newCHP.isFFldVanish()) {
                value = 1;
            }
            size += SprmUtils.addSprm((short)2050, value, null, sprmList);
        }
        if (newCHP.isFSpec() != oldCHP.isFSpec() || newCHP.getFcPic() != oldCHP.getFcPic()) {
            size += SprmUtils.addSprm((short)27139, newCHP.getFcPic(), null, sprmList);
        }
        if (newCHP.getIbstRMark() != oldCHP.getIbstRMark()) {
            size += SprmUtils.addSprm((short)18436, newCHP.getIbstRMark(), null, sprmList);
        }
        if (!newCHP.getDttmRMark().equals(oldCHP.getDttmRMark())) {
            byte[] buf = new byte[4];
            newCHP.getDttmRMark().serialize(buf, 0);
            size += SprmUtils.addSprm((short)26629, LittleEndian.getInt(buf), null, sprmList);
        }
        if (newCHP.isFData() != oldCHP.isFData()) {
            int value2 = 0;
            if (newCHP.isFData()) {
                value2 = 1;
            }
            size += SprmUtils.addSprm((short)2054, value2, null, sprmList);
        }
        if (newCHP.isFSpec() && newCHP.getFtcSym() != 0) {
            byte[] varParam = new byte[4];
            LittleEndian.putShort(varParam, 0, (short)newCHP.getFtcSym());
            LittleEndian.putShort(varParam, 2, (short)newCHP.getXchSym());
            size += SprmUtils.addSprm((short)27145, 0, varParam, sprmList);
        }
        if (newCHP.isFOle2() != oldCHP.isFOle2()) {
            int value3 = 0;
            if (newCHP.isFOle2()) {
                value3 = 1;
            }
            size += SprmUtils.addSprm((short)2058, value3, null, sprmList);
        }
        if (newCHP.getIcoHighlight() != oldCHP.getIcoHighlight()) {
            size += SprmUtils.addSprm((short)10764, newCHP.getIcoHighlight(), null, sprmList);
        }
        if (newCHP.getFcObj() != oldCHP.getFcObj()) {
            size += SprmUtils.addSprm((short)26638, newCHP.getFcObj(), null, sprmList);
        }
        if (newCHP.getIstd() != oldCHP.getIstd()) {
            size += SprmUtils.addSprm((short)18992, newCHP.getIstd(), null, sprmList);
        }
        if (newCHP.isFBold() != oldCHP.isFBold()) {
            int value4 = 0;
            if (newCHP.isFBold()) {
                value4 = 1;
            }
            size += SprmUtils.addSprm((short)2101, value4, null, sprmList);
        }
        if (newCHP.isFItalic() != oldCHP.isFItalic()) {
            int value5 = 0;
            if (newCHP.isFItalic()) {
                value5 = 1;
            }
            size += SprmUtils.addSprm((short)2102, value5, null, sprmList);
        }
        if (newCHP.isFStrike() != oldCHP.isFStrike()) {
            int value6 = 0;
            if (newCHP.isFStrike()) {
                value6 = 1;
            }
            size += SprmUtils.addSprm((short)2103, value6, null, sprmList);
        }
        if (newCHP.isFOutline() != oldCHP.isFOutline()) {
            int value7 = 0;
            if (newCHP.isFOutline()) {
                value7 = 1;
            }
            size += SprmUtils.addSprm((short)2104, value7, null, sprmList);
        }
        if (newCHP.isFShadow() != oldCHP.isFShadow()) {
            int value8 = 0;
            if (newCHP.isFShadow()) {
                value8 = 1;
            }
            size += SprmUtils.addSprm((short)2105, value8, null, sprmList);
        }
        if (newCHP.isFSmallCaps() != oldCHP.isFSmallCaps()) {
            int value9 = 0;
            if (newCHP.isFSmallCaps()) {
                value9 = 1;
            }
            size += SprmUtils.addSprm((short)2106, value9, null, sprmList);
        }
        if (newCHP.isFCaps() != oldCHP.isFCaps()) {
            int value10 = 0;
            if (newCHP.isFCaps()) {
                value10 = 1;
            }
            size += SprmUtils.addSprm((short)2107, value10, null, sprmList);
        }
        if (newCHP.isFVanish() != oldCHP.isFVanish()) {
            int value11 = 0;
            if (newCHP.isFVanish()) {
                value11 = 1;
            }
            size += SprmUtils.addSprm((short)2108, value11, null, sprmList);
        }
        if (newCHP.getKul() != oldCHP.getKul()) {
            size += SprmUtils.addSprm((short)10814, newCHP.getKul(), null, sprmList);
        }
        if (newCHP.getDxaSpace() != oldCHP.getDxaSpace()) {
            size += SprmUtils.addSprm((short)-30656, newCHP.getDxaSpace(), null, sprmList);
        }
        if (newCHP.getIco() != oldCHP.getIco()) {
            size += SprmUtils.addSprm((short)10818, newCHP.getIco(), null, sprmList);
        }
        if (newCHP.getHps() != oldCHP.getHps()) {
            size += SprmUtils.addSprm((short)19011, newCHP.getHps(), null, sprmList);
        }
        if (newCHP.getHpsPos() != oldCHP.getHpsPos()) {
            size += SprmUtils.addSprm((short)18501, newCHP.getHpsPos(), null, sprmList);
        }
        if (newCHP.getHpsKern() != oldCHP.getHpsKern()) {
            size += SprmUtils.addSprm((short)18507, newCHP.getHpsKern(), null, sprmList);
        }
        if (newCHP.getHresi().equals(oldCHP.getHresi())) {
            size += SprmUtils.addSprm((short)18510, newCHP.getHresi().getValue(), null, sprmList);
        }
        if (newCHP.getFtcAscii() != oldCHP.getFtcAscii()) {
            size += SprmUtils.addSprm((short)19023, newCHP.getFtcAscii(), null, sprmList);
        }
        if (newCHP.getFtcFE() != oldCHP.getFtcFE()) {
            size += SprmUtils.addSprm((short)19024, newCHP.getFtcFE(), null, sprmList);
        }
        if (newCHP.getFtcOther() != oldCHP.getFtcOther()) {
            size += SprmUtils.addSprm((short)19025, newCHP.getFtcOther(), null, sprmList);
        }
        if (newCHP.isFDStrike() != oldCHP.isFDStrike()) {
            int value12 = 0;
            if (newCHP.isFDStrike()) {
                value12 = 1;
            }
            size += SprmUtils.addSprm((short)10835, value12, null, sprmList);
        }
        if (newCHP.isFImprint() != oldCHP.isFImprint()) {
            int value13 = 0;
            if (newCHP.isFImprint()) {
                value13 = 1;
            }
            size += SprmUtils.addSprm((short)2132, value13, null, sprmList);
        }
        if (newCHP.isFSpec() != oldCHP.isFSpec()) {
            int value14 = 0;
            if (newCHP.isFSpec()) {
                value14 = 1;
            }
            size += SprmUtils.addSprm((short)2133, value14, null, sprmList);
        }
        if (newCHP.isFObj() != oldCHP.isFObj()) {
            int value15 = 0;
            if (newCHP.isFObj()) {
                value15 = 1;
            }
            size += SprmUtils.addSprm((short)2134, value15, null, sprmList);
        }
        if (newCHP.isFEmboss() != oldCHP.isFEmboss()) {
            int value16 = 0;
            if (newCHP.isFEmboss()) {
                value16 = 1;
            }
            size += SprmUtils.addSprm((short)2136, value16, null, sprmList);
        }
        if (newCHP.getSfxtText() != oldCHP.getSfxtText()) {
            size += SprmUtils.addSprm((short)10329, newCHP.getSfxtText(), null, sprmList);
        }
        if (!newCHP.getCv().equals(oldCHP.getCv()) && !newCHP.getCv().isEmpty()) {
            size += SprmUtils.addSprm((short)26736, newCHP.getCv().getValue(), null, sprmList);
        }
        return SprmUtils.getGrpprl(sprmList, size);
    }
}

