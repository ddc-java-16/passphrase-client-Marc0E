package edu.cnm.deepdive.passphrase.service;

import edu.cnm.deepdive.passphrase.model.Passphrase;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PassphraseRepository {

  private final PassphraseServiceProxy serviceProxy;
  private final GoogleSignInService signInService;
  @Inject
  PassphraseRepository(PassphraseServiceProxy serviceProxy, GoogleSignInService signInService) {
    this.serviceProxy = serviceProxy;
    this.signInService = signInService;
  }

  public Single<Passphrase> get(String key) {
    return signInService
        .refreshBearerToken()
        .flatMap((token) -> serviceProxy.get(key, token));
  }

  public Single<List<Passphrase>> get(){
    return signInService
        .refreshBearerToken()
        .flatMap(serviceProxy::get);
  }

  public Single<List<Passphrase>> search(String fragment){
    return signInService
        .refreshBearerToken()
        .flatMap((token) -> serviceProxy.search(fragment, token));
  }

  public Single<Passphrase> add(Passphrase passphrase){
    return signInService
        .refreshBearerToken()
        .flatMap((token) -> serviceProxy.post(passphrase, token));
  }

  public Completable delete(Passphrase passphrase){
    return delete(passphrase.getKey());
  }

  public Completable delete(String key){
    return signInService
        .refreshBearerToken()
        .flatMapCompletable((token) -> serviceProxy.delete(key, token));
  }

  public Single<Passphrase> replace(Passphrase passphrase){
    return signInService
        .refreshBearerToken()
        .flatMap((token) -> serviceProxy.put(passphrase.getKey(), passphrase, token));
  }

}
