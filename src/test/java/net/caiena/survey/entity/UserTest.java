package net.caiena.survey.entity;

import net.caiena.survey.entity.builder.UserBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author bzumpano
 * @since 3/28/16
 *
 * Testes JSR-303
 * nomenclatura de método:
 *      "assert[Tipo de validação][Nome do campo]"
 *      ex: assertNotNullUsername()
 */
public class UserTest {

    private static Validator validator;

    @BeforeClass
    public static void init() {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void assertSuccess() {
        final User user = new UserBuilder().build();

        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void assertNotNullUsername() {
        final User user = new UserBuilder()
                .username(null).build();

        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assert.assertTrue(violations.size() == 1);
    }

    @Test
    public void assertNotNullPassword() {
        final User user = new UserBuilder().password(null).build();

        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assert.assertTrue(violations.size() == 1);
    }

    @Test
    public void assertNotNullRole() {
        final User user = new UserBuilder().role(null).build();

        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assert.assertTrue(violations.size() == 1);
    }
}
