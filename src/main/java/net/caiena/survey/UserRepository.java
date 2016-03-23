package net.caiena.survey;

import net.caiena.survey.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author bzumpano
 * @since 3/23/16
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
