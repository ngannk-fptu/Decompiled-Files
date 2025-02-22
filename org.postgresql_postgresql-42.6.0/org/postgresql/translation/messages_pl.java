/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_pl
extends ResourceBundle {
    private static final String[] table;

    @Override
    public Object handleGetObject(String msgid) throws MissingResourceException {
        String found;
        int hash_val = msgid.hashCode() & Integer.MAX_VALUE;
        int idx = hash_val % 173 << 1;
        String found2 = table[idx];
        if (found2 == null) {
            return null;
        }
        if (msgid.equals(found2)) {
            return table[idx + 1];
        }
        int incr = hash_val % 171 + 1 << 1;
        do {
            if ((idx += incr) >= 346) {
                idx -= 346;
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
                while (this.idx < 346 && table[this.idx] == null) {
                    this.idx += 2;
                }
            }

            @Override
            public boolean hasMoreElements() {
                return this.idx < 346;
            }

            public Object nextElement() {
                String key = table[this.idx];
                do {
                    this.idx += 2;
                } while (this.idx < 346 && table[this.idx] == null);
                return key;
            }
        };
    }

    public ResourceBundle getParent() {
        return this.parent;
    }

    static {
        String[] t = new String[346];
        t[0] = "";
        t[1] = "Project-Id-Version: head-pl\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2005-05-22 03:01+0200\nLast-Translator: Jaros\u0142aw Jan Pyszny <jarek@pyszny.net>\nLanguage-Team:  <pl@li.org>\nLanguage: \nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\nX-Generator: KBabel 1.10\nPlural-Forms:  nplurals=3; plural=(n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2);\n";
        t[2] = "The driver currently does not support COPY operations.";
        t[3] = "Sterownik nie obs\u0142uguje aktualnie operacji COPY.";
        t[4] = "Internal Query: {0}";
        t[5] = "Wewn\u0119trzne Zapytanie: {0}";
        t[6] = "There are no rows in this ResultSet.";
        t[7] = "Nie ma \u017cadnych wierszy w tym ResultSet.";
        t[8] = "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.";
        t[9] = "Znaleziono nieprawid\u0142owy znak. Najprawdopodobniej jest to spowodowane przechowywaniem w bazie znak\u00f3w, kt\u00f3re nie pasuj\u0105 do zestawu znak\u00f3w wybranego podczas tworzenia bazy danych. Najcz\u0119stszy przyk\u0142ad to przechowywanie 8-bitowych znak\u00f3w w bazie o kodowaniu SQL_ASCII.";
        t[12] = "Fastpath call {0} - No result was returned and we expected an integer.";
        t[13] = "Wywo\u0142anie fastpath {0} - Nie otrzymano \u017cadnego wyniku, a oczekiwano liczby ca\u0142kowitej.";
        t[14] = "An error occurred while setting up the SSL connection.";
        t[15] = "Wyst\u0105pi\u0142 b\u0142\u0105d podczas ustanawiania po\u0142\u0105czenia SSL.";
        t[20] = "A CallableStatement was declared, but no call to registerOutParameter(1, <some type>) was made.";
        t[21] = "Funkcja CallableStatement zosta\u0142a zadeklarowana, ale nie wywo\u0142ano registerOutParameter (1, <jaki\u015b typ>).";
        t[24] = "Unexpected command status: {0}.";
        t[25] = "Nieoczekiwany status komendy: {0}.";
        t[32] = "A connection could not be made using the requested protocol {0}.";
        t[33] = "Nie mo\u017cna by\u0142o nawi\u0105za\u0107 po\u0142\u0105czenia stosuj\u0105c \u017c\u0105dany protoko\u0142u {0}.";
        t[38] = "Bad value for type {0} : {1}";
        t[39] = "Z\u0142a warto\u015b\u0107 dla typu {0}: {1}";
        t[40] = "Not on the insert row.";
        t[41] = "Nie na wstawianym rekordzie.";
        t[42] = "Premature end of input stream, expected {0} bytes, but only read {1}.";
        t[43] = "Przedwczesny koniec strumienia wej\u015bciowego, oczekiwano {0} bajt\u00f3w, odczytano tylko {1}.";
        t[48] = "Unknown type {0}.";
        t[49] = "Nieznany typ {0}.";
        t[52] = "The server does not support SSL.";
        t[53] = "Serwer nie obs\u0142uguje SSL.";
        t[60] = "Cannot call updateRow() when on the insert row.";
        t[61] = "Nie mo\u017cna wywo\u0142a\u0107 updateRow() na wstawianym rekordzie.";
        t[62] = "Where: {0}";
        t[63] = "Gdzie: {0}";
        t[72] = "Cannot call cancelRowUpdates() when on the insert row.";
        t[73] = "Nie mo\u017cna wywo\u0142a\u0107 cancelRowUpdates() na wstawianym rekordzie.";
        t[82] = "Server SQLState: {0}";
        t[83] = "Serwer SQLState: {0}";
        t[92] = "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.";
        t[93] = "ResultSet nie jest modyfikowalny (not updateable). Zapytanie, kt\u00f3re zwr\u00f3ci\u0142o ten wynik musi dotyczy\u0107 tylko jednej tabeli oraz musi pobiera\u0107 wszystkie klucze g\u0142\u00f3wne tej tabeli. Zobacz Specyfikacj\u0119 JDBC 2.1 API, rozdzia\u0142 5.6, by uzyska\u0107 wi\u0119cej szczeg\u00f3\u0142\u00f3w.";
        t[102] = "Cannot tell if path is open or closed: {0}.";
        t[103] = "Nie mo\u017cna stwierdzi\u0107, czy \u015bcie\u017cka jest otwarta czy zamkni\u0119ta: {0}.";
        t[108] = "The parameter index is out of range: {0}, number of parameters: {1}.";
        t[109] = "Indeks parametru jest poza zakresem: {0}, liczba parametr\u00f3w: {1}.";
        t[110] = "Unsupported Types value: {0}";
        t[111] = "Nieznana warto\u015b\u0107 Types: {0}";
        t[112] = "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.";
        t[113] = "Aktualna pozycja za ko\u0144cem ResultSet. Nie mo\u017cna wywo\u0142a\u0107 deleteRow().";
        t[114] = "This ResultSet is closed.";
        t[115] = "Ten ResultSet jest zamkni\u0119ty.";
        t[120] = "Conversion of interval failed";
        t[121] = "Konwersja typu interval nie powiod\u0142a si\u0119";
        t[122] = "Unable to load the class {0} responsible for the datatype {1}";
        t[123] = "Nie jest mo\u017cliwe za\u0142adowanie klasy {0} odpowiedzialnej za typ danych {1}";
        t[138] = "Error loading default settings from driverconfig.properties";
        t[139] = "B\u0142\u0105d podczas wczytywania ustawie\u0144 domy\u015blnych z driverconfig.properties";
        t[142] = "The array index is out of range: {0}";
        t[143] = "Indeks tablicy jest poza zakresem: {0}";
        t[146] = "Unknown Types value.";
        t[147] = "Nieznana warto\u015b\u0107 Types.";
        t[154] = "The maximum field size must be a value greater than or equal to 0.";
        t[155] = "Maksymalny rozmiar pola musi by\u0107 warto\u015bci\u0105 dodatni\u0105 lub 0.";
        t[168] = "Detail: {0}";
        t[169] = "Szczeg\u00f3\u0142y: {0}";
        t[170] = "Unknown Response Type {0}.";
        t[171] = "Nieznany typ odpowiedzi {0}.";
        t[172] = "Maximum number of rows must be a value grater than or equal to 0.";
        t[173] = "Maksymalna liczba rekord\u00f3w musi by\u0107 warto\u015bci\u0105 dodatni\u0105 lub 0.";
        t[184] = "Query timeout must be a value greater than or equals to 0.";
        t[185] = "Timeout zapytania musi by\u0107 warto\u015bci\u0105 dodatni\u0105 lub 0.";
        t[186] = "Too many update results were returned.";
        t[187] = "Zapytanie nie zwr\u00f3ci\u0142o \u017cadnych wynik\u00f3w.";
        t[190] = "The connection attempt failed.";
        t[191] = "Pr\u00f3ba nawi\u0105zania po\u0142\u0105czenia nie powiod\u0142a si\u0119.";
        t[198] = "Connection has been closed automatically because a new connection was opened for the same PooledConnection or the PooledConnection has been closed.";
        t[199] = "Po\u0142\u0105czenie zosta\u0142o zamkni\u0119te automatycznie, poniewa\u017c nowe po\u0142\u0105czenie zosta\u0142o otwarte dla tego samego PooledConnection lub PooledConnection zosta\u0142o zamkni\u0119te.";
        t[204] = "Protocol error.  Session setup failed.";
        t[205] = "B\u0142\u0105d protoko\u0142u. Nie uda\u0142o si\u0119 utworzy\u0107 sesji.";
        t[206] = "This PooledConnection has already been closed.";
        t[207] = "To PooledConnection zosta\u0142o ju\u017c zamkni\u0119te.";
        t[208] = "DataSource has been closed.";
        t[209] = "DataSource zosta\u0142o zamkni\u0119te.";
        t[212] = "Method {0} is not yet implemented.";
        t[213] = "Metoda {0}nie jest jeszcze obs\u0142ugiwana.";
        t[216] = "Hint: {0}";
        t[217] = "Wskaz\u00f3wka: {0}";
        t[218] = "No value specified for parameter {0}.";
        t[219] = "Nie podano warto\u015bci dla parametru {0}.";
        t[222] = "Position: {0}";
        t[223] = "Pozycja: {0}";
        t[226] = "Cannot call deleteRow() when on the insert row.";
        t[227] = "Nie mo\u017cna wywo\u0142a\u0107 deleteRow() na wstawianym rekordzie.";
        t[240] = "Conversion of money failed.";
        t[241] = "Konwersja typu money nie powiod\u0142a si\u0119.";
        t[244] = "Internal Position: {0}";
        t[245] = "Wewn\u0119trzna Pozycja: {0}";
        t[248] = "Connection has been closed.";
        t[249] = "Po\u0142\u0105czenie zosta\u0142o zamkni\u0119te.";
        t[254] = "Currently positioned before the start of the ResultSet.  You cannot call deleteRow() here.";
        t[255] = "Aktualna pozycja przed pocz\u0105tkiem ResultSet. Nie mo\u017cna wywo\u0142a\u0107 deleteRow().";
        t[258] = "Failed to create object for: {0}.";
        t[259] = "Nie powiod\u0142o si\u0119 utworzenie obiektu dla: {0}.";
        t[262] = "Fetch size must be a value greater to or equal to 0.";
        t[263] = "Rozmiar pobierania musi by\u0107 warto\u015bci\u0105 dodatni\u0105 lub 0.";
        t[270] = "No results were returned by the query.";
        t[271] = "Zapytanie nie zwr\u00f3ci\u0142o \u017cadnych wynik\u00f3w.";
        t[276] = "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.";
        t[277] = "Uwierzytelnienie typu {0} nie jest obs\u0142ugiwane. Upewnij si\u0119, \u017ce skonfigurowa\u0142e\u015b plik pg_hba.conf tak, \u017ce zawiera on adres IP lub podsie\u0107 klienta oraz \u017ce u\u017cyta metoda uwierzytelnienia jest wspierana przez ten sterownik.";
        t[280] = "Conversion to type {0} failed: {1}.";
        t[281] = "Konwersja do typu {0} nie powiod\u0142a si\u0119: {1}.";
        t[282] = "A result was returned when none was expected.";
        t[283] = "Zwr\u00f3cono wynik zapytania, cho\u0107 nie by\u0142 on oczekiwany.";
        t[292] = "Transaction isolation level {0} not supported.";
        t[293] = "Poziom izolacji transakcji {0} nie jest obs\u0142ugiwany.";
        t[306] = "ResultSet not positioned properly, perhaps you need to call next.";
        t[307] = "Z\u0142a pozycja w ResultSet, mo\u017ce musisz wywo\u0142a\u0107 next.";
        t[308] = "Location: File: {0}, Routine: {1}, Line: {2}";
        t[309] = "Lokalizacja: Plik: {0}, Procedura: {1}, Linia: {2}";
        t[314] = "An unexpected result was returned by a query.";
        t[315] = "Zapytanie zwr\u00f3ci\u0142o nieoczekiwany wynik.";
        t[316] = "The column index is out of range: {0}, number of columns: {1}.";
        t[317] = "Indeks kolumny jest poza zakresem: {0}, liczba kolumn: {1}.";
        t[318] = "Expected command status BEGIN, got {0}.";
        t[319] = "Spodziewano si\u0119 statusu komendy BEGIN, otrzymano {0}.";
        t[320] = "The fastpath function {0} is unknown.";
        t[321] = "Funkcja fastpath {0} jest nieznana.";
        t[324] = "The server requested password-based authentication, but no password was provided.";
        t[325] = "Serwer za\u017c\u0105da\u0142 uwierzytelnienia opartego na ha\u015ble, ale \u017cadne has\u0142o nie zosta\u0142o dostarczone.";
        t[332] = "The array index is out of range: {0}, number of elements: {1}.";
        t[333] = "Indeks tablicy jest poza zakresem: {0}, liczba element\u00f3w: {1}.";
        t[338] = "Something unusual has occurred to cause the driver to fail. Please report this exception.";
        t[339] = "Co\u015b niezwyk\u0142ego spowodowa\u0142o pad sterownika. Prosz\u0119, zg\u0142o\u015b ten wyj\u0105tek.";
        t[342] = "Zero bytes may not occur in string parameters.";
        t[343] = "Zerowe bajty nie mog\u0105 pojawia\u0107 si\u0119 w parametrach typu \u0142a\u0144cuch znakowy.";
        table = t;
    }
}

