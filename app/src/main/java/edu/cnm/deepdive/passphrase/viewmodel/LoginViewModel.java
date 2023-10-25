package edu.cnm.deepdive.passphrase.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.passphrase.service.GoogleSignInService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

@HiltViewModel
public class LoginViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final GoogleSignInService signInService;
  private final MutableLiveData<GoogleSignInAccount> account;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  @Inject
  LoginViewModel(@ApplicationContext Context context, GoogleSignInService signInService){
    this.signInService = signInService;
    account = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    refresh();
  }

  public LiveData<GoogleSignInAccount> getAccount() {
    return account;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void refresh(){
    throwable.postValue(null);
    signInService.refresh()
        .subscribe(
            account::postValue,
            (throwable) -> account.postValue(null),
            pending
        );
  }
  public void startSignIn(ActivityResultLauncher<Intent> launcher){
    throwable.postValue(null);
    signInService.startSignIn(launcher);
  }

  public void completeSignIn(ActivityResult result){
    throwable.postValue(null);
    signInService.completeSignIn(result)
        .subscribe(
            account::postValue,
            this::postThrowable,
            pending
        );
  }
  public void signOut(){
    throwable.postValue(null);
    signInService.signOut()
        .doFinally(()-> account.postValue(null))
        .subscribe(
        ()->{},this::postThrowable,pending
    );
  }

  @Override
  public void onStop(@NotNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onStop(owner);
    pending.clear();
  }

  private void postThrowable(@NonNull Throwable throwable){
    Log.e(getClass().getSimpleName(),throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

}
