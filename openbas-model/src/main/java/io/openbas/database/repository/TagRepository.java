package io.openbas.database.repository;

import io.openbas.database.model.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, String>, JpaSpecificationExecutor<Tag> {

    @NotNull
    Optional<Tag> findById(@NotNull String id);

    Optional<Tag> findByName(@NotNull String name);
}
