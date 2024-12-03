/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.util.Arrays;
import java.util.Deque;

public final class HwmfTernaryRasterOp
extends Enum<HwmfTernaryRasterOp> {
    public static final /* enum */ HwmfTernaryRasterOp BLACKNESS = new HwmfTernaryRasterOp(66);
    public static final /* enum */ HwmfTernaryRasterOp DPSOON = new HwmfTernaryRasterOp(66185);
    public static final /* enum */ HwmfTernaryRasterOp DPSONA = new HwmfTernaryRasterOp(134281);
    public static final /* enum */ HwmfTernaryRasterOp PSON = new HwmfTernaryRasterOp(196778);
    public static final /* enum */ HwmfTernaryRasterOp SDPONA = new HwmfTernaryRasterOp(265352);
    public static final /* enum */ HwmfTernaryRasterOp DPON = new HwmfTernaryRasterOp(327849);
    public static final /* enum */ HwmfTernaryRasterOp PDSXNON = new HwmfTernaryRasterOp(395365);
    public static final /* enum */ HwmfTernaryRasterOp PDSAON = new HwmfTernaryRasterOp(459461);
    public static final /* enum */ HwmfTernaryRasterOp SDPNAA = new HwmfTernaryRasterOp(528136);
    public static final /* enum */ HwmfTernaryRasterOp PDSXON = new HwmfTernaryRasterOp(590405);
    public static final /* enum */ HwmfTernaryRasterOp DPNA = new HwmfTernaryRasterOp(656169);
    public static final /* enum */ HwmfTernaryRasterOp PSDNAON = new HwmfTernaryRasterOp(723754);
    public static final /* enum */ HwmfTernaryRasterOp SPNA = new HwmfTernaryRasterOp(787236);
    public static final /* enum */ HwmfTernaryRasterOp PDSNAON = new HwmfTernaryRasterOp(854821);
    public static final /* enum */ HwmfTernaryRasterOp PDSONON = new HwmfTernaryRasterOp(919717);
    public static final /* enum */ HwmfTernaryRasterOp PN = new HwmfTernaryRasterOp(983041);
    public static final /* enum */ HwmfTernaryRasterOp PDSONA = new HwmfTernaryRasterOp(1051781);
    public static final /* enum */ HwmfTernaryRasterOp NOTSRCERASE = new HwmfTernaryRasterOp(1114278);
    public static final /* enum */ HwmfTernaryRasterOp SDPXNON = new HwmfTernaryRasterOp(1181800);
    public static final /* enum */ HwmfTernaryRasterOp SDPAON = new HwmfTernaryRasterOp(1245896);
    public static final /* enum */ HwmfTernaryRasterOp DPSXNON = new HwmfTernaryRasterOp(1312873);
    public static final /* enum */ HwmfTernaryRasterOp DPSAON = new HwmfTernaryRasterOp(1376969);
    public static final /* enum */ HwmfTernaryRasterOp PSDPSANAXX = new HwmfTernaryRasterOp(1465546);
    public static final /* enum */ HwmfTernaryRasterOp SSPXDSXAXN = new HwmfTernaryRasterOp(1514836);
    public static final /* enum */ HwmfTernaryRasterOp SPXPDXA = new HwmfTernaryRasterOp(1576281);
    public static final /* enum */ HwmfTernaryRasterOp SDPSANAXN = new HwmfTernaryRasterOp(1645768);
    public static final /* enum */ HwmfTernaryRasterOp PDSPAOX = new HwmfTernaryRasterOp(1705669);
    public static final /* enum */ HwmfTernaryRasterOp SDPSXAXN = new HwmfTernaryRasterOp(1771368);
    public static final /* enum */ HwmfTernaryRasterOp PSDPAOX = new HwmfTernaryRasterOp(1836746);
    public static final /* enum */ HwmfTernaryRasterOp DSPDXAXN = new HwmfTernaryRasterOp(1902438);
    public static final /* enum */ HwmfTernaryRasterOp PDSOX = new HwmfTernaryRasterOp(1966501);
    public static final /* enum */ HwmfTernaryRasterOp PDSOAN = new HwmfTernaryRasterOp(2032517);
    public static final /* enum */ HwmfTernaryRasterOp DPSNAA = new HwmfTernaryRasterOp(2101001);
    public static final /* enum */ HwmfTernaryRasterOp SDPXON = new HwmfTernaryRasterOp(2163272);
    public static final /* enum */ HwmfTernaryRasterOp DSNA = new HwmfTernaryRasterOp(2229030);
    public static final /* enum */ HwmfTernaryRasterOp SPDNAON = new HwmfTernaryRasterOp(2296612);
    public static final /* enum */ HwmfTernaryRasterOp SPXDSXA = new HwmfTernaryRasterOp(2362709);
    public static final /* enum */ HwmfTernaryRasterOp PDSPANAXN = new HwmfTernaryRasterOp(2432197);
    public static final /* enum */ HwmfTernaryRasterOp SDPSAOX = new HwmfTernaryRasterOp(2492104);
    public static final /* enum */ HwmfTernaryRasterOp SDPSXNOX = new HwmfTernaryRasterOp(2562152);
    public static final /* enum */ HwmfTernaryRasterOp DPSXA = new HwmfTernaryRasterOp(2622313);
    public static final /* enum */ HwmfTernaryRasterOp PSDPSAOXXN = new HwmfTernaryRasterOp(2692810);
    public static final /* enum */ HwmfTernaryRasterOp DPSANA = new HwmfTernaryRasterOp(2755785);
    public static final /* enum */ HwmfTernaryRasterOp SSPXPDXAXN = new HwmfTernaryRasterOp(2825560);
    public static final /* enum */ HwmfTernaryRasterOp SPDSOAX = new HwmfTernaryRasterOp(2885508);
    public static final /* enum */ HwmfTernaryRasterOp PSDNOX = new HwmfTernaryRasterOp(2950666);
    public static final /* enum */ HwmfTernaryRasterOp PSDPXOX = new HwmfTernaryRasterOp(3016266);
    public static final /* enum */ HwmfTernaryRasterOp PSDNOAN = new HwmfTernaryRasterOp(3083818);
    public static final /* enum */ HwmfTernaryRasterOp PSNA = new HwmfTernaryRasterOp(3146538);
    public static final /* enum */ HwmfTernaryRasterOp SDPNAON = new HwmfTernaryRasterOp(3214120);
    public static final /* enum */ HwmfTernaryRasterOp SDPSOOX = new HwmfTernaryRasterOp(3278472);
    public static final /* enum */ HwmfTernaryRasterOp NOTSRCCOPY = new HwmfTernaryRasterOp(0x330008);
    public static final /* enum */ HwmfTernaryRasterOp SPDSAOX = new HwmfTernaryRasterOp(3409604);
    public static final /* enum */ HwmfTernaryRasterOp SPDSXNOX = new HwmfTernaryRasterOp(3479652);
    public static final /* enum */ HwmfTernaryRasterOp SDPOX = new HwmfTernaryRasterOp(3539368);
    public static final /* enum */ HwmfTernaryRasterOp SDPOAN = new HwmfTernaryRasterOp(3605384);
    public static final /* enum */ HwmfTernaryRasterOp PSDPOAX = new HwmfTernaryRasterOp(3671946);
    public static final /* enum */ HwmfTernaryRasterOp SPDNOX = new HwmfTernaryRasterOp(3737092);
    public static final /* enum */ HwmfTernaryRasterOp SPDSXOX = new HwmfTernaryRasterOp(3802692);
    public static final /* enum */ HwmfTernaryRasterOp SPDNOAN = new HwmfTernaryRasterOp(3870244);
    public static final /* enum */ HwmfTernaryRasterOp PSX = new HwmfTernaryRasterOp(3932234);
    public static final /* enum */ HwmfTernaryRasterOp SPDSONOX = new HwmfTernaryRasterOp(4004004);
    public static final /* enum */ HwmfTernaryRasterOp SPDSNAOX = new HwmfTernaryRasterOp(4070180);
    public static final /* enum */ HwmfTernaryRasterOp PSAN = new HwmfTernaryRasterOp(4129002);
    public static final /* enum */ HwmfTernaryRasterOp PSDNAA = new HwmfTernaryRasterOp(4198154);
    public static final /* enum */ HwmfTernaryRasterOp DPSXON = new HwmfTernaryRasterOp(4260425);
    public static final /* enum */ HwmfTernaryRasterOp SDXPDXA = new HwmfTernaryRasterOp(4328797);
    public static final /* enum */ HwmfTernaryRasterOp SPDSANAXN = new HwmfTernaryRasterOp(4398276);
    public static final /* enum */ HwmfTernaryRasterOp SRCERASE = new HwmfTernaryRasterOp(4457256);
    public static final /* enum */ HwmfTernaryRasterOp DPSNAON = new HwmfTernaryRasterOp(4524841);
    public static final /* enum */ HwmfTernaryRasterOp DSPDAOX = new HwmfTernaryRasterOp(4589254);
    public static final /* enum */ HwmfTernaryRasterOp PSDPXAXN = new HwmfTernaryRasterOp(4654954);
    public static final /* enum */ HwmfTernaryRasterOp SDPXA = new HwmfTernaryRasterOp(4719464);
    public static final /* enum */ HwmfTernaryRasterOp PDSPDAOXXN = new HwmfTernaryRasterOp(4789957);
    public static final /* enum */ HwmfTernaryRasterOp DPSDOAX = new HwmfTernaryRasterOp(4851593);
    public static final /* enum */ HwmfTernaryRasterOp PDSNOX = new HwmfTernaryRasterOp(4916741);
    public static final /* enum */ HwmfTernaryRasterOp SDPANA = new HwmfTernaryRasterOp(4984008);
    public static final /* enum */ HwmfTernaryRasterOp SSPXDSXOXN = new HwmfTernaryRasterOp(5052756);
    public static final /* enum */ HwmfTernaryRasterOp PDSPXOX = new HwmfTernaryRasterOp(5113413);
    public static final /* enum */ HwmfTernaryRasterOp PDSNOAN = new HwmfTernaryRasterOp(5180965);
    public static final /* enum */ HwmfTernaryRasterOp PDNA = new HwmfTernaryRasterOp(5243685);
    public static final /* enum */ HwmfTernaryRasterOp DSPNAON = new HwmfTernaryRasterOp(5311270);
    public static final /* enum */ HwmfTernaryRasterOp DPSDAOX = new HwmfTernaryRasterOp(5375689);
    public static final /* enum */ HwmfTernaryRasterOp SPDSXAXN = new HwmfTernaryRasterOp(5441380);
    public static final /* enum */ HwmfTernaryRasterOp DPSONON = new HwmfTernaryRasterOp(5507241);
    public static final /* enum */ HwmfTernaryRasterOp DSTINVERT = new HwmfTernaryRasterOp(0x550009);
    public static final /* enum */ HwmfTernaryRasterOp DPSOX = new HwmfTernaryRasterOp(5636521);
    public static final /* enum */ HwmfTernaryRasterOp DPSOAN = new HwmfTernaryRasterOp(5702537);
    public static final /* enum */ HwmfTernaryRasterOp PDSPOAX = new HwmfTernaryRasterOp(5769093);
    public static final /* enum */ HwmfTernaryRasterOp DPSNOX = new HwmfTernaryRasterOp(5834249);
    public static final /* enum */ HwmfTernaryRasterOp PATINVERT = new HwmfTernaryRasterOp(5898313);
    public static final /* enum */ HwmfTernaryRasterOp DPSDONOX = new HwmfTernaryRasterOp(5970089);
    public static final /* enum */ HwmfTernaryRasterOp DPSDXOX = new HwmfTernaryRasterOp(6030921);
    public static final /* enum */ HwmfTernaryRasterOp DPSNOAN = new HwmfTernaryRasterOp(6098473);
    public static final /* enum */ HwmfTernaryRasterOp DPSDNAOX = new HwmfTernaryRasterOp(6167337);
    public static final /* enum */ HwmfTernaryRasterOp DPAN = new HwmfTernaryRasterOp(6226153);
    public static final /* enum */ HwmfTernaryRasterOp PDSXA = new HwmfTernaryRasterOp(6292325);
    public static final /* enum */ HwmfTernaryRasterOp DSPDSAOXXN = new HwmfTernaryRasterOp(0x6116C6);
    public static final /* enum */ HwmfTernaryRasterOp DSPDOAX = new HwmfTernaryRasterOp(6424454);
    public static final /* enum */ HwmfTernaryRasterOp SDPNOX = new HwmfTernaryRasterOp(6489608);
    public static final /* enum */ HwmfTernaryRasterOp SDPSOAX = new HwmfTernaryRasterOp(6555528);
    public static final /* enum */ HwmfTernaryRasterOp DSPNOX = new HwmfTernaryRasterOp(0x650606);
    public static final /* enum */ HwmfTernaryRasterOp SRCINVERT = new HwmfTernaryRasterOp(0x660046);
    public static final /* enum */ HwmfTernaryRasterOp SDPSONOX = new HwmfTernaryRasterOp(6756520);
    public static final /* enum */ HwmfTernaryRasterOp DSPDSONOXXN = new HwmfTernaryRasterOp(6838438);
    public static final /* enum */ HwmfTernaryRasterOp PDSXXN = new HwmfTernaryRasterOp(6881605);
    public static final /* enum */ HwmfTernaryRasterOp DPSAX = new HwmfTernaryRasterOp(6947305);
    public static final /* enum */ HwmfTernaryRasterOp PSDPSOAXXN = new HwmfTernaryRasterOp(7018378);
    public static final /* enum */ HwmfTernaryRasterOp SDPAX = new HwmfTernaryRasterOp(7078376);
    public static final /* enum */ HwmfTernaryRasterOp PDSPDOAXXN = new HwmfTernaryRasterOp(7149445);
    public static final /* enum */ HwmfTernaryRasterOp SDPSNOAX = new HwmfTernaryRasterOp(7216680);
    public static final /* enum */ HwmfTernaryRasterOp PDSXNAN = new HwmfTernaryRasterOp(7277669);
    public static final /* enum */ HwmfTernaryRasterOp PDSANA = new HwmfTernaryRasterOp(7343301);
    public static final /* enum */ HwmfTernaryRasterOp SSDXPDXAXN = new HwmfTernaryRasterOp(7413084);
    public static final /* enum */ HwmfTernaryRasterOp SDPSXOX = new HwmfTernaryRasterOp(7472712);
    public static final /* enum */ HwmfTernaryRasterOp SDPNOAN = new HwmfTernaryRasterOp(7540264);
    public static final /* enum */ HwmfTernaryRasterOp DSPDXOX = new HwmfTernaryRasterOp(7603782);
    public static final /* enum */ HwmfTernaryRasterOp DSPNOAN = new HwmfTernaryRasterOp(7671334);
    public static final /* enum */ HwmfTernaryRasterOp SDPSNAOX = new HwmfTernaryRasterOp(7740200);
    public static final /* enum */ HwmfTernaryRasterOp DSAN = new HwmfTernaryRasterOp(7799014);
    public static final /* enum */ HwmfTernaryRasterOp PDSAX = new HwmfTernaryRasterOp(7864805);
    public static final /* enum */ HwmfTernaryRasterOp DSPDSOAXXN = new HwmfTernaryRasterOp(7935878);
    public static final /* enum */ HwmfTernaryRasterOp DPSDNOAX = new HwmfTernaryRasterOp(8003113);
    public static final /* enum */ HwmfTernaryRasterOp SDPXNAN = new HwmfTernaryRasterOp(8064104);
    public static final /* enum */ HwmfTernaryRasterOp SPDSNOAX = new HwmfTernaryRasterOp(8134180);
    public static final /* enum */ HwmfTernaryRasterOp DPSXNAN = new HwmfTernaryRasterOp(8195177);
    public static final /* enum */ HwmfTernaryRasterOp SPXDSXO = new HwmfTernaryRasterOp(8259925);
    public static final /* enum */ HwmfTernaryRasterOp DPSAAN = new HwmfTernaryRasterOp(8324041);
    public static final /* enum */ HwmfTernaryRasterOp DPSAA = new HwmfTernaryRasterOp(8389609);
    public static final /* enum */ HwmfTernaryRasterOp SPXDSXON = new HwmfTernaryRasterOp(8456565);
    public static final /* enum */ HwmfTernaryRasterOp DPSXNA = new HwmfTernaryRasterOp(8522825);
    public static final /* enum */ HwmfTernaryRasterOp SPDSNOAXN = new HwmfTernaryRasterOp(8592900);
    public static final /* enum */ HwmfTernaryRasterOp SDPXNA = new HwmfTernaryRasterOp(8653896);
    public static final /* enum */ HwmfTernaryRasterOp PDSPNOAXN = new HwmfTernaryRasterOp(8723973);
    public static final /* enum */ HwmfTernaryRasterOp DSPDSOAXX = new HwmfTernaryRasterOp(8787878);
    public static final /* enum */ HwmfTernaryRasterOp PDSAXN = new HwmfTernaryRasterOp(8847813);
    public static final /* enum */ HwmfTernaryRasterOp SRCAND = new HwmfTernaryRasterOp(8913094);
    public static final /* enum */ HwmfTernaryRasterOp SDPSNAOXN = new HwmfTernaryRasterOp(8985352);
    public static final /* enum */ HwmfTernaryRasterOp DSPNOA = new HwmfTernaryRasterOp(9047558);
    public static final /* enum */ HwmfTernaryRasterOp DSPDXOXN = new HwmfTernaryRasterOp(9111142);
    public static final /* enum */ HwmfTernaryRasterOp SDPNOA = new HwmfTernaryRasterOp(9178632);
    public static final /* enum */ HwmfTernaryRasterOp SDPSXOXN = new HwmfTernaryRasterOp(9242216);
    public static final /* enum */ HwmfTernaryRasterOp SSDXPDXAX = new HwmfTernaryRasterOp(9313660);
    public static final /* enum */ HwmfTernaryRasterOp PDSANAN = new HwmfTernaryRasterOp(9374949);
    public static final /* enum */ HwmfTernaryRasterOp PDSXNA = new HwmfTernaryRasterOp(9440325);
    public static final /* enum */ HwmfTernaryRasterOp SDPSNOAXN = new HwmfTernaryRasterOp(9510408);
    public static final /* enum */ HwmfTernaryRasterOp DPSDPOAXX = new HwmfTernaryRasterOp(9574313);
    public static final /* enum */ HwmfTernaryRasterOp SPDAXN = new HwmfTernaryRasterOp(9634244);
    public static final /* enum */ HwmfTernaryRasterOp PSDPSOAXX = new HwmfTernaryRasterOp(9705386);
    public static final /* enum */ HwmfTernaryRasterOp DPSAXN = new HwmfTernaryRasterOp(9765321);
    public static final /* enum */ HwmfTernaryRasterOp DPSXX = new HwmfTernaryRasterOp(9830761);
    public static final /* enum */ HwmfTernaryRasterOp PSDPSONOXX = new HwmfTernaryRasterOp(9918602);
    public static final /* enum */ HwmfTernaryRasterOp SDPSONOXN = new HwmfTernaryRasterOp(0x981888);
    public static final /* enum */ HwmfTernaryRasterOp DSXN = new HwmfTernaryRasterOp(0x990066);
    public static final /* enum */ HwmfTernaryRasterOp DPSNAX = new HwmfTernaryRasterOp(10094345);
    public static final /* enum */ HwmfTernaryRasterOp SDPSOAXN = new HwmfTernaryRasterOp(10160040);
    public static final /* enum */ HwmfTernaryRasterOp SPDNAX = new HwmfTernaryRasterOp(10225412);
    public static final /* enum */ HwmfTernaryRasterOp DSPDOAXN = new HwmfTernaryRasterOp(10291110);
    public static final /* enum */ HwmfTernaryRasterOp DSPDSAOXX = new HwmfTernaryRasterOp(10360550);
    public static final /* enum */ HwmfTernaryRasterOp PDSXAN = new HwmfTernaryRasterOp(10421061);
    public static final /* enum */ HwmfTernaryRasterOp DPA = new HwmfTernaryRasterOp(10485961);
    public static final /* enum */ HwmfTernaryRasterOp PDSPNAOXN = new HwmfTernaryRasterOp(10558213);
    public static final /* enum */ HwmfTernaryRasterOp DPSNOA = new HwmfTernaryRasterOp(10620425);
    public static final /* enum */ HwmfTernaryRasterOp DPSDXOXN = new HwmfTernaryRasterOp(10684009);
    public static final /* enum */ HwmfTernaryRasterOp PDSPONOXN = new HwmfTernaryRasterOp(10754181);
    public static final /* enum */ HwmfTernaryRasterOp PDXN = new HwmfTernaryRasterOp(10813541);
    public static final /* enum */ HwmfTernaryRasterOp DSPNAX = new HwmfTernaryRasterOp(10880774);
    public static final /* enum */ HwmfTernaryRasterOp PDSPOAXN = new HwmfTernaryRasterOp(10946469);
    public static final /* enum */ HwmfTernaryRasterOp DPSOA = new HwmfTernaryRasterOp(11010985);
    public static final /* enum */ HwmfTernaryRasterOp DPSOXN = new HwmfTernaryRasterOp(11075977);
    public static final /* enum */ HwmfTernaryRasterOp D = new HwmfTernaryRasterOp(11141161);
    public static final /* enum */ HwmfTernaryRasterOp DPSONO = new HwmfTernaryRasterOp(11208841);
    public static final /* enum */ HwmfTernaryRasterOp SPDSXAX = new HwmfTernaryRasterOp(11274052);
    public static final /* enum */ HwmfTernaryRasterOp DPSDAOXN = new HwmfTernaryRasterOp(11339497);
    public static final /* enum */ HwmfTernaryRasterOp DSPNAO = new HwmfTernaryRasterOp(11406086);
    public static final /* enum */ HwmfTernaryRasterOp DPNO = new HwmfTernaryRasterOp(11469353);
    public static final /* enum */ HwmfTernaryRasterOp PDSNOA = new HwmfTernaryRasterOp(11537925);
    public static final /* enum */ HwmfTernaryRasterOp PDSPXOXN = new HwmfTernaryRasterOp(11601509);
    public static final /* enum */ HwmfTernaryRasterOp SSPXDSXOX = new HwmfTernaryRasterOp(11671924);
    public static final /* enum */ HwmfTernaryRasterOp SDPANAN = new HwmfTernaryRasterOp(11734248);
    public static final /* enum */ HwmfTernaryRasterOp PSDNAX = new HwmfTernaryRasterOp(11798282);
    public static final /* enum */ HwmfTernaryRasterOp DPSDOAXN = new HwmfTernaryRasterOp(11863977);
    public static final /* enum */ HwmfTernaryRasterOp DPSDPAOXX = new HwmfTernaryRasterOp(11933417);
    public static final /* enum */ HwmfTernaryRasterOp SDPXAN = new HwmfTernaryRasterOp(11993928);
    public static final /* enum */ HwmfTernaryRasterOp PSDPXAX = new HwmfTernaryRasterOp(12060490);
    public static final /* enum */ HwmfTernaryRasterOp DSPDAOXN = new HwmfTernaryRasterOp(12125926);
    public static final /* enum */ HwmfTernaryRasterOp DPSNAO = new HwmfTernaryRasterOp(12192521);
    public static final /* enum */ HwmfTernaryRasterOp MERGEPAINT = new HwmfTernaryRasterOp(12255782);
    public static final /* enum */ HwmfTernaryRasterOp SPDSANAX = new HwmfTernaryRasterOp(12328164);
    public static final /* enum */ HwmfTernaryRasterOp SDXPDXAN = new HwmfTernaryRasterOp(12389757);
    public static final /* enum */ HwmfTernaryRasterOp DPSXO = new HwmfTernaryRasterOp(12452457);
    public static final /* enum */ HwmfTernaryRasterOp DPSANO = new HwmfTernaryRasterOp(12519625);
    public static final /* enum */ HwmfTernaryRasterOp MERGECOPY = new HwmfTernaryRasterOp(0xC000CA);
    public static final /* enum */ HwmfTernaryRasterOp SPDSNAOXN = new HwmfTernaryRasterOp(12655364);
    public static final /* enum */ HwmfTernaryRasterOp SPDSONOXN = new HwmfTernaryRasterOp(12720260);
    public static final /* enum */ HwmfTernaryRasterOp PSXN = new HwmfTernaryRasterOp(12779626);
    public static final /* enum */ HwmfTernaryRasterOp SPDNOA = new HwmfTernaryRasterOp(12848644);
    public static final /* enum */ HwmfTernaryRasterOp SPDSXOXN = new HwmfTernaryRasterOp(12912228);
    public static final /* enum */ HwmfTernaryRasterOp SDPNAX = new HwmfTernaryRasterOp(12977928);
    public static final /* enum */ HwmfTernaryRasterOp PSDPOAXN = new HwmfTernaryRasterOp(13043626);
    public static final /* enum */ HwmfTernaryRasterOp SDPOA = new HwmfTernaryRasterOp(13108136);
    public static final /* enum */ HwmfTernaryRasterOp SPDOXN = new HwmfTernaryRasterOp(13173124);
    public static final /* enum */ HwmfTernaryRasterOp DPSDXAX = new HwmfTernaryRasterOp(13240137);
    public static final /* enum */ HwmfTernaryRasterOp SPDSAOXN = new HwmfTernaryRasterOp(13305572);
    public static final /* enum */ HwmfTernaryRasterOp SRCCOPY = new HwmfTernaryRasterOp(0xCC0020);
    public static final /* enum */ HwmfTernaryRasterOp SDPONO = new HwmfTernaryRasterOp(13437064);
    public static final /* enum */ HwmfTernaryRasterOp SDPNAO = new HwmfTernaryRasterOp(13503240);
    public static final /* enum */ HwmfTernaryRasterOp SPNO = new HwmfTernaryRasterOp(13566500);
    public static final /* enum */ HwmfTernaryRasterOp PSDNOA = new HwmfTernaryRasterOp(13635082);
    public static final /* enum */ HwmfTernaryRasterOp PSDPXOXN = new HwmfTernaryRasterOp(13698666);
    public static final /* enum */ HwmfTernaryRasterOp PDSNAX = new HwmfTernaryRasterOp(13764357);
    public static final /* enum */ HwmfTernaryRasterOp SPDSOAXN = new HwmfTernaryRasterOp(13830052);
    public static final /* enum */ HwmfTernaryRasterOp SSPXPDXAX = new HwmfTernaryRasterOp(13901176);
    public static final /* enum */ HwmfTernaryRasterOp DPSANAN = new HwmfTernaryRasterOp(13962473);
    public static final /* enum */ HwmfTernaryRasterOp PSDPSAOXX = new HwmfTernaryRasterOp(14030570);
    public static final /* enum */ HwmfTernaryRasterOp DPSXAN = new HwmfTernaryRasterOp(14091081);
    public static final /* enum */ HwmfTernaryRasterOp PDSPXAX = new HwmfTernaryRasterOp(14157637);
    public static final /* enum */ HwmfTernaryRasterOp SDPSAOXN = new HwmfTernaryRasterOp(14223080);
    public static final /* enum */ HwmfTernaryRasterOp DPSDANAX = new HwmfTernaryRasterOp(14294249);
    public static final /* enum */ HwmfTernaryRasterOp SPXDSXAN = new HwmfTernaryRasterOp(14355829);
    public static final /* enum */ HwmfTernaryRasterOp SPDNAO = new HwmfTernaryRasterOp(14420740);
    public static final /* enum */ HwmfTernaryRasterOp SDNO = new HwmfTernaryRasterOp(14484008);
    public static final /* enum */ HwmfTernaryRasterOp SDPXO = new HwmfTernaryRasterOp(14549608);
    public static final /* enum */ HwmfTernaryRasterOp SDPANO = new HwmfTernaryRasterOp(14616776);
    public static final /* enum */ HwmfTernaryRasterOp PDSOA = new HwmfTernaryRasterOp(14680997);
    public static final /* enum */ HwmfTernaryRasterOp PDSOXN = new HwmfTernaryRasterOp(14745989);
    public static final /* enum */ HwmfTernaryRasterOp DSPDXAX = new HwmfTernaryRasterOp(14812998);
    public static final /* enum */ HwmfTernaryRasterOp PSDPAOXN = new HwmfTernaryRasterOp(14878442);
    public static final /* enum */ HwmfTernaryRasterOp SDPSXAX = new HwmfTernaryRasterOp(14944072);
    public static final /* enum */ HwmfTernaryRasterOp PDSPAOXN = new HwmfTernaryRasterOp(15009509);
    public static final /* enum */ HwmfTernaryRasterOp SDPSANAX = new HwmfTernaryRasterOp(15080680);
    public static final /* enum */ HwmfTernaryRasterOp SPXPDXAN = new HwmfTernaryRasterOp(15142265);
    public static final /* enum */ HwmfTernaryRasterOp SSPXDSXAX = new HwmfTernaryRasterOp(15211892);
    public static final /* enum */ HwmfTernaryRasterOp DSPDSANAXXN = new HwmfTernaryRasterOp(15293670);
    public static final /* enum */ HwmfTernaryRasterOp DPSAO = new HwmfTernaryRasterOp(15336169);
    public static final /* enum */ HwmfTernaryRasterOp DPSXNO = new HwmfTernaryRasterOp(15403081);
    public static final /* enum */ HwmfTernaryRasterOp SDPAO = new HwmfTernaryRasterOp(15467240);
    public static final /* enum */ HwmfTernaryRasterOp SDPXNO = new HwmfTernaryRasterOp(15534152);
    public static final /* enum */ HwmfTernaryRasterOp SRCPAINT = new HwmfTernaryRasterOp(15597702);
    public static final /* enum */ HwmfTernaryRasterOp SDPNOO = new HwmfTernaryRasterOp(15665672);
    public static final /* enum */ HwmfTernaryRasterOp PATCOPY = new HwmfTernaryRasterOp(15728673);
    public static final /* enum */ HwmfTernaryRasterOp PDSONO = new HwmfTernaryRasterOp(15796357);
    public static final /* enum */ HwmfTernaryRasterOp PDSNAO = new HwmfTernaryRasterOp(15862533);
    public static final /* enum */ HwmfTernaryRasterOp PSNO = new HwmfTernaryRasterOp(15925802);
    public static final /* enum */ HwmfTernaryRasterOp PSDNAO = new HwmfTernaryRasterOp(15993610);
    public static final /* enum */ HwmfTernaryRasterOp PDNO = new HwmfTernaryRasterOp(16056869);
    public static final /* enum */ HwmfTernaryRasterOp PDSXO = new HwmfTernaryRasterOp(16122469);
    public static final /* enum */ HwmfTernaryRasterOp PDSANO = new HwmfTernaryRasterOp(16189637);
    public static final /* enum */ HwmfTernaryRasterOp PDSAO = new HwmfTernaryRasterOp(16253669);
    public static final /* enum */ HwmfTernaryRasterOp PDSXNO = new HwmfTernaryRasterOp(16320581);
    public static final /* enum */ HwmfTernaryRasterOp DPO = new HwmfTernaryRasterOp(16384137);
    public static final /* enum */ HwmfTernaryRasterOp PATPAINT = new HwmfTernaryRasterOp(16452105);
    public static final /* enum */ HwmfTernaryRasterOp PSO = new HwmfTernaryRasterOp(16515210);
    public static final /* enum */ HwmfTernaryRasterOp PSDNOO = new HwmfTernaryRasterOp(16583178);
    public static final /* enum */ HwmfTernaryRasterOp DPSOO = new HwmfTernaryRasterOp(16646825);
    public static final /* enum */ HwmfTernaryRasterOp WHITENESS = new HwmfTernaryRasterOp(16711778);
    private static final String[] ARG_ORDER;
    private static final String OPS = "nxoa";
    private final int opValue;
    private static final /* synthetic */ HwmfTernaryRasterOp[] $VALUES;

    public static HwmfTernaryRasterOp[] values() {
        return (HwmfTernaryRasterOp[])$VALUES.clone();
    }

    public static HwmfTernaryRasterOp valueOf(String name) {
        return Enum.valueOf(HwmfTernaryRasterOp.class, name);
    }

    private HwmfTernaryRasterOp(int opValue) {
        this.opValue = opValue;
    }

    public static HwmfTernaryRasterOp valueOf(int opIndex) {
        for (HwmfTernaryRasterOp bb : HwmfTernaryRasterOp.values()) {
            if (bb.getOpIndex() != opIndex) continue;
            return bb;
        }
        return null;
    }

    public int getOpIndex() {
        return this.opValue >>> 16;
    }

    public int getOpCode() {
        return this.opValue & 0xFFFF;
    }

    public String describeCmd() {
        String[] stack = new String[10];
        int stackPnt = 0;
        block9: for (char c : this.calcCmd().toCharArray()) {
            switch (c) {
                case 'D': 
                case 'P': 
                case 'S': {
                    stack[stackPnt++] = "" + c;
                    continue block9;
                }
                case 'n': {
                    stack[stackPnt - 1] = "not(" + stack[stackPnt - 1] + ")";
                    continue block9;
                }
                case 'a': {
                    stack[stackPnt - 2] = "(" + stack[stackPnt - 1] + " and " + stack[stackPnt - 2] + ")";
                    --stackPnt;
                    continue block9;
                }
                case 'o': {
                    stack[stackPnt - 2] = "(" + stack[stackPnt - 1] + " or " + stack[stackPnt - 2] + ")";
                    --stackPnt;
                    continue block9;
                }
                case 'x': {
                    stack[stackPnt - 2] = "(" + stack[stackPnt - 1] + " xor " + stack[stackPnt - 2] + ")";
                    --stackPnt;
                    continue block9;
                }
                case '1': {
                    stack[stackPnt++] = "all white";
                    continue block9;
                }
                case '0': {
                    stack[stackPnt++] = "all black";
                    continue block9;
                }
                default: {
                    throw new RuntimeException("unknown cmd '" + c + "'.");
                }
            }
        }
        return stack[--stackPnt];
    }

    public String calcCmd() {
        String ret;
        String argOrder = ARG_ORDER[this.opValue & 0x1F];
        assert (argOrder != null);
        int nbrOfOps = 0;
        int[] opArr = new int[5];
        int i = 0;
        int bit = 6;
        while (i < opArr.length) {
            opArr[i] = this.opValue >>> bit & 3;
            if (opArr[i] != 0) {
                nbrOfOps = i + 1;
            }
            ++i;
            bit += 2;
        }
        StringBuilder sbArg = new StringBuilder();
        StringBuilder sbOp = new StringBuilder();
        sbArg.append(argOrder.charAt(0));
        int argIdx = 1;
        for (int opIdx = 0; opIdx < nbrOfOps; ++opIdx) {
            char opCh = OPS.charAt(opArr[opIdx]);
            char ch = argOrder.charAt(argIdx);
            sbOp.append(opCh);
            if (ch == '.') {
                sbArg.insert(argIdx, sbOp.charAt(0));
                sbOp.deleteCharAt(0);
            }
            if (opCh == 'n') continue;
            sbArg.append(ch == '.' ? argOrder.charAt(++argIdx) : ch);
            ++argIdx;
        }
        sbArg.append((CharSequence)sbOp);
        if (nbrOfOps % 2 == (this.opValue >>> 5 & 1)) {
            sbArg.append('n');
        }
        return (ret = sbArg.toString()).startsWith("DDx") ? ("DDx".equals(ret) ? "0" : "1") : ret;
    }

    public void process(Deque<int[]> stack, int[] dst, int[] src, int[] pat) {
        block11: for (char op : this.calcCmd().toCharArray()) {
            switch (op) {
                case 'S': {
                    HwmfTernaryRasterOp.opS(stack, dst, src, pat);
                    continue block11;
                }
                case 'P': {
                    HwmfTernaryRasterOp.opP(stack, dst, src, pat);
                    continue block11;
                }
                case 'D': {
                    HwmfTernaryRasterOp.opD(stack, dst, src, pat);
                    continue block11;
                }
                case 'n': {
                    HwmfTernaryRasterOp.opN(stack, dst, src, pat);
                    continue block11;
                }
                case 'a': {
                    HwmfTernaryRasterOp.opA(stack, dst, src, pat);
                    continue block11;
                }
                case 'o': {
                    HwmfTernaryRasterOp.opO(stack, dst, src, pat);
                    continue block11;
                }
                case 'x': {
                    HwmfTernaryRasterOp.opX(stack, dst, src, pat);
                    continue block11;
                }
                case '1': {
                    HwmfTernaryRasterOp.op1(stack, dst, src, pat);
                    continue block11;
                }
                case '0': {
                    HwmfTernaryRasterOp.op0(stack, dst, src, pat);
                    continue block11;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
    }

    private static void opS(Deque<int[]> stack, int[] dst, int[] src, int[] pat) {
        stack.push(src);
    }

    private static void opP(Deque<int[]> stack, int[] dst, int[] src, int[] pat) {
        stack.push(pat);
    }

    private static void opD(Deque<int[]> stack, int[] dst, int[] src, int[] pat) {
        stack.push(dst);
    }

    private static void opN(Deque<int[]> stack, int[] dst, int[] src, int[] pat) {
        int[] oper = HwmfTernaryRasterOp.checkClone(stack.pop(), dst, src, pat, true);
        for (int i = 0; i < oper.length; ++i) {
            oper[i] = oper[i] & 0xFF000000 | ~oper[i] & 0xFFFFFF;
        }
        stack.push(oper);
    }

    private static void opA(Deque<int[]> stack, int[] dst, int[] src, int[] pat) {
        int[] oper1 = HwmfTernaryRasterOp.checkClone(stack.pop(), dst, src, pat, true);
        int[] oper2 = HwmfTernaryRasterOp.checkClone(stack.pop(), dst, src, pat, false);
        for (int i = 0; i < oper1.length; ++i) {
            oper1[i] = oper1[i] & 0xFF000000 | oper1[i] & oper2[i] & 0xFFFFFF;
        }
        stack.push(oper1);
    }

    private static void opO(Deque<int[]> stack, int[] dst, int[] src, int[] pat) {
        int[] oper1 = HwmfTernaryRasterOp.checkClone(stack.pop(), dst, src, pat, true);
        int[] oper2 = HwmfTernaryRasterOp.checkClone(stack.pop(), dst, src, pat, false);
        for (int i = 0; i < oper1.length; ++i) {
            oper1[i] = oper1[i] & 0xFF000000 | (oper1[i] | oper2[i]) & 0xFFFFFF;
        }
        stack.push(oper1);
    }

    private static void opX(Deque<int[]> stack, int[] dst, int[] src, int[] pat) {
        int[] oper1 = HwmfTernaryRasterOp.checkClone(stack.pop(), dst, src, pat, true);
        int[] oper2 = HwmfTernaryRasterOp.checkClone(stack.pop(), dst, src, pat, false);
        for (int i = 0; i < oper1.length; ++i) {
            oper1[i] = oper1[i] & 0xFF000000 | (oper1[i] ^ oper2[i]) & 0xFFFFFF;
        }
        stack.push(oper1);
    }

    private static void op1(Deque<int[]> stack, int[] dst, int[] src, int[] pat) {
        int[] oper = new int[dst.length];
        Arrays.fill(oper, -1);
        stack.push(oper);
    }

    private static void op0(Deque<int[]> stack, int[] dst, int[] src, int[] pat) {
        int[] oper = new int[dst.length];
        Arrays.fill(oper, -16777216);
        stack.push(oper);
    }

    private static int[] checkClone(int[] oper, int[] dst, int[] src, int[] pat, boolean force) {
        if (force && (oper == src || oper == pat || oper == dst)) {
            return (int[])oper.clone();
        }
        return oper;
    }

    static {
        $VALUES = new HwmfTernaryRasterOp[]{BLACKNESS, DPSOON, DPSONA, PSON, SDPONA, DPON, PDSXNON, PDSAON, SDPNAA, PDSXON, DPNA, PSDNAON, SPNA, PDSNAON, PDSONON, PN, PDSONA, NOTSRCERASE, SDPXNON, SDPAON, DPSXNON, DPSAON, PSDPSANAXX, SSPXDSXAXN, SPXPDXA, SDPSANAXN, PDSPAOX, SDPSXAXN, PSDPAOX, DSPDXAXN, PDSOX, PDSOAN, DPSNAA, SDPXON, DSNA, SPDNAON, SPXDSXA, PDSPANAXN, SDPSAOX, SDPSXNOX, DPSXA, PSDPSAOXXN, DPSANA, SSPXPDXAXN, SPDSOAX, PSDNOX, PSDPXOX, PSDNOAN, PSNA, SDPNAON, SDPSOOX, NOTSRCCOPY, SPDSAOX, SPDSXNOX, SDPOX, SDPOAN, PSDPOAX, SPDNOX, SPDSXOX, SPDNOAN, PSX, SPDSONOX, SPDSNAOX, PSAN, PSDNAA, DPSXON, SDXPDXA, SPDSANAXN, SRCERASE, DPSNAON, DSPDAOX, PSDPXAXN, SDPXA, PDSPDAOXXN, DPSDOAX, PDSNOX, SDPANA, SSPXDSXOXN, PDSPXOX, PDSNOAN, PDNA, DSPNAON, DPSDAOX, SPDSXAXN, DPSONON, DSTINVERT, DPSOX, DPSOAN, PDSPOAX, DPSNOX, PATINVERT, DPSDONOX, DPSDXOX, DPSNOAN, DPSDNAOX, DPAN, PDSXA, DSPDSAOXXN, DSPDOAX, SDPNOX, SDPSOAX, DSPNOX, SRCINVERT, SDPSONOX, DSPDSONOXXN, PDSXXN, DPSAX, PSDPSOAXXN, SDPAX, PDSPDOAXXN, SDPSNOAX, PDSXNAN, PDSANA, SSDXPDXAXN, SDPSXOX, SDPNOAN, DSPDXOX, DSPNOAN, SDPSNAOX, DSAN, PDSAX, DSPDSOAXXN, DPSDNOAX, SDPXNAN, SPDSNOAX, DPSXNAN, SPXDSXO, DPSAAN, DPSAA, SPXDSXON, DPSXNA, SPDSNOAXN, SDPXNA, PDSPNOAXN, DSPDSOAXX, PDSAXN, SRCAND, SDPSNAOXN, DSPNOA, DSPDXOXN, SDPNOA, SDPSXOXN, SSDXPDXAX, PDSANAN, PDSXNA, SDPSNOAXN, DPSDPOAXX, SPDAXN, PSDPSOAXX, DPSAXN, DPSXX, PSDPSONOXX, SDPSONOXN, DSXN, DPSNAX, SDPSOAXN, SPDNAX, DSPDOAXN, DSPDSAOXX, PDSXAN, DPA, PDSPNAOXN, DPSNOA, DPSDXOXN, PDSPONOXN, PDXN, DSPNAX, PDSPOAXN, DPSOA, DPSOXN, D, DPSONO, SPDSXAX, DPSDAOXN, DSPNAO, DPNO, PDSNOA, PDSPXOXN, SSPXDSXOX, SDPANAN, PSDNAX, DPSDOAXN, DPSDPAOXX, SDPXAN, PSDPXAX, DSPDAOXN, DPSNAO, MERGEPAINT, SPDSANAX, SDXPDXAN, DPSXO, DPSANO, MERGECOPY, SPDSNAOXN, SPDSONOXN, PSXN, SPDNOA, SPDSXOXN, SDPNAX, PSDPOAXN, SDPOA, SPDOXN, DPSDXAX, SPDSAOXN, SRCCOPY, SDPONO, SDPNAO, SPNO, PSDNOA, PSDPXOXN, PDSNAX, SPDSOAXN, SSPXPDXAX, DPSANAN, PSDPSAOXX, DPSXAN, PDSPXAX, SDPSAOXN, DPSDANAX, SPXDSXAN, SPDNAO, SDNO, SDPXO, SDPANO, PDSOA, PDSOXN, DSPDXAX, PSDPAOXN, SDPSXAX, PDSPAOXN, SDPSANAX, SPXPDXAN, SSPXDSXAX, DSPDSANAXXN, DPSAO, DPSXNO, SDPAO, SDPXNO, SRCPAINT, SDPNOO, PATCOPY, PDSONO, PDSNAO, PSNO, PSDNAO, PDNO, PDSXO, PDSANO, PDSAO, PDSXNO, DPO, PATPAINT, PSO, PSDNOO, DPSOO, WHITENESS};
        ARG_ORDER = new String[]{"SSSSSS", "PPPPPP", "DDDDDD", null, "SPDSPD", "PDSPDS", "DSPDSP", null, "SDPSDP", "DPSDPS", "PSDPSD", null, null, null, null, null, null, null, null, null, "SSP.DS", "SP.DS", null, null, "SSP.PD", "SP.PD", null, null, "SSD.PD", "SD.PD", null, null};
    }
}

