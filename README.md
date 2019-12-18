# Java serialization fix for Kotlin object

Solves the problem of Kotlin object being having different instance after deserialization using built-in Java serialization.
The issue is described quite well [here](https://blog.stylingandroid.com/kotlin-serializable-objects/).

The plugin adds `readResolve` method for every object which either:
 - implements `java.util.Serializable`;
 - extends a class implementing `java.util.Serializable`.

## Getting it:

```groovy
// build.gradle

buildscript {
    classpath 'me.shika:kotlin-object-java-serialization:1.0.0'
}

apply plugin: 'me.shika.kotlin-object-java-serialization'
```
