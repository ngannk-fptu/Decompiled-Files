/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_sr
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
        t[1] = "Project-Id-Version: PostgreSQL 8.1\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2009-05-26 11:13+0100\nLast-Translator: Bojan \u0160kaljac <skaljac (at) gmail.com>\nLanguage-Team: Srpski <skaljac@gmail.com>\nLanguage: \nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\nX-Poedit-Language: Serbian\nX-Poedit-Country: YUGOSLAVIA\n";
        t[2] = "Not implemented: 2nd phase commit must be issued using an idle connection. commit xid={0}, currentXid={1}, state={2}, transactionState={3}";
        t[3] = "Nije implementirano: Dvofazni commit mora biti izdat uz kori\u0161tenje besposlene konekcije. commit xid={0}, currentXid={1}, state={2}, transactionState={3}";
        t[4] = "DataSource has been closed.";
        t[5] = "DataSource je zatvoren.";
        t[8] = "Invalid flags {0}";
        t[9] = "Neva\u017ee\u0107e zastavice {0}";
        t[18] = "Where: {0}";
        t[19] = "Gde: {0}";
        t[24] = "Unknown XML Source class: {0}";
        t[25] = "Nepoznata XML ulazna klasa: {0}";
        t[26] = "The connection attempt failed.";
        t[27] = "Poku\u0161aj konektovanja propao.";
        t[28] = "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.";
        t[29] = "Trenutna pozicija posle kraja ResultSet-a.  Ne mo\u017eete pozvati deleteRow() na toj poziciji.";
        t[32] = "Can''t use query methods that take a query string on a PreparedStatement.";
        t[33] = "Ne mo\u017eete da koristite metode za upit koji uzimaju string iz upita u PreparedStatement-u.";
        t[36] = "Multiple ResultSets were returned by the query.";
        t[37] = "Vi\u0161estruki ResultSet-vi su vra\u0107eni od strane upita.";
        t[50] = "Too many update results were returned.";
        t[51] = "Previ\u0161e rezultata za a\u017euriranje je vra\u0107eno.";
        t[58] = "Illegal UTF-8 sequence: initial byte is {0}: {1}";
        t[59] = "Ilegalna UTF-8 sekvenca: inicijalni bajt je {0}: {1}";
        t[66] = "The column name {0} was not found in this ResultSet.";
        t[67] = "Ime kolone {0} nije pronadjeno u ResultSet.";
        t[70] = "Fastpath call {0} - No result was returned and we expected an integer.";
        t[71] = "Fastpath poziv {0} - Nikakav rezultat nije vra\u0107en a o\u010dekivan je integer.";
        t[74] = "Protocol error.  Session setup failed.";
        t[75] = "Gre\u0161ka protokola.  Zakazivanje sesije propalo.";
        t[76] = "A CallableStatement was declared, but no call to registerOutParameter(1, <some type>) was made.";
        t[77] = "CallableStatement jedeklarisan ali nije bilo poziva registerOutParameter (1, <neki_tip>).";
        t[78] = "ResultSets with concurrency CONCUR_READ_ONLY cannot be updated.";
        t[79] = "ResultSets sa osobinom CONCUR_READ_ONLY ne moe\u017ee biti a\u017euriran.";
        t[90] = "LOB positioning offsets start at 1.";
        t[91] = "LOB pozicija ofset po\u010dinje kod 1.";
        t[92] = "Internal Position: {0}";
        t[93] = "Interna pozicija: {0}";
        t[96] = "free() was called on this LOB previously";
        t[97] = "free() je pozvan na ovom LOB-u prethodno";
        t[100] = "Cannot change transaction read-only property in the middle of a transaction.";
        t[101] = "Nije mogu\u0107e izmeniti read-only osobinu transakcije u sred izvr\u0161avanja transakcije.";
        t[102] = "The JVM claims not to support the {0} encoding.";
        t[103] = "JVM tvrdi da ne podr\u017eava {0} encoding.";
        t[108] = "{0} function doesn''t take any argument.";
        t[109] = "Funkcija {0} nema parametara.";
        t[112] = "xid must not be null";
        t[113] = "xid ne sme biti null";
        t[114] = "Connection has been closed.";
        t[115] = "Konekcija je ve\u0107 zatvorena.";
        t[122] = "The server does not support SSL.";
        t[123] = "Server ne podr\u017eava SSL.";
        t[124] = "Custom type maps are not supported.";
        t[125] = "Mape sa korisni\u010dki definisanim tipovima nisu podr\u017eane.";
        t[140] = "Illegal UTF-8 sequence: byte {0} of {1} byte sequence is not 10xxxxxx: {2}";
        t[141] = "Ilegalna UTF-8 sekvenca: bajt {0} od {1} bajtova sekvence nije 10xxxxxx: {2}";
        t[148] = "Hint: {0}";
        t[149] = "Nagovest: {0}";
        t[152] = "Unable to find name datatype in the system catalogs.";
        t[153] = "Nije mogu\u0107e prona\u0107i ime tipa podatka u sistemskom katalogu.";
        t[156] = "Unsupported Types value: {0}";
        t[157] = "Za tip nije podr\u017eana vrednost: {0}";
        t[158] = "Unknown type {0}.";
        t[159] = "Nepoznat tip {0}.";
        t[166] = "{0} function takes two and only two arguments.";
        t[167] = "Funkcija {0} prima dva i samo dva parametra.";
        t[170] = "Finalizing a Connection that was never closed:";
        t[171] = "Dovr\u0161avanje konekcije koja nikada nije zatvorena:";
        t[180] = "The maximum field size must be a value greater than or equal to 0.";
        t[181] = "Maksimalna vrednost veli\u010dine polja mora biti vrednost ve\u0107a ili jednaka 0.";
        t[186] = "PostgreSQL LOBs can only index to: {0}";
        t[187] = "PostgreSQL LOB mogu jedino da ozna\u010davaju: {0}";
        t[194] = "Method {0} is not yet implemented.";
        t[195] = "Metod {0} nije jo\u0161 impelemtiran.";
        t[198] = "Error loading default settings from driverconfig.properties";
        t[199] = "Gre\u0161ka u \u010ditanju standardnih pode\u0161avanja iz driverconfig.properties";
        t[200] = "Results cannot be retrieved from a CallableStatement before it is executed.";
        t[201] = "Razultat nemo\u017ee da se primi iz CallableStatement pre nego \u0161to se on izvr\u0161i.";
        t[202] = "Large Objects may not be used in auto-commit mode.";
        t[203] = "Veliki objekti (Large Object) se nemogu koristiti u auto-commit modu.";
        t[208] = "Expected command status BEGIN, got {0}.";
        t[209] = "O\u010dekivan status komande je BEGIN, a dobijeno je {0}.";
        t[218] = "Invalid fetch direction constant: {0}.";
        t[219] = "Pogre\u0161na konstanta za direkciju dono\u0161enja: {0}.";
        t[222] = "{0} function takes three and only three arguments.";
        t[223] = "Funkcija {0} prima tri i samo tri parametra.";
        t[226] = "This SQLXML object has already been freed.";
        t[227] = "Ovaj SQLXML je ve\u0107 obrisan.";
        t[228] = "Cannot update the ResultSet because it is either before the start or after the end of the results.";
        t[229] = "Nije mogu\u0107e a\u017eurirati ResultSet zato \u0161to je ili po\u010detak ili kraj rezultata.";
        t[230] = "The JVM claims not to support the encoding: {0}";
        t[231] = "JVM tvrdi da ne podr\u017eava encoding: {0}";
        t[232] = "Parameter of type {0} was registered, but call to get{1} (sqltype={2}) was made.";
        t[233] = "Parametar tipa {0} je registrovan,ali poziv za get{1} (sql tip={2}) je izvr\u0161en.";
        t[234] = "Error rolling back prepared transaction. rollback xid={0}, preparedXid={1}, currentXid={2}";
        t[235] = "Gre\u0161ka prilikom povratka na prethodo pripremljenu transakciju. rollback xid={0}, preparedXid={1}, currentXid={2}";
        t[240] = "Cannot establish a savepoint in auto-commit mode.";
        t[241] = "U auto-commit modu nije mogu\u0107e pode\u0161avanje ta\u010dki snimanja.";
        t[242] = "Cannot retrieve the id of a named savepoint.";
        t[243] = "Nije mogu\u0107e primiti id imena ta\u010dke snimanja.";
        t[244] = "The column index is out of range: {0}, number of columns: {1}.";
        t[245] = "Indeks kolone van osega: {0}, broj kolona: {1}.";
        t[250] = "Something unusual has occurred to cause the driver to fail. Please report this exception.";
        t[251] = "Ne\u0161to neobi\u010dno se dogodilo i drajver je zakazao. Molim prijavite ovaj izuzetak.";
        t[260] = "Cannot cast an instance of {0} to type {1}";
        t[261] = "Nije mogu\u0107e kastovati instancu {0} u tip {1}";
        t[264] = "Unknown Types value.";
        t[265] = "Nepoznata vrednost za Types.";
        t[266] = "Invalid stream length {0}.";
        t[267] = "Neva\u017ee\u0107a du\u017eina toka {0}.";
        t[272] = "Cannot retrieve the name of an unnamed savepoint.";
        t[273] = "Nije mogu\u0107e izvaditi ime ta\u010dke snimanja koja nema ime.";
        t[274] = "Unable to translate data into the desired encoding.";
        t[275] = "Nije mogu\u0107e prevesti podatke u odabrani encoding format.";
        t[276] = "Expected an EOF from server, got: {0}";
        t[277] = "O\u010dekivan EOF od servera, a dobijeno: {0}";
        t[278] = "Bad value for type {0} : {1}";
        t[279] = "Pogre\u0161na vrednost za tip {0} : {1}";
        t[280] = "The server requested password-based authentication, but no password was provided.";
        t[281] = "Server zahteva autentifikaciju baziranu na \u0161ifri, ali \u0161ifra nije prosle\u0111ena.";
        t[286] = "Unable to create SAXResult for SQLXML.";
        t[287] = "Nije mogu\u0107e kreirati SAXResult za SQLXML.";
        t[292] = "Error during recover";
        t[293] = "Gre\u0161ka prilikom oporavljanja.";
        t[294] = "tried to call end without corresponding start call. state={0}, start xid={1}, currentXid={2}, preparedXid={3}";
        t[295] = "Poku\u0161aj pozivanja kraja pre odgovaraju\u0107eg po\u010detka. state={0}, start xid={1}, currentXid={2}, preparedXid={3}";
        t[296] = "Truncation of large objects is only implemented in 8.3 and later servers.";
        t[297] = "Skra\u0107ivanje velikih objekata je implementirano samo u 8.3 i novijim serverima.";
        t[298] = "This PooledConnection has already been closed.";
        t[299] = "PooledConnection je ve\u0107 zatvoren.";
        t[302] = "ClientInfo property not supported.";
        t[303] = "ClientInfo property nije podr\u017ean.";
        t[306] = "Fetch size must be a value greater to or equal to 0.";
        t[307] = "Doneta veli\u010dina mora biti vrednost ve\u0107a ili jednaka 0.";
        t[312] = "A connection could not be made using the requested protocol {0}.";
        t[313] = "Konekciju nije mogu\u0107e kreirati uz pomo\u0107 protokola {0}.";
        t[318] = "Unknown XML Result class: {0}";
        t[319] = "nepoznata XML klasa rezultata: {0}";
        t[322] = "There are no rows in this ResultSet.";
        t[323] = "U ResultSet-u nema redova.";
        t[324] = "Unexpected command status: {0}.";
        t[325] = "Neo\u010dekivan komandni status: {0}.";
        t[330] = "Heuristic commit/rollback not supported. forget xid={0}";
        t[331] = "Heuristi\u010dki commit/rollback nije podr\u017ean. forget xid={0}";
        t[334] = "Not on the insert row.";
        t[335] = "Nije mod ubacivanja redova.";
        t[336] = "This SQLXML object has already been initialized, so you cannot manipulate it further.";
        t[337] = "SQLXML objekat je ve\u0107 inicijalizovan, tako da ga nije mogu\u0107e dodatno menjati.";
        t[344] = "Server SQLState: {0}";
        t[345] = "SQLState servera: {0}";
        t[348] = "The server''s standard_conforming_strings parameter was reported as {0}. The JDBC driver expected on or off.";
        t[349] = "Serverov standard_conforming_strings parametar javlja {0}. JDBC drajver ocekuje on ili off.";
        t[360] = "The driver currently does not support COPY operations.";
        t[361] = "Drajver trenutno ne podr\u017eava COPY operacije.";
        t[364] = "The array index is out of range: {0}, number of elements: {1}.";
        t[365] = "Indeks niza je van opsega: {0}, broj elemenata: {1}.";
        t[374] = "suspend/resume not implemented";
        t[375] = "obustavljanje/nastavljanje nije implementirano.";
        t[378] = "Not implemented: one-phase commit must be issued using the same connection that was used to start it";
        t[379] = "Nije implementirano: Commit iz jedne faze mora biti izdat uz kori\u0161tenje iste konekcije koja je kori\u0161tena za startovanje.";
        t[380] = "Error during one-phase commit. commit xid={0}";
        t[381] = "Kre\u0161ka prilikom commit-a iz jedne faze. commit xid={0}";
        t[398] = "Cannot call cancelRowUpdates() when on the insert row.";
        t[399] = "Nije mogu\u0107e pozvati cancelRowUpdates() prilikom ubacivanja redova.";
        t[400] = "Cannot reference a savepoint after it has been released.";
        t[401] = "Nije mogu\u0107e referenciranje ta\u010dke snimanja nakon njenog osloba\u0111anja.";
        t[402] = "You must specify at least one column value to insert a row.";
        t[403] = "Morate specificirati barem jednu vrednost za kolonu da bi ste ubacili red.";
        t[404] = "Unable to determine a value for MaxIndexKeys due to missing system catalog data.";
        t[405] = "Nije mogu\u0107e odrediti vrednost za MaxIndexKezs zbog nedostatka podataka u sistemskom katalogu.";
        t[412] = "Illegal UTF-8 sequence: final value is out of range: {0}";
        t[413] = "Ilegalna UTF-8 sekvenca: finalna vrednost je van opsega: {0}";
        t[414] = "{0} function takes two or three arguments.";
        t[415] = "Funkcija {0} prima dva ili tri parametra.";
        t[428] = "Unable to convert DOMResult SQLXML data to a string.";
        t[429] = "Nije mogu\u0107e konvertovati DOMResult SQLXML podatke u string.";
        t[434] = "Unable to decode xml data.";
        t[435] = "Neuspe\u0161no dekodiranje XML podataka.";
        t[440] = "Unexpected error writing large object to database.";
        t[441] = "Neo\u010dekivana gre\u0161ka prilikom upisa velikog objekta u bazu podataka.";
        t[442] = "Zero bytes may not occur in string parameters.";
        t[443] = "Nula bajtovji se ne smeju pojavljivati u string parametrima.";
        t[444] = "A result was returned when none was expected.";
        t[445] = "Rezultat vra\u0107en ali nikakav rezultat nije o\u010dekivan.";
        t[450] = "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.";
        t[451] = "ResultSet nije mogu\u0107e a\u017eurirati. Upit koji je generisao ovaj razultat mora selektoati jedino tabelu,i mora selektovati sve primrne klju\u010deve iz te tabele. Pogledajte API specifikaciju za JDBC 2.1, sekciju 5.6 za vi\u0161e detalja.";
        t[454] = "Bind message length {0} too long.  This can be caused by very large or incorrect length specifications on InputStream parameters.";
        t[455] = "Du\u017eina vezivne poruke {0} prevelika.  Ovo je mo\u017eda rezultat veoma velike ili pogre\u0161ne du\u017eine specifikacije za InputStream parametre.";
        t[460] = "Statement has been closed.";
        t[461] = "Statemen je ve\u0107 zatvoren.";
        t[462] = "No value specified for parameter {0}.";
        t[463] = "Nije zadata vrednost za parametar {0}.";
        t[468] = "The array index is out of range: {0}";
        t[469] = "Indeks niza je van opsega: {0}";
        t[474] = "Unable to bind parameter values for statement.";
        t[475] = "Nije mogu\u0107e na\u0107i vrednost vezivnog parametra za izjavu (statement).";
        t[476] = "Can''t refresh the insert row.";
        t[477] = "Nije mogu\u0107e osve\u017eiti uba\u010deni red.";
        t[480] = "No primary key found for table {0}.";
        t[481] = "Nije prona\u0111en klju\u010d za tabelu {0}.";
        t[482] = "Cannot change transaction isolation level in the middle of a transaction.";
        t[483] = "Nije mogu\u0107e izmeniti nivo izolacije transakcije u sred izvr\u0161avanja transakcije.";
        t[498] = "Provided InputStream failed.";
        t[499] = "Pribaljeni InputStream zakazao.";
        t[500] = "The parameter index is out of range: {0}, number of parameters: {1}.";
        t[501] = "Index parametra je van opsega: {0}, broj parametara je: {1}.";
        t[502] = "The server''s DateStyle parameter was changed to {0}. The JDBC driver requires DateStyle to begin with ISO for correct operation.";
        t[503] = "Serverov DataStyle parametar promenjen u {0}. JDBC zahteva da DateStyle po\u010dinje sa ISO za uspe\u0161no zavr\u0161avanje operacije.";
        t[508] = "Connection attempt timed out.";
        t[509] = "Isteklo je vreme za poku\u0161aj konektovanja.";
        t[512] = "Internal Query: {0}";
        t[513] = "Interni upit: {0}";
        t[514] = "Error preparing transaction. prepare xid={0}";
        t[515] = "Gre\u0161ka u pripremanju transakcije. prepare xid={0}";
        t[518] = "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.";
        t[519] = "Tip autentifikacije {0} nije podr\u017ean. Proverite dali imate pode\u0161en pg_hba.conf fajl koji uklju\u010duje klijentovu IP adresu ili podmre\u017eu, i da ta mre\u017ea koristi \u0161emu autentifikacije koja je podr\u017eana od strane ovog drajvera.";
        t[526] = "Interval {0} not yet implemented";
        t[527] = "Interval {0} jo\u0161 nije implementiran.";
        t[532] = "Conversion of interval failed";
        t[533] = "Konverzija intervala propala.";
        t[540] = "Query timeout must be a value greater than or equals to 0.";
        t[541] = "Tajm-aut mora biti vrednost ve\u0107a ili jednaka 0.";
        t[542] = "Connection has been closed automatically because a new connection was opened for the same PooledConnection or the PooledConnection has been closed.";
        t[543] = "Konekcija je zatvorena automatski zato \u0161to je nova konekcija otvorena za isti PooledConnection ili je PooledConnection zatvoren.";
        t[544] = "ResultSet not positioned properly, perhaps you need to call next.";
        t[545] = "ResultSet nije pravilno pozicioniran, mo\u017eda je potrebno da pozovete next.";
        t[546] = "Prepare called before end. prepare xid={0}, state={1}";
        t[547] = "Pripremanje poziva pre kraja. prepare xid={0}, state={1}";
        t[548] = "Invalid UUID data.";
        t[549] = "Neva\u017ee\u0107a UUID podatak.";
        t[550] = "This statement has been closed.";
        t[551] = "Statement je zatvoren.";
        t[552] = "Can''t infer the SQL type to use for an instance of {0}. Use setObject() with an explicit Types value to specify the type to use.";
        t[553] = "Nije mogu\u0107e zaklju\u010diti SQL tip koji bi se koristio sa instancom {0}. Koristite setObject() sa zadatim eksplicitnim tipom vrednosti.";
        t[554] = "Cannot call updateRow() when on the insert row.";
        t[555] = "Nije mogu\u0107e pozvati updateRow() prilikom ubacivanja redova.";
        t[562] = "Detail: {0}";
        t[563] = "Detalji: {0}";
        t[566] = "Cannot call deleteRow() when on the insert row.";
        t[567] = "Nije mogu\u0107e pozvati deleteRow() prilikom ubacivanja redova.";
        t[568] = "Currently positioned before the start of the ResultSet.  You cannot call deleteRow() here.";
        t[569] = "Trenutna pozicija pre po\u010detka ResultSet-a.  Ne mo\u017eete pozvati deleteRow() na toj poziciji.";
        t[576] = "Illegal UTF-8 sequence: final value is a surrogate value: {0}";
        t[577] = "Ilegalna UTF-8 sekvenca: finalna vrednost je zamena vrednosti: {0}";
        t[578] = "Unknown Response Type {0}.";
        t[579] = "Nepoznat tip odziva {0}.";
        t[582] = "Unsupported value for stringtype parameter: {0}";
        t[583] = "Vrednost za parametar tipa string nije podr\u017eana: {0}";
        t[584] = "Conversion to type {0} failed: {1}.";
        t[585] = "Konverzija u tip {0} propala: {1}.";
        t[586] = "This SQLXML object has not been initialized, so you cannot retrieve data from it.";
        t[587] = "SQLXML objekat nije inicijalizovan tako da nije mogu\u0107e preuzimati podatke iz njega.";
        t[600] = "Unable to load the class {0} responsible for the datatype {1}";
        t[601] = "Nije mogu\u0107e u\u010ditati kalsu {0} odgovornu za tip podataka {1}";
        t[604] = "The fastpath function {0} is unknown.";
        t[605] = "Fastpath funkcija {0} je nepoznata.";
        t[608] = "Malformed function or procedure escape syntax at offset {0}.";
        t[609] = "Pogre\u0161na sintaksa u funkciji ili proceduri na poziciji {0}.";
        t[612] = "Provided Reader failed.";
        t[613] = "Pribavljeni \u010dita\u010d (Reader) zakazao.";
        t[614] = "Maximum number of rows must be a value grater than or equal to 0.";
        t[615] = "Maksimalni broj redova mora biti vrednosti ve\u0107e ili jednake 0.";
        t[616] = "Failed to create object for: {0}.";
        t[617] = "Propao poku\u0161aj kreiranja objekta za: {0}.";
        t[620] = "Conversion of money failed.";
        t[621] = "Konverzija novca (money) propala.";
        t[622] = "Premature end of input stream, expected {0} bytes, but only read {1}.";
        t[623] = "Prevremen zavr\u0161etak ulaznog toka podataka,o\u010dekivano {0} bajtova, a pro\u010ditano samo {1}.";
        t[626] = "An unexpected result was returned by a query.";
        t[627] = "Nepredvi\u0111en rezultat je vra\u0107en od strane upita.";
        t[644] = "Invalid protocol state requested. Attempted transaction interleaving is not supported. xid={0}, currentXid={1}, state={2}, flags={3}";
        t[645] = "Preplitanje transakcija nije implementirano. xid={0}, currentXid={1}, state={2}, flags={3}";
        t[646] = "An error occurred while setting up the SSL connection.";
        t[647] = "Gre\u0161ka se dogodila prilikom pode\u0161avanja SSL konekcije.";
        t[654] = "Illegal UTF-8 sequence: {0} bytes used to encode a {1} byte value: {2}";
        t[655] = "Ilegalna UTF-8 sekvenca: {0} bytes used to encode a {1} byte value: {2}";
        t[656] = "Not implemented: Prepare must be issued using the same connection that started the transaction. currentXid={0}, prepare xid={1}";
        t[657] = "Nije implementirano: Spremanje mora biti pozvano uz kori\u0161\u0107enje iste konekcije koja se koristi za startovanje transakcije. currentXid={0}, prepare xid={1}";
        t[658] = "The SSLSocketFactory class provided {0} could not be instantiated.";
        t[659] = "SSLSocketFactory klasa koju pru\u017ea {0} se nemo\u017ee instancirati.";
        t[662] = "Failed to convert binary xml data to encoding: {0}.";
        t[663] = "Neuspe\u0161no konvertovanje binarnih XML podataka u kodnu stranu: {0}.";
        t[670] = "Position: {0}";
        t[671] = "Pozicija: {0}";
        t[676] = "Location: File: {0}, Routine: {1}, Line: {2}";
        t[677] = "Lokacija: Fajl: {0}, Rutina: {1}, Linija: {2}";
        t[684] = "Cannot tell if path is open or closed: {0}.";
        t[685] = "Nije mogu\u0107e utvrditi dali je putanja otvorena ili zatvorena: {0}.";
        t[690] = "Unable to create StAXResult for SQLXML";
        t[691] = "Nije mogu\u0107e kreirati StAXResult za SQLXML";
        t[700] = "Cannot convert an instance of {0} to type {1}";
        t[701] = "Nije mogu\u0107e konvertovati instancu {0} u tip {1}";
        t[710] = "{0} function takes four and only four argument.";
        t[711] = "Funkcija {0} prima \u010detiri i samo \u010detiri parametra.";
        t[718] = "Interrupted while attempting to connect.";
        t[719] = "Prekinut poku\u0161aj konektovanja.";
        t[722] = "Your security policy has prevented the connection from being attempted.  You probably need to grant the connect java.net.SocketPermission to the database server host and port that you wish to connect to.";
        t[723] = "Sigurnosna pode\u0161avanja su spre\u010dila konekciju. Verovatno je potrebno da dozvolite konekciju klasi java.net.SocketPermission na bazu na serveru.";
        t[734] = "No function outputs were registered.";
        t[735] = "Nije registrovan nikakv izlaz iz funkcije.";
        t[736] = "{0} function takes one and only one argument.";
        t[737] = "Funkcija {0} prima jedan i samo jedan parametar.";
        t[744] = "This ResultSet is closed.";
        t[745] = "ResultSet je zatvoren.";
        t[746] = "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.";
        t[747] = "Prona\u0111eni su neva\u017ee\u0107i karakter podaci. Uzrok je najverovatnije to \u0161to pohranjeni podaci sadr\u017ee karaktere koji su neva\u017ee\u0107i u setu karaktera sa kojima je baza kreirana.  Npr. \u010cuvanje 8bit podataka u SQL_ASCII bazi podataka.";
        t[752] = "Error disabling autocommit";
        t[753] = "Gre\u0161ka u isklju\u010divanju autokomita";
        t[754] = "Ran out of memory retrieving query results.";
        t[755] = "Nestalo je memorije prilikom preuzimanja rezultata upita.";
        t[756] = "Returning autogenerated keys is not supported.";
        t[757] = "Vra\u0107anje autogenerisanih klju\u010deva nije podr\u017eano.";
        t[760] = "Operation requires a scrollable ResultSet, but this ResultSet is FORWARD_ONLY.";
        t[761] = "Operacija zahteva skrolabilan ResultSet,ali ovaj ResultSet je FORWARD_ONLY.";
        t[762] = "A CallableStatement function was executed and the out parameter {0} was of type {1} however type {2} was registered.";
        t[763] = "CallableStatement funkcija je izvr\u0161ena dok je izlazni parametar {0} tipa {1} a tip {2} je registrovan kao izlazni parametar.";
        t[764] = "Unable to find server array type for provided name {0}.";
        t[765] = "Neuspe\u0161no nala\u017eenje liste servera za zadato ime {0}.";
        t[768] = "Unknown ResultSet holdability setting: {0}.";
        t[769] = "Nepoznata ResultSet pode\u0161avanja za mogu\u0107nost dr\u017eanja (holdability): {0}.";
        t[772] = "Transaction isolation level {0} not supported.";
        t[773] = "Nivo izolacije transakcije {0} nije podr\u017ean.";
        t[774] = "Zero bytes may not occur in identifiers.";
        t[775] = "Nula bajtovji se ne smeju pojavljivati u identifikatorima.";
        t[776] = "No results were returned by the query.";
        t[777] = "Nikakav rezultat nije vra\u0107en od strane upita.";
        t[778] = "A CallableStatement was executed with nothing returned.";
        t[779] = "CallableStatement je izvr\u0161en ali ni\u0161ta nije vre\u0107eno kao rezultat.";
        t[780] = "wasNull cannot be call before fetching a result.";
        t[781] = "wasNull nemo\u017ee biti pozvan pre zahvatanja rezultata.";
        t[784] = "Returning autogenerated keys by column index is not supported.";
        t[785] = "Vra\u0107anje autogenerisanih klju\u010deva po kloloni nije podr\u017eano.";
        t[786] = "This statement does not declare an OUT parameter.  Use '{' ?= call ... '}' to declare one.";
        t[787] = "Izraz ne deklari\u0161e izlazni parametar. Koristite '{' ?= poziv ... '}' za deklarisanje.";
        t[788] = "Can''t use relative move methods while on the insert row.";
        t[789] = "Ne mo\u017ee se koristiti metod relativnog pomeranja prilikom ubacivanja redova.";
        t[790] = "A CallableStatement was executed with an invalid number of parameters";
        t[791] = "CallableStatement je izvr\u0161en sa neva\u017ee\u0107im brojem parametara";
        t[792] = "Connection is busy with another transaction";
        t[793] = "Konekcija je zauzeta sa drugom transakciom.";
        table = t;
    }
}

