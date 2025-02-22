/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_pt_BR
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
        t[1] = "Project-Id-Version: PostgreSQL 8.4\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2004-10-31 20:48-0300\nLast-Translator: Euler Taveira de Oliveira <euler@timbira.com>\nLanguage-Team: Brazilian Portuguese <pgbr-dev@listas.postgresql.org.br>\nLanguage: pt_BR\nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\n";
        t[2] = "Not implemented: 2nd phase commit must be issued using an idle connection. commit xid={0}, currentXid={1}, state={2}, transactionState={3}";
        t[3] = "N\u00e3o est\u00e1 implementado: efetiva\u00e7\u00e3o da segunda fase deve ser executada utilizado uma conex\u00e3o ociosa. commit xid={0}, currentXid={1}, state={2}, transactionState={3}";
        t[4] = "DataSource has been closed.";
        t[5] = "DataSource foi fechado.";
        t[8] = "Invalid flags {0}";
        t[9] = "Marcadores={0} inv\u00e1lidos";
        t[18] = "Where: {0}";
        t[19] = "Onde: {0}";
        t[24] = "Unknown XML Source class: {0}";
        t[25] = "Classe XML Source desconhecida: {0}";
        t[26] = "The connection attempt failed.";
        t[27] = "A tentativa de conex\u00e3o falhou.";
        t[28] = "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.";
        t[29] = "Posicionado depois do fim do ResultSet.  Voc\u00ea n\u00e3o pode chamar deleteRow() aqui.";
        t[32] = "Can''t use query methods that take a query string on a PreparedStatement.";
        t[33] = "N\u00e3o pode utilizar m\u00e9todos de consulta que pegam uma consulta de um comando preparado.";
        t[36] = "Multiple ResultSets were returned by the query.";
        t[37] = "ResultSets m\u00faltiplos foram retornados pela consulta.";
        t[50] = "Too many update results were returned.";
        t[51] = "Muitos resultados de atualiza\u00e7\u00e3o foram retornados.";
        t[58] = "Illegal UTF-8 sequence: initial byte is {0}: {1}";
        t[59] = "Sequ\u00eancia UTF-8 ilegal: byte inicial \u00e9 {0}: {1}";
        t[66] = "The column name {0} was not found in this ResultSet.";
        t[67] = "A nome da coluna {0} n\u00e3o foi encontrado neste ResultSet.";
        t[70] = "Fastpath call {0} - No result was returned and we expected an integer.";
        t[71] = "Chamada ao Fastpath {0} - Nenhum resultado foi retornado e n\u00f3s esper\u00e1vamos um inteiro.";
        t[74] = "Protocol error.  Session setup failed.";
        t[75] = "Erro de Protocolo. Configura\u00e7\u00e3o da sess\u00e3o falhou.";
        t[76] = "A CallableStatement was declared, but no call to registerOutParameter(1, <some type>) was made.";
        t[77] = "Uma fun\u00e7\u00e3o foi declarada mas nenhuma chamada a registerOutParameter (1, <algum_tipo>) foi feita.";
        t[78] = "ResultSets with concurrency CONCUR_READ_ONLY cannot be updated.";
        t[79] = "ResultSets com CONCUR_READ_ONLY concorrentes n\u00e3o podem ser atualizados.";
        t[90] = "LOB positioning offsets start at 1.";
        t[91] = "Deslocamentos da posi\u00e7\u00e3o de LOB come\u00e7am em 1.";
        t[92] = "Internal Position: {0}";
        t[93] = "Posi\u00e7\u00e3o Interna: {0}";
        t[96] = "free() was called on this LOB previously";
        t[97] = "free() j\u00e1 foi chamado neste LOB";
        t[100] = "Cannot change transaction read-only property in the middle of a transaction.";
        t[101] = "N\u00e3o pode mudar propriedade somente-leitura da transa\u00e7\u00e3o no meio de uma transa\u00e7\u00e3o.";
        t[102] = "The JVM claims not to support the {0} encoding.";
        t[103] = "A JVM reclamou que n\u00e3o suporta a codifica\u00e7\u00e3o {0}.";
        t[108] = "{0} function doesn''t take any argument.";
        t[109] = "fun\u00e7\u00e3o {0} n\u00e3o recebe nenhum argumento.";
        t[112] = "xid must not be null";
        t[113] = "xid n\u00e3o deve ser nulo";
        t[114] = "Connection has been closed.";
        t[115] = "Conex\u00e3o foi fechada.";
        t[122] = "The server does not support SSL.";
        t[123] = "O servidor n\u00e3o suporta SSL.";
        t[124] = "Custom type maps are not supported.";
        t[125] = "Mapeamento de tipos personalizados n\u00e3o s\u00e3o suportados.";
        t[140] = "Illegal UTF-8 sequence: byte {0} of {1} byte sequence is not 10xxxxxx: {2}";
        t[141] = "Sequ\u00eancia UTF-8 ilegal: byte {0} da sequ\u00eancia de bytes {1} n\u00e3o \u00e9 10xxxxxx: {2}";
        t[148] = "Hint: {0}";
        t[149] = "Dica: {0}";
        t[152] = "Unable to find name datatype in the system catalogs.";
        t[153] = "N\u00e3o foi poss\u00edvel encontrar tipo de dado name nos cat\u00e1logos do sistema.";
        t[156] = "Unsupported Types value: {0}";
        t[157] = "Valor de Types n\u00e3o \u00e9 suportado: {0}";
        t[158] = "Unknown type {0}.";
        t[159] = "Tipo desconhecido {0}.";
        t[166] = "{0} function takes two and only two arguments.";
        t[167] = "fun\u00e7\u00e3o {0} recebe somente dois argumentos.";
        t[170] = "Finalizing a Connection that was never closed:";
        t[171] = "Fechando uma Conex\u00e3o que n\u00e3o foi fechada:";
        t[180] = "The maximum field size must be a value greater than or equal to 0.";
        t[181] = "O tamanho m\u00e1ximo de um campo deve ser um valor maior ou igual a 0.";
        t[186] = "PostgreSQL LOBs can only index to: {0}";
        t[187] = "LOBs do PostgreSQL s\u00f3 podem indexar at\u00e9: {0}";
        t[194] = "Method {0} is not yet implemented.";
        t[195] = "M\u00e9todo {0} ainda n\u00e3o foi implementado.";
        t[198] = "Error loading default settings from driverconfig.properties";
        t[199] = "Erro ao carregar configura\u00e7\u00f5es padr\u00e3o do driverconfig.properties";
        t[200] = "Results cannot be retrieved from a CallableStatement before it is executed.";
        t[201] = "Resultados n\u00e3o podem ser recuperados de uma fun\u00e7\u00e3o antes dela ser executada.";
        t[202] = "Large Objects may not be used in auto-commit mode.";
        t[203] = "Objetos Grandes n\u00e3o podem ser usados no modo de efetiva\u00e7\u00e3o autom\u00e1tica (auto-commit).";
        t[208] = "Expected command status BEGIN, got {0}.";
        t[209] = "Status do comando BEGIN esperado, recebeu {0}.";
        t[218] = "Invalid fetch direction constant: {0}.";
        t[219] = "Constante de dire\u00e7\u00e3o da busca \u00e9 inv\u00e1lida: {0}.";
        t[222] = "{0} function takes three and only three arguments.";
        t[223] = "fun\u00e7\u00e3o {0} recebe tr\u00eas e somente tr\u00eas argumentos.";
        t[226] = "This SQLXML object has already been freed.";
        t[227] = "Este objeto SQLXML j\u00e1 foi liberado.";
        t[228] = "Cannot update the ResultSet because it is either before the start or after the end of the results.";
        t[229] = "N\u00e3o pode atualizar o ResultSet porque ele est\u00e1 antes do in\u00edcio ou depois do fim dos resultados.";
        t[230] = "The JVM claims not to support the encoding: {0}";
        t[231] = "A JVM reclamou que n\u00e3o suporta a codifica\u00e7\u00e3o: {0}";
        t[232] = "Parameter of type {0} was registered, but call to get{1} (sqltype={2}) was made.";
        t[233] = "Par\u00e2metro do tipo {0} foi registrado, mas uma chamada a get{1} (tiposql={2}) foi feita.";
        t[240] = "Cannot establish a savepoint in auto-commit mode.";
        t[241] = "N\u00e3o pode estabelecer um savepoint no modo de efetiva\u00e7\u00e3o autom\u00e1tica (auto-commit).";
        t[242] = "Cannot retrieve the id of a named savepoint.";
        t[243] = "N\u00e3o pode recuperar o id de um savepoint com nome.";
        t[244] = "The column index is out of range: {0}, number of columns: {1}.";
        t[245] = "O \u00edndice da coluna est\u00e1 fora do intervalo: {0}, n\u00famero de colunas: {1}.";
        t[250] = "Something unusual has occurred to cause the driver to fail. Please report this exception.";
        t[251] = "Alguma coisa n\u00e3o usual ocorreu para causar a falha do driver. Por favor reporte esta exce\u00e7\u00e3o.";
        t[260] = "Cannot cast an instance of {0} to type {1}";
        t[261] = "N\u00e3o pode converter uma inst\u00e2ncia de {0} para tipo {1}";
        t[264] = "Unknown Types value.";
        t[265] = "Valor de Types desconhecido.";
        t[266] = "Invalid stream length {0}.";
        t[267] = "Tamanho de dado {0} \u00e9 inv\u00e1lido.";
        t[272] = "Cannot retrieve the name of an unnamed savepoint.";
        t[273] = "N\u00e3o pode recuperar o nome de um savepoint sem nome.";
        t[274] = "Unable to translate data into the desired encoding.";
        t[275] = "N\u00e3o foi poss\u00edvel traduzir dado para codifica\u00e7\u00e3o desejada.";
        t[276] = "Expected an EOF from server, got: {0}";
        t[277] = "Esperado um EOF do servidor, recebido: {0}";
        t[278] = "Bad value for type {0} : {1}";
        t[279] = "Valor inv\u00e1lido para tipo {0} : {1}";
        t[280] = "The server requested password-based authentication, but no password was provided.";
        t[281] = "O servidor pediu autentica\u00e7\u00e3o baseada em senha, mas nenhuma senha foi fornecida.";
        t[286] = "Unable to create SAXResult for SQLXML.";
        t[287] = "N\u00e3o foi poss\u00edvel criar SAXResult para SQLXML.";
        t[292] = "Error during recover";
        t[293] = "Erro durante recupera\u00e7\u00e3o";
        t[294] = "tried to call end without corresponding start call. state={0}, start xid={1}, currentXid={2}, preparedXid={3}";
        t[295] = "tentou executar end sem a chamada ao start correspondente. state={0}, start xid={1}, currentXid={2}, preparedXid={3}";
        t[296] = "Truncation of large objects is only implemented in 8.3 and later servers.";
        t[297] = "Truncar objetos grandes s\u00f3 \u00e9 implementado por servidores 8.3 ou superiores.";
        t[298] = "This PooledConnection has already been closed.";
        t[299] = "Este PooledConnection j\u00e1 foi fechado.";
        t[302] = "ClientInfo property not supported.";
        t[303] = "propriedade ClientInfo n\u00e3o \u00e9 suportada.";
        t[306] = "Fetch size must be a value greater to or equal to 0.";
        t[307] = "Tamanho da busca deve ser um valor maior ou igual a 0.";
        t[312] = "A connection could not be made using the requested protocol {0}.";
        t[313] = "A conex\u00e3o n\u00e3o pode ser feita usando protocolo informado {0}.";
        t[318] = "Unknown XML Result class: {0}";
        t[319] = "Classe XML Result desconhecida: {0}";
        t[322] = "There are no rows in this ResultSet.";
        t[323] = "N\u00e3o h\u00e1 nenhum registro neste ResultSet.";
        t[324] = "Unexpected command status: {0}.";
        t[325] = "Status do comando inesperado: {0}.";
        t[330] = "Heuristic commit/rollback not supported. forget xid={0}";
        t[331] = "Efetiva\u00e7\u00e3o/Cancelamento heur\u00edstico n\u00e3o \u00e9 suportado. forget xid={0}";
        t[334] = "Not on the insert row.";
        t[335] = "N\u00e3o est\u00e1 inserindo um registro.";
        t[336] = "This SQLXML object has already been initialized, so you cannot manipulate it further.";
        t[337] = "Este objeto SQLXML j\u00e1 foi inicializado, ent\u00e3o voc\u00ea n\u00e3o pode manipul\u00e1-lo depois.";
        t[344] = "Server SQLState: {0}";
        t[345] = "SQLState: {0}";
        t[348] = "The server''s standard_conforming_strings parameter was reported as {0}. The JDBC driver expected on or off.";
        t[349] = "O par\u00e2metro do servidor standard_conforming_strings foi definido como {0}. O driver JDBC espera que seja on ou off.";
        t[360] = "The driver currently does not support COPY operations.";
        t[361] = "O driver atualmente n\u00e3o suporta opera\u00e7\u00f5es COPY.";
        t[364] = "The array index is out of range: {0}, number of elements: {1}.";
        t[365] = "O \u00edndice da matriz est\u00e1 fora do intervalo: {0}, n\u00famero de elementos: {1}.";
        t[374] = "suspend/resume not implemented";
        t[375] = "suspender/recome\u00e7ar n\u00e3o est\u00e1 implementado";
        t[378] = "Not implemented: one-phase commit must be issued using the same connection that was used to start it";
        t[379] = "N\u00e3o est\u00e1 implementado: efetivada da primeira fase deve ser executada utilizando a mesma conex\u00e3o que foi utilizada para inici\u00e1-la";
        t[380] = "Error during one-phase commit. commit xid={0}";
        t[381] = "Erro durante efetiva\u00e7\u00e3o de uma fase. commit xid={0}";
        t[398] = "Cannot call cancelRowUpdates() when on the insert row.";
        t[399] = "N\u00e3o pode chamar cancelRowUpdates() quando estiver inserindo registro.";
        t[400] = "Cannot reference a savepoint after it has been released.";
        t[401] = "N\u00e3o pode referenciar um savepoint ap\u00f3s ele ser descartado.";
        t[402] = "You must specify at least one column value to insert a row.";
        t[403] = "Voc\u00ea deve especificar pelo menos uma coluna para inserir um registro.";
        t[404] = "Unable to determine a value for MaxIndexKeys due to missing system catalog data.";
        t[405] = "N\u00e3o foi poss\u00edvel determinar um valor para MaxIndexKeys por causa de falta de dados no cat\u00e1logo do sistema.";
        t[410] = "commit called before end. commit xid={0}, state={1}";
        t[411] = "commit executado antes do end. commit xid={0}, state={1}";
        t[412] = "Illegal UTF-8 sequence: final value is out of range: {0}";
        t[413] = "Sequ\u00eancia UTF-8 ilegal: valor final est\u00e1 fora do intervalo: {0}";
        t[414] = "{0} function takes two or three arguments.";
        t[415] = "fun\u00e7\u00e3o {0} recebe dois ou tr\u00eas argumentos.";
        t[428] = "Unable to convert DOMResult SQLXML data to a string.";
        t[429] = "N\u00e3o foi poss\u00edvel converter dado SQLXML do DOMResult para uma cadeia de caracteres.";
        t[434] = "Unable to decode xml data.";
        t[435] = "N\u00e3o foi poss\u00edvel decodificar dado xml.";
        t[440] = "Unexpected error writing large object to database.";
        t[441] = "Erro inesperado ao escrever objeto grande no banco de dados.";
        t[442] = "Zero bytes may not occur in string parameters.";
        t[443] = "Zero bytes n\u00e3o podem ocorrer em par\u00e2metros de cadeia de caracteres.";
        t[444] = "A result was returned when none was expected.";
        t[445] = "Um resultado foi retornado quando nenhum era esperado.";
        t[450] = "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.";
        t[451] = "ResultSet n\u00e3o \u00e9 atualiz\u00e1vel. A consulta que gerou esse conjunto de resultados deve selecionar somente uma tabela, e deve selecionar todas as chaves prim\u00e1rias daquela tabela. Veja a especifica\u00e7\u00e3o na API do JDBC 2.1, se\u00e7\u00e3o 5.6 para obter mais detalhes.";
        t[454] = "Bind message length {0} too long.  This can be caused by very large or incorrect length specifications on InputStream parameters.";
        t[455] = "Tamanho de mensagem de liga\u00e7\u00e3o {0} \u00e9 muito longo. Isso pode ser causado por especifica\u00e7\u00f5es de tamanho incorretas ou muito grandes nos par\u00e2metros do InputStream.";
        t[460] = "Statement has been closed.";
        t[461] = "Comando foi fechado.";
        t[462] = "No value specified for parameter {0}.";
        t[463] = "Nenhum valor especificado para par\u00e2metro {0}.";
        t[468] = "The array index is out of range: {0}";
        t[469] = "O \u00edndice da matriz est\u00e1 fora do intervalo: {0}";
        t[474] = "Unable to bind parameter values for statement.";
        t[475] = "N\u00e3o foi poss\u00edvel ligar valores de par\u00e2metro ao comando.";
        t[476] = "Can''t refresh the insert row.";
        t[477] = "N\u00e3o pode renovar um registro inserido.";
        t[480] = "No primary key found for table {0}.";
        t[481] = "Nenhuma chave prim\u00e1ria foi encontrada para tabela {0}.";
        t[482] = "Cannot change transaction isolation level in the middle of a transaction.";
        t[483] = "N\u00e3o pode mudar n\u00edvel de isolamento da transa\u00e7\u00e3o no meio de uma transa\u00e7\u00e3o.";
        t[498] = "Provided InputStream failed.";
        t[499] = "InputStream fornecido falhou.";
        t[500] = "The parameter index is out of range: {0}, number of parameters: {1}.";
        t[501] = "O \u00edndice de par\u00e2metro est\u00e1 fora do intervalo: {0}, n\u00famero de par\u00e2metros: {1}.";
        t[502] = "The server''s DateStyle parameter was changed to {0}. The JDBC driver requires DateStyle to begin with ISO for correct operation.";
        t[503] = "O par\u00e2metro do servidor DateStyle foi alterado para {0}. O driver JDBC requer que o DateStyle come\u00e7e com ISO para opera\u00e7\u00e3o normal.";
        t[508] = "Connection attempt timed out.";
        t[509] = "Tentativa de conex\u00e3o falhou.";
        t[512] = "Internal Query: {0}";
        t[513] = "Consulta Interna: {0}";
        t[514] = "Error preparing transaction. prepare xid={0}";
        t[515] = "Erro ao preparar transa\u00e7\u00e3o. prepare xid={0}";
        t[518] = "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.";
        t[519] = "O tipo de autentica\u00e7\u00e3o {0} n\u00e3o \u00e9 suportado. Verifique se voc\u00ea configurou o arquivo pg_hba.conf incluindo a subrede ou endere\u00e7o IP do cliente, e se est\u00e1 utilizando o esquema de autentica\u00e7\u00e3o suportado pelo driver.";
        t[526] = "Interval {0} not yet implemented";
        t[527] = "Intervalo {0} ainda n\u00e3o foi implementado";
        t[532] = "Conversion of interval failed";
        t[533] = "Convers\u00e3o de interval falhou";
        t[540] = "Query timeout must be a value greater than or equals to 0.";
        t[541] = "Tempo de espera da consulta deve ser um valor maior ou igual a 0.";
        t[542] = "Connection has been closed automatically because a new connection was opened for the same PooledConnection or the PooledConnection has been closed.";
        t[543] = "Conex\u00e3o foi fechada automaticamente porque uma nova conex\u00e3o foi aberta pelo mesmo PooledConnection ou o PooledConnection foi fechado.";
        t[544] = "ResultSet not positioned properly, perhaps you need to call next.";
        t[545] = "ResultSet n\u00e3o est\u00e1 posicionado corretamente, talvez voc\u00ea precise chamar next.";
        t[546] = "Prepare called before end. prepare xid={0}, state={1}";
        t[547] = "Prepare executado antes do end. prepare xid={0}, state={1}";
        t[548] = "Invalid UUID data.";
        t[549] = "dado UUID \u00e9 inv\u00e1lido.";
        t[550] = "This statement has been closed.";
        t[551] = "Este comando foi fechado.";
        t[552] = "Can''t infer the SQL type to use for an instance of {0}. Use setObject() with an explicit Types value to specify the type to use.";
        t[553] = "N\u00e3o pode inferir um tipo SQL a ser usado para uma inst\u00e2ncia de {0}. Use setObject() com um valor de Types expl\u00edcito para especificar o tipo a ser usado.";
        t[554] = "Cannot call updateRow() when on the insert row.";
        t[555] = "N\u00e3o pode chamar updateRow() quando estiver inserindo registro.";
        t[562] = "Detail: {0}";
        t[563] = "Detalhe: {0}";
        t[566] = "Cannot call deleteRow() when on the insert row.";
        t[567] = "N\u00e3o pode chamar deleteRow() quando estiver inserindo registro.";
        t[568] = "Currently positioned before the start of the ResultSet.  You cannot call deleteRow() here.";
        t[569] = "Posicionado antes do in\u00edcio do ResultSet.  Voc\u00ea n\u00e3o pode chamar deleteRow() aqui.";
        t[576] = "Illegal UTF-8 sequence: final value is a surrogate value: {0}";
        t[577] = "Sequ\u00eancia UTF-8 ilegal: valor final \u00e9 um valor suplementar: {0}";
        t[578] = "Unknown Response Type {0}.";
        t[579] = "Tipo de Resposta Desconhecido {0}.";
        t[582] = "Unsupported value for stringtype parameter: {0}";
        t[583] = "Valor do par\u00e2metro stringtype n\u00e3o \u00e9 suportado: {0}";
        t[584] = "Conversion to type {0} failed: {1}.";
        t[585] = "Convers\u00e3o para tipo {0} falhou: {1}.";
        t[586] = "This SQLXML object has not been initialized, so you cannot retrieve data from it.";
        t[587] = "Este objeto SQLXML n\u00e3o foi inicializado, ent\u00e3o voc\u00ea n\u00e3o pode recuperar dados dele.";
        t[600] = "Unable to load the class {0} responsible for the datatype {1}";
        t[601] = "N\u00e3o foi poss\u00edvel carregar a classe {0} respons\u00e1vel pelo tipo de dado {1}";
        t[604] = "The fastpath function {0} is unknown.";
        t[605] = "A fun\u00e7\u00e3o do fastpath {0} \u00e9 desconhecida.";
        t[608] = "Malformed function or procedure escape syntax at offset {0}.";
        t[609] = "Sintaxe de escape mal formada da fun\u00e7\u00e3o ou do procedimento no deslocamento {0}.";
        t[612] = "Provided Reader failed.";
        t[613] = "Reader fornecido falhou.";
        t[614] = "Maximum number of rows must be a value grater than or equal to 0.";
        t[615] = "N\u00famero m\u00e1ximo de registros deve ser um valor maior ou igual a 0.";
        t[616] = "Failed to create object for: {0}.";
        t[617] = "Falhou ao criar objeto para: {0}.";
        t[620] = "Conversion of money failed.";
        t[621] = "Convers\u00e3o de money falhou.";
        t[622] = "Premature end of input stream, expected {0} bytes, but only read {1}.";
        t[623] = "Fim de entrada prematuro, eram esperados {0} bytes, mas somente {1} foram lidos.";
        t[626] = "An unexpected result was returned by a query.";
        t[627] = "Um resultado inesperado foi retornado pela consulta.";
        t[644] = "Invalid protocol state requested. Attempted transaction interleaving is not supported. xid={0}, currentXid={1}, state={2}, flags={3}";
        t[645] = "Intercala\u00e7\u00e3o de transa\u00e7\u00e3o n\u00e3o est\u00e1 implementado. xid={0}, currentXid={1}, state={2}, flags={3}";
        t[646] = "An error occurred while setting up the SSL connection.";
        t[647] = "Um erro ocorreu ao estabelecer uma conex\u00e3o SSL.";
        t[654] = "Illegal UTF-8 sequence: {0} bytes used to encode a {1} byte value: {2}";
        t[655] = "Sequ\u00eancia UTF-8 ilegal: {0} bytes utilizados para codificar um valor de {1} bytes: {2}";
        t[656] = "Not implemented: Prepare must be issued using the same connection that started the transaction. currentXid={0}, prepare xid={1}";
        t[657] = "N\u00e3o est\u00e1 implementado: Prepare deve ser executado utilizando a mesma conex\u00e3o que iniciou a transa\u00e7\u00e3o. currentXid={0}, prepare xid={1}";
        t[658] = "The SSLSocketFactory class provided {0} could not be instantiated.";
        t[659] = "A classe SSLSocketFactory forneceu {0} que n\u00e3o p\u00f4de ser instanciado.";
        t[662] = "Failed to convert binary xml data to encoding: {0}.";
        t[663] = "Falhou ao converter dados xml bin\u00e1rios para codifica\u00e7\u00e3o: {0}.";
        t[670] = "Position: {0}";
        t[671] = "Posi\u00e7\u00e3o: {0}";
        t[676] = "Location: File: {0}, Routine: {1}, Line: {2}";
        t[677] = "Local: Arquivo: {0}, Rotina: {1}, Linha: {2}";
        t[684] = "Cannot tell if path is open or closed: {0}.";
        t[685] = "N\u00e3o pode dizer se caminho est\u00e1 aberto ou fechado: {0}.";
        t[690] = "Unable to create StAXResult for SQLXML";
        t[691] = "N\u00e3o foi poss\u00edvel criar StAXResult para SQLXML";
        t[700] = "Cannot convert an instance of {0} to type {1}";
        t[701] = "N\u00e3o pode converter uma inst\u00e2ncia de {0} para tipo {1}";
        t[710] = "{0} function takes four and only four argument.";
        t[711] = "fun\u00e7\u00e3o {0} recebe somente quatro argumentos.";
        t[716] = "Error disabling autocommit";
        t[717] = "Erro ao desabilitar autocommit";
        t[718] = "Interrupted while attempting to connect.";
        t[719] = "Interrompido ao tentar se conectar.";
        t[722] = "Your security policy has prevented the connection from being attempted.  You probably need to grant the connect java.net.SocketPermission to the database server host and port that you wish to connect to.";
        t[723] = "Sua pol\u00edtica de seguran\u00e7a impediu que a conex\u00e3o pudesse ser estabelecida. Voc\u00ea provavelmente precisa conceder permiss\u00e3o em java.net.SocketPermission para a m\u00e1quina e a porta do servidor de banco de dados que voc\u00ea deseja se conectar.";
        t[734] = "No function outputs were registered.";
        t[735] = "Nenhum sa\u00edda de fun\u00e7\u00e3o foi registrada.";
        t[736] = "{0} function takes one and only one argument.";
        t[737] = "fun\u00e7\u00e3o {0} recebe somente um argumento.";
        t[744] = "This ResultSet is closed.";
        t[745] = "Este ResultSet est\u00e1 fechado.";
        t[746] = "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.";
        t[747] = "Caracter inv\u00e1lido foi encontrado. Isso \u00e9 mais comumente causado por dado armazenado que cont\u00e9m caracteres que s\u00e3o inv\u00e1lidos para a codifica\u00e7\u00e3o que foi criado o banco de dados. O exemplo mais comum disso \u00e9 armazenar dados de 8 bits em um banco de dados SQL_ASCII.";
        t[752] = "GSS Authentication failed";
        t[753] = "Autentica\u00e7\u00e3o GSS falhou";
        t[754] = "Ran out of memory retrieving query results.";
        t[755] = "Mem\u00f3ria insuficiente ao recuperar resultados da consulta.";
        t[756] = "Returning autogenerated keys is not supported.";
        t[757] = "Retorno de chaves geradas automaticamente n\u00e3o \u00e9 suportado.";
        t[760] = "Operation requires a scrollable ResultSet, but this ResultSet is FORWARD_ONLY.";
        t[761] = "Opera\u00e7\u00e3o requer um ResultSet rol\u00e1vel, mas este ResultSet \u00e9 FORWARD_ONLY (somente para frente).";
        t[762] = "A CallableStatement function was executed and the out parameter {0} was of type {1} however type {2} was registered.";
        t[763] = "Uma fun\u00e7\u00e3o foi executada e o par\u00e2metro de retorno {0} era do tipo {1} contudo tipo {2} foi registrado.";
        t[764] = "Unable to find server array type for provided name {0}.";
        t[765] = "N\u00e3o foi poss\u00edvel encontrar tipo matriz para nome fornecido {0}.";
        t[768] = "Unknown ResultSet holdability setting: {0}.";
        t[769] = "Defini\u00e7\u00e3o de durabilidade do ResultSet desconhecida: {0}.";
        t[772] = "Transaction isolation level {0} not supported.";
        t[773] = "N\u00edvel de isolamento da transa\u00e7\u00e3o {0} n\u00e3o \u00e9 suportado.";
        t[774] = "Zero bytes may not occur in identifiers.";
        t[775] = "Zero bytes n\u00e3o podem ocorrer em identificadores.";
        t[776] = "No results were returned by the query.";
        t[777] = "Nenhum resultado foi retornado pela consulta.";
        t[778] = "A CallableStatement was executed with nothing returned.";
        t[779] = "Uma fun\u00e7\u00e3o foi executada e nada foi retornado.";
        t[780] = "wasNull cannot be call before fetching a result.";
        t[781] = "wasNull n\u00e3o pode ser chamado antes de obter um resultado.";
        t[784] = "Returning autogenerated keys by column index is not supported.";
        t[785] = "Retorno de chaves geradas automaticamente por \u00edndice de coluna n\u00e3o \u00e9 suportado.";
        t[786] = "This statement does not declare an OUT parameter.  Use '{' ?= call ... '}' to declare one.";
        t[787] = "Este comando n\u00e3o declara um par\u00e2metro de sa\u00edda. Utilize '{' ?= chamada ... '}' para declarar um)";
        t[788] = "Can''t use relative move methods while on the insert row.";
        t[789] = "N\u00e3o pode utilizar m\u00e9todos de movimenta\u00e7\u00e3o relativos enquanto estiver inserindo registro.";
        t[790] = "A CallableStatement was executed with an invalid number of parameters";
        t[791] = "Uma fun\u00e7\u00e3o foi executada com um n\u00famero inv\u00e1lido de par\u00e2metros";
        t[792] = "Connection is busy with another transaction";
        t[793] = "Conex\u00e3o est\u00e1 ocupada com outra transa\u00e7\u00e3o";
        table = t;
    }
}

