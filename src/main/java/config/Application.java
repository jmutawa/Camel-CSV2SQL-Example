package config;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Application extends SingleRouteCamelConfiguration {

	@Bean
	public ActiveMQComponent activeMQComponent() {
		ActiveMQComponent component = new ActiveMQComponent();
		component.setBrokerURL("tcp://localhost:61616");
		component.setUserName("admin");
		component.setPassword("admin");
		return component;
	}

	@Override
	public RouteBuilder route() {
		return new RouteBuilder() {
			public void configure() {
				from("jms:queue:jalal")
				.split(body().tokenize("\r\n"))
				.unmarshal().csv()
				.to("velocity:sql.vm")
				.to("jms:queue:jalal2");
			}
		};
	}
	
	@Override
	protected void setupCamelContext(CamelContext camelContext) throws Exception {
		camelContext.addComponent("jms", activeMQComponent());
		camelContext.addRoutes(route());
	}
}
