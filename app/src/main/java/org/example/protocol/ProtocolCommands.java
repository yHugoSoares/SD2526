package protocol;

/**
 * Definições de comandos do protocolo binário
 * Cada comando é um inteiro de identificação única
 */
public class ProtocolCommands {
    // Autenticação
    public static final int REGISTER = 1;
    public static final int LOGIN = 2;
    
    // Operações
    public static final int ADD_EVENT = 10;
    public static final int NEXT_DAY = 11;
    public static final int GET_QUANTITY = 20;
    public static final int GET_VOLUME = 21;
    public static final int GET_PRICE_STATS = 22;
    public static final int GET_EVENTS = 23;
    public static final int WAIT_SIMULTANEOUS = 30;
    public static final int WAIT_CONSECUTIVE = 31;
    
    // Respostas
    public static final int RESPONSE_SUCCESS = 100;
    public static final int RESPONSE_ERROR = 101;
}