package net.caiena.survey.entity.builder;

import net.caiena.survey.entity.User;
import net.caiena.survey.enumeration.Role;

/**
 * @author bzumpano
 * @since 3/28/16
 */
public class UserBuilder {

    private Long id = 0L;

    private String username = "user";

    private String password = "password";

    private Role role = Role.USER;

    public UserBuilder id(final Long id) {
        this.id = id;
        return this;
    }

    public UserBuilder username(final String username) {
        this.username = username;
        return this;
    }

    public UserBuilder password(final String password) {
        this.password = password;
        return this;
    }

    public UserBuilder role(final Role role) {
        this.role = role;
        return this;
    }

    public User build() {
        final User user = new User();

        user.setId(this.id);
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setRole(this.role);

        return user;
    }
}
