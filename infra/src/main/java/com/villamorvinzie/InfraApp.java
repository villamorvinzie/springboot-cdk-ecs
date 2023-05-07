package com.villamorvinzie;

import org.apache.commons.configuration2.ex.ConfigurationException;

import com.villamorvinzie.config.AppConfig;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class InfraApp {
    public static void main(final String[] args) throws ConfigurationException {
        App app = new App();
        AppConfig config = AppConfig.build(null);

        new InfraStack(app, "SCEInfraTask", StackProps.builder()
                .env(Environment.builder()
                        .account(config.getStringValue("cdk.default.account"))
                        .region(config.getStringValue("cdk.default.region"))
                        .build())
                .build(), config);

        app.synth();
    }
}
