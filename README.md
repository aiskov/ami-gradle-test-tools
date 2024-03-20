Gradle Test Tools
=================

This plugin provides a set of test tools generators for Java projects. It allows generating assertions and fixtures 
for target classes.

## Requirements

Assertions Generator:
* AssertJ

Fixtures Generator
* Lombok
  * Target classes of fixtures should be annotated with `@Builder` 

## Configuration

Generation of assertions:

```groovy
generateAssertions {
    outputDir = 'src/test/generated'
    targetClasses = [
            'com.aiskov.example.Response1',
            'com.aiskov.example.Response2'
    ]
}
```

Generation of fixtures:

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

Add the generated source to test classpath:

```groovy
sourceSets {
    test {
        java {
            srcDirs += 'src/test/generated'
        }
    }
}
```
