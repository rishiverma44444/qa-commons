package com.noonpay.qa.common.custom.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TestDataSetup {

	String endpoint();
	
	String tearup() default "";
	
	String teardown()  default "";
	
}
