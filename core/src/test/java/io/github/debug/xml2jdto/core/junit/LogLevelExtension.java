package io.github.debug.xml2jdto.core.junit;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * The LogLevelExtension class is a JUnit 5 extension that allows for the temporary modification of logging levels for specified packages during the
 * execution of test methods.
 * 
 * <p>
 * This extension implements the {@link BeforeEachCallback} and {@link AfterEachCallback} interfaces to set up and tear down logging levels before and
 * after each test method, respectively.
 * </p>
 * 
 * <p>
 * Usage:
 * </p>
 * 
 * <pre>
 * &#64;ExtendWith(LogLevelExtension.class)
 * public class MyTest {
 * 
 *     &#64;LogLevel(packageToLevel = { "com.example=DEBUG", "org.example=INFO" })
 *     &#64;Test
 *     public void testMethod() {
 *         // test code
 *     }
 * }
 * </pre>
 * 
 * <p>
 * The logging levels specified in the {@link LogLevel} annotation will be applied before the test method is executed and reverted back to their
 * original levels after the test method completes.
 * </p>
 * 
 * <p>
 * Note: This extension relies on the presence of a {@link LogLevel} annotation on the test methods to determine the desired logging levels.
 * </p>
 * 
 * @see BeforeEachCallback
 * @see AfterEachCallback
 * @see LogLevel
 * 
 * @author scheffer.imrich
 */
public class LogLevelExtension implements BeforeEachCallback, AfterEachCallback {

    private Map<String, Map<String, Level>> methodExistingPackageLogLevel = new HashMap<>();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        context.getTestMethod().ifPresent(method -> {
            // activate log level desired, remember what they were
            LogLevel logLevelAnnotation = method.getAnnotation(LogLevel.class);
            if (logLevelAnnotation != null) {
                activate(method, logLevelAnnotation.packageToLevel());
            }
        });
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

        context.getTestMethod().ifPresent(method -> {
            if (method.isAnnotationPresent(LogLevel.class)) {
                Map<String, Level> existingPackageLogLevel = methodExistingPackageLogLevel.get(method.getName());
                if (existingPackageLogLevel != null) {
                    // revert the log level back to what it was
                    if (!existingPackageLogLevel.isEmpty()) {
                        deactivate(existingPackageLogLevel);
                    }
                }
                ;
            }
        });
    }

    /**
     * Activates the logging level for the specified packages for the given method.
     * 
     * @param method
     *            the method for which the logging levels are being set
     * @param packageToLevel
     *            an array of strings where each string is in the format "package=level", specifying the package and the corresponding logging level
     *            to be set
     */
    protected void activate(Method method, String[] packageToLevel) {
        for (String pkgToLevel : packageToLevel) {
            String[] split = pkgToLevel.split("=");
            String pkg = split[0];
            String levelString = split[1];
            Logger logger = Logger.getLogger(pkg);
            Level level = logger.getLevel();
            methodExistingPackageLogLevel.putIfAbsent(method.getName(), new HashMap<>());
            Map<String, Level> existingPackageLogLevel = methodExistingPackageLogLevel.get(method.getName());
            existingPackageLogLevel.put(pkg, level);
            logger.setLevel(Level.parse(levelString));
        }
    }

    /**
     * Restores the log levels for the specified packages to their original levels.
     *
     * @param existingPackageLogLevel
     *            a map containing package names as keys and their corresponding log levels as values.
     */
    protected void deactivate(Map<String, Level> existingPackageLogLevel) {
        for (Map.Entry<String, Level> e : existingPackageLogLevel.entrySet()) {
            Logger.getLogger(e.getKey()).setLevel(e.getValue());
        }
    }

}
