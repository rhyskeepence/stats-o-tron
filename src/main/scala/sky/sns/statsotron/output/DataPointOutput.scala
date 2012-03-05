package sky.sns.statsotron.output

import sky.sns.statsotron.model.DataPoint

trait DataPointOutput {
  def write(dataPoint: DataPoint)
}
