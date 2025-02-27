package io.github.debug.xml2jdto.core.junit;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * A JUnit {@link MethodRule} that allows setting and reverting log levels for specific packages during the execution of a test method.
 * 
 * <p>
 * This rule activates the specified log levels before the test method is executed and reverts them back to their original levels after the test
 * method completes.
 * </p>
 * 
 * <p>
 * Usage:
 * </p>
 * 
 * <pre>
 * {@code
 * &#64;Rule
 * public LogLevelRule logLevelRule = new LogLevelRule();
 * 
 * @LogLevel(packageToLevel = {"com.example=DEBUG", "org.example=INFO"})
 * public void testMethod() {
 *     // test code
 * }
 * }
 * </pre>
 * 
 * <p>
 * The {@code @LogLevel} annotation is used to specify the log levels for the packages. The format for each package-level pair is "package=level".
 * </p>
 * 
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * {@code
 * @LogLevel(packageToLevel = { "com.example=DEBUG", "org.example=INFO" })
 * public void testMethod() {
 *     // test code
 * }
 * }
 * </pre>
 * 
 * <p>
 * This rule ensures that the log levels are reverted even if the test method throws an exception.
 * </p>
 * 
 * @see MethodRule
 * @see LogLevel
 * 
 * @author scheffer.imrich
 */
public class LogLevelRule implements MethodRule {

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                // activate log level desired, remember what they were
                Map<String, Level> existingPackageLogLevel = new HashMap<>();
                LogLevel logLevelAnnotation = method.getAnnotation(LogLevel.class);
                if (logLevelAnnotation != null) {
                    activate(logLevelAnnotation.packageToLevel(), existingPackageLogLevel);
                }

                // run the test
                Throwable testFailure = evaluateSafely(base);

                // revert the log level back to what it was
                if (!existingPackageLogLevel.isEmpty()) {
                    deactivate(existingPackageLogLevel);
                }

                if (testFailure != null) {
                    throw testFailure;
                }
            }

            /**
             * Execute the test safely so that if it fails, we can still revert the log level
             * 
             * @param base
             *            the {@link Statement} to be evaluated
             * @return the {@link Throwable} thrown during the evaluation, or {@code null} if no exception was thrown
             */
            private Throwable evaluateSafely(Statement base) {
                try {
                    base.evaluate();
                    return null;
                } catch (Throwable throwable) {
                    return throwable;
                }
            }
        };
    }

    /**
     * Activates the logging levels for the specified packages.
     *
     * @param packageToLevel
     *            an array of strings where each string is in the format "package=level", specifying the logging level to be set for each package.
     * @param existingPackageLogLevel
     *            a map to store the existing logging levels of the specified packages. The keys are package names and the values are their
     *            corresponding logging levels.
     */
    protected void activate(String[] packageToLevel, Map<String, Level> existingPackageLogLevel) {
        for (String pkgToLevel : packageToLevel) {
            String[] split = pkgToLevel.split("=");
            String pkg = split[0];
            String levelString = split[1];
            Logger logger = Logger.getLogger(pkg);
            // Logger logger = LogManager.getLogger(pkg);
            Level level = logger.getLevel();
            existingPackageLogLevel.put(pkg, level);
            logger.setLevel(Level.parse(levelString));
        }
    }

    /**
     * Deactivates the logging levels for the specified packages.
     * 
     * This method restores the logging levels for the packages specified in the provided map. Each entry in the map represents a package name and its
     * corresponding logging level to be restored.
     * 
     * @param existingPackageLogLevel
     *            a map where the key is the package name and the value is the logging level to be restored
     */
    protected void deactivate(Map<String, Level> existingPackageLogLevel) {
        for (Map.Entry<String, Level> e : existingPackageLogLevel.entrySet()) {
            Logger.getLogger(e.getKey()).setLevel(e.getValue());
        }
    }
}
