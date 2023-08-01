package org.pidu.javamalloc;

import io.grpc.stub.StreamObserver;
import org.pidu.proto.GreeterGrpc;
import org.pidu.proto.GreetingsServer;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    private static final int PERIOD_MILLIS = 10_000;
    private final AtomicLong itemsNumber = new AtomicLong(0);

    private final AtomicLong previousItems = new AtomicLong(0);

    private final AtomicBoolean access = new AtomicBoolean(false);

    private final AtomicLong time = new AtomicLong(0);

    private static StringBuilder multiplyInputName(GreetingsServer.HelloRequest req) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            builder.append(req.getName()).append(";");
        }
        return builder;
    }


    @Override
    public void sayHello(GreetingsServer.HelloRequest req, StreamObserver<GreetingsServer.HelloReply> responseObserver) {
        measure();
        StringBuilder builder = multiplyInputName(req);
        GreetingsServer.HelloReply reply = GreetingsServer.HelloReply.newBuilder()
                .setMessage(builder.append("Hello ").toString())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    private void measure() {
        Instant currentTime = Instant.now();
        if (currentTime.toEpochMilli() - time.get() > PERIOD_MILLIS && access.compareAndSet(false, true)) {
            long currentItems = itemsNumber.get();
            double itemsPerSecond = (currentItems - previousItems.get()) * 1.0 / PERIOD_MILLIS * 1000.0;
            System.out.println(currentTime + " : " + itemsPerSecond);
            previousItems.set(currentItems);
            access.set(false);
            time.set(currentTime.toEpochMilli());
        }
        itemsNumber.incrementAndGet();
    }
}
