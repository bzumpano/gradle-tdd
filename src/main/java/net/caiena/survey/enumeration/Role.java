package net.caiena.survey.enumeration;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author bzumpano
 * @since 3/23/16
 */
public enum Role implements GrantedAuthority {

    ADMIN,
    USER;

    private final String ROLE_PREFIX = "ROLE_";

    @Override
    public String getAuthority() {
        return ROLE_PREFIX + this.name().toUpperCase();
    }
}
