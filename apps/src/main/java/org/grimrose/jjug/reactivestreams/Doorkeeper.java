package org.grimrose.jjug.reactivestreams;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface Doorkeeper {

    @GET("/events/{id}")
    public Observable<DoorkeeperEvent> findEventById(
            @Path("id") Long id
    );
}
