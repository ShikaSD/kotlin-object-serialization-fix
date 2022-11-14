# Java serialization fix for Kotlin object

Solves the problem of Kotlin object being having different instance after deserialization using built-in Java serialization.
The issue is described quite well [here](https://blog.stylingandroid.com/kotlin-serializable-objects/).

The plugin adds `readResolve` method for every object which either:
 - implements `java.io.Serializable`;
 - extends a class implementing `java.io.Serializable`.

## Getting it:

```groovy
// build.gradle

// plugins dsl
plugins {
  id "me.shika.kotlin-object-java-serialization" version "1.4.0"
}

// or else
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "me.shika:kotlin-object-java-serialization:1.4.0"
  }
}

apply plugin: 'me.shika.kotlin-object-java-serialization'
```
