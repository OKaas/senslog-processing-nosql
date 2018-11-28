package cz.senslog.processing.db.repository;

import cz.senslog.model.db.Observation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "userGroup", path = "userGroup")
public interface UserGroupRepository extends MongoRepository<Observation, String> {

    /* --- Collaborates --- */
}


