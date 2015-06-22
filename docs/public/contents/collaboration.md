# Reactive Streamsのミドルウェア同士の連携

## Akka StreamsとRxJava

``` java
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorFlowMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.testkit.JavaTestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import rx.Observable;
import rx.RxReactiveStreams;

import scala.runtime.BoxedUnit;

import java.util.concurrent.TimeUnit;

public class CollaborationTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("ReactiveStreams");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void _RxJavaAndAkkaStreams() {
        new JavaTestKit(system) {
            {
                JavaTestKit probe = new JavaTestKit(system);
                LoggingAdapter log = Logging.getLogger(probe.getSystem(), this);

                long start = System.nanoTime();
                ActorFlowMaterializer mat = ActorFlowMaterializer.create(system);

                Subscriber<Integer> subscriber = RxReactiveStreams.toSubscriber(new rx.Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log.info("subscribe:" + integer.toString());
                    }
                });

                Observable<Integer> range = Observable.range(1, 10);

                Publisher<Integer> publisher = RxReactiveStreams.toPublisher(range);

                publisher.subscribe(subscriber);

                Source<Integer, BoxedUnit> source = Source
                        .from(publisher)
                        .map(i -> i * 2)
                        .scan(0, (prev, next) -> prev + next);

                Publisher<Integer> akkaPublisher = source.runWith(Sink.fanoutPublisher(1, 1), mat);

                RxReactiveStreams.toObservable(akkaPublisher).map(s -> "result:" + s).toBlocking().forEach(log::info);

                long end = System.nanoTime();

                log.info(TimeUnit.MILLISECONDS.convert((end - start), TimeUnit.NANOSECONDS) + "ms");
            }
        };
    }

}

```

## その他の連携サンプル

* Akka streamベース

  https://github.com/smaldini/ReactiveStreamsExamples/blob/master/src/main/java/org/reactivestreams/examples/Interop101.java

* Ratpackベース

  https://github.com/rkuhn/ReactiveStreamsInterop/blob/master/src%2Fmain%2Fjava%2Fcom%2Frolandkuhn%2Frsinterop%2FJavaMain.java

  https://github.com/ReactiveX/RxJavaReactiveStreams/blob/v1.0.1/examples/ratpack/src/test/java/rx/reactivestreams/example/ratpack/RatpackExamples.java
