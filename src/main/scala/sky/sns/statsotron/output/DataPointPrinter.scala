package sky.sns.statsotron.output

import sky.sns.statsotron.model.DataPoint

class DataPointPrinter extends DataPointOutput {
  def write(dataPoint: DataPoint) = println(dataPoint)
}
