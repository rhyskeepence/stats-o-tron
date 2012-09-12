resolvers += Resolver.url("artifactory", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

resolvers += "rhys's releases" at "https://github.com/rhyskeepence/mvn-repo/raw/master/releases"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.4")

addSbtPlugin("com.gu" % "sbt-teamcity-test-reporting-plugin" % "1.2")
