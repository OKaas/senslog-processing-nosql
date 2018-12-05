package cz.senslog.processing.db.repository;

import cz.senslog.model.db.UserPrivilege;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "userPrivilege", path = "userPrivilege")
public interface UserPrivilegeRepository extends MongoRepository<UserPrivilege, ObjectId> {

    List<UserPrivilege> findAllByUserIdInAndUnitGroupId(List<ObjectId> usersIds, ObjectId unitGroupId);

    List<UserPrivilege> findAllByPrivilegesIdsContains(ObjectId privilegeId);

    /* --- Collaborates --- */
}


