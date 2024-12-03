/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class CatalanStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final CatalanStemmer methodObject = new CatalanStemmer();
    private static final Among[] a_0 = new Among[]{new Among("", -1, 13, "", methodObject), new Among("\u00b7", 0, 12, "", methodObject), new Among("\u00e0", 0, 2, "", methodObject), new Among("\u00e1", 0, 1, "", methodObject), new Among("\u00e8", 0, 4, "", methodObject), new Among("\u00e9", 0, 3, "", methodObject), new Among("\u00ec", 0, 6, "", methodObject), new Among("\u00ed", 0, 5, "", methodObject), new Among("\u00ef", 0, 11, "", methodObject), new Among("\u00f2", 0, 8, "", methodObject), new Among("\u00f3", 0, 7, "", methodObject), new Among("\u00fa", 0, 9, "", methodObject), new Among("\u00fc", 0, 10, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("la", -1, 1, "", methodObject), new Among("-la", 0, 1, "", methodObject), new Among("sela", 0, 1, "", methodObject), new Among("le", -1, 1, "", methodObject), new Among("me", -1, 1, "", methodObject), new Among("-me", 4, 1, "", methodObject), new Among("se", -1, 1, "", methodObject), new Among("-te", -1, 1, "", methodObject), new Among("hi", -1, 1, "", methodObject), new Among("'hi", 8, 1, "", methodObject), new Among("li", -1, 1, "", methodObject), new Among("-li", 10, 1, "", methodObject), new Among("'l", -1, 1, "", methodObject), new Among("'m", -1, 1, "", methodObject), new Among("-m", -1, 1, "", methodObject), new Among("'n", -1, 1, "", methodObject), new Among("-n", -1, 1, "", methodObject), new Among("ho", -1, 1, "", methodObject), new Among("'ho", 17, 1, "", methodObject), new Among("lo", -1, 1, "", methodObject), new Among("selo", 19, 1, "", methodObject), new Among("'s", -1, 1, "", methodObject), new Among("las", -1, 1, "", methodObject), new Among("selas", 22, 1, "", methodObject), new Among("les", -1, 1, "", methodObject), new Among("-les", 24, 1, "", methodObject), new Among("'ls", -1, 1, "", methodObject), new Among("-ls", -1, 1, "", methodObject), new Among("'ns", -1, 1, "", methodObject), new Among("-ns", -1, 1, "", methodObject), new Among("ens", -1, 1, "", methodObject), new Among("los", -1, 1, "", methodObject), new Among("selos", 31, 1, "", methodObject), new Among("nos", -1, 1, "", methodObject), new Among("-nos", 33, 1, "", methodObject), new Among("vos", -1, 1, "", methodObject), new Among("us", -1, 1, "", methodObject), new Among("-us", 36, 1, "", methodObject), new Among("'t", -1, 1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("ica", -1, 4, "", methodObject), new Among("l\u00f3gica", 0, 3, "", methodObject), new Among("enca", -1, 1, "", methodObject), new Among("ada", -1, 2, "", methodObject), new Among("ancia", -1, 1, "", methodObject), new Among("encia", -1, 1, "", methodObject), new Among("\u00e8ncia", -1, 1, "", methodObject), new Among("\u00edcia", -1, 1, "", methodObject), new Among("logia", -1, 3, "", methodObject), new Among("inia", -1, 1, "", methodObject), new Among("\u00edinia", 9, 1, "", methodObject), new Among("eria", -1, 1, "", methodObject), new Among("\u00e0ria", -1, 1, "", methodObject), new Among("at\u00f2ria", -1, 1, "", methodObject), new Among("alla", -1, 1, "", methodObject), new Among("ella", -1, 1, "", methodObject), new Among("\u00edvola", -1, 1, "", methodObject), new Among("ima", -1, 1, "", methodObject), new Among("\u00edssima", 17, 1, "", methodObject), new Among("qu\u00edssima", 18, 5, "", methodObject), new Among("ana", -1, 1, "", methodObject), new Among("ina", -1, 1, "", methodObject), new Among("era", -1, 1, "", methodObject), new Among("sfera", 22, 1, "", methodObject), new Among("ora", -1, 1, "", methodObject), new Among("dora", 24, 1, "", methodObject), new Among("adora", 25, 1, "", methodObject), new Among("adura", -1, 1, "", methodObject), new Among("esa", -1, 1, "", methodObject), new Among("osa", -1, 1, "", methodObject), new Among("assa", -1, 1, "", methodObject), new Among("essa", -1, 1, "", methodObject), new Among("issa", -1, 1, "", methodObject), new Among("eta", -1, 1, "", methodObject), new Among("ita", -1, 1, "", methodObject), new Among("ota", -1, 1, "", methodObject), new Among("ista", -1, 1, "", methodObject), new Among("ialista", 36, 1, "", methodObject), new Among("ionista", 36, 1, "", methodObject), new Among("iva", -1, 1, "", methodObject), new Among("ativa", 39, 1, "", methodObject), new Among("n\u00e7a", -1, 1, "", methodObject), new Among("log\u00eda", -1, 3, "", methodObject), new Among("ic", -1, 4, "", methodObject), new Among("\u00edstic", 43, 1, "", methodObject), new Among("enc", -1, 1, "", methodObject), new Among("esc", -1, 1, "", methodObject), new Among("ud", -1, 1, "", methodObject), new Among("atge", -1, 1, "", methodObject), new Among("ble", -1, 1, "", methodObject), new Among("able", 49, 1, "", methodObject), new Among("ible", 49, 1, "", methodObject), new Among("isme", -1, 1, "", methodObject), new Among("ialisme", 52, 1, "", methodObject), new Among("ionisme", 52, 1, "", methodObject), new Among("ivisme", 52, 1, "", methodObject), new Among("aire", -1, 1, "", methodObject), new Among("icte", -1, 1, "", methodObject), new Among("iste", -1, 1, "", methodObject), new Among("ici", -1, 1, "", methodObject), new Among("\u00edci", -1, 1, "", methodObject), new Among("logi", -1, 3, "", methodObject), new Among("ari", -1, 1, "", methodObject), new Among("tori", -1, 1, "", methodObject), new Among("al", -1, 1, "", methodObject), new Among("il", -1, 1, "", methodObject), new Among("all", -1, 1, "", methodObject), new Among("ell", -1, 1, "", methodObject), new Among("\u00edvol", -1, 1, "", methodObject), new Among("isam", -1, 1, "", methodObject), new Among("issem", -1, 1, "", methodObject), new Among("\u00ecssem", -1, 1, "", methodObject), new Among("\u00edssem", -1, 1, "", methodObject), new Among("\u00edssim", -1, 1, "", methodObject), new Among("qu\u00edssim", 73, 5, "", methodObject), new Among("amen", -1, 1, "", methodObject), new Among("\u00ecssin", -1, 1, "", methodObject), new Among("ar", -1, 1, "", methodObject), new Among("ificar", 77, 1, "", methodObject), new Among("egar", 77, 1, "", methodObject), new Among("ejar", 77, 1, "", methodObject), new Among("itar", 77, 1, "", methodObject), new Among("itzar", 77, 1, "", methodObject), new Among("fer", -1, 1, "", methodObject), new Among("or", -1, 1, "", methodObject), new Among("dor", 84, 1, "", methodObject), new Among("dur", -1, 1, "", methodObject), new Among("doras", -1, 1, "", methodObject), new Among("ics", -1, 4, "", methodObject), new Among("l\u00f3gics", 88, 3, "", methodObject), new Among("uds", -1, 1, "", methodObject), new Among("nces", -1, 1, "", methodObject), new Among("ades", -1, 2, "", methodObject), new Among("ancies", -1, 1, "", methodObject), new Among("encies", -1, 1, "", methodObject), new Among("\u00e8ncies", -1, 1, "", methodObject), new Among("\u00edcies", -1, 1, "", methodObject), new Among("logies", -1, 3, "", methodObject), new Among("inies", -1, 1, "", methodObject), new Among("\u00ednies", -1, 1, "", methodObject), new Among("eries", -1, 1, "", methodObject), new Among("\u00e0ries", -1, 1, "", methodObject), new Among("at\u00f2ries", -1, 1, "", methodObject), new Among("bles", -1, 1, "", methodObject), new Among("ables", 103, 1, "", methodObject), new Among("ibles", 103, 1, "", methodObject), new Among("imes", -1, 1, "", methodObject), new Among("\u00edssimes", 106, 1, "", methodObject), new Among("qu\u00edssimes", 107, 5, "", methodObject), new Among("formes", -1, 1, "", methodObject), new Among("ismes", -1, 1, "", methodObject), new Among("ialismes", 110, 1, "", methodObject), new Among("ines", -1, 1, "", methodObject), new Among("eres", -1, 1, "", methodObject), new Among("ores", -1, 1, "", methodObject), new Among("dores", 114, 1, "", methodObject), new Among("idores", 115, 1, "", methodObject), new Among("dures", -1, 1, "", methodObject), new Among("eses", -1, 1, "", methodObject), new Among("oses", -1, 1, "", methodObject), new Among("asses", -1, 1, "", methodObject), new Among("ictes", -1, 1, "", methodObject), new Among("ites", -1, 1, "", methodObject), new Among("otes", -1, 1, "", methodObject), new Among("istes", -1, 1, "", methodObject), new Among("ialistes", 124, 1, "", methodObject), new Among("ionistes", 124, 1, "", methodObject), new Among("iques", -1, 4, "", methodObject), new Among("l\u00f3giques", 127, 3, "", methodObject), new Among("ives", -1, 1, "", methodObject), new Among("atives", 129, 1, "", methodObject), new Among("log\u00edes", -1, 3, "", methodObject), new Among("alleng\u00fces", -1, 1, "", methodObject), new Among("icis", -1, 1, "", methodObject), new Among("\u00edcis", -1, 1, "", methodObject), new Among("logis", -1, 3, "", methodObject), new Among("aris", -1, 1, "", methodObject), new Among("toris", -1, 1, "", methodObject), new Among("ls", -1, 1, "", methodObject), new Among("als", 138, 1, "", methodObject), new Among("ells", 138, 1, "", methodObject), new Among("ims", -1, 1, "", methodObject), new Among("\u00edssims", 141, 1, "", methodObject), new Among("qu\u00edssims", 142, 5, "", methodObject), new Among("ions", -1, 1, "", methodObject), new Among("cions", 144, 1, "", methodObject), new Among("acions", 145, 2, "", methodObject), new Among("esos", -1, 1, "", methodObject), new Among("osos", -1, 1, "", methodObject), new Among("assos", -1, 1, "", methodObject), new Among("issos", -1, 1, "", methodObject), new Among("ers", -1, 1, "", methodObject), new Among("ors", -1, 1, "", methodObject), new Among("dors", 152, 1, "", methodObject), new Among("adors", 153, 1, "", methodObject), new Among("idors", 153, 1, "", methodObject), new Among("ats", -1, 1, "", methodObject), new Among("itats", 156, 1, "", methodObject), new Among("bilitats", 157, 1, "", methodObject), new Among("ivitats", 157, 1, "", methodObject), new Among("ativitats", 159, 1, "", methodObject), new Among("\u00eftats", 156, 1, "", methodObject), new Among("ets", -1, 1, "", methodObject), new Among("ants", -1, 1, "", methodObject), new Among("ents", -1, 1, "", methodObject), new Among("ments", 164, 1, "", methodObject), new Among("aments", 165, 1, "", methodObject), new Among("ots", -1, 1, "", methodObject), new Among("uts", -1, 1, "", methodObject), new Among("ius", -1, 1, "", methodObject), new Among("trius", 169, 1, "", methodObject), new Among("atius", 169, 1, "", methodObject), new Among("\u00e8s", -1, 1, "", methodObject), new Among("\u00e9s", -1, 1, "", methodObject), new Among("\u00eds", -1, 1, "", methodObject), new Among("d\u00eds", 174, 1, "", methodObject), new Among("\u00f3s", -1, 1, "", methodObject), new Among("itat", -1, 1, "", methodObject), new Among("bilitat", 177, 1, "", methodObject), new Among("ivitat", 177, 1, "", methodObject), new Among("ativitat", 179, 1, "", methodObject), new Among("\u00eftat", -1, 1, "", methodObject), new Among("et", -1, 1, "", methodObject), new Among("ant", -1, 1, "", methodObject), new Among("ent", -1, 1, "", methodObject), new Among("ient", 184, 1, "", methodObject), new Among("ment", 184, 1, "", methodObject), new Among("ament", 186, 1, "", methodObject), new Among("isament", 187, 1, "", methodObject), new Among("ot", -1, 1, "", methodObject), new Among("isseu", -1, 1, "", methodObject), new Among("\u00ecsseu", -1, 1, "", methodObject), new Among("\u00edsseu", -1, 1, "", methodObject), new Among("triu", -1, 1, "", methodObject), new Among("\u00edssiu", -1, 1, "", methodObject), new Among("atiu", -1, 1, "", methodObject), new Among("\u00f3", -1, 1, "", methodObject), new Among("i\u00f3", 196, 1, "", methodObject), new Among("ci\u00f3", 197, 1, "", methodObject), new Among("aci\u00f3", 198, 1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("aba", -1, 1, "", methodObject), new Among("esca", -1, 1, "", methodObject), new Among("isca", -1, 1, "", methodObject), new Among("\u00efsca", -1, 1, "", methodObject), new Among("ada", -1, 1, "", methodObject), new Among("ida", -1, 1, "", methodObject), new Among("uda", -1, 1, "", methodObject), new Among("\u00efda", -1, 1, "", methodObject), new Among("ia", -1, 1, "", methodObject), new Among("aria", 8, 1, "", methodObject), new Among("iria", 8, 1, "", methodObject), new Among("ara", -1, 1, "", methodObject), new Among("iera", -1, 1, "", methodObject), new Among("ira", -1, 1, "", methodObject), new Among("adora", -1, 1, "", methodObject), new Among("\u00efra", -1, 1, "", methodObject), new Among("ava", -1, 1, "", methodObject), new Among("ixa", -1, 1, "", methodObject), new Among("itza", -1, 1, "", methodObject), new Among("\u00eda", -1, 1, "", methodObject), new Among("ar\u00eda", 19, 1, "", methodObject), new Among("er\u00eda", 19, 1, "", methodObject), new Among("ir\u00eda", 19, 1, "", methodObject), new Among("\u00efa", -1, 1, "", methodObject), new Among("isc", -1, 1, "", methodObject), new Among("\u00efsc", -1, 1, "", methodObject), new Among("ad", -1, 1, "", methodObject), new Among("ed", -1, 1, "", methodObject), new Among("id", -1, 1, "", methodObject), new Among("ie", -1, 1, "", methodObject), new Among("re", -1, 1, "", methodObject), new Among("dre", 30, 1, "", methodObject), new Among("ase", -1, 1, "", methodObject), new Among("iese", -1, 1, "", methodObject), new Among("aste", -1, 1, "", methodObject), new Among("iste", -1, 1, "", methodObject), new Among("ii", -1, 1, "", methodObject), new Among("ini", -1, 1, "", methodObject), new Among("esqui", -1, 1, "", methodObject), new Among("eixi", -1, 1, "", methodObject), new Among("itzi", -1, 1, "", methodObject), new Among("am", -1, 1, "", methodObject), new Among("em", -1, 1, "", methodObject), new Among("arem", 42, 1, "", methodObject), new Among("irem", 42, 1, "", methodObject), new Among("\u00e0rem", 42, 1, "", methodObject), new Among("\u00edrem", 42, 1, "", methodObject), new Among("\u00e0ssem", 42, 1, "", methodObject), new Among("\u00e9ssem", 42, 1, "", methodObject), new Among("iguem", 42, 1, "", methodObject), new Among("\u00efguem", 42, 1, "", methodObject), new Among("avem", 42, 1, "", methodObject), new Among("\u00e0vem", 42, 1, "", methodObject), new Among("\u00e1vem", 42, 1, "", methodObject), new Among("ir\u00ecem", 42, 1, "", methodObject), new Among("\u00edem", 42, 1, "", methodObject), new Among("ar\u00edem", 55, 1, "", methodObject), new Among("ir\u00edem", 55, 1, "", methodObject), new Among("assim", -1, 1, "", methodObject), new Among("essim", -1, 1, "", methodObject), new Among("issim", -1, 1, "", methodObject), new Among("\u00e0ssim", -1, 1, "", methodObject), new Among("\u00e8ssim", -1, 1, "", methodObject), new Among("\u00e9ssim", -1, 1, "", methodObject), new Among("\u00edssim", -1, 1, "", methodObject), new Among("\u00efm", -1, 1, "", methodObject), new Among("an", -1, 1, "", methodObject), new Among("aban", 66, 1, "", methodObject), new Among("arian", 66, 1, "", methodObject), new Among("aran", 66, 1, "", methodObject), new Among("ieran", 66, 1, "", methodObject), new Among("iran", 66, 1, "", methodObject), new Among("\u00edan", 66, 1, "", methodObject), new Among("ar\u00edan", 72, 1, "", methodObject), new Among("er\u00edan", 72, 1, "", methodObject), new Among("ir\u00edan", 72, 1, "", methodObject), new Among("en", -1, 1, "", methodObject), new Among("ien", 76, 1, "", methodObject), new Among("arien", 77, 1, "", methodObject), new Among("irien", 77, 1, "", methodObject), new Among("aren", 76, 1, "", methodObject), new Among("eren", 76, 1, "", methodObject), new Among("iren", 76, 1, "", methodObject), new Among("\u00e0ren", 76, 1, "", methodObject), new Among("\u00efren", 76, 1, "", methodObject), new Among("asen", 76, 1, "", methodObject), new Among("iesen", 76, 1, "", methodObject), new Among("assen", 76, 1, "", methodObject), new Among("essen", 76, 1, "", methodObject), new Among("issen", 76, 1, "", methodObject), new Among("\u00e9ssen", 76, 1, "", methodObject), new Among("\u00efssen", 76, 1, "", methodObject), new Among("esquen", 76, 1, "", methodObject), new Among("isquen", 76, 1, "", methodObject), new Among("\u00efsquen", 76, 1, "", methodObject), new Among("aven", 76, 1, "", methodObject), new Among("ixen", 76, 1, "", methodObject), new Among("eixen", 96, 1, "", methodObject), new Among("\u00efxen", 76, 1, "", methodObject), new Among("\u00efen", 76, 1, "", methodObject), new Among("in", -1, 1, "", methodObject), new Among("inin", 100, 1, "", methodObject), new Among("sin", 100, 1, "", methodObject), new Among("isin", 102, 1, "", methodObject), new Among("assin", 102, 1, "", methodObject), new Among("essin", 102, 1, "", methodObject), new Among("issin", 102, 1, "", methodObject), new Among("\u00efssin", 102, 1, "", methodObject), new Among("esquin", 100, 1, "", methodObject), new Among("eixin", 100, 1, "", methodObject), new Among("aron", -1, 1, "", methodObject), new Among("ieron", -1, 1, "", methodObject), new Among("ar\u00e1n", -1, 1, "", methodObject), new Among("er\u00e1n", -1, 1, "", methodObject), new Among("ir\u00e1n", -1, 1, "", methodObject), new Among("i\u00efn", -1, 1, "", methodObject), new Among("ado", -1, 1, "", methodObject), new Among("ido", -1, 1, "", methodObject), new Among("ando", -1, 2, "", methodObject), new Among("iendo", -1, 1, "", methodObject), new Among("io", -1, 1, "", methodObject), new Among("ixo", -1, 1, "", methodObject), new Among("eixo", 121, 1, "", methodObject), new Among("\u00efxo", -1, 1, "", methodObject), new Among("itzo", -1, 1, "", methodObject), new Among("ar", -1, 1, "", methodObject), new Among("tzar", 125, 1, "", methodObject), new Among("er", -1, 1, "", methodObject), new Among("eixer", 127, 1, "", methodObject), new Among("ir", -1, 1, "", methodObject), new Among("ador", -1, 1, "", methodObject), new Among("as", -1, 1, "", methodObject), new Among("abas", 131, 1, "", methodObject), new Among("adas", 131, 1, "", methodObject), new Among("idas", 131, 1, "", methodObject), new Among("aras", 131, 1, "", methodObject), new Among("ieras", 131, 1, "", methodObject), new Among("\u00edas", 131, 1, "", methodObject), new Among("ar\u00edas", 137, 1, "", methodObject), new Among("er\u00edas", 137, 1, "", methodObject), new Among("ir\u00edas", 137, 1, "", methodObject), new Among("ids", -1, 1, "", methodObject), new Among("es", -1, 1, "", methodObject), new Among("ades", 142, 1, "", methodObject), new Among("ides", 142, 1, "", methodObject), new Among("udes", 142, 1, "", methodObject), new Among("\u00efdes", 142, 1, "", methodObject), new Among("atges", 142, 1, "", methodObject), new Among("ies", 142, 1, "", methodObject), new Among("aries", 148, 1, "", methodObject), new Among("iries", 148, 1, "", methodObject), new Among("ares", 142, 1, "", methodObject), new Among("ires", 142, 1, "", methodObject), new Among("adores", 142, 1, "", methodObject), new Among("\u00efres", 142, 1, "", methodObject), new Among("ases", 142, 1, "", methodObject), new Among("ieses", 142, 1, "", methodObject), new Among("asses", 142, 1, "", methodObject), new Among("esses", 142, 1, "", methodObject), new Among("isses", 142, 1, "", methodObject), new Among("\u00efsses", 142, 1, "", methodObject), new Among("ques", 142, 1, "", methodObject), new Among("esques", 161, 1, "", methodObject), new Among("\u00efsques", 161, 1, "", methodObject), new Among("aves", 142, 1, "", methodObject), new Among("ixes", 142, 1, "", methodObject), new Among("eixes", 165, 1, "", methodObject), new Among("\u00efxes", 142, 1, "", methodObject), new Among("\u00efes", 142, 1, "", methodObject), new Among("abais", -1, 1, "", methodObject), new Among("arais", -1, 1, "", methodObject), new Among("ierais", -1, 1, "", methodObject), new Among("\u00edais", -1, 1, "", methodObject), new Among("ar\u00edais", 172, 1, "", methodObject), new Among("er\u00edais", 172, 1, "", methodObject), new Among("ir\u00edais", 172, 1, "", methodObject), new Among("aseis", -1, 1, "", methodObject), new Among("ieseis", -1, 1, "", methodObject), new Among("asteis", -1, 1, "", methodObject), new Among("isteis", -1, 1, "", methodObject), new Among("inis", -1, 1, "", methodObject), new Among("sis", -1, 1, "", methodObject), new Among("isis", 181, 1, "", methodObject), new Among("assis", 181, 1, "", methodObject), new Among("essis", 181, 1, "", methodObject), new Among("issis", 181, 1, "", methodObject), new Among("\u00efssis", 181, 1, "", methodObject), new Among("esquis", -1, 1, "", methodObject), new Among("eixis", -1, 1, "", methodObject), new Among("itzis", -1, 1, "", methodObject), new Among("\u00e1is", -1, 1, "", methodObject), new Among("ar\u00e9is", -1, 1, "", methodObject), new Among("er\u00e9is", -1, 1, "", methodObject), new Among("ir\u00e9is", -1, 1, "", methodObject), new Among("ams", -1, 1, "", methodObject), new Among("ados", -1, 1, "", methodObject), new Among("idos", -1, 1, "", methodObject), new Among("amos", -1, 1, "", methodObject), new Among("\u00e1bamos", 197, 1, "", methodObject), new Among("\u00e1ramos", 197, 1, "", methodObject), new Among("i\u00e9ramos", 197, 1, "", methodObject), new Among("\u00edamos", 197, 1, "", methodObject), new Among("ar\u00edamos", 201, 1, "", methodObject), new Among("er\u00edamos", 201, 1, "", methodObject), new Among("ir\u00edamos", 201, 1, "", methodObject), new Among("aremos", -1, 1, "", methodObject), new Among("eremos", -1, 1, "", methodObject), new Among("iremos", -1, 1, "", methodObject), new Among("\u00e1semos", -1, 1, "", methodObject), new Among("i\u00e9semos", -1, 1, "", methodObject), new Among("imos", -1, 1, "", methodObject), new Among("adors", -1, 1, "", methodObject), new Among("ass", -1, 1, "", methodObject), new Among("erass", 212, 1, "", methodObject), new Among("ess", -1, 1, "", methodObject), new Among("ats", -1, 1, "", methodObject), new Among("its", -1, 1, "", methodObject), new Among("ents", -1, 1, "", methodObject), new Among("\u00e0s", -1, 1, "", methodObject), new Among("ar\u00e0s", 218, 1, "", methodObject), new Among("ir\u00e0s", 218, 1, "", methodObject), new Among("ar\u00e1s", -1, 1, "", methodObject), new Among("er\u00e1s", -1, 1, "", methodObject), new Among("ir\u00e1s", -1, 1, "", methodObject), new Among("\u00e9s", -1, 1, "", methodObject), new Among("ar\u00e9s", 224, 1, "", methodObject), new Among("\u00eds", -1, 1, "", methodObject), new Among("i\u00efs", -1, 1, "", methodObject), new Among("at", -1, 1, "", methodObject), new Among("it", -1, 1, "", methodObject), new Among("ant", -1, 1, "", methodObject), new Among("ent", -1, 1, "", methodObject), new Among("int", -1, 1, "", methodObject), new Among("ut", -1, 1, "", methodObject), new Among("\u00eft", -1, 1, "", methodObject), new Among("au", -1, 1, "", methodObject), new Among("erau", 235, 1, "", methodObject), new Among("ieu", -1, 1, "", methodObject), new Among("ineu", -1, 1, "", methodObject), new Among("areu", -1, 1, "", methodObject), new Among("ireu", -1, 1, "", methodObject), new Among("\u00e0reu", -1, 1, "", methodObject), new Among("\u00edreu", -1, 1, "", methodObject), new Among("asseu", -1, 1, "", methodObject), new Among("esseu", -1, 1, "", methodObject), new Among("eresseu", 244, 1, "", methodObject), new Among("\u00e0sseu", -1, 1, "", methodObject), new Among("\u00e9sseu", -1, 1, "", methodObject), new Among("igueu", -1, 1, "", methodObject), new Among("\u00efgueu", -1, 1, "", methodObject), new Among("\u00e0veu", -1, 1, "", methodObject), new Among("\u00e1veu", -1, 1, "", methodObject), new Among("itzeu", -1, 1, "", methodObject), new Among("\u00eceu", -1, 1, "", methodObject), new Among("ir\u00eceu", 253, 1, "", methodObject), new Among("\u00edeu", -1, 1, "", methodObject), new Among("ar\u00edeu", 255, 1, "", methodObject), new Among("ir\u00edeu", 255, 1, "", methodObject), new Among("assiu", -1, 1, "", methodObject), new Among("issiu", -1, 1, "", methodObject), new Among("\u00e0ssiu", -1, 1, "", methodObject), new Among("\u00e8ssiu", -1, 1, "", methodObject), new Among("\u00e9ssiu", -1, 1, "", methodObject), new Among("\u00edssiu", -1, 1, "", methodObject), new Among("\u00efu", -1, 1, "", methodObject), new Among("ix", -1, 1, "", methodObject), new Among("eix", 265, 1, "", methodObject), new Among("\u00efx", -1, 1, "", methodObject), new Among("itz", -1, 1, "", methodObject), new Among("i\u00e0", -1, 1, "", methodObject), new Among("ar\u00e0", -1, 1, "", methodObject), new Among("ir\u00e0", -1, 1, "", methodObject), new Among("itz\u00e0", -1, 1, "", methodObject), new Among("ar\u00e1", -1, 1, "", methodObject), new Among("er\u00e1", -1, 1, "", methodObject), new Among("ir\u00e1", -1, 1, "", methodObject), new Among("ir\u00e8", -1, 1, "", methodObject), new Among("ar\u00e9", -1, 1, "", methodObject), new Among("er\u00e9", -1, 1, "", methodObject), new Among("ir\u00e9", -1, 1, "", methodObject), new Among("\u00ed", -1, 1, "", methodObject), new Among("i\u00ef", -1, 1, "", methodObject), new Among("i\u00f3", -1, 1, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("a", -1, 1, "", methodObject), new Among("e", -1, 1, "", methodObject), new Among("i", -1, 1, "", methodObject), new Among("\u00efn", -1, 1, "", methodObject), new Among("o", -1, 1, "", methodObject), new Among("ir", -1, 1, "", methodObject), new Among("s", -1, 1, "", methodObject), new Among("is", 6, 1, "", methodObject), new Among("os", 6, 1, "", methodObject), new Among("\u00efs", 6, 1, "", methodObject), new Among("it", -1, 1, "", methodObject), new Among("eu", -1, 1, "", methodObject), new Among("iu", -1, 1, "", methodObject), new Among("iqu", -1, 2, "", methodObject), new Among("itz", -1, 1, "", methodObject), new Among("\u00e0", -1, 1, "", methodObject), new Among("\u00e1", -1, 1, "", methodObject), new Among("\u00e9", -1, 1, "", methodObject), new Among("\u00ec", -1, 1, "", methodObject), new Among("\u00ed", -1, 1, "", methodObject), new Among("\u00ef", -1, 1, "", methodObject), new Among("\u00f3", -1, 1, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0080', '\u0081', 'Q', '\u0006', '\n'};
    private int I_p2;
    private int I_p1;

    private void copy_from(CatalanStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        super.copy_from(other);
    }

    private boolean r_mark_regions() {
        int v_1;
        block8: {
            this.I_p1 = this.limit;
            this.I_p2 = this.limit;
            v_1 = this.cursor;
            while (!this.in_grouping(g_v, 97, 252)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block8;
            }
            while (!this.out_grouping(g_v, 97, 252)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block8;
            }
            this.I_p1 = this.cursor;
            while (!this.in_grouping(g_v, 97, 252)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block8;
            }
            while (!this.out_grouping(g_v, 97, 252)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block8;
            }
            this.I_p2 = this.cursor;
        }
        this.cursor = v_1;
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_cleaning() {
        int v_1;
        block16: while (true) {
            v_1 = ++this.cursor;
            this.bra = this.cursor;
            int among_var = this.find_among(a_0, 13);
            if (among_var == 0) break;
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break block16;
                }
                case 1: {
                    this.slice_from("a");
                    break;
                }
                case 2: {
                    this.slice_from("a");
                    break;
                }
                case 3: {
                    this.slice_from("e");
                    break;
                }
                case 4: {
                    this.slice_from("e");
                    break;
                }
                case 5: {
                    this.slice_from("i");
                    break;
                }
                case 6: {
                    this.slice_from("i");
                    break;
                }
                case 7: {
                    this.slice_from("o");
                    break;
                }
                case 8: {
                    this.slice_from("o");
                    break;
                }
                case 9: {
                    this.slice_from("u");
                    break;
                }
                case 10: {
                    this.slice_from("u");
                    break;
                }
                case 11: {
                    this.slice_from("i");
                    break;
                }
                case 12: {
                    this.slice_from(".");
                    break;
                }
                case 13: {
                    if (this.cursor >= this.limit) break block16;
                }
            }
        }
        this.cursor = v_1;
        return true;
    }

    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }

    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }

    private boolean r_attached_pronoun() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_1, 39);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_standard_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_2, 200);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 3: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_from("log");
                break;
            }
            case 4: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_from("ic");
                break;
            }
            case 5: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("c");
            }
        }
        return true;
    }

    private boolean r_verb_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_3, 283);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_residual_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_4, 22);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("ic");
            }
        }
        return true;
    }

    @Override
    public boolean stem() {
        int v_1 = this.cursor;
        if (!this.r_mark_regions()) {
            // empty if block
        }
        this.limit_backward = this.cursor = v_1;
        this.cursor = this.limit;
        int v_2 = this.limit - this.cursor;
        if (!this.r_attached_pronoun()) {
            // empty if block
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        int v_4 = this.limit - this.cursor;
        if (!this.r_standard_suffix()) {
            this.cursor = this.limit - v_4;
            if (!this.r_verb_suffix()) {
                // empty if block
            }
        }
        this.cursor = this.limit - v_3;
        int v_5 = this.limit - this.cursor;
        if (!this.r_residual_suffix()) {
            // empty if block
        }
        this.cursor = this.limit - v_5;
        int v_6 = this.cursor = this.limit_backward;
        if (!this.r_cleaning()) {
            // empty if block
        }
        this.cursor = v_6;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof CatalanStemmer;
    }

    public int hashCode() {
        return CatalanStemmer.class.getName().hashCode();
    }
}

