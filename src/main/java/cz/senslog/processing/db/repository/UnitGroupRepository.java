package cz.senslog.processing.db.repository;

import cz.senslog.model.db.UnitGroup;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "unitGroup", path = "unitGroup")
public interface UnitGroupRepository extends MongoRepository<UnitGroup, ObjectId> {

    @Query("{ 'units.uid' : ?0 }")
    UnitGroup findByUnitsIsContaining(ObjectId unitId);
    /* --- Collaborates --- */
}


