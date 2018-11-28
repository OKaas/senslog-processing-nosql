package cz.senslog.processing.db.repository;

import cz.senslog.model.db.Privilege;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "privilege", path = "privilege")
public interface PrivilegeRepository extends MongoRepository<Privilege, String> {

    /* --- Collaborates --- */
}


