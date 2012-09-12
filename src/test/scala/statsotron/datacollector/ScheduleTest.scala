package statsotron.datacollector

import org.specs.SpecificationWithJUnit
import java.io.File

class ScheduleTest extends SpecificationWithJUnit {

  "Environment name is config file name without .xml extension" in {
    Schedule(new File("x/y/z/my-environment.xml")).environment mustEqual "my-environment"
  }

  "When extension doesnt exist" in {
    Schedule(new File("x/y/z/some.environment")).environment mustEqual "some.environment"
  }
  
}
