/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_cs
extends ResourceBundle {
    private static final String[] table;

    @Override
    public Object handleGetObject(String msgid) throws MissingResourceException {
        String found;
        int hash_val = msgid.hashCode() & Integer.MAX_VALUE;
        int idx = hash_val % 157 << 1;
        String found2 = table[idx];
        if (found2 == null) {
            return null;
        }
        if (msgid.equals(found2)) {
            return table[idx + 1];
        }
        int incr = hash_val % 155 + 1 << 1;
        do {
            if ((idx += incr) >= 314) {
                idx -= 314;
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
                while (this.idx < 314 && table[this.idx] == null) {
                    this.idx += 2;
                }
            }

            @Override
            public boolean hasMoreElements() {
                return this.idx < 314;
            }

            public Object nextElement() {
                String key = table[this.idx];
                do {
                    this.idx += 2;
                } while (this.idx < 314 && table[this.idx] == null);
                return key;
            }
        };
    }

    public ResourceBundle getParent() {
        return this.parent;
    }

    static {
        String[] t = new String[314];
        t[0] = "";
        t[1] = "Project-Id-Version: PostgreSQL JDBC Driver 8.0\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2005-08-21 20:00+0200\nLast-Translator: Petr Dittrich <bodyn@medoro.org>\nLanguage-Team: \nLanguage: \nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\n";
        t[2] = "A connection could not be made using the requested protocol {0}.";
        t[3] = "Spojen\u00ed nelze vytvo\u0159it s pou\u017eit\u00edm \u017e\u00e1dan\u00e9ho protokolu {0}.";
        t[4] = "Malformed function or procedure escape syntax at offset {0}.";
        t[5] = "Po\u0161kozen\u00e1 funkce nebo opu\u0161t\u011bn\u00ed procedury na pozici {0}.";
        t[8] = "Cannot cast an instance of {0} to type {1}";
        t[9] = "Nemohu p\u0159etypovat instanci {0} na typ {1}";
        t[12] = "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.";
        t[13] = "ResultSet nen\u00ed aktualizavateln\u00fd. Dotaz mus\u00ed vyb\u00edrat pouze z jedn\u00e9 tabulky a mus\u00ed obsahovat v\u0161echny prim\u00e1rn\u00ed kl\u00ed\u010de tabulky. Koukni do JDBC 2.1 API Specifikace, sekce 5.6 pro v\u00edce podrobnost\u00ed.";
        t[14] = "The JVM claims not to support the {0} encoding.";
        t[15] = "JVM tvrd\u00ed, \u017ee nepodporuje kodov\u00e1n\u00ed {0}.";
        t[16] = "An I/O error occurred while sending to the backend.";
        t[17] = "Vystupn\u011b/v\u00fdstupn\u00ed chyba p\u0159i odes\u00edl\u00e1n\u00ed k backend.";
        t[18] = "Statement has been closed.";
        t[19] = "Statement byl uzav\u0159en.";
        t[20] = "Unknown Types value.";
        t[21] = "Nezn\u00e1m\u00e1 hodnota typu.";
        t[22] = "ResultSets with concurrency CONCUR_READ_ONLY cannot be updated.";
        t[23] = "ResultSets se soub\u011b\u017enost\u00ed CONCUR_READ_ONLY nem\u016f\u017ee b\u00fdt aktualizov\u00e1no";
        t[26] = "You must specify at least one column value to insert a row.";
        t[27] = "Mus\u00edte vyplnit alespo\u0148 jeden sloupec pro vlo\u017een\u00ed \u0159\u00e1dku.";
        t[32] = "No primary key found for table {0}.";
        t[33] = "Nenalezen prim\u00e1rn\u00ed kl\u00ed\u010d pro tabulku {0}.";
        t[34] = "Cannot establish a savepoint in auto-commit mode.";
        t[35] = "Nemohu vytvo\u0159it savepoint v auto-commit modu.";
        t[38] = "Can''t use relative move methods while on the insert row.";
        t[39] = "Nem\u016f\u017eete pou\u017e\u00edvat relativn\u00ed p\u0159esuny p\u0159i vkl\u00e1d\u00e1n\u00ed \u0159\u00e1dku.";
        t[44] = "The column name {0} was not found in this ResultSet.";
        t[45] = "Sloupec pojmenovan\u00fd {0} nebyl nalezen v ResultSet.";
        t[46] = "This statement has been closed.";
        t[47] = "P\u0159\u00edkaz byl uzav\u0159en.";
        t[48] = "The SSLSocketFactory class provided {0} could not be instantiated.";
        t[49] = "T\u0159\u00edda SSLSocketFactory poskytla {0} co\u017e nem\u016f\u017ee b\u00fdt instancionizov\u00e1no.";
        t[50] = "Multiple ResultSets were returned by the query.";
        t[51] = "V\u00edcen\u00e1sobn\u00fd ResultSet byl vr\u00e1cen dotazem.";
        t[52] = "DataSource has been closed.";
        t[53] = "DataSource byl uzav\u0159en.";
        t[56] = "Error loading default settings from driverconfig.properties";
        t[57] = "Chyba na\u010d\u00edt\u00e1n\u00ed standardn\u00edho nastaven\u00ed z driverconfig.properties";
        t[62] = "Bad value for type {0} : {1}";
        t[63] = "\u0160patn\u00e1 hodnota pro typ {0} : {1}";
        t[66] = "Method {0} is not yet implemented.";
        t[67] = "Metoda {0} nen\u00ed implementov\u00e1na.";
        t[68] = "The array index is out of range: {0}";
        t[69] = "Index pole mimo rozsah: {0}";
        t[70] = "Unexpected command status: {0}.";
        t[71] = "Neo\u010dek\u00e1van\u00fd stav p\u0159\u00edkazu: {0}.";
        t[74] = "Expected command status BEGIN, got {0}.";
        t[75] = "O\u010dek\u00e1v\u00e1n p\u0159\u00edkaz BEGIN, obdr\u017een {0}.";
        t[76] = "Cannot retrieve the id of a named savepoint.";
        t[77] = "Nemohu z\u00edskat id nepojmenovan\u00e9ho savepointu.";
        t[78] = "Unexpected error writing large object to database.";
        t[79] = "Neo\u010dek\u00e1van\u00e1 chyba p\u0159i zapisov\u00e1n\u00ed velk\u00e9ho objektu do datab\u00e1ze.";
        t[84] = "Not on the insert row.";
        t[85] = "Ne na vkl\u00e1dan\u00e9m \u0159\u00e1dku.";
        t[86] = "Returning autogenerated keys is not supported.";
        t[87] = "Vr\u00e1cen\u00ed automaticky generovan\u00fdch kl\u00ed\u010d\u016f nen\u00ed podporov\u00e1no.";
        t[88] = "The server requested password-based authentication, but no password was provided.";
        t[89] = "Server vy\u017eaduje ov\u011b\u0159en\u00ed heslem, ale \u017e\u00e1dn\u00e9 nebylo posl\u00e1no.";
        t[98] = "Unable to load the class {0} responsible for the datatype {1}";
        t[99] = "Nemohu na\u010d\u00edst t\u0159\u00eddu {0} odpov\u011bdnou za typ {1}";
        t[100] = "Invalid fetch direction constant: {0}.";
        t[101] = "\u0160patn\u00fd sm\u011br \u010dten\u00ed: {0}.";
        t[102] = "Conversion of money failed.";
        t[103] = "P\u0159evod pen\u011bz selhal.";
        t[104] = "Connection has been closed.";
        t[105] = "Spojeni bylo uzav\u0159eno.";
        t[106] = "Cannot retrieve the name of an unnamed savepoint.";
        t[107] = "Nemohu z\u00edskat n\u00e1zev nepojmenovan\u00e9ho savepointu.";
        t[108] = "Large Objects may not be used in auto-commit mode.";
        t[109] = "Velk\u00e9 objecky nemohou b\u00fdt pou\u017eity v auto-commit modu.";
        t[110] = "This ResultSet is closed.";
        t[111] = "Tento ResultSet je uzav\u0159en\u00fd.";
        t[116] = "Something unusual has occurred to cause the driver to fail. Please report this exception.";
        t[117] = "N\u011bco neobvykl\u00e9ho p\u0159inutilo ovlada\u010d selhat. Pros\u00edm nahlaste tuto vyj\u00edmku.";
        t[118] = "The server does not support SSL.";
        t[119] = "Server nepodporuje SSL.";
        t[120] = "Invalid stream length {0}.";
        t[121] = "Vadn\u00e1 d\u00e9lka proudu {0}.";
        t[126] = "The maximum field size must be a value greater than or equal to 0.";
        t[127] = "Maxim\u00e1ln\u00ed velikost pole mus\u00ed b\u00fdt nez\u00e1porn\u00e9 \u010d\u00edslo.";
        t[130] = "Cannot call updateRow() when on the insert row.";
        t[131] = "Nemohu volat updateRow() na vlk\u00e1dan\u00e9m \u0159\u00e1dku.";
        t[132] = "A CallableStatement was executed with nothing returned.";
        t[133] = "CallableStatement byl spu\u0161t\u011bn, le\u010d nic nebylo vr\u00e1ceno.";
        t[134] = "Provided Reader failed.";
        t[135] = "Selhal poskytnut\u00fd Reader.";
        t[146] = "Cannot call deleteRow() when on the insert row.";
        t[147] = "Nem\u016f\u017eete volat deleteRow() p\u0159i vkl\u00e1d\u00e1n\u00ed \u0159\u00e1dku.";
        t[156] = "Where: {0}";
        t[157] = "Kde: {0}";
        t[158] = "An unexpected result was returned by a query.";
        t[159] = "Obdr\u017een neo\u010dek\u00e1van\u00fd v\u00fdsledek dotazu.";
        t[160] = "The connection attempt failed.";
        t[161] = "Pokus o p\u0159ipojen\u00ed selhal.";
        t[162] = "Too many update results were returned.";
        t[163] = "Bylo vr\u00e1ceno p\u0159\u00edli\u0161 mnoho v\u00fdsledk\u016f aktualizac\u00ed.";
        t[164] = "Unknown type {0}.";
        t[165] = "Nezn\u00e1m\u00fd typ {0}.";
        t[166] = "{0} function takes two and only two arguments.";
        t[167] = "Funkce {0} bere pr\u00e1v\u011b dva argumenty.";
        t[168] = "{0} function doesn''t take any argument.";
        t[169] = "Funkce {0} nebere \u017e\u00e1dn\u00fd argument.";
        t[172] = "Unable to find name datatype in the system catalogs.";
        t[173] = "Nemohu naj\u00edt n\u00e1zev typu v syst\u00e9mov\u00e9m katalogu.";
        t[174] = "Protocol error.  Session setup failed.";
        t[175] = "Chyba protokolu. Nastaven\u00ed relace selhalo.";
        t[176] = "{0} function takes one and only one argument.";
        t[177] = "Funkce {0} bere jeden argument.";
        t[186] = "The driver currently does not support COPY operations.";
        t[187] = "Ovlada\u010d nyn\u00ed nepodporuje p\u0159\u00edkaz COPY.";
        t[190] = "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.";
        t[191] = "Nalezena vada ve znakov\u00fdch datech. Toto m\u016f\u017ee b\u00fdt zp\u016fsobeno ulo\u017een\u00fdmi daty obsahuj\u00edc\u00edmi znaky, kter\u00e9 jsou z\u00e1vadn\u00e9 pro znakovou sadu nastavenou p\u0159i zakl\u00e1d\u00e1n\u00ed datab\u00e1ze. Nejzn\u00e1mej\u0161\u00ed p\u0159\u00edklad je ukl\u00e1d\u00e1n\u00ed 8bitov\u00fdch dat vSQL_ASCII datab\u00e1zi.";
        t[196] = "Fetch size must be a value greater to or equal to 0.";
        t[197] = "Nabran\u00e1 velikost mus\u00ed b\u00fdt nez\u00e1porn\u00e1.";
        t[204] = "Unsupported Types value: {0}";
        t[205] = "Nepodporovan\u00e1 hodnota typu: {0}";
        t[206] = "Can''t refresh the insert row.";
        t[207] = "Nemohu obnovit vkl\u00e1dan\u00fd \u0159\u00e1dek.";
        t[210] = "Maximum number of rows must be a value grater than or equal to 0.";
        t[211] = "Maxim\u00e1ln\u00ed po\u010det \u0159\u00e1dek mus\u00ed b\u00fdt nez\u00e1porn\u00e9 \u010d\u00edslo.";
        t[216] = "No value specified for parameter {0}.";
        t[217] = "Nespecifikov\u00e1na hodnota parametru {0}.";
        t[218] = "The array index is out of range: {0}, number of elements: {1}.";
        t[219] = "Index pole mimo rozsah: {0}, po\u010det prvk\u016f: {1}.";
        t[220] = "Provided InputStream failed.";
        t[221] = "Selhal poskytnut\u00fd InputStream.";
        t[228] = "Cannot reference a savepoint after it has been released.";
        t[229] = "Nemohu z\u00edskat odkaz na savepoint, kdy\u017e byl uvoln\u011bn.";
        t[232] = "An error occurred while setting up the SSL connection.";
        t[233] = "Nastala chyba p\u0159i nastaven\u00ed SSL spojen\u00ed.";
        t[246] = "Detail: {0}";
        t[247] = "Detail: {0}";
        t[248] = "This PooledConnection has already been closed.";
        t[249] = "Tento PooledConnection byl uzav\u0159en.";
        t[250] = "A result was returned when none was expected.";
        t[251] = "Obdr\u017een v\u00fdsledek, ikdy\u017e \u017e\u00e1dn\u00fd nebyl o\u010dek\u00e1v\u00e1n.";
        t[254] = "The JVM claims not to support the encoding: {0}";
        t[255] = "JVM tvrd\u00ed, \u017ee nepodporuje kodov\u00e1n\u00ed: {0}";
        t[256] = "The parameter index is out of range: {0}, number of parameters: {1}.";
        t[257] = "Index parametru mimo rozsah: {0}, po\u010det parametr\u016f {1}.";
        t[258] = "LOB positioning offsets start at 1.";
        t[259] = "Za\u010d\u00e1tek pozicov\u00e1n\u00ed LOB za\u010d\u00edna na 1.";
        t[260] = "{0} function takes two or three arguments.";
        t[261] = "Funkce {0} bere dva nebo t\u0159i argumenty.";
        t[262] = "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.";
        t[263] = "Pr\u00e1v\u011b jste za pozic\u00ed konce ResultSetu. Zde nem\u016f\u017eete volat deleteRow().s";
        t[266] = "Server SQLState: {0}";
        t[267] = "Server SQLState: {0}";
        t[270] = "{0} function takes four and only four argument.";
        t[271] = "Funkce {0} bere p\u0159esn\u011b \u010dty\u0159i argumenty.";
        t[272] = "Failed to create object for: {0}.";
        t[273] = "Selhalo vytvo\u0159en\u00ed objektu: {0}.";
        t[274] = "No results were returned by the query.";
        t[275] = "Neobdr\u017een \u017e\u00e1dn\u00fd v\u00fdsledek dotazu.";
        t[276] = "Position: {0}";
        t[277] = "Pozice: {0}";
        t[278] = "The column index is out of range: {0}, number of columns: {1}.";
        t[279] = "Index sloupece je mimo rozsah: {0}, po\u010det sloupc\u016f: {1}.";
        t[280] = "Unknown Response Type {0}.";
        t[281] = "Nezn\u00e1m\u00fd typ odpov\u011bdi {0}.";
        t[284] = "Hint: {0}";
        t[285] = "Rada: {0}";
        t[286] = "Location: File: {0}, Routine: {1}, Line: {2}";
        t[287] = "Poloha: Soubor: {0}, Rutina: {1}, \u0158\u00e1dek: {2}";
        t[288] = "Query timeout must be a value greater than or equals to 0.";
        t[289] = "\u010casov\u00fd limit dotazu mus\u00ed b\u00fdt nez\u00e1porn\u00e9 \u010d\u00edslo.";
        t[292] = "Unable to translate data into the desired encoding.";
        t[293] = "Nemohu p\u0159elo\u017eit data do po\u017eadovan\u00e9ho k\u00f3dov\u00e1n\u00ed.";
        t[296] = "Cannot call cancelRowUpdates() when on the insert row.";
        t[297] = "Nem\u016f\u017eete volat cancelRowUpdates() p\u0159i vkl\u00e1d\u00e1n\u00ed \u0159\u00e1dku.";
        t[298] = "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.";
        t[299] = "Ov\u011b\u0159en\u00ed typu {0} nen\u00ed podporov\u00e1no. Zkontrolujte zda konfigura\u010dn\u00ed soubor pg_hba.conf obsahuje klientskou IP adresu \u010di pods\u00ed\u0165 a zda je pou\u017eit\u00e9 ov\u011b\u0159enovac\u00ed sch\u00e9ma podporov\u00e1no ovlada\u010dem.";
        t[308] = "There are no rows in this ResultSet.";
        t[309] = "\u017d\u00e1dn\u00fd \u0159\u00e1dek v ResultSet.";
        table = t;
    }
}

