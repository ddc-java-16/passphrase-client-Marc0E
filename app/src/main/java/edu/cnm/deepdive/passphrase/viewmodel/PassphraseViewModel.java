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
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

@HiltViewModel
public class PassphraseViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final PassphraseRepository repository;
  private final MutableLiveData<Passphrase> passphrase;
  private final MutableLiveData<List<Passphrase>> passphrases;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  @Inject
  PassphraseViewModel(@ApplicationContext Context context, PassphraseRepository repository){
    this.repository = repository;
    passphrase = new MutableLiveData<>();
    passphrases = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
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
    repository
        .get(key)
        .subscribe(this.passphrase::postValue, this::postThrowable, pending);
  }

  public void fetch(){
    repository
        .get()
        .subscribe(this.passphrases::postValue, this::postThrowable, pending);
  }

  public void search(String fragment){
    repository
        .search(fragment)
        .subscribe(this.passphrases::postValue, this::postThrowable, pending);
  }

  public void save(Passphrase passphrase){
    repository
        .save(passphrase)
        .subscribe(
            this.passphrase::postValue, this::postThrowable, pending);
  }

public void delete(String key){
    repository
        .delete(key)
        .subscribe(() -> {}, this::postThrowable, pending);
  // TODO: 11/2/23 Refresh displayed list.
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
}
