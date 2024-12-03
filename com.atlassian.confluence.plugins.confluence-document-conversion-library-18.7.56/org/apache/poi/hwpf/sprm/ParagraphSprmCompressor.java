/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.poi.hwpf.sprm.SprmUtils;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class ParagraphSprmCompressor {
    public static byte[] compressParagraphProperty(ParagraphProperties newPAP, ParagraphProperties oldPAP) {
        byte[] buf;
        ArrayList<byte[]> sprmList = new ArrayList<byte[]>();
        int size = 0;
        if (newPAP.getIstd() != oldPAP.getIstd()) {
            size += SprmUtils.addSprm((short)17920, newPAP.getIstd(), null, sprmList);
        }
        if (newPAP.getJc() != oldPAP.getJc()) {
            size += SprmUtils.addSprm((short)9219, newPAP.getJc(), null, sprmList);
        }
        if (newPAP.getFSideBySide() != oldPAP.getFSideBySide()) {
            size += SprmUtils.addSprm((short)9220, newPAP.getFSideBySide(), sprmList);
        }
        if (newPAP.getFKeep() != oldPAP.getFKeep()) {
            size += SprmUtils.addSprm((short)9221, newPAP.getFKeep(), sprmList);
        }
        if (newPAP.getFKeepFollow() != oldPAP.getFKeepFollow()) {
            size += SprmUtils.addSprm((short)9222, newPAP.getFKeepFollow(), sprmList);
        }
        if (newPAP.getFPageBreakBefore() != oldPAP.getFPageBreakBefore()) {
            size += SprmUtils.addSprm((short)9223, newPAP.getFPageBreakBefore(), sprmList);
        }
        if (newPAP.getBrcl() != oldPAP.getBrcl()) {
            size += SprmUtils.addSprm((short)9224, newPAP.getBrcl(), null, sprmList);
        }
        if (newPAP.getBrcp() != oldPAP.getBrcp()) {
            size += SprmUtils.addSprm((short)9225, newPAP.getBrcp(), null, sprmList);
        }
        if (newPAP.getIlvl() != oldPAP.getIlvl()) {
            size += SprmUtils.addSprm((short)9738, newPAP.getIlvl(), null, sprmList);
        }
        if (newPAP.getIlfo() != oldPAP.getIlfo()) {
            size += SprmUtils.addSprm((short)17931, newPAP.getIlfo(), null, sprmList);
        }
        if (newPAP.getFNoLnn() != oldPAP.getFNoLnn()) {
            size += SprmUtils.addSprm((short)9228, newPAP.getFNoLnn(), sprmList);
        }
        if (newPAP.getItbdMac() != oldPAP.getItbdMac() || !Arrays.equals(newPAP.getRgdxaTab(), oldPAP.getRgdxaTab()) || !Arrays.equals(newPAP.getRgtbd(), oldPAP.getRgtbd())) {
            // empty if block
        }
        if (newPAP.getDxaLeft() != oldPAP.getDxaLeft()) {
            size += SprmUtils.addSprm((short)-31729, newPAP.getDxaLeft(), null, sprmList);
        }
        if (newPAP.getDxaLeft1() != oldPAP.getDxaLeft1()) {
            size += SprmUtils.addSprm((short)-31727, newPAP.getDxaLeft1(), null, sprmList);
        }
        if (newPAP.getDxaRight() != oldPAP.getDxaRight()) {
            size += SprmUtils.addSprm((short)-31730, newPAP.getDxaRight(), null, sprmList);
        }
        if (newPAP.getDxcLeft() != oldPAP.getDxcLeft()) {
            size += SprmUtils.addSprm((short)17494, newPAP.getDxcLeft(), null, sprmList);
        }
        if (newPAP.getDxcLeft1() != oldPAP.getDxcLeft1()) {
            size += SprmUtils.addSprm((short)17495, newPAP.getDxcLeft1(), null, sprmList);
        }
        if (newPAP.getDxcRight() != oldPAP.getDxcRight()) {
            size += SprmUtils.addSprm((short)17493, newPAP.getDxcRight(), null, sprmList);
        }
        if (!newPAP.getLspd().equals(oldPAP.getLspd())) {
            buf = new byte[4];
            newPAP.getLspd().serialize(buf, 0);
            size += SprmUtils.addSprm((short)25618, LittleEndian.getInt(buf), null, sprmList);
        }
        if (newPAP.getDyaBefore() != oldPAP.getDyaBefore()) {
            size += SprmUtils.addSprm((short)-23533, newPAP.getDyaBefore(), null, sprmList);
        }
        if (newPAP.getDyaAfter() != oldPAP.getDyaAfter()) {
            size += SprmUtils.addSprm((short)-23532, newPAP.getDyaAfter(), null, sprmList);
        }
        if (newPAP.getFDyaBeforeAuto() != oldPAP.getFDyaBeforeAuto()) {
            size += SprmUtils.addSprm((short)9307, newPAP.getFDyaBeforeAuto(), sprmList);
        }
        if (newPAP.getFDyaAfterAuto() != oldPAP.getFDyaAfterAuto()) {
            size += SprmUtils.addSprm((short)9308, newPAP.getFDyaAfterAuto(), sprmList);
        }
        if (newPAP.getFInTable() != oldPAP.getFInTable()) {
            size += SprmUtils.addSprm((short)9238, newPAP.getFInTable(), sprmList);
        }
        if (newPAP.getFTtp() != oldPAP.getFTtp()) {
            size += SprmUtils.addSprm((short)9239, newPAP.getFTtp(), sprmList);
        }
        if (newPAP.getDxaAbs() != oldPAP.getDxaAbs()) {
            size += SprmUtils.addSprm((short)-31720, newPAP.getDxaAbs(), null, sprmList);
        }
        if (newPAP.getDyaAbs() != oldPAP.getDyaAbs()) {
            size += SprmUtils.addSprm((short)-31719, newPAP.getDyaAbs(), null, sprmList);
        }
        if (newPAP.getDxaWidth() != oldPAP.getDxaWidth()) {
            size += SprmUtils.addSprm((short)-31718, newPAP.getDxaWidth(), null, sprmList);
        }
        if (newPAP.getWr() != oldPAP.getWr()) {
            size += SprmUtils.addSprm((short)9251, newPAP.getWr(), null, sprmList);
        }
        if (newPAP.getBrcBar().equals(oldPAP.getBrcBar())) {
            int brc = newPAP.getBrcBar().toInt();
            size += SprmUtils.addSprm((short)25640, brc, null, sprmList);
        }
        if (!newPAP.getBrcBottom().equals(oldPAP.getBrcBottom())) {
            int brc = newPAP.getBrcBottom().toInt();
            size += SprmUtils.addSprm((short)25638, brc, null, sprmList);
        }
        if (!newPAP.getBrcLeft().equals(oldPAP.getBrcLeft())) {
            int brc = newPAP.getBrcLeft().toInt();
            size += SprmUtils.addSprm((short)25637, brc, null, sprmList);
        }
        if (!newPAP.getBrcRight().equals(oldPAP.getBrcRight())) {
            int brc = newPAP.getBrcRight().toInt();
            size += SprmUtils.addSprm((short)25639, brc, null, sprmList);
        }
        if (!newPAP.getBrcTop().equals(oldPAP.getBrcTop())) {
            int brc = newPAP.getBrcTop().toInt();
            size += SprmUtils.addSprm((short)25636, brc, null, sprmList);
        }
        if (newPAP.getFNoAutoHyph() != oldPAP.getFNoAutoHyph()) {
            size += SprmUtils.addSprm((short)9258, newPAP.getFNoAutoHyph(), sprmList);
        }
        if (newPAP.getDyaHeight() != oldPAP.getDyaHeight() || newPAP.getFMinHeight() != oldPAP.getFMinHeight()) {
            short val = (short)newPAP.getDyaHeight();
            if (newPAP.getFMinHeight()) {
                val = (short)(val | 0x8000);
            }
            size += SprmUtils.addSprm((short)17451, val, null, sprmList);
        }
        if (newPAP.getDcs() != null && !newPAP.getDcs().equals(oldPAP.getDcs())) {
            size += SprmUtils.addSprm((short)17452, newPAP.getDcs().toShort(), null, sprmList);
        }
        if (newPAP.getDyaFromText() != oldPAP.getDyaFromText()) {
            size += SprmUtils.addSprm((short)-31698, newPAP.getDyaFromText(), null, sprmList);
        }
        if (newPAP.getDxaFromText() != oldPAP.getDxaFromText()) {
            size += SprmUtils.addSprm((short)-31697, newPAP.getDxaFromText(), null, sprmList);
        }
        if (newPAP.getFLocked() != oldPAP.getFLocked()) {
            size += SprmUtils.addSprm((short)9264, newPAP.getFLocked(), sprmList);
        }
        if (newPAP.getFWidowControl() != oldPAP.getFWidowControl()) {
            size += SprmUtils.addSprm((short)9265, newPAP.getFWidowControl(), sprmList);
        }
        if (newPAP.getFKinsoku() != oldPAP.getFKinsoku()) {
            size += SprmUtils.addSprm((short)9267, newPAP.getDyaBefore(), null, sprmList);
        }
        if (newPAP.getFWordWrap() != oldPAP.getFWordWrap()) {
            size += SprmUtils.addSprm((short)9268, newPAP.getFWordWrap(), sprmList);
        }
        if (newPAP.getFOverflowPunct() != oldPAP.getFOverflowPunct()) {
            size += SprmUtils.addSprm((short)9269, newPAP.getFOverflowPunct(), sprmList);
        }
        if (newPAP.getFTopLinePunct() != oldPAP.getFTopLinePunct()) {
            size += SprmUtils.addSprm((short)9270, newPAP.getFTopLinePunct(), sprmList);
        }
        if (newPAP.getFAutoSpaceDE() != oldPAP.getFAutoSpaceDE()) {
            size += SprmUtils.addSprm((short)9271, newPAP.getFAutoSpaceDE(), sprmList);
        }
        if (newPAP.getFAutoSpaceDN() != oldPAP.getFAutoSpaceDN()) {
            size += SprmUtils.addSprm((short)9272, newPAP.getFAutoSpaceDN(), sprmList);
        }
        if (newPAP.getWAlignFont() != oldPAP.getWAlignFont()) {
            size += SprmUtils.addSprm((short)17465, newPAP.getWAlignFont(), null, sprmList);
        }
        if (newPAP.isFBackward() != oldPAP.isFBackward() || newPAP.isFVertical() != oldPAP.isFVertical() || newPAP.isFRotateFont() != oldPAP.isFRotateFont()) {
            int val = 0;
            if (newPAP.isFBackward()) {
                val |= 2;
            }
            if (newPAP.isFVertical()) {
                val |= 1;
            }
            if (newPAP.isFRotateFont()) {
                val |= 4;
            }
            size += SprmUtils.addSprm((short)17466, val, null, sprmList);
        }
        if (!Arrays.equals(newPAP.getAnld(), oldPAP.getAnld())) {
            size += SprmUtils.addSprm((short)-14786, 0, newPAP.getAnld(), sprmList);
        }
        if (newPAP.getFPropRMark() != oldPAP.getFPropRMark() || newPAP.getIbstPropRMark() != oldPAP.getIbstPropRMark() || !newPAP.getDttmPropRMark().equals(oldPAP.getDttmPropRMark())) {
            buf = new byte[7];
            buf[0] = (byte)(newPAP.getFPropRMark() ? 1 : 0);
            LittleEndian.putShort(buf, 1, (short)newPAP.getIbstPropRMark());
            newPAP.getDttmPropRMark().serialize(buf, 3);
            size += SprmUtils.addSprm((short)-14785, 0, buf, sprmList);
        }
        if (newPAP.getLvl() != oldPAP.getLvl()) {
            size += SprmUtils.addSprm((short)9792, newPAP.getLvl(), null, sprmList);
        }
        if (newPAP.getFBiDi() != oldPAP.getFBiDi()) {
            size += SprmUtils.addSprm((short)9281, newPAP.getFBiDi(), sprmList);
        }
        if (newPAP.getFNumRMIns() != oldPAP.getFNumRMIns()) {
            size += SprmUtils.addSprm((short)9283, newPAP.getFNumRMIns(), sprmList);
        }
        if (!Arrays.equals(newPAP.getNumrm(), oldPAP.getNumrm())) {
            size += SprmUtils.addSprm((short)-14779, 0, newPAP.getNumrm(), sprmList);
        }
        if (newPAP.getFInnerTableCell() != oldPAP.getFInnerTableCell()) {
            size += SprmUtils.addSprm((short)9291, newPAP.getFInnerTableCell(), sprmList);
        }
        if (newPAP.getFTtpEmbedded() != oldPAP.getFTtpEmbedded()) {
            size += SprmUtils.addSprm((short)9292, newPAP.getFTtpEmbedded(), sprmList);
        }
        if (newPAP.getShd() != null && !newPAP.getShd().equals(oldPAP.getShd())) {
            size += SprmUtils.addSprm((short)-14771, 0, newPAP.getShd().serialize(), sprmList);
        }
        if (newPAP.getItap() != oldPAP.getItap()) {
            size += SprmUtils.addSprm((short)26185, newPAP.getItap(), null, sprmList);
        }
        if (newPAP.getRsid() != oldPAP.getRsid()) {
            byte[] value = new byte[4];
            LittleEndian.putUInt(value, 0, newPAP.getRsid());
            size += SprmUtils.addSprm((short)25703, 0, value, sprmList);
        }
        return SprmUtils.getGrpprl(sprmList, size);
    }
}

