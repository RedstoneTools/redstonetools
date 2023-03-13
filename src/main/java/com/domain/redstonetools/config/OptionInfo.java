package com.domain.redstonetools.config;

import com.domain.redstonetools.features.arguments.Argument;
import com.domain.redstonetools.features.arguments.TypeSerializer;

// caches info about a setting
// compiled from an argument
public record OptionInfo(FeatureConfiguration configuration,
                         String section /* the section this should be under */,
                         String name,
                         Argument<?> argument,
                         TypeSerializer<Object, Object> type,
                         Accessor<Object> accessor /* provides get() and set() for values */) {

}
