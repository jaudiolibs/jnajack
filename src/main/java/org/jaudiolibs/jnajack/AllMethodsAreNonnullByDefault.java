package org.jaudiolibs.jnajack;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * This annotation can be applied to a package, class or method to indicate that all
 * method parameters and return values in that element are nonnull by default unless overridden.
 *
 * Modified from <a href="https://stackoverflow.com/a/13429092/7175336">David Harkness on StackOverflow</a>
 */
@Documented
@Nonnull
@TypeQualifierDefault({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@interface AllMethodsAreNonnullByDefault {
}