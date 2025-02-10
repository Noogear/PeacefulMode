package cn.peacefulMode.Configuration.Form;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Comments {
    String[] value() default {};
}