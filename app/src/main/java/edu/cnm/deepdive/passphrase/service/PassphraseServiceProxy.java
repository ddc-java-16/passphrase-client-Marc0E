package edu.cnm.deepdive.passphrase.service;

import edu.cnm.deepdive.passphrase.model.Passphrase;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PassphraseServiceProxy {

  @GET("passphrases/{key}")
  Single<Passphrase> get(@Path("key") String key, @Header("Authorization") String bearerToken);

  @GET("passphrases")
  Single<List<Passphrase>> get(@Header("Authorization") String bearerToken);

  @GET("passphrases")
Single<List<Passphrase>> search(@Query("q") String fragment, @Header("Authorization") String bearerToken);

  @POST("passphrases")
  Single<Passphrase> post(@Body Passphrase passphrase, @Header("Authorization") String bearerToken);

  @DELETE("passphrases/{key}")
  Completable delete(@Path("key") String key, @Header("Authorization") String bearerToken);

 @Headers("Content-Type: application/merge-patch+json")
  @PATCH("passphrases/{key}")
  Single<Passphrase> patch(@Path("key") String key,@Body Passphrase passphrase, @Header("Authorization") String bearerToken);

  @PUT("passphrases/{key}/name")
  Single<String> put(@Path("key") String key,@Body String name, @Header("Authorization") String bearerToken);

  @PUT("passphrases/{key}/words")
  Single<List<String>> put(@Path("key") String key,@Body List<String> words, @Header("Authorization") String bearerToken);

  @POST("passphrases/generate")
  Single<List<String>> generate(@Query("length") int length, @Header("Authorization") String bearerToken);
}
