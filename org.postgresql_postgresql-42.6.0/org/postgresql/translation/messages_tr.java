/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_tr
extends ResourceBundle {
    private static final String[] table;

    @Override
    public Object handleGetObject(String msgid) throws MissingResourceException {
        String found;
        int hash_val = msgid.hashCode() & Integer.MAX_VALUE;
        int idx = hash_val % 397 << 1;
        String found2 = table[idx];
        if (found2 == null) {
            return null;
        }
        if (msgid.equals(found2)) {
            return table[idx + 1];
        }
        int incr = hash_val % 395 + 1 << 1;
        do {
            if ((idx += incr) >= 794) {
                idx -= 794;
            }
            if ((found = table[idx]) != null) continue;
            return null;
        } while (!msgid.equals(found));
        return table[idx + 1];
    }

    public Enumeration getKeys() {
        return new Enumeration(){
            private int idx = 0;
            {
                while (this.idx < 794 && table[this.idx] == null) {
                    this.idx += 2;
                }
            }

            @Override
            public boolean hasMoreElements() {
                return this.idx < 794;
            }

            public Object nextElement() {
                String key = table[this.idx];
                do {
                    this.idx += 2;
                } while (this.idx < 794 && table[this.idx] == null);
                return key;
            }
        };
    }

    public ResourceBundle getParent() {
        return this.parent;
    }

    static {
        String[] t = new String[794];
        t[0] = "";
        t[1] = "Project-Id-Version: jdbc-tr\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2009-05-31 21:47+0200\nLast-Translator: Devrim G\u00dcND\u00dcZ <devrim@gunduz.org>\nLanguage-Team: Turkish <pgsql-tr-genel@PostgreSQL.org>\nLanguage: tr\nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\nX-Generator: KBabel 1.3.1\nX-Poedit-Language: Turkish\nX-Poedit-Country: TURKEY\n";
        t[2] = "Not implemented: 2nd phase commit must be issued using an idle connection. commit xid={0}, currentXid={1}, state={2}, transactionState={3}";
        t[3] = "Desteklenmiyor: 2nd phase commit, at\u0131l bir ba\u011flant\u0131dan ba\u015flat\u0131lmal\u0131d\u0131r. commit xid={0}, currentXid={1}, state={2}, transactionState={3}";
        t[4] = "DataSource has been closed.";
        t[5] = "DataSource kapat\u0131ld\u0131.";
        t[8] = "Invalid flags {0}";
        t[9] = "Ge\u00e7ersiz se\u00e7enekler {0}";
        t[18] = "Where: {0}";
        t[19] = "Where: {0}";
        t[24] = "Unknown XML Source class: {0}";
        t[25] = "Bilinmeyen XML Kaynak S\u0131n\u0131f\u0131: {0}";
        t[26] = "The connection attempt failed.";
        t[27] = "Ba\u011flant\u0131 denemesi ba\u015far\u0131s\u0131z oldu.";
        t[28] = "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.";
        t[29] = "\u015eu an ResultSet sonucundan sonra konumland\u0131. deleteRow() burada \u00e7a\u011f\u0131rabilirsiniz.";
        t[32] = "Can''t use query methods that take a query string on a PreparedStatement.";
        t[33] = "PreparedStatement ile sorgu sat\u0131r\u0131 alan sorgu y\u00f6ntemleri kullan\u0131lamaz.";
        t[36] = "Multiple ResultSets were returned by the query.";
        t[37] = "Sorgu taraf\u0131ndan birden fazla ResultSet getirildi.";
        t[50] = "Too many update results were returned.";
        t[51] = "\u00c7ok fazla g\u00fcncelleme sonucu d\u00f6nd\u00fcr\u00fcld\u00fc.";
        t[58] = "Illegal UTF-8 sequence: initial byte is {0}: {1}";
        t[59] = "Ge\u00e7ersiz UTF-8 \u00e7oklu bayt karakteri: ilk bayt {0}: {1}";
        t[66] = "The column name {0} was not found in this ResultSet.";
        t[67] = "Bu ResultSet i\u00e7inde {0} s\u00fctun ad\u0131 bulunamad\u0131.";
        t[70] = "Fastpath call {0} - No result was returned and we expected an integer.";
        t[71] = "Fastpath call {0} - Integer beklenirken hi\u00e7bir sonu\u00e7 getirilmedi.";
        t[74] = "Protocol error.  Session setup failed.";
        t[75] = "Protokol hatas\u0131.  Oturum kurulumu ba\u015far\u0131s\u0131z oldu.";
        t[76] = "A CallableStatement was declared, but no call to registerOutParameter(1, <some type>) was made.";
        t[77] = "CallableStatement bildirildi ancak registerOutParameter(1, < bir tip>) tan\u0131t\u0131m\u0131 yap\u0131lmad\u0131.";
        t[78] = "ResultSets with concurrency CONCUR_READ_ONLY cannot be updated.";
        t[79] = "E\u015f zamanlama CONCUR_READ_ONLY olan ResultSet''ler de\u011fi\u015ftirilemez";
        t[90] = "LOB positioning offsets start at 1.";
        t[91] = "LOB ba\u011flang\u0131\u00e7 adresi 1Den ba\u015fl\u0131yor";
        t[92] = "Internal Position: {0}";
        t[93] = "Internal Position: {0}";
        t[96] = "free() was called on this LOB previously";
        t[97] = "Bu LOB'da free() daha \u00f6nce \u00e7a\u011f\u0131r\u0131ld\u0131";
        t[100] = "Cannot change transaction read-only property in the middle of a transaction.";
        t[101] = "Transaction ortas\u0131nda ge\u00e7erli transactionun read-only \u00f6zell\u011fi de\u011fi\u015ftirilemez.";
        t[102] = "The JVM claims not to support the {0} encoding.";
        t[103] = "JVM, {0} dil kodlamas\u0131n\u0131 desteklememektedir.";
        t[108] = "{0} function doesn''t take any argument.";
        t[109] = "{0} fonksiyonu parametre almaz.";
        t[112] = "xid must not be null";
        t[113] = "xid null olamaz";
        t[114] = "Connection has been closed.";
        t[115] = "Ba\u011flant\u0131 kapat\u0131ld\u0131.";
        t[122] = "The server does not support SSL.";
        t[123] = "Sunucu SSL desteklemiyor.";
        t[124] = "Custom type maps are not supported.";
        t[125] = "\u00d6zel tip e\u015fle\u015ftirmeleri desteklenmiyor.";
        t[140] = "Illegal UTF-8 sequence: byte {0} of {1} byte sequence is not 10xxxxxx: {2}";
        t[141] = "Ge\u00e7ersiz UTF-8 \u00e7oklu bayt karakteri: {0}/{1} bayt\u0131 10xxxxxx de\u011fildir: {2}";
        t[148] = "Hint: {0}";
        t[149] = "\u0130pucu: {0}";
        t[152] = "Unable to find name datatype in the system catalogs.";
        t[153] = "Sistem kataloglar\u0131nda name veri tipi bulunam\u0131yor.";
        t[156] = "Unsupported Types value: {0}";
        t[157] = "Ge\u00e7ersiz Types de\u011feri: {0}";
        t[158] = "Unknown type {0}.";
        t[159] = "Bilinmeyen tip {0}.";
        t[166] = "{0} function takes two and only two arguments.";
        t[167] = "{0} fonksiyonunu sadece iki parametre alabilir.";
        t[170] = "Finalizing a Connection that was never closed:";
        t[171] = "Kapat\u0131lmam\u0131\u015f ba\u011flant\u0131 sonland\u0131r\u0131l\u0131yor.";
        t[180] = "The maximum field size must be a value greater than or equal to 0.";
        t[181] = "En b\u00fcy\u00fck alan boyutu s\u0131f\u0131r ya da s\u0131f\u0131rdan b\u00fcy\u00fck bir de\u011fer olmal\u0131.";
        t[186] = "PostgreSQL LOBs can only index to: {0}";
        t[187] = "PostgreSQL LOB g\u00f6stergeleri sadece {0} referans edebilir";
        t[194] = "Method {0} is not yet implemented.";
        t[195] = "{0} y\u00f6ntemi hen\u00fcz kodlanmad\u0131.";
        t[198] = "Error loading default settings from driverconfig.properties";
        t[199] = "driverconfig.properties dosyas\u0131ndan varsay\u0131lan ayarlar\u0131 y\u00fckleme hatas\u0131";
        t[200] = "Results cannot be retrieved from a CallableStatement before it is executed.";
        t[201] = "CallableStatement \u00e7al\u0131\u015ft\u0131r\u0131lmadan sonu\u00e7lar ondan al\u0131namaz.";
        t[202] = "Large Objects may not be used in auto-commit mode.";
        t[203] = "Auto-commit bi\u00e7imde large object kullan\u0131lamaz.";
        t[208] = "Expected command status BEGIN, got {0}.";
        t[209] = "BEGIN komut durumunu beklenirken {0} al\u0131nd\u0131.";
        t[218] = "Invalid fetch direction constant: {0}.";
        t[219] = "Getirme y\u00f6n\u00fc de\u011fi\u015fmezi ge\u00e7ersiz: {0}.";
        t[222] = "{0} function takes three and only three arguments.";
        t[223] = "{0} fonksiyonunu sadece \u00fc\u00e7 parametre alabilir.";
        t[226] = "This SQLXML object has already been freed.";
        t[227] = "Bu SQLXML nesnesi zaten bo\u015falt\u0131lm\u0131\u015f.";
        t[228] = "Cannot update the ResultSet because it is either before the start or after the end of the results.";
        t[229] = "ResultSet, sonu\u00e7lar\u0131n ilk kayd\u0131ndan \u00f6nce veya son kayd\u0131ndan sonra oldu\u011fu i\u00e7in g\u00fcncelleme yap\u0131lamamaktad\u0131r.";
        t[230] = "The JVM claims not to support the encoding: {0}";
        t[231] = "JVM, {0} dil kodlamas\u0131n\u0131 desteklememektedir.";
        t[232] = "Parameter of type {0} was registered, but call to get{1} (sqltype={2}) was made.";
        t[233] = "{0} tipinde parametre tan\u0131t\u0131ld\u0131, ancak {1} (sqltype={2}) tipinde geri getirmek i\u00e7in \u00e7a\u011fr\u0131 yap\u0131ld\u0131.";
        t[234] = "Error rolling back prepared transaction. rollback xid={0}, preparedXid={1}, currentXid={2}";
        t[235] = "Haz\u0131rlanm\u0131\u015f transaction rollback hatas\u0131. rollback xid={0}, preparedXid={1}, currentXid={2}";
        t[240] = "Cannot establish a savepoint in auto-commit mode.";
        t[241] = "Auto-commit bi\u00e7imde savepoint olu\u015fturulam\u0131yor.";
        t[242] = "Cannot retrieve the id of a named savepoint.";
        t[243] = "Adland\u0131r\u0131lm\u0131\u015f savepointin id de\u011ferine eri\u015filemiyor.";
        t[244] = "The column index is out of range: {0}, number of columns: {1}.";
        t[245] = "S\u00fctun g\u00e7stergesi kapsam d\u0131\u015f\u0131d\u0131r: {0}, s\u00fctun say\u0131s\u0131: {1}.";
        t[250] = "Something unusual has occurred to cause the driver to fail. Please report this exception.";
        t[251] = "S\u0131rad\u0131\u015f\u0131 bir durum s\u00fcr\u00fcc\u00fcn\u00fcn hata vermesine sebep oldu. L\u00fctfen bu durumu geli\u015ftiricilere bildirin.";
        t[260] = "Cannot cast an instance of {0} to type {1}";
        t[261] = "{0} tipi {1} tipine d\u00f6n\u00fc\u015ft\u00fcr\u00fclemiyor";
        t[264] = "Unknown Types value.";
        t[265] = "Ge\u00e7ersiz Types de\u011feri.";
        t[266] = "Invalid stream length {0}.";
        t[267] = "Ge\u00e7ersiz ak\u0131m uzunlu\u011fu {0}.";
        t[272] = "Cannot retrieve the name of an unnamed savepoint.";
        t[273] = "Ad\u0131 verilmemi\u015f savepointin id de\u011ferine eri\u015filemiyor.";
        t[274] = "Unable to translate data into the desired encoding.";
        t[275] = "Veri, istenilen dil kodlamas\u0131na \u00e7evrilemiyor.";
        t[276] = "Expected an EOF from server, got: {0}";
        t[277] = "Sunucudan EOF beklendi; ama {0} al\u0131nd\u0131.";
        t[278] = "Bad value for type {0} : {1}";
        t[279] = "{0} veri tipi i\u00e7in ge\u00e7ersiz de\u011fer : {1}";
        t[280] = "The server requested password-based authentication, but no password was provided.";
        t[281] = "Sunucu \u015fifre tabanl\u0131 yetkilendirme istedi; ancak bir \u015fifre sa\u011flanmad\u0131.";
        t[286] = "Unable to create SAXResult for SQLXML.";
        t[287] = "SQLXML i\u00e7in SAXResult yarat\u0131lamad\u0131.";
        t[292] = "Error during recover";
        t[293] = "Kurtarma s\u0131ras\u0131nda hata";
        t[294] = "tried to call end without corresponding start call. state={0}, start xid={1}, currentXid={2}, preparedXid={3}";
        t[295] = "start \u00e7a\u011f\u0131r\u0131m\u0131 olmadan end \u00e7a\u011f\u0131r\u0131lm\u0131\u015ft\u0131r. state={0}, start xid={1}, currentXid={2}, preparedXid={3}";
        t[296] = "Truncation of large objects is only implemented in 8.3 and later servers.";
        t[297] = "Large objectlerin temizlenmesi 8.3 ve sonraki s\u00fcr\u00fcmlerde kodlanm\u0131\u015ft\u0131r.";
        t[298] = "This PooledConnection has already been closed.";
        t[299] = "Ge\u00e7erli PooledConnection zaten \u00f6nceden kapat\u0131ld\u0131.";
        t[302] = "ClientInfo property not supported.";
        t[303] = "Clientinfo property'si desteklenememktedir.";
        t[306] = "Fetch size must be a value greater to or equal to 0.";
        t[307] = "Fetch boyutu s\u0131f\u0131r veya daha b\u00fcy\u00fck bir de\u011fer olmal\u0131d\u0131r.";
        t[312] = "A connection could not be made using the requested protocol {0}.";
        t[313] = "\u0130stenilen protokol ile ba\u011flant\u0131 kurulamad\u0131 {0}";
        t[318] = "Unknown XML Result class: {0}";
        t[319] = "Bilinmeyen XML Sonu\u00e7 s\u0131n\u0131f\u0131: {0}.";
        t[322] = "There are no rows in this ResultSet.";
        t[323] = "Bu ResultSet i\u00e7inde kay\u0131t bulunamad\u0131.";
        t[324] = "Unexpected command status: {0}.";
        t[325] = "Beklenmeyen komut durumu: {0}.";
        t[330] = "Heuristic commit/rollback not supported. forget xid={0}";
        t[331] = "Heuristic commit/rollback desteklenmiyor. forget xid={0}";
        t[334] = "Not on the insert row.";
        t[335] = "Insert kayd\u0131 de\u011fil.";
        t[336] = "This SQLXML object has already been initialized, so you cannot manipulate it further.";
        t[337] = "Bu SQLXML nesnesi daha \u00f6nceden ilklendirilmi\u015ftir; o y\u00fczden daha fazla m\u00fcdahale edilemez.";
        t[344] = "Server SQLState: {0}";
        t[345] = "Sunucu SQLState: {0}";
        t[348] = "The server''s standard_conforming_strings parameter was reported as {0}. The JDBC driver expected on or off.";
        t[349] = "\u0130stemcinin client_standard_conforming_strings parametresi {0} olarak raporland\u0131. JDBC s\u00fcr\u00fcc\u00fcs\u00fc on ya da off olarak bekliyordu.";
        t[360] = "The driver currently does not support COPY operations.";
        t[361] = "Bu sunucu \u015fu a\u015famada COPY i\u015flemleri desteklememktedir.";
        t[364] = "The array index is out of range: {0}, number of elements: {1}.";
        t[365] = "Dizin g\u00f6stergisi kapsam d\u0131\u015f\u0131d\u0131r: {0}, \u00f6\u011fe say\u0131s\u0131: {1}.";
        t[374] = "suspend/resume not implemented";
        t[375] = "suspend/resume desteklenmiyor";
        t[378] = "Not implemented: one-phase commit must be issued using the same connection that was used to start it";
        t[379] = "Desteklenmiyor: one-phase commit, i\u015flevinde ba\u015flatan ve bitiren ba\u011flant\u0131 ayn\u0131 olmal\u0131d\u0131r";
        t[380] = "Error during one-phase commit. commit xid={0}";
        t[381] = "One-phase commit s\u0131ras\u0131nda hata. commit xid={0}";
        t[398] = "Cannot call cancelRowUpdates() when on the insert row.";
        t[399] = "Insert edilmi\u015f kayd\u0131n \u00fczerindeyken cancelRowUpdates() \u00e7a\u011f\u0131r\u0131lamaz.";
        t[400] = "Cannot reference a savepoint after it has been released.";
        t[401] = "B\u0131rak\u0131ld\u0131ktan sonra savepoint referans edilemez.";
        t[402] = "You must specify at least one column value to insert a row.";
        t[403] = "Bir sat\u0131r eklemek i\u00e7in en az bir s\u00fctun de\u011ferini belirtmelisiniz.";
        t[404] = "Unable to determine a value for MaxIndexKeys due to missing system catalog data.";
        t[405] = "Sistem katalo\u011fu olmad\u0131\u011f\u0131ndan MaxIndexKeys de\u011ferini tespit edilememektedir.";
        t[410] = "commit called before end. commit xid={0}, state={1}";
        t[411] = "commit, sondan \u00f6nce \u00e7a\u011f\u0131r\u0131ld\u0131. commit xid={0}, state={1}";
        t[412] = "Illegal UTF-8 sequence: final value is out of range: {0}";
        t[413] = "Ge\u00e7ersiz UTF-8 \u00e7oklu bayt karakteri: son de\u011fer s\u0131ra d\u0131\u015f\u0131d\u0131r: {0}";
        t[414] = "{0} function takes two or three arguments.";
        t[415] = "{0} fonksiyonu yaln\u0131z iki veya \u00fc\u00e7 arg\u00fcman alabilir.";
        t[428] = "Unable to convert DOMResult SQLXML data to a string.";
        t[429] = "DOMResult SQLXML verisini diziye d\u00f6n\u00fc\u015ft\u00fcr\u00fclemedi.";
        t[434] = "Unable to decode xml data.";
        t[435] = "XML verisinin kodu \u00e7\u00f6z\u00fclemedi.";
        t[440] = "Unexpected error writing large object to database.";
        t[441] = "Large object veritaban\u0131na yaz\u0131l\u0131rken beklenmeyan hata.";
        t[442] = "Zero bytes may not occur in string parameters.";
        t[443] = "String parametrelerinde s\u0131f\u0131r bayt olamaz.";
        t[444] = "A result was returned when none was expected.";
        t[445] = "Hi\u00e7bir sonu\u00e7 kebklenimezken sonu\u00e7 getirildi.";
        t[450] = "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.";
        t[451] = "ResultSet de\u011fi\u015ftirilemez. Bu sonucu \u00fcreten sorgu tek bir tablodan sorgulamal\u0131 ve tablonun t\u00fcm primary key alanlar\u0131 belirtmelidir. Daha fazla bilgi i\u00e7in bk. JDBC 2.1 API Specification, section 5.6.";
        t[454] = "Bind message length {0} too long.  This can be caused by very large or incorrect length specifications on InputStream parameters.";
        t[455] = "Bind mesaj uzunlu\u011fu ({0}) fazla uzun. Bu durum InputStream yaln\u0131\u015f uzunluk belirtimlerden kaynaklanabilir.";
        t[460] = "Statement has been closed.";
        t[461] = "Komut kapat\u0131ld\u0131.";
        t[462] = "No value specified for parameter {0}.";
        t[463] = "{0} parametresi i\u00e7in hi\u00e7 bir de\u011fer belirtilmedi.";
        t[468] = "The array index is out of range: {0}";
        t[469] = "Dizi g\u00f6stergesi kapsam d\u0131\u015f\u0131d\u0131r: {0}";
        t[474] = "Unable to bind parameter values for statement.";
        t[475] = "Komut i\u00e7in parametre de\u011ferlei ba\u011flanamad\u0131.";
        t[476] = "Can''t refresh the insert row.";
        t[477] = "Inser sat\u0131r\u0131 yenilenemiyor.";
        t[480] = "No primary key found for table {0}.";
        t[481] = "{0} tablosunda primary key yok.";
        t[482] = "Cannot change transaction isolation level in the middle of a transaction.";
        t[483] = "Transaction ortas\u0131nda ge\u00e7erli transactionun transaction isolation level \u00f6zell\u011fi de\u011fi\u015ftirilemez.";
        t[498] = "Provided InputStream failed.";
        t[499] = "Sa\u011flanm\u0131\u015f InputStream ba\u015far\u0131s\u0131z.";
        t[500] = "The parameter index is out of range: {0}, number of parameters: {1}.";
        t[501] = "Dizin g\u00f6stergisi kapsam d\u0131\u015f\u0131d\u0131r: {0}, \u00f6\u011fe say\u0131s\u0131: {1}.";
        t[502] = "The server''s DateStyle parameter was changed to {0}. The JDBC driver requires DateStyle to begin with ISO for correct operation.";
        t[503] = "Sunucunun DateStyle parametresi {0} olarak de\u011fi\u015ftirildi. JDBC s\u00fcr\u00fcc\u00fcs\u00fc do\u011fru i\u015flemesi i\u00e7in DateStyle tan\u0131m\u0131n\u0131n ISO i\u015fle ba\u015flamas\u0131n\u0131 gerekir.";
        t[508] = "Connection attempt timed out.";
        t[509] = "Ba\u011flant\u0131 denemesi zaman a\u015f\u0131m\u0131na u\u011frad\u0131.";
        t[512] = "Internal Query: {0}";
        t[513] = "Internal Query: {0}";
        t[514] = "Error preparing transaction. prepare xid={0}";
        t[515] = "Transaction haz\u0131rlama hatas\u0131. prepare xid={0}";
        t[518] = "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.";
        t[519] = "{0} yetkinlendirme tipi desteklenmemektedir. pg_hba.conf dosyan\u0131z\u0131 istemcinin IP adresini ya da subnetini i\u00e7erecek \u015fekilde ayarlay\u0131p ayarlamad\u0131\u011f\u0131n\u0131z\u0131 ve s\u00fcr\u00fcc\u00fc taraf\u0131ndan desteklenen yetkilendirme y\u00f6ntemlerinden birisini kullan\u0131p kullanmad\u0131\u011f\u0131n\u0131 kontrol ediniz.";
        t[526] = "Interval {0} not yet implemented";
        t[527] = "{0} aral\u0131\u011f\u0131 hen\u00fcz kodlanmad\u0131.";
        t[532] = "Conversion of interval failed";
        t[533] = "Interval d\u00f6n\u00fc\u015ft\u00fcrmesi ba\u015far\u0131s\u0131z.";
        t[540] = "Query timeout must be a value greater than or equals to 0.";
        t[541] = "Sorgu zaman a\u015f\u0131m\u0131 de\u011fer s\u0131f\u0131r veya s\u0131f\u0131rdan b\u00fcy\u00fck bir say\u0131 olmal\u0131d\u0131r.";
        t[542] = "Connection has been closed automatically because a new connection was opened for the same PooledConnection or the PooledConnection has been closed.";
        t[543] = "PooledConnection kapat\u0131ld\u0131\u011f\u0131 i\u00e7in veya ayn\u0131 PooledConnection i\u00e7in yeni bir ba\u011flant\u0131 a\u00e7\u0131ld\u0131\u011f\u0131 i\u00e7in ge\u00e7erli ba\u011flant\u0131 otomatik kapat\u0131ld\u0131.";
        t[544] = "ResultSet not positioned properly, perhaps you need to call next.";
        t[545] = "ResultSet do\u011fru konumlanmam\u0131\u015ft\u0131r, next i\u015flemi \u00e7a\u011f\u0131rman\u0131z gerekir.";
        t[546] = "Prepare called before end. prepare xid={0}, state={1}";
        t[547] = "Sondan \u00f6nce prepare \u00e7a\u011f\u0131r\u0131lm\u0131\u015f. prepare xid={0}, state={1}";
        t[548] = "Invalid UUID data.";
        t[549] = "Ge\u00e7ersiz UUID verisi.";
        t[550] = "This statement has been closed.";
        t[551] = "Bu komut kapat\u0131ld\u0131.";
        t[552] = "Can''t infer the SQL type to use for an instance of {0}. Use setObject() with an explicit Types value to specify the type to use.";
        t[553] = "{0}''nin \u00f6rne\u011fi ile kullan\u0131lacak SQL tip bulunamad\u0131. Kullan\u0131lacak tip belirtmek i\u00e7in kesin Types de\u011ferleri ile setObject() kullan\u0131n.";
        t[554] = "Cannot call updateRow() when on the insert row.";
        t[555] = "Insert  kayd\u0131 \u00fczerinde updateRow() \u00e7a\u011f\u0131r\u0131lamaz.";
        t[562] = "Detail: {0}";
        t[563] = "Ayr\u0131nt\u0131: {0}";
        t[566] = "Cannot call deleteRow() when on the insert row.";
        t[567] = "Insert  kayd\u0131 \u00fczerinde deleteRow() \u00e7a\u011f\u0131r\u0131lamaz.";
        t[568] = "Currently positioned before the start of the ResultSet.  You cannot call deleteRow() here.";
        t[569] = "\u015eu an ResultSet ba\u015flangc\u0131\u0131ndan \u00f6nce konumland\u0131. deleteRow() burada \u00e7a\u011f\u0131rabilirsiniz.";
        t[576] = "Illegal UTF-8 sequence: final value is a surrogate value: {0}";
        t[577] = "Ge\u00e7ersiz UTF-8 \u00e7oklu bayt karakteri: son de\u011fer yapay bir de\u011ferdir: {0}";
        t[578] = "Unknown Response Type {0}.";
        t[579] = "Bilinmeyen yan\u0131t tipi {0}";
        t[582] = "Unsupported value for stringtype parameter: {0}";
        t[583] = "strinftype parametresi i\u00e7in destekleneyen de\u011fer: {0}";
        t[584] = "Conversion to type {0} failed: {1}.";
        t[585] = "{0} veri tipine d\u00f6n\u00fc\u015ft\u00fcrme hatas\u0131: {1}.";
        t[586] = "This SQLXML object has not been initialized, so you cannot retrieve data from it.";
        t[587] = "Bu SQLXML nesnesi ilklendirilmemi\u015f; o y\u00fczden ondan veri alamazs\u0131n\u0131z.";
        t[600] = "Unable to load the class {0} responsible for the datatype {1}";
        t[601] = "{1} veri tipinden sorumlu {0} s\u0131n\u0131f\u0131 y\u00fcklenemedi";
        t[604] = "The fastpath function {0} is unknown.";
        t[605] = "{0} fastpath fonksiyonu bilinmemektedir.";
        t[608] = "Malformed function or procedure escape syntax at offset {0}.";
        t[609] = "{0} adresinde fonksiyon veya yordamda ka\u00e7\u0131\u015f s\u00f6z dizimi ge\u00e7ersiz.";
        t[612] = "Provided Reader failed.";
        t[613] = "Sa\u011flanm\u0131\u015f InputStream ba\u015far\u0131s\u0131z.";
        t[614] = "Maximum number of rows must be a value grater than or equal to 0.";
        t[615] = "En b\u00fcy\u00fck getirilecek sat\u0131r say\u0131s\u0131 s\u0131f\u0131rdan b\u00fcy\u00fck olmal\u0131d\u0131r.";
        t[616] = "Failed to create object for: {0}.";
        t[617] = "{0} i\u00e7in nesne olu\u015fturma hatas\u0131.";
        t[620] = "Conversion of money failed.";
        t[621] = "Money d\u00f6n\u00fc\u015ft\u00fcrmesi ba\u015far\u0131s\u0131z.";
        t[622] = "Premature end of input stream, expected {0} bytes, but only read {1}.";
        t[623] = "Giri\u015f ak\u0131m\u0131nda beklenmeyen dosya sonu, {0} bayt beklenirken sadece {1} bayt al\u0131nd\u0131.";
        t[626] = "An unexpected result was returned by a query.";
        t[627] = "Sorgu beklenmeyen bir sonu\u00e7 d\u00f6nd\u00fcrd\u00fc.";
        t[644] = "Invalid protocol state requested. Attempted transaction interleaving is not supported. xid={0}, currentXid={1}, state={2}, flags={3}";
        t[645] = "Transaction interleaving desteklenmiyor. xid={0}, currentXid={1}, state={2}, flags={3}";
        t[646] = "An error occurred while setting up the SSL connection.";
        t[647] = "SSL ba\u011flant\u0131s\u0131 ayarlan\u0131rken bir hata olu\u015ftu.";
        t[654] = "Illegal UTF-8 sequence: {0} bytes used to encode a {1} byte value: {2}";
        t[655] = "Ge\u00e7ersiz UTF-8 \u00e7oklu bayt karakteri: {0} bayt, {1} bayt de\u011feri kodlamak i\u00e7in kullan\u0131lm\u0131\u015f: {2}";
        t[656] = "Not implemented: Prepare must be issued using the same connection that started the transaction. currentXid={0}, prepare xid={1}";
        t[657] = "Desteklenmiyor: Prepare, transaction ba\u015flatran ba\u011flant\u0131 taraf\u0131ndan \u00e7a\u011f\u0131rmal\u0131d\u0131r. currentXid={0}, prepare xid={1}";
        t[658] = "The SSLSocketFactory class provided {0} could not be instantiated.";
        t[659] = "SSLSocketFactory {0} ile \u00f6rneklenmedi.";
        t[662] = "Failed to convert binary xml data to encoding: {0}.";
        t[663] = "xml verisinin \u015fu dil kodlamas\u0131na \u00e7evirilmesi ba\u015far\u0131s\u0131z oldu: {0}";
        t[670] = "Position: {0}";
        t[671] = "Position: {0}";
        t[676] = "Location: File: {0}, Routine: {1}, Line: {2}";
        t[677] = "Yer: Dosya: {0}, Yordam: {1}, Sat\u0131r: {2}";
        t[684] = "Cannot tell if path is open or closed: {0}.";
        t[685] = "Path\u0131n a\u00e7\u0131k m\u0131 kapal\u0131 oldu\u011funu tespit edilemiyor: {0}.";
        t[690] = "Unable to create StAXResult for SQLXML";
        t[691] = "SQLXML i\u00e7in StAXResult yarat\u0131lamad\u0131";
        t[700] = "Cannot convert an instance of {0} to type {1}";
        t[701] = "{0} instance, {1} tipine d\u00f6n\u00fc\u015ft\u00fcr\u00fclemiyor";
        t[710] = "{0} function takes four and only four argument.";
        t[711] = "{0} fonksiyonunu yaln\u0131z d\u00f6rt parametre alabilir.";
        t[718] = "Interrupted while attempting to connect.";
        t[719] = "Ba\u011flan\u0131rken kesildi.";
        t[722] = "Your security policy has prevented the connection from being attempted.  You probably need to grant the connect java.net.SocketPermission to the database server host and port that you wish to connect to.";
        t[723] = "G\u00fcvenlik politikan\u0131z ba\u011flant\u0131n\u0131n kurulmas\u0131n\u0131 engelledi. java.net.SocketPermission'a veritaban\u0131na ve de ba\u011flanaca\u011f\u0131 porta ba\u011flant\u0131 izni vermelisiniz.";
        t[734] = "No function outputs were registered.";
        t[735] = "Hi\u00e7bir fonksiyon \u00e7\u0131kt\u0131s\u0131 kaydedilmedi.";
        t[736] = "{0} function takes one and only one argument.";
        t[737] = "{0} fonksiyonunu yaln\u0131z tek bir parametre alabilir.";
        t[744] = "This ResultSet is closed.";
        t[745] = "ResultSet kapal\u0131d\u0131r.";
        t[746] = "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.";
        t[747] = "Ge\u00e7ersiz karakterler bulunmu\u015ftur. Bunun sebebi, verilerde veritaban\u0131n destekledi\u011fi dil kodlamadaki karakterlerin d\u0131\u015f\u0131nda bir karaktere rastlamas\u0131d\u0131r. Bunun en yayg\u0131n \u00f6rne\u011fi 8 bitlik veriyi SQL_ASCII veritaban\u0131nda saklamas\u0131d\u0131r.";
        t[752] = "Error disabling autocommit";
        t[753] = "autocommit'i devre d\u0131\u015f\u0131 b\u0131rakma s\u0131ras\u0131nda hata";
        t[754] = "Ran out of memory retrieving query results.";
        t[755] = "Sorgu sonu\u00e7lar\u0131 al\u0131n\u0131rken bellek yetersiz.";
        t[756] = "Returning autogenerated keys is not supported.";
        t[757] = "Otomatik \u00fcretilen de\u011ferlerin getirilmesi desteklenememktedir.";
        t[760] = "Operation requires a scrollable ResultSet, but this ResultSet is FORWARD_ONLY.";
        t[761] = "\u0130\u015flem, kayd\u0131r\u0131labilen ResultSet gerektirir, ancak bu ResultSet FORWARD_ONLYdir.";
        t[762] = "A CallableStatement function was executed and the out parameter {0} was of type {1} however type {2} was registered.";
        t[763] = "CallableStatement \u00e7al\u0131\u015ft\u0131r\u0131ld\u0131, ancak {2} tipi kaydedilmesine ra\u011fmen d\u00f6nd\u00fcrme parametresi {0} ve tipi {1} idi.";
        t[764] = "Unable to find server array type for provided name {0}.";
        t[765] = "Belirtilen {0} ad\u0131 i\u00e7in sunucu array tipi bulunamad\u0131.";
        t[768] = "Unknown ResultSet holdability setting: {0}.";
        t[769] = "ResultSet tutabilme ayar\u0131 ge\u00e7ersiz: {0}.";
        t[772] = "Transaction isolation level {0} not supported.";
        t[773] = "Transaction isolation level {0} desteklenmiyor.";
        t[774] = "Zero bytes may not occur in identifiers.";
        t[775] = "Belirte\u00e7lerde s\u0131f\u0131r bayt olamaz.";
        t[776] = "No results were returned by the query.";
        t[777] = "Sorgudan hi\u00e7 bir sonu\u00e7 d\u00f6nmedi.";
        t[778] = "A CallableStatement was executed with nothing returned.";
        t[779] = "CallableStatement \u00e7al\u0131\u015ft\u0131rma sonucunda veri getirilmedi.";
        t[780] = "wasNull cannot be call before fetching a result.";
        t[781] = "wasNull sonu\u00e7 \u00e7ekmeden \u00f6nce \u00e7a\u011f\u0131r\u0131lamaz.";
        t[784] = "Returning autogenerated keys by column index is not supported.";
        t[785] = "Kolonlar\u0131n indexlenmesi ile otomatik olarak olu\u015fturulan anahtarlar\u0131n d\u00f6nd\u00fcr\u00fclmesi desteklenmiyor.";
        t[786] = "This statement does not declare an OUT parameter.  Use '{' ?= call ... '}' to declare one.";
        t[787] = "Bu komut OUT parametresi bildirmemektedir.  Bildirmek i\u00e7in '{' ?= call ... '}' kullan\u0131n.";
        t[788] = "Can''t use relative move methods while on the insert row.";
        t[789] = "Insert kayd\u0131 \u00fczerinde relative move method kullan\u0131lamaz.";
        t[790] = "A CallableStatement was executed with an invalid number of parameters";
        t[791] = "CallableStatement ge\u00e7ersiz say\u0131da parametre ile \u00e7al\u0131\u015ft\u0131r\u0131ld\u0131.";
        t[792] = "Connection is busy with another transaction";
        t[793] = "Ba\u011flant\u0131, ba\u015fka bir transaction taraf\u0131ndan me\u015fgul ediliyor";
        table = t;
    }
}

