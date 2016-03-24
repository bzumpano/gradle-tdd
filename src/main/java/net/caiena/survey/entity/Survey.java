package net.caiena.survey.entity;

import javax.persistence.*;

/**
 * @author bzumpano
 * @since 3/24/16
 */
@Entity
@Table(name = "SURVEYS")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
