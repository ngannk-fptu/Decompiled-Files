/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.poi.hwpf.sprm.SprmUtils;
import org.apache.poi.hwpf.usermodel.SectionProperties;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class SectionSprmCompressor {
    private static final SectionProperties DEFAULT_SEP = new SectionProperties();

    public static byte[] compressSectionProperty(SectionProperties newSEP) {
        int size = 0;
        ArrayList<byte[]> sprmList = new ArrayList<byte[]>();
        if (newSEP.getCnsPgn() != DEFAULT_SEP.getCnsPgn()) {
            size += SprmUtils.addSprm((short)12288, newSEP.getCnsPgn(), null, sprmList);
        }
        if (newSEP.getIHeadingPgn() != DEFAULT_SEP.getIHeadingPgn()) {
            size += SprmUtils.addSprm((short)12289, newSEP.getIHeadingPgn(), null, sprmList);
        }
        if (!Arrays.equals(newSEP.getOlstAnm(), DEFAULT_SEP.getOlstAnm())) {
            size += SprmUtils.addSprm((short)-11774, 0, newSEP.getOlstAnm(), sprmList);
        }
        if (newSEP.getFEvenlySpaced() != DEFAULT_SEP.getFEvenlySpaced()) {
            size += SprmUtils.addSprm((short)12293, newSEP.getFEvenlySpaced() ? 1 : 0, null, sprmList);
        }
        if (newSEP.getFUnlocked() != DEFAULT_SEP.getFUnlocked()) {
            size += SprmUtils.addSprm((short)12294, newSEP.getFUnlocked() ? 1 : 0, null, sprmList);
        }
        if (newSEP.getDmBinFirst() != DEFAULT_SEP.getDmBinFirst()) {
            size += SprmUtils.addSprm((short)20487, newSEP.getDmBinFirst(), null, sprmList);
        }
        if (newSEP.getDmBinOther() != DEFAULT_SEP.getDmBinOther()) {
            size += SprmUtils.addSprm((short)20488, newSEP.getDmBinOther(), null, sprmList);
        }
        if (newSEP.getBkc() != DEFAULT_SEP.getBkc()) {
            size += SprmUtils.addSprm((short)12297, newSEP.getBkc(), null, sprmList);
        }
        if (newSEP.getFTitlePage() != DEFAULT_SEP.getFTitlePage()) {
            size += SprmUtils.addSprm((short)12298, newSEP.getFTitlePage() ? 1 : 0, null, sprmList);
        }
        if (newSEP.getCcolM1() != DEFAULT_SEP.getCcolM1()) {
            size += SprmUtils.addSprm((short)20491, newSEP.getCcolM1(), null, sprmList);
        }
        if (newSEP.getDxaColumns() != DEFAULT_SEP.getDxaColumns()) {
            size += SprmUtils.addSprm((short)-28660, newSEP.getDxaColumns(), null, sprmList);
        }
        if (newSEP.getFAutoPgn() != DEFAULT_SEP.getFAutoPgn()) {
            size += SprmUtils.addSprm((short)12301, newSEP.getFAutoPgn() ? 1 : 0, null, sprmList);
        }
        if (newSEP.getNfcPgn() != DEFAULT_SEP.getNfcPgn()) {
            size += SprmUtils.addSprm((short)12302, newSEP.getNfcPgn(), null, sprmList);
        }
        if (newSEP.getDyaPgn() != DEFAULT_SEP.getDyaPgn()) {
            size += SprmUtils.addSprm((short)-20465, newSEP.getDyaPgn(), null, sprmList);
        }
        if (newSEP.getDxaPgn() != DEFAULT_SEP.getDxaPgn()) {
            size += SprmUtils.addSprm((short)-20464, newSEP.getDxaPgn(), null, sprmList);
        }
        if (newSEP.getFPgnRestart() != DEFAULT_SEP.getFPgnRestart()) {
            size += SprmUtils.addSprm((short)12305, newSEP.getFPgnRestart() ? 1 : 0, null, sprmList);
        }
        if (newSEP.getFEndNote() != DEFAULT_SEP.getFEndNote()) {
            size += SprmUtils.addSprm((short)12306, newSEP.getFEndNote() ? 1 : 0, null, sprmList);
        }
        if (newSEP.getLnc() != DEFAULT_SEP.getLnc()) {
            size += SprmUtils.addSprm((short)12307, newSEP.getLnc(), null, sprmList);
        }
        if (newSEP.getGrpfIhdt() != DEFAULT_SEP.getGrpfIhdt()) {
            size += SprmUtils.addSprm((short)12308, newSEP.getGrpfIhdt(), null, sprmList);
        }
        if (newSEP.getNLnnMod() != DEFAULT_SEP.getNLnnMod()) {
            size += SprmUtils.addSprm((short)20501, newSEP.getNLnnMod(), null, sprmList);
        }
        if (newSEP.getDxaLnn() != DEFAULT_SEP.getDxaLnn()) {
            size += SprmUtils.addSprm((short)-28650, newSEP.getDxaLnn(), null, sprmList);
        }
        if (newSEP.getDyaHdrTop() != DEFAULT_SEP.getDyaHdrTop()) {
            size += SprmUtils.addSprm((short)-20457, newSEP.getDyaHdrTop(), null, sprmList);
        }
        if (newSEP.getDyaHdrBottom() != DEFAULT_SEP.getDyaHdrBottom()) {
            size += SprmUtils.addSprm((short)-20456, newSEP.getDyaHdrBottom(), null, sprmList);
        }
        if (newSEP.getFLBetween() != DEFAULT_SEP.getFLBetween()) {
            size += SprmUtils.addSprm((short)12313, newSEP.getFLBetween() ? 1 : 0, null, sprmList);
        }
        if (newSEP.getVjc() != DEFAULT_SEP.getVjc()) {
            size += SprmUtils.addSprm((short)12314, newSEP.getVjc(), null, sprmList);
        }
        if (newSEP.getLnnMin() != DEFAULT_SEP.getLnnMin()) {
            size += SprmUtils.addSprm((short)20507, newSEP.getLnnMin(), null, sprmList);
        }
        if (newSEP.getPgnStart() != DEFAULT_SEP.getPgnStart()) {
            size += SprmUtils.addSprm((short)20508, newSEP.getPgnStart(), null, sprmList);
        }
        if (newSEP.getDmOrientPage() != DEFAULT_SEP.getDmOrientPage()) {
            size += SprmUtils.addSprm((short)12317, newSEP.getDmOrientPage() ? 1 : 0, null, sprmList);
        }
        if (newSEP.getXaPage() != DEFAULT_SEP.getXaPage()) {
            size += SprmUtils.addSprm((short)-20449, newSEP.getXaPage(), null, sprmList);
        }
        if (newSEP.getYaPage() != DEFAULT_SEP.getYaPage()) {
            size += SprmUtils.addSprm((short)-20448, newSEP.getYaPage(), null, sprmList);
        }
        if (newSEP.getDxaLeft() != DEFAULT_SEP.getDxaLeft()) {
            size += SprmUtils.addSprm((short)-20447, newSEP.getDxaLeft(), null, sprmList);
        }
        if (newSEP.getDxaRight() != DEFAULT_SEP.getDxaRight()) {
            size += SprmUtils.addSprm((short)-20446, newSEP.getDxaRight(), null, sprmList);
        }
        if (newSEP.getDyaTop() != DEFAULT_SEP.getDyaTop()) {
            size += SprmUtils.addSprm((short)-28637, newSEP.getDyaTop(), null, sprmList);
        }
        if (newSEP.getDyaBottom() != DEFAULT_SEP.getDyaBottom()) {
            size += SprmUtils.addSprm((short)-28636, newSEP.getDyaBottom(), null, sprmList);
        }
        if (newSEP.getDzaGutter() != DEFAULT_SEP.getDzaGutter()) {
            size += SprmUtils.addSprm((short)-20443, newSEP.getDzaGutter(), null, sprmList);
        }
        if (newSEP.getDmPaperReq() != DEFAULT_SEP.getDmPaperReq()) {
            size += SprmUtils.addSprm((short)20518, newSEP.getDmPaperReq(), null, sprmList);
        }
        if (newSEP.getFPropMark() != DEFAULT_SEP.getFPropMark() || newSEP.getIbstPropRMark() != DEFAULT_SEP.getIbstPropRMark() || !newSEP.getDttmPropRMark().equals(DEFAULT_SEP.getDttmPropRMark())) {
            byte[] buf = new byte[7];
            buf[0] = (byte)(newSEP.getFPropMark() ? 1 : 0);
            int offset = 1;
            LittleEndian.putShort(buf, 0, (short)newSEP.getIbstPropRMark());
            newSEP.getDttmPropRMark().serialize(buf, offset += 2);
            size += SprmUtils.addSprm((short)-11737, -1, buf, sprmList);
        }
        if (!newSEP.getBrcTop().equals(DEFAULT_SEP.getBrcTop())) {
            size += SprmUtils.addSprm((short)28715, newSEP.getBrcTop().toInt(), null, sprmList);
        }
        if (!newSEP.getBrcLeft().equals(DEFAULT_SEP.getBrcLeft())) {
            size += SprmUtils.addSprm((short)28716, newSEP.getBrcLeft().toInt(), null, sprmList);
        }
        if (!newSEP.getBrcBottom().equals(DEFAULT_SEP.getBrcBottom())) {
            size += SprmUtils.addSprm((short)28717, newSEP.getBrcBottom().toInt(), null, sprmList);
        }
        if (!newSEP.getBrcRight().equals(DEFAULT_SEP.getBrcRight())) {
            size += SprmUtils.addSprm((short)28718, newSEP.getBrcRight().toInt(), null, sprmList);
        }
        if (newSEP.getPgbProp() != DEFAULT_SEP.getPgbProp()) {
            size += SprmUtils.addSprm((short)21039, newSEP.getPgbProp(), null, sprmList);
        }
        if (newSEP.getDxtCharSpace() != DEFAULT_SEP.getDxtCharSpace()) {
            size += SprmUtils.addSprm((short)28720, newSEP.getDxtCharSpace(), null, sprmList);
        }
        if (newSEP.getDyaLinePitch() != DEFAULT_SEP.getDyaLinePitch()) {
            size += SprmUtils.addSprm((short)-28623, newSEP.getDyaLinePitch(), null, sprmList);
        }
        if (newSEP.getClm() != DEFAULT_SEP.getClm()) {
            size += SprmUtils.addSprm((short)20530, newSEP.getClm(), null, sprmList);
        }
        if (newSEP.getWTextFlow() != DEFAULT_SEP.getWTextFlow()) {
            size += SprmUtils.addSprm((short)20531, newSEP.getWTextFlow(), null, sprmList);
        }
        return SprmUtils.getGrpprl(sprmList, size);
    }
}

