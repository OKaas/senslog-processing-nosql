package cz.senslog.processing.db.repository;

import cz.senslog.model.db.Observation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by OK on 02-Nov-18.
 */
@RepositoryRestResource(collectionResourceRel = "observation", path = "observation")
public interface ObservationRepository extends MongoRepository<Observation, ObjectId> {

    /* --- Collaborates --- */
}


