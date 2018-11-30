package cz.senslog.processing.db.repository;

import cz.senslog.model.db.Unit;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "unit", path = "unit")
public interface UnitRepository extends MongoRepository<Unit, ObjectId> {

    /* --- Collaborates --- */
}


