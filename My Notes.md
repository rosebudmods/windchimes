## Useful Commands For Myself

#### Change Active Version

`./gradlew use {minecraft version}`

#### Run

```
./gradlew clientFabric
./gradlew clientNeoForge
./gradlew serverFabric
./gradlew serverNeoForge
```

#### Build
```
./gradlew buildJars {minecraft version}
or run 'Build All Jars' task to build all versions
```

#### New Versions?
Add them in `/gradle/stonecutter-targets.gradle`.

The key is the version given by the developer when using `./gradlew use {version}`, and the value is the project version it gets mapped to.

For example 1.21 and 1.21.1 use the same jar, and uses the 1.21.1 source to build from.

#### ToDo Before Committing
- `./gradlew reset` to set active to the default 1.21 branch