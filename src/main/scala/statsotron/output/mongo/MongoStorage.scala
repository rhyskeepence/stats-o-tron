package statsotron.output.mongo

import com.mongodb.casbah.{MongoCollection, MongoConnection}

class MongoStorage(host: String, port: Int) {
  def withCollection[T](collectionName: String)(actionOnCollection: MongoCollection => T) = {
    val mongo = MongoConnection(host, port)
    val snoggin = mongo("snoggin")

    try {
      val collection = snoggin(collectionName)
      actionOnCollection(collection)

    } finally {
      mongo.close()
    }
  }
}
