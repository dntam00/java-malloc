package org.pidu.sim;

import com.github.phisgr.gatling.kt.grpc.GrpcDsl;
import com.github.phisgr.gatling.kt.grpc.StaticGrpcProtocol;
import com.github.phisgr.gatling.kt.grpc.action.GrpcCallActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.grpc.ManagedChannelBuilder;
import org.pidu.proto.Main;
import org.pidu.proto.GreeterGrpc;
import org.pidu.proto.GreetingsServer;

import java.time.LocalDateTime;

import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;

public class GatlingSimulation extends Simulation {

    final String text = createText();
    final StaticGrpcProtocol protocol = GrpcDsl.grpc(ManagedChannelBuilder.forAddress("memory-fragment-test.default.svc.cluster.local", Main.PORT).usePlaintext())
            .disableWarmUp()
            .shareChannel();

    final ScenarioBuilder scn = scenario("GreetingsServiceScenario")
            .exec(getRequest("helloGrpc"))
            .exec(session -> {
                System.out.println("send " + LocalDateTime.now());
                return session;
            });

    {
        setUp(scn.injectClosed(constantConcurrentUsers(20).during(1000000),
                        constantConcurrentUsers(20).during(10000000))
                .protocols(protocol));
    }

    private static String createText() {
        String textToMultiply = "onionapple|";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            builder.append(textToMultiply);
        }
        return builder.toString();
    }

    private GrpcCallActionBuilder<GreetingsServer.HelloRequest, GreetingsServer.HelloReply> getRequest(String name) {
        return GrpcDsl.grpc(name)
                .rpc(GreeterGrpc.getSayHelloMethod())
                .payload(session -> GreetingsServer.HelloRequest.newBuilder()
                        .setName(text)
                        .build());
    }

}
