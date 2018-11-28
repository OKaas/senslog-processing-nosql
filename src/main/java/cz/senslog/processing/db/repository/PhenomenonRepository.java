package cz.senslog.processing.db.repository;

import cz.senslog.model.db.Phenomenon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "phenomenon", path = "phenomenon")
public interface PhenomenonRepository extends MongoRepository<Phenomenon, String> {

    /* --- Collaborates --- */
}


