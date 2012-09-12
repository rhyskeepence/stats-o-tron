package statsotron.output

import statsotron.model.DataPoint

trait DataPointOutput {
  def write(dataPoint: DataPoint)
}
