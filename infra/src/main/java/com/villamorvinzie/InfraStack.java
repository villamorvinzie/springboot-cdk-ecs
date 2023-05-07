package com.villamorvinzie;

import java.util.List;
import java.util.Map;

import com.villamorvinzie.config.AppConfig;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.autoscaling.AutoScalingGroup;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcLookupOptions;
import software.amazon.awscdk.services.ecr.IRepository;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecs.AsgCapacityProvider;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.Ec2TaskDefinition;
import software.amazon.awscdk.services.ecs.EcsOptimizedImage;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.NetworkMode;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedEc2Service;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;

public class InfraStack extends Stack {
    public InfraStack(final Construct scope, final String id, AppConfig config) {
        this(scope, id, null, config);
    }

    public InfraStack(final Construct scope, final String id, final StackProps props, AppConfig config) {
        super(scope, id, props);

        final String ecrRepoName = config.getStringValue("ecr.repository.name");
        final String asgInstanceType = config.getStringValue("asg.instance.type");
        final String vpcId = config.getStringValue("vpc.id");
        final int asgMaxCapacity = config.getIntValue("asg.instance.max.capacity");
        final int asgMinCapacity = config.getIntValue("asg.instance.min.capacity");
        final String weatherStackAccessKey = config.getStringValue("weatherstack.access.key");

        // VPC
        IVpc vpc = Vpc.fromLookup(this, "SceVpc", VpcLookupOptions.builder().vpcId(vpcId).build());

        // ECR
        IRepository repository = Repository.fromRepositoryName(this, "SceEcrRepository", ecrRepoName);

        // ASG
        AutoScalingGroup autoScalingGroup = AutoScalingGroup.Builder.create(this, "SceAsg")
                .autoScalingGroupName("sce-asg")
                .vpc(vpc)
                .instanceType(new InstanceType(asgInstanceType))
                .machineImage(EcsOptimizedImage.amazonLinux2())
                .minCapacity(asgMinCapacity)
                .maxCapacity(asgMaxCapacity)
                .build();

        AsgCapacityProvider capacityProvider = AsgCapacityProvider.Builder.create(this, "AsgCapacityProvider")
                .autoScalingGroup(autoScalingGroup)
                .build();

        // ECS Task Definition
        Ec2TaskDefinition taskDefinition = Ec2TaskDefinition.Builder.create(this, "SceTaskDef")
                .networkMode(NetworkMode.AWS_VPC)
                .family("sce-task-definiton")
                .build();

        taskDefinition.addContainer("sce-app", ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromEcrRepository(repository))
                .cpu(512)
                .memoryReservationMiB(512)
                .portMappings(List.of(
                        PortMapping.builder().hostPort(8080).containerPort(8080)
                                .protocol(Protocol.TCP).build()))
                .environment(Map.of("WEATHERSTACK_ACCESS_KEY", weatherStackAccessKey))
                .logging(LogDriver.awsLogs(
                        AwsLogDriverProps.builder().streamPrefix("sce-app")
                                .logRetention(RetentionDays.ONE_DAY)
                                .build()))
                .build());

        // ECS Cluster
        Cluster cluster = Cluster.Builder.create(this, "SceEcsCluster")
                .vpc(vpc)
                .clusterName("sce-cluster")
                .build();

        cluster.addAsgCapacityProvider(capacityProvider);

        // ECS Service
        ApplicationLoadBalancedEc2Service loadBalancedEcsService = ApplicationLoadBalancedEc2Service.Builder
                .create(this, "Service")
                .cluster(cluster)
                .loadBalancerName("sce-alb")
                .serviceName("sce-service")
                .taskDefinition(taskDefinition)
                .desiredCount(1)
                .build();

        loadBalancedEcsService.getTargetGroup()
                .configureHealthCheck(HealthCheck.builder().path("/actuator/health").build());
    }
}
