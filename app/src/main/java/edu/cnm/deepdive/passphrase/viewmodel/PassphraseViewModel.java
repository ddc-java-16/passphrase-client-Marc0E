package edu.cnm.deepdive.passphrase.viewmodel;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.passphrase.model.Passphrase;
import edu.cnm.deepdive.passphrase.service.PassphraseRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

@HiltViewModel
public class PassphraseViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final PassphraseRepository repository;
  private final MutableLiveData<Passphrase> passphrase;
  private final MutableLiveData<List<Passphrase>> passphrases;
  private final MutableLiveData<List<String>> generated;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  @Inject
  PassphraseViewModel(@ApplicationContext Context context, PassphraseRepository repository){
    this.repository = repository;
    passphrase = new MutableLiveData<>();
    passphrases = new MutableLiveData<>();
    generated = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    fetch();
  }

  public LiveData<Passphrase> getPassphrase() {
    return passphrase;
  }

  public LiveData<List<Passphrase>> getPassphrases() {
    return passphrases;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void fetch(String key){
    execute(repository.get(key), passphrase::postValue);
//    repository without the helper method.
//        .get(key)
//        .subscribe(this.passphrase::postValue, this::postThrowable, pending);
  }

  public LiveData<List<String>> getGenerated() {
    return generated;
  }

  public void fetch(){
    execute(repository.get(), passphrases::postValue);
//    repository
//        .get()
//        .subscribe(this.passphrases::postValue, this::postThrowable, pending);
  }

  public void search(String fragment){
    execute(repository.search(fragment), passphrases::postValue);
//    repository
//        .search(fragment)
//        .subscribe(this.passphrases::postValue, this::postThrowable, pending);
  }

  public void save(Passphrase passphrase){
    execute(repository.save(passphrase), this.passphrase::postValue);
//    repository
//        .save(passphrase)
//        .subscribe(
//            this.passphrase::postValue, this::postThrowable, pending);
  }

public void delete(String key){
    repository
        .delete(key)
        .subscribe(() -> {}, this::postThrowable, pending);
  // TODO: 11/2/23 Refresh displayed list.
}
public void generate(int length){
  execute(repository.generate(length), generated::postValue);
//    repository
//        .generate(length)
//        .subscribe(generated::postValue, this::postThrowable, pending);
}

public void clearGenerated(){
    generated.postValue(null);
}

public void clearPassphrase(){
    passphrase.postValue(null);
}
  @Override
  public void onStop(@NotNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onStop(owner);
    pending.clear();
  }

  private void postThrowable(Throwable throwable){
    Log.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

  private <T> void execute(Single<T> stream, Consumer<? super T> consumer, MutableLiveData<?>... clearList){
    clearLiveData(clearList);
    stream.subscribe(consumer, this::postThrowable, pending);
  }

  private void execute(Completable stream, Action action, MutableLiveData<?>... clearList){
    clearLiveData(clearList);
    stream.subscribe(action, this::postThrowable, pending);
  }

  private void clearLiveData(MutableLiveData<?>[] clearList) {
    throwable.postValue(null);
    for(MutableLiveData<?> ld: clearList){
      ld.postValue(null);
    }
  }
}
