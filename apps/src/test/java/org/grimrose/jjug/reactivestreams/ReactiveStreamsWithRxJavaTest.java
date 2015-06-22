package org.grimrose.jjug.reactivestreams;

import com.squareup.okhttp.OkHttpClient;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import rx.Observable;
import rx.RxReactiveStreams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReactiveStreamsWithRxJavaTest {

    private static Logger logger = LoggerFactory.getLogger(ReactiveStreamsWithRxJavaTest.class);

    @Test
    public void _scenario_1() throws Exception {
        /**
         * まず、送り手が送るソースとなるものを用意する。
         */
        List<Integer> list =  IntStream.range(0, 5).boxed().collect(Collectors.toList());

        /**
         * 今回は、RxJavaを通してReactive Streamsを見るため、一旦Observableにする。
         */
        Observable<Integer> observable = Observable.from(list);

        /**
         * RxReactiveStreamsを用いてObservableから、Publisherへ変換する
         */
        Publisher<Integer> publisher = RxReactiveStreams.toPublisher(observable);

        /**
         * 受け手側のSubscriberを用意する。
         */
        List<Integer> collection = new ArrayList<>();
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer value) {
                collection.add(value);
                logger.info("onNext:" + value);
            }

            @Override
            public void onError(Throwable e) {
                logger.error(e.getMessage(), e);
            }

            @Override
            public void onComplete() {

            }
        };

        /**
         * 送り手に受け手を登録する。
         */
        publisher.subscribe(subscriber);

        // Verify
        assertThat(collection.size(), is(5));
    }

    @Test
    public void _RxJava_Subscriber_to_ReactiveStreams_Subscriber() throws Exception {
        // Setup
        Observable<Integer> observable = Observable.range(10, 5);
        Publisher<Integer> publisher = RxReactiveStreams.toPublisher(observable);

        List<Integer> collection = new ArrayList<>();
        /**
         * RxJavaのSubscriberをReactiveStreamsのSubscriberへ変換する。
         */
        Subscriber<Integer> subscriber = RxReactiveStreams.toSubscriber(new rx.Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                logger.error(e.getMessage(), e);
            }

            @Override
            public void onNext(Integer value) {
                collection.add(value);
                logger.info("onNext:" + value);
            }
        });

        // Exercise
        publisher.subscribe(subscriber);

        // Verify
        assertThat(collection.size(), is(5));
    }

    @Test
    public void _From_Other_Publisher() throws Exception {
        // Setup
        Publisher<DoorkeeperEvent> publisher = fromOtherPublisher();
        /**
         * 他のPublisherからRxJavaのObservableへ変換する。
         */
        Observable<DoorkeeperEvent> fromObservable = RxReactiveStreams.toObservable(publisher);

        // Exercise
        List<DoorkeeperEvent> collection = new ArrayList<>();
        fromObservable.toBlocking().forEach(value -> {
            collection.add(value);
            logger.info("forEach:{}", value);
        });

        // Verify
        assertThat(collection.size(), is(1));
    }

    private Publisher<DoorkeeperEvent> fromOtherPublisher() {
        /**
         * DoorkeeperのAPIを利用する
         */
        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint("http://api.doorkeeper.jp/")
                .setClient(new OkClient(new OkHttpClient()))
                .setRequestInterceptor(request -> request.addHeader("content-type", "application/json"))
                .build();
        Doorkeeper doorkeeper = retrofit.create(Doorkeeper.class);
        /**
         * 今回のイベントを探す。
         */
        Observable<DoorkeeperEvent> events = doorkeeper.findEventById(26547L);
        return RxReactiveStreams.toPublisher(events);
    }

}
