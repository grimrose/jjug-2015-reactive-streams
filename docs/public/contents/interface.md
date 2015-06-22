## Reactive Streamsのインターフェースについて

Reactive Streamsを使う際には、主にPublisherとSubscriberの振る舞いを覚えておく必要があります。

それぞれのインターフェースは以下の様な振る舞いと役割を持っています。

* Publisherは、Subscriberにイベントを渡す。
* Publisherは、エラーまたは完了のイベントで終了する。
* Subscriberは、SubscriptionにもとづいてPublisherからのイベントを受け取る。
* Subscriberは、次のイベントを受け取る時、エラーまたは完了のイベント時の処理が必要。
* Subscriptionは、Subscriberがどれくらいリクエストを受け付ける事ができるのか、どういった時にキャンセル出来るのかを制御する。
* Processorは、PublisherとSubscriberの機能を兼ね備えている。

Reactorのドキュメントに詳しい内容が載っているので、参考にしてください。
また、それぞれのインターフェースのイメージもあります。

http://projectreactor.io/docs/reference/index.html#reactivestreams

http://projectreactor.io/docs/reference/images/rs.png


### Publisher

``` java
public interface Publisher<T> {
    public void subscribe(Subscriber<? super T> s);
}
```

### Subscriber

``` java
public interface Subscriber<T> {
    public void onSubscribe(Subscription s);
    public void onNext(T t);
    public void onError(Throwable t);
    public void onComplete();
}
```

### Subscription

``` java
public interface Subscription {
    public void request(long n);
    public void cancel();
}
```

### Processor

``` java
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}
```
