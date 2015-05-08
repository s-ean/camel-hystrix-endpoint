# camel-hystrix-endpoint

[![Build Status](https://travis-ci.org/jollydays/camel-hystrix-endpoint.svg?branch=master)](https://travis-ci.org/jollydays/camel-hystrix-endpoint)

Camel endpoint which wraps child endpoints into a synchronous hystrix circuit breaker.

This project allows easy integration of the hystrix component into camel routes. The very simple example might look like this:

```java
	private class TestRoute extends RouteBuilder {

		@Override
		public void configure() throws Exception {
			onException(HystrixRuntimeException.class).handled(true).setBody().constant("error");
			
			from("direct:start").to("hystrix:mock:protectedRoute?groupId=test");
		}
	}
```
In this example the mock endpoint is protected by a hystrix endpoint by prefixing the mock endpoint with __hystrix__. The _groupId_ can be used to identify and configure circuit breakers in scaled environments.

It is not only possible to protect endpoints. When wrapping __direct__ endpoints with the hystrix component, whole sections of a route can be protected, including processors or endpoints:

```java
	private class TestRoute extends RouteBuilder {

		@Override
		public void configure() throws Exception {
			onException(HystrixRuntimeException.class).handled(true).setBody().constant("error");
			
			from("direct:start").to("hystrix:direct:protectedRoute?groupId=test");
			from("direct:protectedRoute").marsha().json().to("mock:result");
		}
	}
```

