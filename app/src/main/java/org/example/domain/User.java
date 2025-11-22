package org.example.domain;

/**
 * Representa um utilizador autenticado
 */
public class User {
    private String username;
    private String passwordHash;
    
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }
    
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    
    // Hash simples para demonstração (em produção usar bcrypt)
    public static String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }
}
