package net.caiena.survey.repository;

import net.caiena.survey.entity.Survey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author bzumpano
 * @since 3/24/16
 */
@Repository
public interface SurveyRepository extends CrudRepository<Survey, Long> {
}
