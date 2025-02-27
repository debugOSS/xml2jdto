package io.github.debug.xml2jdto.core.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify log levels for specific packages.
 * 
 * <p>
 * This annotation can be applied to methods to define the log levels for different packages during the execution of the annotated method.
 * </p>
 * 
 * <p>
 * Usage example:
 * </p>
 * 
 * <pre>
 * {@code
 * @LogLevel(packageToLevel = { "com.example.package1:DEBUG", "com.example.package2:INFO" })
 * public void someMethod() {
 *     // method implementation
 * }
 * }
 * </pre>
 * 
 * <p>
 * The format for each entry in the {@code packageToLevel} array is {@code "packageName:logLevel"}, where {@code packageName} is the name of the
 * package and {@code logLevel} is the desired log level for that package.
 * </p>
 * 
 * <p>
 * This annotation is inherited, meaning that if a class is annotated with {@code @LogLevel}, the annotation will also apply to its subclasses.
 * </p>
 * 
 * @see java.lang.annotation.ElementType
 * @see java.lang.annotation.RetentionPolicy
 * @see java.lang.annotation.Inherited
 * 
 * @author scheffer.imrich
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LogLevel {
    String[] packageToLevel();
}
