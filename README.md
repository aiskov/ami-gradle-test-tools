Gradle Test Tools
=================

This plugin provides a set of test tools generators for Java projects. It allows generating assertions and fixtures 
for target classes.

## Features

This plugin provides the following features:

* Assertions Generator
* Fixtures Generator
  * Using documentation annotations with examples (it may use any annotation that have `example` param)
  * Using random values

## Getting Started

To use this plugin, include it in your `build.gradle` file:

```groovy
plugins {
  id 'com.aiskov.gradle-test-tool' version '1.0.1'
}
```

## Requirements

Assertions Generator:
* AssertJ
* Apache Commons Lang 3 (`org.apache.commons:commons-lang3`)

Fixtures Generator
* Lombok
  * Target classes of fixtures should be annotated with `@Builder` 

## Configuration

### Assertions Generator

To generate assertions, configure the `generateAssertions` task in your `build.gradle`:

```groovy
generateAssertions {
    outputDir = 'src/test/generated'
    targetClasses = [
            'com.aiskov.example.Response1',
            'com.aiskov.example.Response2'
    ]
}
```

### Fixtures Generator

To generate fixtures, configure the `generateFixtures` task in your `build.gradle`:

```groovy
generateFixtures {
    outputDir = 'src/test/generated'
    targetClasses = [
            'com.aiskov.example.Command1',
            'com.aiskov.example.Command2'
    ]
    valueProviderSnippets = [
            'orderId': '"ORDER-" + RANDOM.nextInt(9999) + " (" + java.util.UUID.randomUUID().toString().substring(0, 8) + ")"'
    ]
}
```

### Generated sources

Add generated source to test classpath:

```groovy
sourceSets {
    test {
        java {
            srcDirs += 'src/test/generated'
        }
    }
}
```

## Contributing
Contributions are welcome. Please submit a pull request with your changes.
