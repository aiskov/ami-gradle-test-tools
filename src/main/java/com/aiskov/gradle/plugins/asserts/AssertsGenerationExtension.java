package com.aiskov.gradle.plugins.asserts;

import lombok.Data;

import java.util.List;

@Data
public class AssertsGenerationExtension {
    String outputDir;
    List<String> targetClasses;
}
