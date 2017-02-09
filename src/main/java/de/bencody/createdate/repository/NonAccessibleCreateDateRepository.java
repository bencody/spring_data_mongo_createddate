package de.bencody.createdate.repository;

import de.bencody.createdate.model.NonAccessibleCreateDateModel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by bco on 09.02.2017.
 */
public interface NonAccessibleCreateDateRepository extends MongoRepository<NonAccessibleCreateDateModel, ObjectId> {
}
