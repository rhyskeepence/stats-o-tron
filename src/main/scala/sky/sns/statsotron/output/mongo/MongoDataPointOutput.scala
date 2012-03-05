package sky.sns.statsotron.output.mongo

import sky.sns.statsotron.output.DataPointOutput
import sky.sns.statsotron.model.DataPoint
import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.Imports._

class MongoDataPointOutput(mongoStorage: MongoStorage) extends DataPointOutput {

  def write(dataPoint: DataPoint) {
    withCollection(dataPoint.environment) {
      collection =>
        collection += dataPointToMongoObject(dataPoint)
    }
  }

  private def dataPointToMongoObject: (DataPoint) => DBObject = {
    item =>
      val contentItemBuilder = MongoDBObject.newBuilder
      contentItemBuilder += "_id" -> item.timestamp

      item.metrics.foreach {
        metric =>
          contentItemBuilder += metric.name -> metric.value
      }

      contentItemBuilder.result()
  }

  private def withCollection[T](environment: String)(doWithContent: MongoCollection => T) = {
    val legalCollectionName = environment replaceAll("\\-", "_")
    mongoStorage.withCollection(legalCollectionName)(doWithContent)
  }
}
